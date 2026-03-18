## Java Version & CI Build — FAQ

This section explains how the project handles multiple JDK versions in CI
and how Maven profiles relate to the build matrix.

---

### How does CI test across multiple Java versions?

The workflow `.github/workflows/main.yml` runs a matrix build across
JDK versions **8, 11, 17, 21, and 23**. For each version it installs
the JDK via `actions/setup-java@v4` and runs:
```sh
mvn clean test -ntp
```

---

### What is the `jdk-17plus` Maven profile and when does it activate?

The profile in `pom.xml` is defined as:
```xml
<profile>
    <id>jdk-17plus</id>
    <activation>
        <jdk>[17,)</jdk>
    </activation>
    <properties>
        <java.version>17</java.version>
    </properties>
</profile>
```

Maven activates it **automatically** when the build runs on JDK 17 or
higher — no explicit flag is needed in the workflow. The CI matrix
therefore behaves like this:

| JDK in matrix | `jdk-17plus` active? |
|---------------|----------------------|
| 8             | No                   |
| 11            | No                   |
| 17            | **Yes**              |
| 21            | **Yes**              |
| 23            | **Yes**              |

When the profile is active it sets `java.version=17` and adds Surefire
`--add-opens` flags to handle Java module-system restrictions on newer
JDKs.

To confirm which profiles are active in a given build, add this step(optional):
```yaml
- run: mvn -ntp help:active-profiles
```

---

### How is the compiler version decided for JDK 8 and 11?

The `jdk-17plus` profile does **not** activate on JDK 8 or 11, so
`${java.version}` is undefined on those runs. Compilation is instead
controlled by these properties in the parent POM, which are always
present regardless of profile:
```xml
<java-compiler-source.version>1.8</java-compiler-source.version>
<java-compiler-target.version>1.8</java-compiler-target.version>
```

These feed into the `maven-compiler-plugin`:
```xml
<source>${java-compiler-source.version}</source>  <!-- 1.8 -->
<target>${java-compiler-target.version}</target>  <!-- 1.8 -->
```

So on **all** matrix entries the compiler is told to produce Java 8
bytecode, regardless of which JDK toolchain is running the build.

---

### There is no `java11` profile — how does the JDK 11 CI job work?

No special POM entry is needed for Java 11. The JDK 11 job simply runs
the build using the JDK 11 compiler (`javac 11`) but with
`-source 1.8 -target 1.8`, so it still emits Java 8 bytecode. This is
a standard pattern: **build with a newer JDK, target an older runtime
level**.

What the Java 11 CI job validates:
- The project compiles and all tests pass when Maven runs on JDK 11.
- Dependencies that require JDK 11 at runtime (e.g. WireMock 3.x,
  Logback 1.4+, RESTEasy 4.x) do not break the build environment.

It does **not** mean the output bytecode is compiled to Java 11 level.

---

### `source`/`target` vs `--release` — what is the difference?

Using `-source 1.8 -target 1.8` allows a JDK 11 (or 17+) compiler to
accidentally use APIs that were added after Java 8, because it does not
restrict the bootstrap classpath. If you hit a "compiles on JDK 11 but
fails at runtime on JDK 8" issue, the fix is to switch to:
```xml
<release>8</release>
```

The `--release` flag enforces both the language level **and** the
available API surface in a single, safer setting.
