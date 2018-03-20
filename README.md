# Jenkins Lockable Resources Plugin

This plugins allows to define "lockable resources" in the global configuration.
These resources can then be "required" by jobs. If a job requires a resource
which is already locked, it will be put in queue until the resource is released.

### Internal AHL release notes
* Update pom.xml to give version tag a suitable version number (usually update the date)
* Build the hpi `/apps/research/tools/maven/3.5.0/bin/mvn package`
* Test the hpi on dev Jenkins (the hpi will be here `./target/lockable-resources.hpi`)
* Merge the pom.xml version change to master
* Create a release here https://github.com/manahl/lockable-resources-plugin/releases
* Upload hpi to build Jenkins
