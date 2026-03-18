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

## CI matrix and Java compatibility

### CI workflow overview

The CI workflow (`.github/workflows/main.yml`) runs a build matrix across five JDK versions:

| Matrix JDK | `jdk-17plus` profile active? | Bytecode target |
|:----------:|:----------------------------:|:---------------:|
| 8          | No                           | Java 8          |
| 11         | No                           | Java 8          |
| 17         | **Yes**                      | Java 8          |
| 21         | **Yes**                      | Java 8          |
| 23         | **Yes**                      | Java 8          |

Each job uses `actions/setup-java@v4` (Temurin distribution) to install the requested JDK, then runs:

```
mvn clean test -ntp
```

### Compiler settings — always targeting Java 8 bytecode

The parent `pom.xml` pins two properties:

```xml
<java-compiler-source.version>1.8</java-compiler-source.version>
<java-compiler-target.version>1.8</java-compiler-target.version>
```

These are passed to `maven-compiler-plugin` via `<source>` and `<target>`, so **all CI jobs produce Java 8–compatible bytecode** regardless of which JDK is running the build.

> **Note — `-source`/`-target` vs `--release`**  
> The current configuration uses `<source>` and `<target>` flags. When building on JDK 9+, using `<release>8</release>` instead provides stronger API compatibility guarantees by preventing accidental use of APIs that did not exist in Java 8. This is a nuance to be aware of but not necessarily a required change for the current setup.

### The `jdk-17plus` Maven profile

The parent `pom.xml` declares the following profile:

```xml
<profile>
    <id>jdk-17plus</id>
    <activation>
        <jdk>[17,)</jdk>
    </activation>
    <properties>
        <java.version>17</java.version>
    </properties>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <argLine>
                        --add-opens java.base/java.lang=ALL-UNNAMED
                        --add-opens java.base/java.lang.reflect=ALL-UNNAMED
                    </argLine>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

**When running on JDK 17, 21, or 23**, Maven auto-activates `jdk-17plus`, which:
- Sets the `java.version` property to `17`.
- Passes `--add-opens` JVM arguments to `maven-surefire-plugin` so that tests that rely on reflective access (which is restricted by default in newer JDKs) continue to work.

**When running on JDK 8 or 11**, this profile is *not* activated and the `--add-opens` flags are not needed.

### Running locally with different JDKs

Switch your `JAVA_HOME` to the desired JDK and run the standard build commands. The correct `jdk-17plus` behavior is automatic:

```bash
# JDK 8 or 11 — profile NOT activated, no --add-opens flags applied
export JAVA_HOME=/path/to/jdk8    # or jdk11
mvn clean test

# JDK 17, 21, or 23 — profile IS activated automatically
export JAVA_HOME=/path/to/jdk17   # or jdk21, jdk23
mvn clean test
```

### Troubleshooting and verification

**Check which profiles are active for the current JDK:**

```bash
mvn help:active-profiles
```

You should see `jdk-17plus` listed when running under JDK 17+, and not listed for JDK 8/11.

**Check the value of the `java.version` property resolved by Maven:**

```bash
mvn -q -DforceStdout help:evaluate -Dexpression=java.version
```

- Returns `17` (or whatever the profile sets) when `jdk-17plus` is active.
- Property is not defined by the parent POM when `jdk-17plus` is inactive (JDK 8/11), so it may fall back to a value set elsewhere or be undefined.

**Verify the compiler source/target used:**

```bash
mvn help:effective-pom | grep -A2 "maven-compiler-plugin" | grep -E "source|target|release"
```

This should always show `1.8` for both source and target.

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
