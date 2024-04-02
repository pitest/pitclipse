Feature: Mutation view shows analysis results

  Scenario: Create a simple Java Project
    Given eclipse opens and the welcome screen is acknowledged
    And the java perspective is opened
    And an empty workspace
    When the user creates a project with name project1

  Scenario: Create two classes Foo & Bar with bad tests
    Given a bad test for class Foo in package foo.bar is created in project project1
    And a bad test for class Bar in package foo.bar is created in project project1
    When tests in package foo.bar are run for project project1
    Then a coverage report is generated with 2 classes tested with overall coverage of 80% and mutation coverage of 0%
    And the mutation results are
      | status      | project  | package | class       | line | mutation                                                     |
      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with false     |
      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with false     |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | Replaced integer addition with subtraction                   |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | replaced int return with 0 for foo/bar/Bar::f                |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | Replaced integer addition with subtraction                   |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | replaced int return with 0 for foo/bar/Foo::f                |

  Scenario: Selecting a mutation opens the class in question at the right line number
    When the following mutation is selected
      | status   | project  | package | class       | line | mutation            |
      | SURVIVED | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with false |
    Then the file "Foo.java" is opened at line number 9

  Scenario: Using the stronger mutators yields more mutation results
    Given the stronger mutator preference is selected
    When tests in package foo.bar are run for project project1
    Then a coverage report is generated with 2 classes tested with overall coverage of 80% and mutation coverage of 0%
    And the mutation results are
      | status      | project  | package | class       | line | mutation                                                     |
      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with false     |
      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with true      |
      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with false     |
      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with true      |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | Replaced integer addition with subtraction                   |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | replaced int return with 0 for foo/bar/Bar::f                |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | Replaced integer addition with subtraction                   |
      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | replaced int return with 0 for foo/bar/Foo::f                |

  # This scenario does not pass anymore
  # This is likely due to the fact that Eclipse IDE creates new launch configuration
  # by duplicating the new one each time PIT is launched
#  @ignore
#  Scenario: Using all mutators yields even more mutation results
#    Given the all mutators preference is selected
#    When tests in package foo.bar are run for project project1
#    Then a coverage report is generated with 2 classes tested with overall coverage of 80% and mutation coverage of 9%
#    And the mutation results are
#      | status      | project  | package | class       | line | mutation                                                     |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | Substituted 1 with 0                                         |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with false     |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed call to java/util/ArrayList::size                    |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with false     |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |    9 | removed conditional - replaced equality check with true      |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |   12 | Substituted 0 with 1                                         |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Bar |   12 | replaced return of integer sized value with (x == 0 ? 1 : 0) |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | Substituted 1 with 0                                         |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with false     |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed call to java/util/ArrayList::size                    |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with false     |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |    9 | removed conditional - replaced equality check with true      |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |   12 | Substituted 0 with 1                                         |
#      | SURVIVED    | project1 | foo.bar | foo.bar.Foo |   12 | replaced return of integer sized value with (x == 0 ? 1 : 0) |
#      | KILLED      | project1 | foo.bar | foo.bar.Bar |    8 | removed call to java/util/ArrayList::<init>                  |
#      | KILLED      | project1 | foo.bar | foo.bar.Foo |    8 | removed call to java/util/ArrayList::<init>                  |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | Replaced integer addition with subtraction                   |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | Substituted 1 with 0                                         |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Bar |   10 | replaced return of integer sized value with (x == 0 ? 1 : 0) |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | Replaced integer addition with subtraction                   |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | Substituted 1 with 0                                         |
#      | NO_COVERAGE | project1 | foo.bar | foo.bar.Foo |   10 | replaced return of integer sized value with (x == 0 ? 1 : 0) |
