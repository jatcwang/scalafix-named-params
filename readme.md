# Use Named Parameters

This is a scalafix rule to add named parameters to function calls without one.

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

The rule also works for:

* Constructors
* Curried functions (inc constructors)

**Note**: this rule isn't published atm. I'm trying to find an umbrella project to make this more discoverable. Let me know :)

## Configuration

By default, the rule will apply named parameter to function calls with 3 or more parameters.

You can configure this in `.scalafix.conf`
```
UseNamedParameters.minParams = 2
```
