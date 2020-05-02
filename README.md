<h1 align="center">
  <a name="logo" href="https://gemoc.org/ale-lang"><img src="https://pitest.org/images/pit-black-150x152.png" alt="ALE logo" width="25"/></a>
  Pitclipse
</h1>
<p align="center">
	<i>Tests your tests right into your IDE!</i>
</p>

<div align="center">

[![Build Status](https://travis-ci.com/pitest/pitclipse.svg?branch=master)](https://travis-ci.com/pitest/pitclipse) [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=sqale_index)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=coverage)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse) [ ![Download](https://api.bintray.com/packages/kazejiyu/Pitclipse/releases/images/download.svg) ](https://bintray.com/kazejiyu/Pitclipse/releases/_latestVersion)

</div>

Provides mutation coverage for your Java programs within the Eclipse IDE. Built on [PIT (Pitest)](http://pitest.org) for reliability.

## What is mutation testing?

> Faults (or mutations) are automatically seeded into your code, then your tests are run. If your tests fail then the mutation is killed, if your tests pass then the mutation lived.
>
> The quality of your tests can be gauged from the percentage of mutations killed.
>
> *Henry Coles, [pitest.org](https://pitest.org)*

## Main Features

- **Reliability**: uses [PIT (Pitest)](http://pitest.org) to compute
- **Customization**: provides numerous preferences to tailor analysis
- **JUnit support**: works with both JUnit 4 and JUnit 5 tests

## Usage

First of all, you need to install Pitclipse in your Eclipse IDE (see [Installation](Installation) below).

Once the plug-in is installed, you can run Pitest:
- Right-click on a Java project defining unit tests
- `Run As` > `PIT Mutation Test`

Wait a few seconds, two views should open to show the results:
- **PIT Summary**: shows the percentage of mutation coverage
- **PIT Mutations**: shows the detected mutations and their location in code

It is also possible to run a single JUnit test class. Specific PIT options can be configured from the Launch Configuration window:
- `Run` > `Run Configurations...`
- Double-click on `PIT Mutation Test`
- Specify the options
- Press `Run`

Preferences also allow to change mutation settings (`Window > Preferences > Pitest`).

## Installation

The plug-in is available in the [Eclipse Marketplace](https://marketplace.eclipse.org/content/pitclipse).

Drag the following button to your running Eclipse workspace to start the installation:
<div align="center">
  <a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=1426461" class="drag" title="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client"><img typeof="foaf:Image" class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png" alt="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client" /></a>
</div>

<details>
  <summary><b>Or show how to install it manually</b></summary>

  1. Open Eclipse IDE
  2. Go to *Help > Install New Software...*
  3. Copy the update site’s URL in the *Work with* textbox:
     	- https://dl.bintray.com/kazejiyu/Pitclipse/updates/
  4. Hit *Enter* and wait for the list to load
  5. Check *Action Language for EMF*
  6. Click *Next* then *Finish*
</details>

## How to contribute?

<details>

  <summary><b>Requirements</b></summary>

- [Maven 3.x](https://maven.apache.org/download.cgi)
- [Java 8 JDK](https://adoptopenjdk.net/upstream.html)
- latest [Eclipse IDE for RCP](https://www.eclipse.org/downloads/packages/) release

</details>

<details>

  <summary><b>Setup your dev environment</b></summary>

First of all, clone the repository:

```
git clone https://github.com/pitest/pitclipse.git
```

Then:

1. Import all the plug-ins within your Eclipse IDE workspace
2. Open the `releng/org.pitest.pitclipse.target/org.pitest.pitclipse.target.target` file
3. Click on "_Set as Active Target Platform_"
4. Wait for the dependencies to be loaded (may take a while)
5. Open the `pom.xml` in the project `org.pitest`, navigate to the `<execution>` element marked with error, use the menu "Edit" > "Quick Fix" and select "Discover new m2e connectors"; in the dialog select `maven-dependency-plugin` and press "Finish"; conclude the installation procedure and restart Eclipse when prompted
6. Use the menu "Project" > "Clean" and clean all projects; you should now have an error free workspace.

</details>

<details>

  <summary><b>Submit your changes</b></summary>


Make your changes, then make sure the tests still pass:
```
mvn clean verify
```
Commit your changes, then submit a PR.

</details>

> See [CONTRIBUTING.md](CONTRIBUTING.md) for further details.
