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

## With tests executed(kafka)
Some of the tests require a running Kafka (and some related components like kafka-rest, and kafka-schema-registry).

In the [zerocode-docker-factory repository](https://github.com/authorjapps/zerocode-docker-factory/) ([direct download link](https://raw.githubusercontent.com/authorjapps/zerocode-docker-factory/master/compose/kafka-schema-registry.yml)) 
you'll find 'kafka-schema-registry.yml', a docker-compose file that provides these components.

Download the file, and run(or `cd to the docker` dir and run)
```
docker-compose -f kafka-schema-registry.yml up -d

Note:
The above command brings up all necessary Kafka components in the docker containers. 
There is no need of bringing up more containers. 

We have provided other compose-files just in-case anyone has to experiment tests with 
single-node or multi-node cluster(s) independently.
```

Then you can run
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
