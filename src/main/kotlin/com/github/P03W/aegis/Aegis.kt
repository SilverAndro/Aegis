/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:Suppress("unused", "DEPRECATION")

package com.github.p03w.aegis

import com.github.p03w.aegis.internal.types.EnumArgument
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.argument.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor

/**
 * The core builder
 *
 * Receiver functions are used, so builder functions can be called easily
 *
 * @param rootLiteralValue the value of the literal node used as root
 */
class AegisCommandBuilder(rootLiteralValue: String, method: AegisCommandBuilder.()->Unit) {
    val devEnv = FabricLoader.getInstance().isDevelopmentEnvironment

    @Deprecated("Using this is bad practice, use raw or custom")
    @PublishedApi
    internal var currentNode: ArgumentBuilder<ServerCommandSource, *> = CommandManager.literal(rootLiteralValue)

    init {
        method()
    }

    /**
     * Internal function used to create all child nodes all then attach to the parent
     *
     * This is usually recursive, as new nodes call this when being attached
     */
    @PublishedApi
    internal inline fun runThenAttach(method: AegisCommandBuilder.()->Boolean, node: ArgumentBuilder<ServerCommandSource, *>): Boolean {
        val oldNode = currentNode
        currentNode = node

        val result = method()

        currentNode = oldNode
        currentNode.then(node)
        return result
    }

    /**
     * Finalises the tree and returns the root node
     *
     * This should always be called last, usually attached to the end of the method in AegisCommandBuilder
     *
     * For example, `AegisCommandBuilder("sample") {/* arguments */}.build`
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Prefer using one of the implicit builders for cleaner code")
    fun build(): LiteralArgumentBuilder<ServerCommandSource> {
        return currentNode as LiteralArgumentBuilder<ServerCommandSource>
    }

    /**
     * Gives access to the current node in the tree being built
     *
     * This exists to fill in gaps where Aegis doesn't have a replacement.
     * Note there is no way to escape a raw block, once you enter one all code must be raw brigadier
     *
     * This implicitly closes the executes check
     */
    inline fun raw(method: ArgumentBuilder<ServerCommandSource, *>.()->Boolean): Boolean {
        method(currentNode)
        return true
    }

    /**
     * Allows for attaching custom arguments to the tree
     *
     * @param argument The custom argument
     */
    inline fun custom(argument: ArgumentBuilder<ServerCommandSource, *>, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument)
    }

    /**
     * Allows for attaching custom arguments to the tree
     *
     * @param name the name of the custom argument type
     * @param argumentType The custom argument type
     */
    inline fun <T : ArgumentType<*>> custom(name: String, argumentType: T, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, argumentType))
    }

    /**
     * Creates a literal argument
     *
     * @param literalValue the value of the literal argument
     * @see CommandManager.literal
     */
    inline fun literal(literalValue: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, CommandManager.literal(literalValue))
    }

    /**
     * Creates an integer argument
     *
     * Values are retrieved with [IntegerArgumentType.getInteger]
     *
     * @param name the name of the argument
     * @param min optional minimum
     * @param max optional maximum
     * @see IntegerArgumentType
     */
    inline fun integer(name: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, IntegerArgumentType.integer(min, max)))
    }

    /**
     * Creates a long argument
     *
     * Values are retrieved with [LongArgumentType.getLong]
     *
     * @param name the name of the argument
     * @param min optional minimum
     * @param max optional maximum
     * @see LongArgumentType
     */
    inline fun long(name: String, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, LongArgumentType.longArg(min, max)))
    }

    /**
     * Creates a float argument
     *
     * Values are retrieved with [FloatArgumentType.getFloat]
     *
     * @param name the name of the argument
     * @param min optional minimum
     * @param max optional maximum
     * @see FloatArgumentType
     */
    inline fun float(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, FloatArgumentType.floatArg(min, max)))
    }

    /**
     * Creates a double argument
     *
     * Values are retrieved with [DoubleArgumentType.getDouble]
     *
     * @param name the name of the argument
     * @param min optional minimum
     * @param max optional maximum
     * @see DoubleArgumentType
     */
    inline fun double(name: String, min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, DoubleArgumentType.doubleArg(min, max)))
    }

    /**
     * Creates a boolean argument
     *
     * Values are retrieved with [BoolArgumentType.getBool]
     *
     * @param name the name of the argument
     * @see BoolArgumentType
     */
    inline fun bool(name: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, BoolArgumentType.bool()))
    }

    /**
     * Creates a string argument (single word or with quotes)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    inline fun string(name: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, StringArgumentType.string()))
    }

    /**
     * Creates a word argument (single word, no quotes)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    inline fun word(name: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, StringArgumentType.word()))
    }

    /**
     * Creates a greedy string argument (captures ***ALL*** text after previous argument)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    inline fun greedyString(name: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, StringArgumentType.greedyString()))
    }

    /**
     * Creates a block pos argument
     *
     * Values are retrieved with [BlockPosArgumentType.getBlockPos]
     *
     * @param name the name of the argument
     * @see BlockPosArgumentType
     */
    inline fun blockPos(name: String, method: AegisCommandBuilder.()->Boolean): Boolean {
        return runThenAttach(method, argument(name, BlockPosArgumentType.blockPos()))
    }

    /**
     * Creates a Vec2 argument
     *
     * Values are retrieved with [Vec2ArgumentType.getVec2]
     *
     * @param name the name of the argument
     * @param centered default true, if position should be centered if specific decimals are not listed
     * @see Vec2ArgumentType
     */
    inline fun vec2(name: String, centered: Boolean = true, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, Vec2ArgumentType(centered)))
    }

    /**
     * Creates a Vec3 argument
     *
     * Values are retrieved with [Vec3ArgumentType.getVec3]
     *
     * @param name the name of the argument
     * @param centered default true, if position should be centered if specific decimals are not listed
     * @see Vec3ArgumentType
     */
    inline fun vec3(name: String, centered: Boolean = true, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, Vec3ArgumentType.vec3(centered)))
    }

    /**
     * Creates a entity argument
     *
     * Values are retrieved with [EntityArgumentType.getEntity]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    inline fun entity(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, EntityArgumentType.entity()))
    }

    /**
     * Creates a multiple entity argument
     *
     * Values are retrieved with [EntityArgumentType.getEntities]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    inline fun entities(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, EntityArgumentType.entities()))
    }

    /**
     * Creates a player argument
     *
     * Values are retrieved with [EntityArgumentType.getEntity]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    inline fun player(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, EntityArgumentType.player()))
    }

    /**
     * Creates a multiple player argument
     *
     * Values are retrieved with [EntityArgumentType.getEntities]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    inline fun players(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, EntityArgumentType.players()))
    }

    /**
     * Creates an angle argument
     *
     * Values are retrieved with [AngleArgumentType.getAngle]
     *
     * @param name the name of the argument
     * @see AngleArgumentType
     */
    inline fun angle(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, AngleArgumentType.angle()))
    }

    /**
     * Creates a rotation argument
     *
     * Values are retrieved with [RotationArgumentType.getRotation]
     *
     * @param name the name of the argument
     * @see RotationArgumentType
     */
    inline fun rotation(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, RotationArgumentType.rotation()))
    }

    /**
     * Creates a dimension argument
     *
     * Values are retrieved with [DimensionArgumentType.getDimensionArgument]
     *
     * @param name the name of the argument
     * @see DimensionArgumentType
     */
    inline fun dimension(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, DimensionArgumentType.dimension()))
    }

    /**
     * Creates an identifier argument
     *
     * Values are retrieved with [IdentifierArgumentType.getIdentifier]
     *
     * @param name the name of the argument
     * @see IdentifierArgumentType
     */
    inline fun identifier(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, IdentifierArgumentType.identifier()))
    }

    /**
     * Creates a text argument (Similar to string, but can accept json text as well)
     *
     * Values are retrieved with [TextArgumentType.getTextArgument]
     *
     * @param name the name of the argument
     * @see TextArgumentType
     */
    inline fun text(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, TextArgumentType.text()))
    }

    /**
     * Creates a uuid argument
     *
     * Values are retrieved with [UuidArgumentType.getUuid]
     *
     * @param name the name of the argument
     * @see UuidArgumentType
     */
    inline fun uuid(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, UuidArgumentType.uuid()))
    }

    /**
     * Creates a game profile argument
     *
     * Values are retrieved with [GameProfileArgumentType.getProfileArgument]
     *
     * @param name the name of the argument
     * @see GameProfileArgumentType
     */
    inline fun gameProfile(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, GameProfileArgumentType.gameProfile()))
    }

    /**
     * Creates a swizzle argument
     *
     * Values are retrieved with [SwizzleArgumentType.getSwizzle]
     *
     * @param name the name of the argument
     * @see SwizzleArgumentType
     */
    inline fun swizzle(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, SwizzleArgumentType.swizzle()))
    }

    /**
     * Creates an NBT Compound argument
     *
     * Values are retrieved with [NbtCompoundArgumentType.getNbtCompound]
     *
     * @param name the name of the argument
     * @see NbtCompoundArgumentType
     */
    inline fun nbtCompound(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, NbtCompoundArgumentType.nbtCompound()))
    }

    /**
     * Creates a color argument
     *
     * Values are retrieved with [ColorArgumentType.getColor]
     *
     * @param name the name of the argument
     * @see ColorArgumentType
     */
    inline fun color(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, ColorArgumentType.color()))
    }

    /**
     * Creates a time argument
     *
     * Values are retrieved with [IntegerArgumentType.getInteger]
     *
     * @param name the name of the argument
     * @see TimeArgumentType
     */
    inline fun time(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, TimeArgumentType.time()))
    }

    /**
     * Creates an operation argument
     *
     * Values are retrieved with [OperationArgumentType.getOperation]
     *
     * @param name the name of the argument
     * @see OperationArgumentType
     */
    inline fun operation(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, OperationArgumentType.operation()))
    }

    /**
     * Creates a command function argument
     *
     * Values are retrieved with [CommandFunctionArgumentType.getFunctionOrTag]
     *
     * @param name the name of the argument
     * @see CommandFunctionArgumentType
     */
    inline fun commandFunction(name: String, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, CommandFunctionArgumentType.commandFunction()))
    }

    /**
     * Creates an enum argument
     *
     * Values are retrieved with [EnumArgument.getEnum]
     *
     * @param name the name of the argument
     * @param enum the enum of the argument
     * @param format how to format the enum names, defaults to how it is in source code
     * @see EnumArgument
     */
    inline fun <T : Enum<T>> enum(name: String, enum: KClass<T>, format: EnumArgument.FormatType = EnumArgument.FormatType.LEAVE, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return runThenAttach(method, argument(name, EnumArgument(enum.java, format)))
    }

    /**
     * Internal function used to manage the logic for attaching parameters from [data] to the tree
     */
    private fun attachParams(name: String, list: List<KParameter>, index: Int, method: AegisCommandBuilder.() -> Boolean): Boolean {
        val param = list[index]
        val typeName = param.type.toString().split(".").last()
        val paramName = "$name.${param.name}: $typeName"
        val next = when (param.type) {
            String::class.createType() -> argument(paramName, StringArgumentType.string())
            Int::class.createType() -> argument(paramName, IntegerArgumentType.integer())
            Long::class.createType() -> argument(paramName, LongArgumentType.longArg())
            Float::class.createType() -> argument(paramName, FloatArgumentType.floatArg())
            Double::class.createType() -> argument(paramName, DoubleArgumentType.doubleArg())
            Boolean::class.createType() -> argument(paramName, BoolArgumentType.bool())
            UUID::class.createType() -> argument(paramName, UuidArgumentType.uuid())
            Identifier::class.createType() -> argument(paramName, IdentifierArgumentType.identifier())
            BlockPos::class.createType() -> argument(paramName, BlockPosArgumentType.blockPos())
            else -> throw IllegalArgumentException("Don't know how to handle parameter ${param.name} which is of type ${param.type}")
        }
        return runThenAttach({ if (index < list.lastIndex) attachParams(name, list, index + 1, method) else method()}, next)
    }

    /**
     * Creates an argument that will return a string in the set with restrictions based on [filter]
     *
     * Values are retrieved with [getData]
     *
     * @param name the name of the argument
     * @param clazz the data class for the argument
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun data(name: String, clazz: KClass<*>, method: AegisCommandBuilder.() -> Boolean): Boolean {
        return attachParams(name, clazz.primaryConstructor!!.parameters, 0, method)
    }

    /**
     * Sets a requirement for the command to be usable
     *
     * @see ArgumentBuilder.requires
     */
    fun requires(method: (ServerCommandSource)->Boolean) {
        currentNode.requires(method)
    }

    /**
     * The final tree argument, executes code in the block when reached
     *
     * @param debug if the error should print its stack trace on failure (Errors are consumed normally later)
     * @param method a lambda that takes in a CommandContext&lt;ServerCommandSource&gt; and returns an int, with the number showing success count, or 1 for generic success
     * @see ArgumentBuilder.executes
     */
    inline fun executesExplicit(debug: Boolean = devEnv, crossinline method: (CommandContext<ServerCommandSource>)->Int): Boolean {
        currentNode.executes {
            try {
                method(it)
            } catch (possible: Throwable) {
                if (debug) {
                    possible.printStackTrace()
                }
                throw possible
            }
        }
        return true
    }

    /**
     * The final tree argument, executes code in the block when reached
     *
     * Returns 1 if the code returns without error, re-throws otherwise
     *
     * @param debug if the error should print its stack trace on failure (Errors are consumed normally later)
     * @param method a lambda that takes in a CommandContext&lt;ServerCommandSource&gt;
     * @see ArgumentBuilder.executes
     */
    inline fun executes(debug: Boolean = devEnv, crossinline method: (CommandContext<ServerCommandSource>)->Unit): Boolean {
        currentNode.executes {
            try {
                method(it)
                1
            } catch (possible: Throwable) {
                if (debug) {
                    possible.printStackTrace()
                }
                throw possible
            }
        }
        return true
    }

    /**
     * Adds suggestions to a node
     *
     * @param method a lambda that takes in a CommandContext&lt;ServerCommandSource&gt; and a SuggestionBuilder, returning a CompletableFuture&lt;Suggestions&gt;
     * @see RequiredArgumentBuilder.suggests
     */
    inline fun suggests(crossinline method: (CommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
        val nodeCopy = currentNode
        if (nodeCopy is RequiredArgumentBuilder<*, *>) {
            nodeCopy.suggests { context, builder ->
                @Suppress("UNCHECKED_CAST")
                method(context as CommandContext<ServerCommandSource>, builder)
            }
        }
        currentNode = nodeCopy
    }
}

fun aegisCommand(rootLiteralValue: String, method: AegisCommandBuilder.()->Unit): LiteralArgumentBuilder<ServerCommandSource> {
    return AegisCommandBuilder(rootLiteralValue, method).build()
}

fun CommandDispatcher<ServerCommandSource>.register(rootLiteralValue: String, method: AegisCommandBuilder.()->Unit) {
    register(aegisCommand(rootLiteralValue, method))
}
