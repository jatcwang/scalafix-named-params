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

**Note**: this rule isn't published atm. I'm trying to find an umbrella project to make this more discoverable. Let me know :)

## Usage

Since this rule isn't published, you'll need to clone this repo:

```
git clone https://github.com/jatcwang/scalafix-named-params --depth=1
cd scalafix-named-params
sbt publishLocal
```

and then in your project's SBT console (make sure your project is using the scalafix plugin)

```
scalafixEnable

yourmodulename/scalafix dependency:UseNamedParameters@com.github.jatcwang:scalafix-named-params:0.1.0-LOCAL

# Run the rule in test files
yourmodulename/test:scalafix dependency:UseNamedParameters@com.github.jatcwang:scalafix-named-params:0.1.0-LOCAL
```

## Configuration

By default, the rule will apply named parameter to function calls with 3 or more parameters.

You can configure this in `.scalafix.conf`
```
UseNamedParameters.minParams = 2
```
