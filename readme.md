# Scalafix rule for Named Parameters

This is a scalafix rule to add named parameters for your constructor and method calls.

The rule should fix any valid Scala code, including:

- Curried calls and var args
- Special characters in field names
- Java classes and methods (by not doing anything :))

For example given this source code:

```
func(1,2,3)

myObj.someMethod(4,5,"6")
```

It will inspect the signature of the function methods and add named parameters:

```
func(first = 1, second = 2, third = 3)

myObj.someMethod(param1 = 4, param2 = 5, strParam =  "6")
```

## Usage

Ensure your project has scalafix enabled and has the scalac option `-P:semanticdb:synthetics:on`.

Run in SBT:

```
scalafixEnable

yourmodulename/scalafix dependency:UseNamedParameters@com.github.jatcwang:scalafix-named-params:<LATEST_VERSION>

# Or run the rule in test files
yourmodulename/test:scalafix dependency:UseNamedParameters@com.github.jatcwang:scalafix-named-params:<LATEST_VERSION>
```
Replace `<LATEST_VERSION>` with the latest version of the rule.

[![Release](https://img.shields.io/nexus/r/com.github.jatcwang/scalafix-named-params_2.12?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/releases/com/github/jatcwang/scalafix-named-params_2.12/)

## Configuration

By default, the rule will apply named parameter to function calls with 3 or more parameters.

You can configure this in `.scalafix.conf`
```
UseNamedParameters.minParams = 2
```
