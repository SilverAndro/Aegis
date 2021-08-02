# Aegis
Aegis is a DSL/Wrapper around brigadier for kotlin with fabric, designed to reduce clutter as well as providing utility types

---
Standard kotlin brigadier (with static imports):
```
dispatcher.register(
    literal("example")
        .then(
            argument(
                "value", IntegerArgumentType.integer(-10, 200)
            ).executes {
                println(IntegerArgumentType.getInteger(it, "value"))
                1
            }
        )
)
```

Aegis:
```
dispatcher.register("example") {
   integer("value", -10, 200) {
        executes { 
            println(it.getInt("value"))
        }
    }
}
 ```
---
### Installation (build.gradle)

Latest version: ![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/P03W/Aegis?label=Latest%20Release)

```
repositories {
    maven { url = "https://api.modrinth.com/maven" }
}
```

```
dependencies {
    // Aegis
    modImplementation "maven.modrinth:aegis:<VERSION>"
    include "maven.modrinth:aegis:<VERSION>"
}
```

---

### Implementation notes
Aegis does not cover every use case (at least, not currently!), however most cases not covered explicitly are covered by the `raw` and `custom` blocks

If you find a use case not covered explicitly that can not be achieved with these blocks, please open an issue!
