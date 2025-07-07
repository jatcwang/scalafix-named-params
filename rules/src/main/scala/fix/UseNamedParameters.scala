package fix

import metaconfig.Configured
import scalafix.v1._

import scala.annotation.tailrec
import scala.meta._

final class UseNamedParameters(config: UseNamedParametersConfig)
    extends SemanticRule(classOf[UseNamedParameters].getSimpleName) {
  def this() = this(UseNamedParametersConfig.default)

  private val singleAlphabetPattern = "^[A-Za-z]\\d*$".r

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    val requiredScalacOptions = Seq("-P:semanticdb:synthetics:on", "-Xsemanticdb")
    if (requiredScalacOptions.exists(config.scalacOptions.contains)) {
      config.conf
        .getOrElse(this.getClass.getSimpleName)(this.config)
        .map(newConfig => new UseNamedParameters(newConfig))
    } else {
      Configured.error(
        s"""This rule requires SemanticDB synthetics to work properly (e.g. to detect case class apply).
          |Please add ${requiredScalacOptions.map("\"" + _ + "\"").mkString(" or ")} to scala compiler options (e.g. scalacOptions in SBT).""".stripMargin
      )
    }
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree
      .collect {
        case Init.After_4_6_0(_, name, argss) if !hasPlaceholder(argss.flatMap(_.values).toList) =>
          resolveScalaMethodSignatureFromSymbol(name.symbol) match {
            case Some(methodSig) =>
              val patchGens: List[(Term, Int) => Patch] =
                methodSig.parameterLists.zipWithIndex.map { case (_, idx) =>
                  mkPatchGenForArgList(config, methodSig, idx)
                }
              argss
                .zip(patchGens)
                .flatMap { case (argsInBlock, patchGen) =>
                  argsInBlock.zipWithIndex.map { case (t, idx) => patchGen(t, idx) }
                }.asPatch.atomic
            case None => Patch.empty
          }
        case Term.Apply.After_4_6_0(fun, args) if !hasPlaceholder(args) =>
          resolveFunctionTerm(fun) match {
            case Some(fname) =>
              val methodSignatureOpt =
                resolveScalaMethodSignatureFromSymbol(fname.symbol).orElse(resolveFromSynthetics(fname))
              (methodSignatureOpt match {
                case Some(methodSig)
                    if methodSig.parameterLists.nonEmpty => // parameterLists.nonEmpty filters out FunctionX types
                  val patchGen: (Term, Int) => Patch =
                    mkPatchGenForArgList(config, methodSig, determineParamBlockIndex(fname))
                  args.zipWithIndex.map { case (t, idx) => patchGen(t, idx) }
                case _ => List.empty
              }).asPatch.atomic
            case _ => Patch.empty
          }
      }
      .asPatch
  }

  private def hasPlaceholder(argTerms: List[Term]): Boolean =
    argTerms.collect { case Term.Placeholder() => true }.exists(x => x)

  private def resolveFunctionTerm(term: Term): Option[Term] =
    term match {
      case fname: Term.Name => Some(fname)
      case fname: Term.Apply =>
        // For curried functions, return the Term as is as we need
        // it to figure out which param block we're currently handling
        Some(fname)
      case Term.ApplyType.After_4_6_0(fname, _) => Some(fname)
      case s: Term.Select => Some(s.name)
      case _ => None
    }

  private def mkPatchGenForArgList(
    config: UseNamedParametersConfig,
    methodSig: MethodSignature,
    paramBlockIdx: Int
  )(implicit doc: SemanticDocument): (Term, Int) => Patch = {
    // If the result of applying the method is a function, it will be IndexOutOfBounds
    if (methodSig.parameterLists.size > paramBlockIdx) {
      val thisParamBlock = methodSig.parameterLists(paramBlockIdx)
      // Whether to apply named param patching is dependent on parameters
      // in the method definition, not use site.
      if (thisParamBlock.length < config.minParams) { (_, _) =>
        Patch.empty
      } else { (term: Term, idx: Int) =>
        {
          term match {
            case _: Term.Assign => Patch.empty // Already using named param, no patch needed
            case _: Term.Block => Patch.empty // map { _ => _ }
            case _: Term.PartialFunction => Patch.empty // map { case _ => _ }
            case t =>
              // Term.Name will escape any weird identifiers
              thisParamBlock.lift(idx) match {
                case Some(symInfo)
                    // In the case of repeated parameters, if the parameter name is given only at the beginning, it is broken.
                    if !symInfo.signature.toString().startsWith("* Tuple") =>
                  val paramName = Term.Name(symInfo.displayName).toString
                  if (config.skipSingleAlphabet && singleAlphabetPattern.findFirstIn(paramName).nonEmpty) {
                    Patch.empty
                  } else {
                    Patch.addLeft(t, s"$paramName = ")
                  }
                case _ => // Var args
                  Patch.empty
              }
          }
        }
      }
    } else { (_, _) =>
      Patch.empty
    }
  }

  private def resolveScalaMethodSignatureFromSymbol(
    funcSymbol: Symbol
  )(implicit doc: SemanticDocument): Option[MethodSignature] =
    funcSymbol.info.flatMap { symInfo =>
      if (symInfo.isScala)
        symInfo.signature match {
          case m: MethodSignature => Some(m)
          case _ => None
        }
      else None
    }

  // To resolve companion object .apply methods
  private def resolveFromSynthetics(funcTerm: Term)(implicit doc: SemanticDocument): Option[MethodSignature] = {
    funcTerm.synthetics
      .flatMap(_.symbol)
      .flatMap(_.info)
      .map(_.signature)
      .collectFirst { case m: MethodSignature =>
        m
      }
  }

  @tailrec
  private def determineParamBlockIndex(curFuncTerm: Term, curIndex: Int = 0): Int =
    curFuncTerm match {
      case Term.Apply.After_4_6_0(innerFuncTerm, _) => determineParamBlockIndex(innerFuncTerm, curIndex + 1)
      case _ => curIndex
    }
}
