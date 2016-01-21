Pitest Bundles
==============

This project builds osgi bundles wrapping the following pitest libraries:
* pitest
* pitest-command-line
* pitest-html-report

These bundles only export packages required for Pitclipse.  If you require pitest osgi bundles for some other purpose, it would be an idea to fork this as no guarantees that the exports won't change.

Build Instructions
------------------
```
mvn clean install
```

N.B. These bundles can be signed if appropriate jarsigner properties are supplied at the command line.  See jarsigner plugin for details.
