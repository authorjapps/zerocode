# Building ZeroCode 

## without executing the tests
```
mvn clean install -DskipTests
```

## with tests executed
Some of the tests require a running Kafka (and some related components like kafka-rest, and kafka-schema-registry).

In the [zerocode-docker-factory repository](https://github.com/authorjapps/zerocode-docker-factory/) ([direct download link](https://raw.githubusercontent.com/authorjapps/zerocode-docker-factory/master/compose/kafka-schema-registry.yml)) 
you'll find 'kafka-schema-registry.yml', a docker-compose file that provides these components.

Download the file, and run
```
docker-compose -f kafka-schema-registry.yml up -d
```

Then you can run
```
mvn clean install
```

More info on the docker-compose file can be found in the [wiki](https://github.com/authorjapps/zerocode-docker-factory/wiki/Docker-container-for-Kafka-and-Schema-Registry)
