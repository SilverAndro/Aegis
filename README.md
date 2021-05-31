# Aegis
Aegis is a DSL/Wrapper around brigadier for kotlin with fabric, designed to reduce clutter

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
)
 ```
---
### Installation (build.gradle)

Latest version: [![](https://jitpack.io/v/P03W/Aegis.svg)](https://jitpack.io/#P03W/Aegis)

```
repositories {
    maven {
        url 'https://jitpack.io'
    }
}
```

```
dependencies {
    // Aegis
    modImplementation 'com.github.P03W:Aegis:<VERSION>'
    include 'com.github.P03W:Aegis:<VERSION>'
}
```

---

### Excepted a Boolean return value but found Unit? Whats this about?

This is Aegis' way of enforcing ending chains with executes blocks. Simply properly close out your chain and the error will disappear.

(Note: the `raw` block will also close it out, because its assumed if your using that you known what your using)

### Implementation notes
Aegis does not cover every use case (at least, not currently!), however most cases not covered explicitly are covered by the `raw` and `custom` blocks

If you find a use case not covered explicitly that can not be achieved with these blocks, please open an issue!
