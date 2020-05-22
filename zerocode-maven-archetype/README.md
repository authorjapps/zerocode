To install archetype locally:
Navigate to `zerocode-maven-archetype` on your machine. 
Then run:
```bash
$ cd .../zerocode/zerocode-maven-archetype
$ mvn install
```
    
To generate anew archetype-based project:
Navigate to the directory that will house the project. 
Then run:
```bash
mvn archetype:generate \
-DarchetypeGroupId=org.jsmart \
-DarchetypeArtifactId=zerocode-maven-archetype \
-DarchetypeVersion=1.3.20 \
-DgroupId=com.myproject \
-DartifactId=my-api-testing \
-Dversion=1.0.0-SNAPSHOT
```     
    
The generic command format is:
```bash
mvn archetype:generate -DarchetypeGroupId=<custom-archetype group id e.g.>
-DarchetypeArtifactId=<custom-archetype artifactid>
-DarchetypeVersion=<custom-archetype version>
-DgroupId=<new project Group Id>
-DartifactId=<new project artifact Id>
```
