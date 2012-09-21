Pitclipse
=========

Eclipse plugin for PIT.

Eclipse Build
=============

Requirements
------------
* Eclipse installation 3.6 or greater
* Guava bundle - update site http://guava-osgi.googlecode.com/svn/trunk/repository/
* SWTBot plugin (to run UI tests) - update site http://download.eclipse.org/technology/swtbot/helios/dev-build/update-site/

Import the project as a maven project.  You may need to disable the maven nature from the org.pitest.pitclipse.ui module (right-click Maven -> Disable Nature).  If you check the build properties of org.pitest.pitclipse.ui, ensure it has the Plug-in Manifest Builder enabled.

The UI tests use SWTBot to drive the UI interactions.  To run the UI tests, open SimpleJavaProjectTest and Run As -> SWTBot Test.  The intention is to run this from JBehave once JBehave can be run from OSGI (hence why some of the test code is annotated @Given, @When< @Then).

Command line build
==================

Due to limitations with Tycho, the PIT build has to be run it two steps:

1.  mvn clean install -Pbundle
2.  mvn clean install -Pplugin

The contents of pitclipse-plugin/org.pitest.pitclipse.site/target/repository/ can then be used as an update site.

NB - there is also a ui-test profile which should run the UI tests.  However, for reasons not yet ascertained, the eclipse instance started does not seem to be able to add JUnit to the classpath of the test projects.
