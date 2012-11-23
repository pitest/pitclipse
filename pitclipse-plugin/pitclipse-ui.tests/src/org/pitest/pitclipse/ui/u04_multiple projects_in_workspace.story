Scenario: Create some projects

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the user creates a project with name project1
And the user creates a project with name project2
And the user creates a project with name project3
And the user creates a project with name project4

Scenario: Create a class and a bad test in project 1
When a class Foo in package foo.bar is created in project project1
And a class FooTest in package foo.bar is created in project project1

Given the class FooTest in package foo.bar in project project1 is selected
When a method "@Test public void fooTest1() {Foo foo = new Foo();}" is created
Given the class Foo in package foo.bar in project project1 is selected
When a method "public int doFoo(int i) {return i + 1;}" is created

Scenario: Create 2 fully tested classes in project 2
When a class Cod in package sea.fish is created in project project2
And a class CodTest in package sea.fish is created in project project2

Given the class CodTest in package sea.fish in project project2 is selected
When a method "@Test public void codTest() {org.junit.Assert.assertEquals(1, new Cod().doBob(1));}" is created
Given the class Cod in package sea.fish in project project2 is selected
When a method "public int doBob(int i) {return i;}" is created

When a class Frog in package lake.amphibian is created in project project2
When a class FrogTest in package lake.amphibian is created in project project2

Given the class FrogTest in package lake.amphibian in project project2 is selected
When a method "@Test public void frogTest() {org.junit.Assert.assertEquals(1, new Frog().doRibbit());}" is created
Given the class Frog in package lake.amphibian in project project2 is selected
When a method "public int doRibbit() {return 1;}" is created

Scenario: Create a fully tested class and an untested classs in project 3
When a class NormaJean in package foo.bar.plebs is created in project project3
And a class NormaJeanTest in package foo.bar.plebs is created in project project3

Given the class NormaJeanTest in package foo.bar.plebs in project project3 is selected
When a method "@Test public void njTestCase1() {org.junit.Assert.assertEquals(21, new NormaJean().doMyThing(1));}" is created
Given the class NormaJean in package foo.bar.plebs in project project3 is selected
When a method "public int doMyThing(int i) {return i + 20;}" is created

When a class TrevorBrookes in package foo.bar.plebs is created in project project3
Given the class TrevorBrookes in package foo.bar.plebs in project project3 is selected
When a method "public int doMyThing(int i) {return 2 * i;}" is created

Scenario: Add projects to classpath of project 4
When the dependent project project1 is added to the classpath of project4
When the dependent project project2 is added to the classpath of project4
When the dependent project project3 is added to the classpath of project4

Scenario: Add a test referencing other projects
When a class TestAll in package foo.bar.suite is created in project project4
And a class TestAllTest in package foo.bar.suite is created in project project4

Given the class TestAllTest in package foo.bar.suite in project project4 is selected
When a method "@Test public void testThings() {org.junit.Assert.assertEquals(4, new TestAll().doStuff(1));}" is created
Given the class TestAll in package foo.bar.suite in project project4 is selected
When a method "public int doStuff(int i) {int j = new Foo().doFoo(i); int k = new Cod().doBob(i); int l = new Frog().doRibbit(); return j + k + l;}" is created

Scenario: Run mutation testing
When tests are run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 50% and mutation coverage of 0%
When tests are run for project project2
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests are run for project project3
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%
When tests are run for project project4
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%



