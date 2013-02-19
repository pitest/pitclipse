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
Then a launch configuration with name FooTest is created

When the launch configuration with name FooTest is selected
Then the run in parallel option on the launch configuration is selected
And the use incremental analysis option on the launch configuration is not selected

Scenario: Change the run in parallel preference
When the mutation tests run in parallel preference is deselected

When a class BarTest in package foo.bar is created in project project5
And test BarTest in package foo.bar is run for project project5

!-- The new test should inherit that test should not run in parallel
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then a launch configuration with name BarTest is created
Then the run in parallel option on the launch configuration is not selected
And the use incremental analysis option on the launch configuration is not selected

!-- The existing test should show the original value
When the launch configuration with name FooTest is selected
Then the run in parallel option on the launch configuration is selected
And the use incremental analysis option on the launch configuration is not selected

Scenario: Change the run incremental analysis preference
When the mutation tests run in parallel preference is selected
When the mutation tests use incremental analysis preference is selected

When a class FooBarTest in package foo.bar is created in project project5
And test FooBarTest in package foo.bar is run for project project5

!-- The new test should inherit that test should not run in parallel
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then a launch configuration with name FooBarTest is created

When the launch configuration with name FooBarTest is selected
Then the run in parallel option on the launch configuration is selected
And the use incremental analysis option on the launch configuration is selected


!-- The existing tests should show the original value
When the launch configuration with name FooTest is selected
Then the run in parallel option on the launch configuration is selected
And the use incremental analysis option on the launch configuration is not selected

When the launch configuration with name BarTest is selected
Then the run in parallel option on the launch configuration is not selected
And the use incremental analysis option on the launch configuration is not selected


