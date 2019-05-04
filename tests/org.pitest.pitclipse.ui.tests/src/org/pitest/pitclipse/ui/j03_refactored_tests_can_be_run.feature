Scenario: Create a simple Java Project

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the user creates a project with name project3
Then the project project3 exists in the workspace

Scenario: Create a class that will be renamed later
When a class NormaJean in package foo.bar.plebs is created in project project3
Then package foo.bar.plebs exists in project project3
And class NormaJean exists in package foo.bar.plebs in project project3

When a class NormaJeanTest in package foo.bar.plebs is created in project project3
Then class NormaJeanTest exists in package foo.bar.plebs in project project3

Given the class NormaJeanTest in package foo.bar.plebs in project project3 is selected
When a method "@Test public void njTestCase1() {org.junit.Assert.assertEquals(21, new NormaJean().doMyThing(1));}" is created
Given the class NormaJean in package foo.bar.plebs in project project3 is selected
When a method "public int doMyThing(int i) {return i + 20;}" is created

When test NormaJeanTest in package foo.bar.plebs is run for project project3
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%

When tests in package foo.bar.plebs are run for project project3
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%

When tests in source root src are run for project project3
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Create another class
When a class TrevorBrookes in package foo.bar.plebs is created in project project3
Then package foo.bar.plebs exists in project project3
And class TrevorBrookes exists in package foo.bar.plebs in project project3

When a class TrevorBrookesTest in package foo.bar.plebs is created in project project3
Then class TrevorBrookesTest exists in package foo.bar.plebs in project project3

Given the class TrevorBrookesTest in package foo.bar.plebs in project project3 is selected
When a method "@Test public void tbTestCase1() {org.junit.Assert.assertEquals(10, new TrevorBrookes().doMyThing(5));}" is created
Given the class TrevorBrookes in package foo.bar.plebs in project project3 is selected
When a method "public int doMyThing(int i) {return 2 * i;}" is created

When test TrevorBrookesTest in package foo.bar.plebs is run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%

When tests in package foo.bar.plebs are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%

When tests in source root src are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Rename a class
Given the class NormaJean in package foo.bar.plebs in project project3 is selected
When the class is renamed to MarilynMonroe
Given the class NormaJeanTest in package foo.bar.plebs in project project3 is selected
When the class is renamed to MarilynMonroeTest

When test MarilynMonroeTest in package foo.bar.plebs is run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%
When tests in package foo.bar.plebs are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests in source root src are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Rename the other class
Given the class TrevorBrookes in package foo.bar.plebs in project project3 is selected
When the class is renamed to BrunoBrookes
Given the class TrevorBrookesTest in package foo.bar.plebs in project project3 is selected
When the class is renamed to BrunoBrookesTest

When test BrunoBrookesTest in package foo.bar.plebs is run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%
When tests in package foo.bar.plebs are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests in source root src are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Rename the package
Given the package foo.bar.plebs in project project3 is selected
When the package is renamed to foo.bar.slebs
When tests in package foo.bar.slebs are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests in source root src are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When test MarilynMonroeTest in package foo.bar.slebs is run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%
When test BrunoBrookesTest in package foo.bar.slebs is run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%


