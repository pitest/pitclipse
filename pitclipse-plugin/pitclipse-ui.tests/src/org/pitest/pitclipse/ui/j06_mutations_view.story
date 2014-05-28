Scenario: Create a simple Java Project

Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

When the user creates a project with name project1

Scenario: Create two classes Foo & Bar with bad tests
Given a bad test for class Foo in package foo.bar is created in project project1
And a bad test for class Bar in package foo.bar is created in project project1

When tests in package foo.bar are run for project project1
Then a coverage report is generated with 2 classes tested with overall coverage of 100% and mutation coverage of 0%
And the mutation results are
|status|project|package|class|line|mutation|
|SURVIVED|project1|foo.bar|foo.bar.Bar|9|removed call to java/util/ArrayList::clear|
|SURVIVED|project1|foo.bar|foo.bar.Bar|10|Replaced integer addition with subtraction|
|SURVIVED|project1|foo.bar|foo.bar.Bar|10|replaced return of integer sized value with (x == 0 ? 1 : 0)|
|SURVIVED|project1|foo.bar|foo.bar.Foo|9|removed call to java/util/ArrayList::clear|
|SURVIVED|project1|foo.bar|foo.bar.Foo|10|Replaced integer addition with subtraction|
|SURVIVED|project1|foo.bar|foo.bar.Foo|10|replaced return of integer sized value with (x == 0 ? 1 : 0)|


Scenario: Selecting a mutation opens the class in question at the right line number
When the following mutation is selected
|status|project|package|class|line|mutation|
|SURVIVED|project1|foo.bar|foo.bar.Foo|9|removed call to java/util/ArrayList::clear|
Then the file Foo.java is opened at line number 9
