package mc.aegis.test

import com.mojang.brigadier.arguments.*
import mc.aegis.AegisCommandBuilder
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

class AegisTestMod : ModInitializer {
    override fun onInitialize() {
        println("Aegis test")
        CommandRegistrationCallback.EVENT.register { dispatcher, dedicated ->
            dispatcher.register(AegisCommandBuilder("aegis") {
                literal("literalTest") {
                    executes {
                        println("Literal test")
                        1
                    }
                }
                literal("intTest") {
                    integer("int", 3, 12) {
                        executes {
                            println("Integer test: ${IntegerArgumentType.getInteger(it, "int")}")
                            1
                        }
                    }
                }
                literal("longTest") {
                    long("long", -124763435564, 327568623523) {
                        executes {
                            println("Long test: ${LongArgumentType.getLong(it, "long")}")
                            1
                        }
                    }
                }
                literal("floatTest") {
                    float("float", -0.4f, 9.3f) {
                        executes {
                            println("Float test: ${FloatArgumentType.getFloat(it, "float")}")
                            1
                        }
                    }
                }
                literal("doubleTest") {
                    double("double", -124763.423423, 41235.254656) {
                        executes {
                            println("Double test: ${DoubleArgumentType.getDouble(it, "double")}")
                            1
                        }
                    }
                }
                literal("boolTest") {
                    bool("boolean") {
                        executes {
                            println("Boolean test: ${BoolArgumentType.getBool(it, "boolean")}")
                            1
                        }
                    }
                }
                literal("stringTest") {
                    string("string") {
                        executes {
                            println("String test: ${StringArgumentType.getString(it, "string")}")
                            1
                        }
                    }
                }
                literal("wordTest") {
                    word("word") {
                        executes {
                            println("Word test: ${StringArgumentType.getString(it, "word")}")
                            1
                        }
                    }
                }
                literal("greedyStringTest") {
                    greedyString("greedyString") {
                        executes {
                            println("Greedy String test: ${StringArgumentType.getString(it, "greedyString")}")
                            1
                        }
                    }
                }
                literal("requireTest") {
                    requires {
                        it.hasPermissionLevel(2)
                    }
                    executes {
                        println("Require test")
                        1
                    }
                }
            }.build())
        }
    }
}