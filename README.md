Pitclipse
=========

Eclipse plugin for PIT.  Update site: http://eclipse.pitest.org/release/







Plugin Build Requirements
=========================
If you wish to build the plugin from source, you will need the following.


* Maven 3.0
* Nexus OSS setup for P2 Repository support https://docs.sonatype.org/display/Nexus/Nexus+OSGi+Experimental+Features+-+P2+Repository+Plugin
OR
* Nexus Pro

You will need to setup p2 proxys for the following update sites:
* http://download.eclipse.org/releases/indigo
* http://download.eclipse.org/technology/swtbot/helios/dev-build/update-site/
* http://guava-osgi.googlecode.com/svn/trunk/repository/
* http://jbehave.org/reference/eclipse/updates/
* http://download.eclipse.org/tools/orbit/downloads/drops/R20130517111416/repository/


Eclipse Build
=============

Requirements
------------
* Eclipse installation 3.6 or greater
* Guava bundle - update site http://guava-osgi.googlecode.com/svn/trunk/repository/
* SWTBot plugin (to drive UI testing) - update site http://download.eclipse.org/technology/swtbot/helios/dev-build/update-site/
* JBehave plugin (to run the UI stories) - update site http://jbehave.org/reference/eclipse/updates/


Import the project as a maven project.  You may need to disable the maven nature from the org.pitest.pitclipse.core & org.pitest.pitclipse.ui modules (right-click Maven -> Disable Nature).  If you check the build properties of org.pitest.pitclipse.core & org.pitest.pitclipse.ui, ensure it has the Plug-in Manifest Builder enabled.

The UI tests use SWTBot to drive the UI interactions.  To run the UI tests, open SimpleJavaProjectTest and Run As -> SWTBot Test.  This launches JBehave and runs stories matching the file name pattern u*.story.

Command line build
==================

Due to limitations with Tycho, the PIT build has to be run it two steps:

1.  mvn clean install deploy -Pbundle
2.  mvn clean install -Pplugin

The contents of pitclipse-plugin/org.pitest.pitclipse.site/target/repository/ can then be used as an update site.

NB - there is also a ui-test profile which should run the UI tests.  However, for reasons not yet ascertained, the eclipse instance started by tycho does not seem to be able to add JUnit to the classpath of the test projects.
