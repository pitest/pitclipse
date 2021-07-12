<h1 align="center">
  <a name="logo" href="https://gemoc.org/ale-lang"><img src="https://pitest.org/images/pit-black-150x152.png" alt="ALE logo" width="25"/></a>
  Pitclipse
</h1>
<p align="center">
	<i>Test your tests right into your IDE!</i>
</p>

<div align="center">

[![Java CI with Maven](https://github.com/pitest/pitclipse/actions/workflows/maven.yml/badge.svg)](https://github.com/pitest/pitclipse/actions/workflows/maven.yml) [![Java CI with Maven on Windows and Mac](https://github.com/pitest/pitclipse/actions/workflows/windows-mac.yml/badge.svg)](https://github.com/pitest/pitclipse/actions/workflows/windows-mac.yml)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=sqale_index)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=coverage)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.pitest%3Aorg.pitest.pitclipse&metric=ncloc)](https://sonarcloud.io/dashboard?id=org.pitest%3Aorg.pitest.pitclipse)

</div>

Provides mutation coverage for your Java programs within the Eclipse IDE. Built on [PIT (Pitest)](http://pitest.org) for reliability.

## What is mutation testing?

> Faults (or mutations) are automatically seeded into your code, then your tests are run. If your tests fail then the mutation is killed, if your tests pass then the mutation lived.
>
> The quality of your tests can be gauged from the percentage of mutations killed.
>
> *Henry Coles, [pitest.org](https://pitest.org)*

## Main Features

- **Reliability**: relies on [PIT (Pitest)](http://pitest.org)
- **Customization**: provides numerous preferences to tailor analysis
- **JUnit support**: works with both JUnit 4 and JUnit 5 tests

## Usage

Once the plug-in is installed (see [Installation](#Installation) below), you can run Pitest:
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
  3. Copy the update site’s URL in the *Work with* textbox (ATTENTION: the update site has changed on 5 May 2021, make sure you remove the previous one hosted on bintray):
     	- https://pitest.github.io/pitclipse-releases/
  4. Hit *Enter* and wait for the list to load
  5. Check everything
  6. Click *Next* then *Finish*
</details>

## Contributing

<details>
  <summary><b>Requirements</b></summary>

  - [Maven 3.6.3 or higher](https://maven.apache.org/download.cgi)
  - [Java 8 JDK](https://adoptopenjdk.net/upstream.html)
  - [Eclipse IDE for RCP](https://www.eclipse.org/downloads/packages/) (latest release)
</details>

<details>
  <summary><b>Import the projects in the IDE</b></summary>

  1. *File > Import... > Team > Team Project Set*
  2. Fill *URL* with "https://raw.githubusercontent.com/pitest/pitclipse/master/eclipse-project-set.psf"
  3. Click on *Finish*

  > Tip: use Working Sets for a better workspace organization:
  > - Open *Project Explorer*'s menu >  *Top Level Elements* > *Working Sets*
  > - Open *Project Explorer*'s menu >  *Select Working Sets* > Check "bundles", "features", "tests" and "releng"
</details>

<details>
  <summary><b>Setup the environment</b></summary>

  1. Open the `org.pitest.pitclipse.target/org.pitest.pitclipse.target.target` file
  2. Click on *Set as Target Platform*
  3. Wait for the dependencies to be loaded (may take a while)
</details>

<details>
  <summary><b>Commit your changes</b></summary>

  1. Make some changes
  2. Make sure tests still pass: `mvn clean verify`
  3. Submit a PR
</details>

See [CONTRIBUTING.md](CONTRIBUTING.md) for further details.
