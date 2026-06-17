## Java Version & CI Build — FAQ

---

### What Java versions are supported?

**Java 17 is the minimum.** Java 21 and 23 are also tested and supported.
Java 8 and 11 support has been dropped.

---

### How does CI test across Java versions?

The workflow `.github/workflows/main.yml` runs a matrix build across **JDK 17, 21, and 23**.
For each version it installs the JDK via `actions/setup-java@v4` and runs:
```sh
mvn clean test -ntp
```

| JDK in matrix | Builds? |
|---------------|---------|
| 17            | ✅      |
| 21            | ✅      |
| 23            | ✅      |

---

### How is the compiler version configured?

The parent POM uses `maven.compiler.release=17`:
```xml
<maven.compiler.release>17</maven.compiler.release>
```

This is the modern, single-flag approach that enforces both the language syntax level
and the available API surface. It replaces the old `source`/`target` pair.

---

### Why `release` and not `source`/`target`?

`maven.compiler.release=17` does three things in one:
- Sets the source syntax to Java 17
- Sets the bytecode target to Java 17
- Locks the bootstrap classpath to Java 17 APIs (prevents accidental use of newer APIs)

The old `<source>/<target>` pair only did the first two, leaving API-level mismatches
possible. `release` is the correct modern approach.

---

### Are any `--add-opens` flags needed?

No. They were previously needed by a POC test (`ZeroCodeUnitRuntimeAnnoTest`) that
mutated annotations via deep reflection into JVM internals. That test has been removed,
so no `--add-opens` flags are required anywhere in the build.

---

### What does CI spin up before running tests?

```yaml
- docker compose -f docker/compose/kafka-schema-registry.yml up -d
- docker compose -f docker/compose/pg_compose.yml up -d
```

Kafka (with schema registry) and PostgreSQL are started before the Maven build runs.

---

### How to run locally?
On a M3 mac:
```sh
./start.sh       # starts Kafka + PostgreSQL via Docker
mvn clean test -ntp
./stop.sh        # tears down containers
```
In the above:
> ./start.sh : It uses "kafka-schema-registry-m3.yml", you can use the same as well from any other Unix/Linux machine.
> 
Otherwise, point to "kafka-schema-registry.yml" in the command above(this is used in CI auto workflow).

