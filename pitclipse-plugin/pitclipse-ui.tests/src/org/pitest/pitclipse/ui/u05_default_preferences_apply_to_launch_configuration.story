Scenario: Check the configurations take in default arguments

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

Scenario: No configurations exist initially
Then no PIT launch configurations exist

Scenario: Create a test
When the user creates a project with name project5
When a class Foo in package foo.bar is created in project project5
When a class FooTest in package foo.bar is created in project project5

Scenario: Launching PIT creates a configuration
When test FooTest in package foo.bar is run for project project5
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|
|FooTest|true|false|||

Scenario: Change the run in parallel preference
When the mutation tests run in parallel preference is deselected

When a class BarTest in package foo.bar is created in project project5
And test BarTest in package foo.bar is run for project project5

!-- The new test should inherit that test should not run in parallel
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|
|BarTest|false|false|||
|FooTest|true|false|||

Scenario: Change the run incremental analysis preference
When the mutation tests run in parallel preference is selected
When the mutation tests use incremental analysis preference is selected

When a class FooBarTest in package foo.bar is created in project project5
And test FooBarTest in package foo.bar is run for project project5

!-- The new test should inherit that test should run incremental analysis
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|
|BarTest|false|false|||
|FooBarTest|true|true|||
|FooTest|true|false|||

Scenario: Change the excluded classes preference
When the mutation tests use incremental analysis preference is deselected
And the excluded classes preference is set to "org.foo.*IntTest, *DbTest"

When a class BarFooTest in package foo.bar is created in project project5
And test BarFooTest in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|
|BarFooTest|true|false|org.foo.*IntTest, *DbTest||
|BarTest|false|false|||
|FooBarTest|true|true|||
|FooTest|true|false|||

Scenario: Change the excluded methods preference
When the excluded classes preference is not set
And the excluded methods preference is set to "*toString*, doNotMutateMe*"

When a class FooBarFooTest in package foo.bar is created in project project5
And test FooBarFooTest in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|
|BarFooTest|true|false|org.foo.*IntTest, *DbTest||
|BarTest|false|false|||
|FooBarFooTest|true|false||*toString*, doNotMutateMe*|
|FooBarTest|true|true|||
|FooTest|true|false|||
