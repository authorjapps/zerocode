# Building ZeroCode 

## Without executing the tests
```
mvn clean install -DskipTests
```
## With tests executed(core)
Either you can cd to the `core` dir and build/run the project
```
cd core
mvn clean install 
or
mvn clean test
```
_This way we don't need to run any Kafka containers as we are not firing Kafka tests._

#### or

You can issue a mvn build command for the specific module(being on the parent dir)
```
mvn -pl core clean install

or

mvn -pl core clean test
```

## Runing Only The Integration tests(core)
Right click and run the "integrationtests" package. It picks the tests ending with "*Test.java"
<img width="632" alt="integration_tests_only_running_" src="https://github.com/authorjapps/zerocode/assets/12598420/6b6e8e33-16c1-43ce-8179-62a18e9a2290">


## With tests executed(kafka)
Some tests require a running Kafka (and some related components like kafka-rest, and kafka-schema-registry).

Location:
https://github.com/authorjapps/zerocode/blob/master/docker/compose/kafka-schema-registry.yml

Download the file, and run(or `cd to the docker/compose` dir and run)
```
docker-compose -f kafka-schema-registry.yml up -d

Note:
The above command brings up all necessary Kafka components in the docker containers. 
There is no need of bringing up more containers. 

We have provided other compose-files just in-case anyone has to experiment tests with 
single-node or multi-node cluster(s) independently.
```

In the [zerocode-docker-factory repository](https://github.com/authorjapps/zerocode-docker-factory/) ([direct download link](https://raw.githubusercontent.com/authorjapps/zerocode-docker-factory/master/compose/kafka-schema-registry.yml)) 
you'll find 'kafka-schema-registry.yml', a docker-compose file that provides these components.

Then you can run:
```
mvn clean install   <---- To build and install all the modules

mvn -pl kafka-testing clean test   <---- To run all the tests

mvn -pl kafka-testing clean install   <---- To run all the tests and install it to .m2 repo

```

More info on the docker-compose file can be found in the [wiki](https://github.com/authorjapps/zerocode-docker-factory/wiki/Docker-container-for-Kafka-and-Schema-Registry)

## With tests executed(all)
As explained above, in the root/parent folder, please issue the below command(the usual way)

```
mvn clean install   <---- To build and install all the modules
```

## Compiling in ARM Processors
You might get the following error when you do a "mvn clean install -DskipTests"

```java
[ERROR] Failed to execute goal com.github.os72:protoc-jar-maven-plugin:3.11.4:run (default) on project kafka-testing: 
    Error extracting protoc for version 3.11.4: Unsupported platform: protoc-3.11.4-osx-aarch_64.exe -> [Help 1]

//
// more details >>
//        
[INFO] ZeroCode TDD Parent ................................ SUCCESS [  0.504 s]
[INFO] Zerocode TDD Core .................................. SUCCESS [  2.365 s]
[INFO] Zerocode Http Testing With Simple YAML and JSON DSL  SUCCESS [  0.413 s]
[INFO] Zerocode Kafka Testing With Simple YAML and JSON DSL FAILURE [  0.507 s]
[INFO] Zerocode JUnit5 Jupiter Load Testing ............... SKIPPED
[INFO] Zerocode Automated Testing Maven Archetype ......... SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.858 s
[INFO] Finished at: 2023-12-16T10:31:44Z
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.github.os72:protoc-jar-maven-plugin:3.11.4:run (default) on project kafka-testing: Error extracting protoc for version 3.11.4: Unsupported platform: protoc-3.11.4-osx-aarch_64.exe -> [Help 1]

```

### Fix:
Go to --> .../zerocode/kafka-testing/pom.xml --> Comment the following line:

```shell
<!--
<plugin>
				<groupId>com.github.os72</groupId>
				<artifactId>protoc-jar-maven-plugin</artifactId>
				<version>3.11.4</version>
                ...
			</plugin>-->
```
Then execute ""mvn clean install -DskipTests"" --> It should be SUCCESS.

Raise an Issue if you want to locally execute the tests involving "protos" and you aren't able to do it.

Visit here for more details:
- https://github.com/os72/protoc-jar-maven-plugin?tab=readme-ov-file
- https://github.com/os72/protoc-jar/issues/93
