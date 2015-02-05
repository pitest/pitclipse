Scenario: Check the configurations take in default arguments

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

Scenario: No configurations exist initially
Then no PIT launch configurations exist

Scenario: Create a test
When the user creates a project with name project5
When a class SomeNewClassTest in package foo.bar is created in project project5

Scenario: Launching PIT creates a configuration
When test SomeNewClassTest in package foo.bar is run for project project5
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.SomeNewClassTest|foo.bar.SomeNewClassTest|project5|||true|false|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Scenario: Change the timeout Constant
Given the timeout constant is 500
When test SomeNewClassTest in package foo.bar is run for project project5
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the options passed to Pit match:
|classUnderTest|timeoutConst|
|foo.bar.SomeNewClassTest|500|

Scenario: Change the timeout Factor
Given the timeout factor is 2

When test SomeNewClassTest in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|timeoutFactor|
|foo.bar.SomeNewClassTest|2|

Scenario: Change the run in parallel preference
When the mutation tests run in parallel preference is deselected

When a class AnotherNewClassTest in package foo.bar is created in project project5
And test AnotherNewClassTest in package foo.bar is run for project project5

!-- The new test should inherit that test should not run in parallel
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.AnotherNewClassTest|foo.bar.AnotherNewClassTest, foo.bar.SomeNewClassTest|project5|||false|false|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|AnotherNewClassTest|false|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Scenario: Change the run incremental analysis preference
When the mutation tests run in parallel preference is selected
When the mutation tests use incremental analysis preference is selected

When a class YetAnotherClassTest in package foo.bar is created in project project5
And test YetAnotherClassTest in package foo.bar is run for project project5

!-- The new test should inherit that test should run incremental analysis
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.YetAnotherClassTest|foo.bar.SomeNewClassTest, foo.bar.AnotherNewClassTest, foo.bar.YetAnotherClassTest|project5|||true|true|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|AnotherNewClassTest|false|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|YetAnotherClassTest|true|true|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Scenario: Change the excluded classes preference
When the mutation tests use incremental analysis preference is deselected
And the excluded classes preference is set to "org.foo.*IntTest, *DbTest"

When a class NoNotAnotherClass in package foo.bar is created in project project5
And test NoNotAnotherClass in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.NoNotAnotherClass|foo.bar.NoNotAnotherClass, foo.bar.SomeNewClassTest, foo.bar.AnotherNewClassTest, foo.bar.YetAnotherClassTest|project5|org.foo.*IntTest, *DbTest||true|false|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|AnotherNewClassTest|false|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|NoNotAnotherClass|true|false|org.foo.*IntTest, *DbTest||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|YetAnotherClassTest|true|true|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Scenario: Change the excluded methods preference
When the excluded classes preference is not set
And the excluded methods preference is set to "*toString*, doNotMutateMe*"

When a class ClassMadnessTest in package foo.bar is created in project project5
And test ClassMadnessTest in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.ClassMadnessTest|foo.bar.NoNotAnotherClass, foo.bar.SomeNewClassTest, foo.bar.AnotherNewClassTest, foo.bar.ClassMadnessTest, foo.bar.YetAnotherClassTest|project5||*toString*, doNotMutateMe*|true|false|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|AnotherNewClassTest|false|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|ClassMadnessTest|true|false||*toString*, doNotMutateMe*|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|NoNotAnotherClass|true|false|org.foo.*IntTest, *DbTest||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|YetAnotherClassTest|true|true|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|


Scenario: Change the avoid Calls To preference
When the excluded methods preference is not set
And the avoid calls to preference is set to "org.slf4j, org.apache"

When a class TestAvoidClassesTest in package foo.bar is created in project project5
And test TestAvoidClassesTest in package foo.bar is run for project project5

Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%

Then the options passed to Pit match:
|classUnderTest|classesToMutate|projects|excludedClasses|excludedMethods|runInParallel|incrementalAnalysis|avoidCallsTo|
|foo.bar.TestAvoidClassesTest|foo.bar.TestAvoidClassesTest, foo.bar.NoNotAnotherClass, foo.bar.SomeNewClassTest, foo.bar.AnotherNewClassTest, foo.bar.ClassMadnessTest, foo.bar.YetAnotherClassTest|project5|||true|false|org.slf4j, org.apache|

Then the launch configurations are configured as:
|name|runInParallel|useIncrementalAnalysis|excludedClasses|excludedMethods|avoidCallsTo|
|AnotherNewClassTest|false|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|ClassMadnessTest|true|false||*toString*, doNotMutateMe*|java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|NoNotAnotherClass|true|false|org.foo.*IntTest, *DbTest||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|SomeNewClassTest|true|false|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
|TestAvoidClassesTest|true|false|||org.slf4j, org.apache|
|YetAnotherClassTest|true|true|||java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging|
