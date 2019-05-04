Scenario: Create a simple Java Project

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the PIT views are opened
And the Console view is closed


When the user creates a project with name project1
Then the project project1 exists in the workspace

Scenario: Create a class Foo and it's test
When a class Foo in package foo.bar is created in project project1
Then package foo.bar exists in project project1
And class Foo exists in package foo.bar in project project1

When a class FooTest in package foo.bar is created in project project1
Then class FooTest exists in package foo.bar in project project1

When test FooTest in package foo.bar is run for project project1
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the mutation results are
|status|project|class|line|mutation|

Scenario: Create an empty test
Given the class FooTest in package foo.bar in project project1 is selected
When a method "@Test public void fooTest1() {Foo foo = new Foo();}" is created
And test FooTest in package foo.bar is run for project project1
Then a coverage report is generated with 0 classes tested with overall coverage of 100% and mutation coverage of 100%
Then the mutation results are
|status|project|class|line|mutation|

Scenario: Create a method in Foo
Given the class Foo in package foo.bar in project project1 is selected
When a method "public int doFoo(int i) {return i + 1;}" is created
And test FooTest in package foo.bar is run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 50% and mutation coverage of 0%
And the mutation results are
|status|project|package|class|line|mutation|
|NO_COVERAGE|project1|foo.bar|foo.bar.Foo|6|Replaced integer addition with subtraction|
|NO_COVERAGE|project1|foo.bar|foo.bar.Foo|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|

Scenario: Create a bad test for doFoo
Given the class FooTest in package foo.bar in project project1 is selected
When a method "@Test public void fooTest2() {new Foo().doFoo(1);}" is created
And test FooTest in package foo.bar is run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 0%
And the mutation results are
|status|project|package|class|line|mutation|
|SURVIVED|project1|foo.bar|foo.bar.Foo|6|Replaced integer addition with subtraction|
|SURVIVED|project1|foo.bar|foo.bar.Foo|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|

Scenario: Create a better test for doFoo
Given the class FooTest in package foo.bar in project project1 is selected
When a method "@Test public void fooTest3() {org.junit.Assert.assertEquals(2, new Foo().doFoo(1));}" is created
And test FooTest in package foo.bar is run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%
And the mutation results are
|status|project|package|class|line|mutation|
|KILLED|project1|foo.bar|foo.bar.Foo|6|Replaced integer addition with subtraction|
|KILLED|project1|foo.bar|foo.bar.Foo|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|

Scenario: Run mutation tests at package, package root & project level
When tests in package foo.bar are run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests in source root src are run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests are run for project project1
Then a coverage report is generated with 1 classes tested with overall coverage of 100% and mutation coverage of 100%

Scenario: Create a class Bar and it's test
When a class Bar in package foo.bar is created in project project1
Then class Bar exists in package foo.bar in project project1

When a class BarTest in package foo.bar is created in project project1
Then class BarTest exists in package foo.bar in project project1

Given the class Bar in package foo.bar in project project1 is selected
When a method "public int doBar(int i) {return i - 1;}" is created

Given the class BarTest in package foo.bar in project project1 is selected
When a method "@Test public void barTestCase1() {org.junit.Assert.assertEquals(0, new Bar().doBar(1));}" is created

Scenario: Run the new test
When test BarTest in package foo.bar is run for project project1
Then a coverage report is generated with 2 classes tested with overall coverage of 50% and mutation coverage of 50%
And the mutation results are
|status|project|package|class|line|mutation|
|KILLED|project1|foo.bar|foo.bar.Bar|6|Replaced integer subtraction with addition|
|KILLED|project1|foo.bar|foo.bar.Bar|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|
|NO_COVERAGE|project1|foo.bar|foo.bar.Foo|6|Replaced integer addition with subtraction|
|NO_COVERAGE|project1|foo.bar|foo.bar.Foo|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|
When tests in package foo.bar are run for project project1
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
And the mutation results are
|status|project|package|class|line|mutation|
|KILLED|project1|foo.bar|foo.bar.Bar|6|Replaced integer subtraction with addition|
|KILLED|project1|foo.bar|foo.bar.Bar|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|
|KILLED|project1|foo.bar|foo.bar.Foo|6|Replaced integer addition with subtraction|
|KILLED|project1|foo.bar|foo.bar.Foo|6|replaced return of integer sized value with (x == 0 ? 1 : 0)|
When tests in source root src are run for project project1
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%
When tests are run for project project1
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 100%

