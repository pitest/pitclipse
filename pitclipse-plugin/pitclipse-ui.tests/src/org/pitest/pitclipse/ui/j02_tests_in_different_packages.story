Scenario: Create a simple Java Project

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the user creates a project with name project2
Then the project project2 exists in the workspace

Scenario: Create some classes in different packages
When a class Cod in package sea.fish is created in project project2
And a class CodTest in package sea.fish is created in project project2

Given the class CodTest in package sea.fish in project project2 is selected
When a method "@Test public void codTest() {org.junit.Assert.assertEquals(1, new Cod().doBob(1));}" is created
Given the class Cod in package sea.fish in project project2 is selected
When a method "public int doBob(int i) {return i;}" is created

When a class Trout in package lake.fish is created in project project2
When a class TroutTest in package lake.fish is created in project project2

Given the class TroutTest in package lake.fish in project project2 is selected
When a method "@Test public void troutTest() {org.junit.Assert.assertEquals(1, new Trout().doBob(1));}" is created
Given the class Trout in package lake.fish in project project2 is selected
When a method "public int doBob(int i) {return i;}" is created

When a class Frog in package lake.amphibian is created in project project2
When a class FrogTest in package lake.amphibian is created in project project2

Given the class FrogTest in package lake.amphibian in project project2 is selected
When a method "@Test public void frogTest() {org.junit.Assert.assertEquals(1, new Frog().doRibbit());}" is created
Given the class Frog in package lake.amphibian in project project2 is selected
When a method "public int doRibbit() {return 1;}" is created

Given a bad test for class Snail is created in the default package in project project2


Scenario: Run mutation testing at a package level
When tests in package sea.fish are run for project project2
Then a coverage report is generated with 4 classes tested with overall coverage of 25% and mutation coverage of 14%

When tests in package lake.fish are run for project project2
Then a coverage report is generated with 4 classes tested with overall coverage of 25% and mutation coverage of 14%

When tests in package lake.amphibian are run for project project2
Then a coverage report is generated with 4 classes tested with overall coverage of 25% and mutation coverage of 14%

Scenario: Run mutation testing at a package root level
When tests in source root src are run for project project2
Then a coverage report is generated with 4 classes tested with overall coverage of 100% and mutation coverage of 43%

Scenario: Run mutation testing at a project level
When tests are run for project project2
Then a coverage report is generated with 4 classes tested with overall coverage of 100% and mutation coverage of 43%

