Pitclipse
=========

Eclipse plugin for PIT.  Please download from Eclipse Marketplace

[![Build Status](https://travis-ci.org/philglover/pitclipse.svg?branch=master)](https://travis-ci.org/philglover/pitclipse)

Manual Update site: http://eclipse.pitest.org/release/ (please note that this is not viewable from a browser, but will work from an Eclipse update)

[Notable previous versions are described here](OLD_MILESTONES.md)

Plugin Build Requirements
=========================
If you wish to build the plugin from source, you will need the following.

* Maven 3.0

Plugin Command line build
=========================

The plugin and all bundled dependencies can be built by running build.sh in the root.

The contents of pitclipse-plugin/org.pitest.pitclipse.site/target/repository/ can then be used as an update site.

NB - there is also a ui-test profile which should run the UI tests.  However, for reasons not yet ascertained, the eclipse instance started by tycho does not seem to be able to add JUnit to the classpath of the test projects.
