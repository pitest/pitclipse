Scenario: Create a simple Java Project

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the user creates a project with name project2
Then the project project2 exists in the workspace

Scenario: Create some classes in different packages
When a class Cod in package sea.fish is created in project project2
Then package sea.fish exists in project project2
And class Cod exists in package sea.fish in project project2

When a class CodTest in package sea.fish is created in project project2
Then class CodTest exists in package sea.fish in project project2

Given the class CodTest in package sea.fish in project project2 is selected
When a method "@Test public void codTest() {org.junit.Assert.assertEquals(1, new Cod().doBob(1));}" is created
Given the class Cod in package sea.fish in project project2 is selected
When a method "public int doBob(int i) {return i;}" is created

When a class Trout in package lake.fish is created in project project2
Then package lake.fish exists in project project2
And class Trout exists in package lake.fish in project project2

When a class TroutTest in package lake.fish is created in project project2
Then class TroutTest exists in package lake.fish in project project2

Given the class TroutTest in package lake.fish in project project2 is selected
When a method "@Test public void troutTest() {org.junit.Assert.assertEquals(1, new Trout().doBob(1));}" is created
Given the class Trout in package lake.fish in project project2 is selected
When a method "public int doBob(int i) {return i;}" is created

When a class Frog in package lake.amphibian is created in project project2
Then package lake.amphibian exists in project project2
And class Frog exists in package lake.amphibian in project project2

When a class FrogTest in package lake.amphibian is created in project project2
Then class FrogTest exists in package lake.amphibian in project project2

Given the class FrogTest in package lake.amphibian in project project2 is selected
When a method "@Test public void frogTest() {org.junit.Assert.assertEquals(1, new Frog().doRibbit());}" is created
Given the class Frog in package lake.amphibian in project project2 is selected
When a method "public int doRibbit() {return 1;}" is created

Scenario: Run mutation testing at a package level
When tests in package sea.fish are run for project project2
Then a coverage report is generated with 3 classes tested with overall coverage of 33% and mutation coverage of 33%

When tests in package lake.fish are run for project project2
Then a coverage report is generated with 3 classes tested with overall coverage of 33% and mutation coverage of 33%

When tests in package lake.amphibian are run for project project2
Then a coverage report is generated with 3 classes tested with overall coverage of 33% and mutation coverage of 33%

Scenario: Run mutation testing at a package root level
When tests in source root src are run for project project2
Then a coverage report is generated with 3 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Run mutation testing at a project level
When tests are run for project project2
Then a coverage report is generated with 3 classes tested with overall coverage of 100% and mutation coverage of 100%

