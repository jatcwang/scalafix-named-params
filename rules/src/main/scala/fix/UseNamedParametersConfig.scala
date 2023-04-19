package fix

import metaconfig.ConfDecoder
import metaconfig.annotation.Description
import metaconfig.generic.Surface

final case class UseNamedParametersConfig(
  @Description(
    "Only perform the rewrite for method calls " +
      "with number of parameters equal or greater than this value"
  )
  minParams: Int,
  @Description("Skip variable names with a single letter or followed by a sequence of numbers")
  skipSingleAlphabet: Boolean
)

object UseNamedParametersConfig {
  val default: UseNamedParametersConfig = UseNamedParametersConfig(minParams = 3, skipSingleAlphabet = false)

  implicit val surface: Surface[UseNamedParametersConfig] =
    metaconfig.generic.deriveSurface[UseNamedParametersConfig]
  implicit val decoder: ConfDecoder[UseNamedParametersConfig] =
    metaconfig.generic.deriveDecoder(default)
}
