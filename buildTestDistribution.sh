cd guava-bundle; mvn install ; cd ..
cd pitest-bundles; mvn install ; cd ..
cd jbehave-bundle; mvn install ; cd ..
cd pitrunner; mvn clean install; cd ..
cd pitclipse-plugin; mvn clean install -Pui-test; cd ..
