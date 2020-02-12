# How to contribute <!-- omit in toc -->

- [What can I do?](#what-can-i-do)
- [How should I contribute?](#how-should-i-contribute)
- [How is the plug-in architected?](#how-is-the-plug-in-architected)
- [How do I make changes?](#how-do-i-make-changes)
  - [Which IDE should I use?](#which-ide-should-i-use)
  - [How do I open the projects in Eclipse IDE?](#how-do-i-open-the-projects-in-eclipse-ide)
  - [What is the coding policy?](#what-is-the-coding-policy)
  - [How do I create a new plug-in?](#how-do-i-create-a-new-plug-in)
  - [How do I create tests for a new plug-in?](#how-do-i-create-tests-for-a-new-plug-in)
- [How can I check that my changes are consistent with the codebase?](#how-can-i-check-that-my-changes-are-consistent-with-the-codebase)
- [How can I check that the tests still pass?](#how-can-i-check-that-the-tests-still-pass)
- [How can I manually test the plug-in?](#how-can-i-manually-test-the-plug-in)
- [How should I commit my changes?](#how-should-i-commit-my-changes)
  - [How should I write a commit message?](#how-should-i-write-a-commit-message)
  - [How should I manage the history?](#how-should-i-manage-the-history)

## What can I do?

If you want to contribute by writing code, feel free to pick an open issue or submit a new feature.

Otherwise, you can create an issue to report a bug, ask for a new feature or for more documentation. This is appreciated too!

> **Note**: if you want to solve an open issue, please leave a comment on the corresponding thread so that we know that someone is working on it.

## How should I contribute?

1. Fork the project
2. Create a new branch with a meaningful name
3. Commit your changes
4. Submit a PR

When a PR is created, it is automatically analyzed by the following CI tools to ensure a good code quality:
- [Travis CI](https://travis-ci.com/pitest/pitclipse)
- [CodeCov](https://codecov.io/github/pitest/pitclipse)
- [SonarCloud](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse)

> **Important**: SonarCloud is not notified when the PR is submitted from a fork. Indeed, because of [security restriction in Travis CI](https://docs.travis-ci.com/user/environment-variables/#defining-encrypted-variables-in-travisyml), forks cannot use encrypted SonarCloud token.

## How is the plug-in architected?

Pitclipse is made of several Eclipse bundles. Each bundle is implemented as an Eclipse project and is available in the `bundles/` directory.

Please see [bundles/README.md](bundles/README.md) for further details.

## How do I make changes?

### Which IDE should I use?

The [Eclipse IDE for RCP developers](https://www.eclipse.org/downloads/packages/) is the preferred IDE to develop Eclipse plug-ins.

### How do I open the projects in Eclipse IDE?

The projects can be imported from Eclipse IDE:

1. `File` > `Import...`
2. `Existing Projects into Workspace`
3. Type to the path of the `bundles` directory in the `Select root directory` field
4. Check all projects
5. `Finish`

Wait for all projects to be imported. Depending on the Eclipse package you are using many errors may come up: some projects' dependencies may be missing. To solve this, we have to tell Eclipse IDE to use a specific environment and to adjust a few other things, related to Maven JARs. Our development environment is specified through a [target platform](https://www.vogella.com/tutorials/EclipseTargetPlatform/article.html). This target platform is located within the `releng` directory; to use it:

1. `File` > `Import...`
2. `Existing Projects into Workspace`
3. Type to the path of the `releng` directory in the `Select root directory` field
4. Check only the `org.pitest.pitclipse.target` project
5. `Finish`
6. Open the `org.pitest.pitclipse.target.target` file; Eclipse IDE starts downloading all the dependencies, which may take some time. The process can be followed thanks to the _Progress_ view
7. On the top-right corner of the editor, click on _Set as Active Target Platform_
8. Open the `pom.xml` in the project `org.pitest`, navigate to the `<execution>` element marked with error, use the menu "Edit" > "Quick Fix" and select "Discover new m2e connectors"; in the dialog select `maven-dependency-plugin` and press "Finish"; conclude the installation procedure and restart Eclipse when prompted
9. Use the menu "Project" > "Clean" and clean all projects; you should now have an error free workspace.

> **Note**: in some rare cases errors still remain. In such a case, opening the `org.pitest.pitclipse.target.target` file and clicking on _Reload target platform_ or restarting Eclipse IDE should definitely fix them.

### What is the coding policy?

To ensure a good code quality:
 - all the features must be tested
 - all the public API must be documented

### How do I create a new plug-in?

A source plug-in should be created in the `bundles/` directory. A new plug-in project can be created as follows:

1. `File` > `New` > `Other...`
2. Select `Plug-in Project`
3. Type the name of the plug-in and change its default location
4. Click `Finish`

> **Caution**: do not forget to add the corresponding module in the `bundles/pom.xml` file, otherwise the plug-in will be ignored by Maven.

### How do I create tests for a new plug-in?

Tests are hosted in _fragments_ located under the `tests/` directory. By convention, a fragment's name is the name of the tested plug-in suffixed with `.tests`. A new fragment project can be created as follows:

1. `File` > `New` > `Other...`
2. Select `Fragment Project`
3. Type the name of the fragment and change its default location
4. Click `Next` then select the host plug-in (the one that contains the code to test)
4. Click `Finish`

In order to include the tests in Maven build, the following steps are required:
1. Add the corresponding module to the `tests/pom.xml` file
2. Add the source and test modules to the `org.pitest.pitclipse.tests.coverage.report/pom.xml` file.

Please take a look at existing tests and make yours consistent.

## How can I check that my changes are consistent with the codebase?

You can check the code with the following command:
```
mvn clean verify
```

It checks that:
 - the code compiles
 - all the tests pass
 - the code style is consistent

Code style is enforced by Checkstyle. You may need to install the [corresponding Eclipse IDE plug-in](https://checkstyle.org/eclipse-cs/#!/). The plug-in is not able to catch everything, so please try to keep the style consistent.

> **Boy Scout Rule**: Leave your code better than you found it!

## How can I check that the tests still pass?

Tests can be run from Maven with the following command:
```
mvn clean verify
```

Alternatively, tests can be run from Eclipse:
In `tests` project you find some stored "Launch configurations", ending with `.launch`; it is best to use such launch configurations to run all the tests of that project, since they are already configured correctly:

1. Right-click on such a `.launch` file
2. `Run As` and select the single menu entry

For UI tests, a new Eclipse window should open during the time of the tests, before closing automatically. Please, do not interact with such a window or you will break test execution.

## How can I manually test the plug-in?

Manual tests are still useful for prototyping, especially since UI tests are not implemented yet.

To open a new Eclipse IDE instance that uses the plug-in under development:

1. Right-click on a project
2. `Run As` > `Eclipse application`

A new Eclipse IDE window should open, in which new projects can be created for testing purposes.

## How should I commit my changes?

### How should I write a commit message?

A commit message is usually made of two sections:
```
<subject>

<details>
```
The subject must:
 - be a one-liner,
 - start with a capital letter,
 - use the imperative, present tense,
 - _not_ end with a dot.

The details must:
 - be separated with the subject by a blank line,
 - explain the reason of the commit, a technical choice or some implementation details.

For instance:
```
Compute code coverage during Travis CI build

The new jacoco Maven profile allows to compute code's coverage
thanks to JaCoCo. It can be used as follows:

 * mvn verify -P jacoco

All reports are aggregated by the org.pitest.pitclipse.tests.coverage.report
module, which makes possible to forward the result to CI tools such as CodeCov
and Coveralls.
```

> **Note**: the 'details' part can be ignored when the commit is straightforward.

### How should I manage the history?

> “A commit, like a well-designed function or class, should represent a single concept. A distinct, cohesive commit is easy to understand, review, and, if necessary, revert.”
>
> © _Jared Carroll ([Crafting Commits in Git](https://blog.carbonfive.com/2011/10/10/crafting-commits-in-git/))_

Before submitting a PR, new commits should be reviewed to ensure that they are cohesive and, above all, easy to understand and review. If a commit affects dozens of files then it is likely too big and should be split into several smaller ones. If a change is spread across several small commits then they should likely be squashed.
