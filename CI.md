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

### Is `<release>8</release>` configured in this project?

No. It is not currently in the POM. The mention of `<release>8</release>`
in this document is a **recommended improvement**, not something already
present. What the project uses today is:
```xml
<source>1.8</source>
<target>1.8</target>
```
// TODO-- Raise a new Issue for this to get implemented. 

---

### What is the difference between `source`/`target` and `release`, and should we switch?

Both approaches produce Java 8 bytecode, but `--release` is stricter:

| Setting              | Bytecode = Java 8 | Blocks Java 9+ API usage |
|----------------------|:-----------------:|:------------------------:|
| `source`/`target`    | ✅                | ❌                       |
| `release`            | ✅                | ✅                       |

With `source/target=1.8`, a JDK 17 compiler can silently compile code
that calls APIs introduced after Java 8 — the build succeeds, but the
library will fail at runtime on a real JDK 8 environment. The `--release`
flag closes that gap by also enforcing the available API surface.

If you want to add this protection, either approach below works:

As a property (simpler):
```xml
<properties>
  <maven.compiler.release>8</maven.compiler.release>
</properties>
```

Or directly in the compiler plugin:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <release>8</release>
  </configuration>
</plugin>
```

Note: `--release` requires JDK 9 or higher to compile with. Since the
CI matrix includes JDK 8, you would need to guard this with a profile
activated on JDK 9+ (similar to the existing `jdk-17plus` profile),
or drop the JDK 8 build job if Java 8 as a **compiler** is no longer
a requirement.

### Does the Java 8 bytecode target apply to JDK 17+ builds too?

Yes. The `jdk-17plus` profile does **not** change the compiler source or
target. It only sets the `java.version` property and adds Surefire
`--add-opens` flags for tests. Compilation is still governed by the
parent POM properties on every matrix entry:

| JDK in matrix | Bytecode target |
|---------------|-----------------|
| 8             | Java 8          |
| 11            | Java 8          |
| 17            | Java 8          |
| 21            | Java 8          |
| 23            | Java 8          |

To verify in a CI log, add this to a Java 17+ matrix job and look for
`-source 1.8 -target 1.8` in the compiler output:
```sh
mvn -ntp -X -DskipTests compile
```

---

### If the bytecode is Java 8, does the library work for users on Java 17+?

Yes — and this is by design. A library compiled to Java 8 bytecode can
run on any JDK from 8 through 21, 23, and beyond, as long as it avoids
APIs that were removed or restricted in newer runtimes. Keeping the
bytecode target at Java 8 maximises compatibility for the widest
possible audience of downstream users.

This compatibility is **verified, not just assumed** — the CI matrix
runs the full test suite on JDK 17, 21, and 23, and the `jdk-17plus`
profile adds `--add-opens` flags to handle Java module-system
restrictions during testing.

The one area to watch: if the library (or any of its dependencies) uses
JDK-internal APIs via reflection, Java's module system may block that
access on JDK 17+, regardless of bytecode level. That is exactly what
the `--add-opens` flags in the `jdk-17plus` profile are there to
address.
