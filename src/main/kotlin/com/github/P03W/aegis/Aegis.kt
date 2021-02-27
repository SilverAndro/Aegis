/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:Suppress("unused")

package com.github.p03w.aegis

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

/**
 * The core builder
 *
 * Receiver functions are used, so builder functions can be called easily
 *
 * @param rootLiteralValue the value of the literal node used as root
 */
class AegisCommandBuilder(rootLiteralValue: String, method: AegisCommandBuilder.()->Unit) {
    private var currentNode: ArgumentBuilder<ServerCommandSource, *> = CommandManager.literal(rootLiteralValue)

    init {
        method()
    }

    /**
     * Internal function used to create all child nodes all then attach to the parent
     *
     * This is usually recursive, as new nodes call this when being attached
     */
    private fun runThenAttach(method: AegisCommandBuilder.()->Unit, node: ArgumentBuilder<ServerCommandSource, *>) {
        val oldNode = currentNode
        currentNode = node

        method()

        currentNode = oldNode
        currentNode.then(node)
    }

    /**
     * Finalises the tree and returns the root node
     *
     * This should always be called last, usually attached to the end of the method in AegisCommandBuilder
     *
     * For example, `AegisCommandBuilder("sample") {/* arguments */}.build`
     */
    fun build(): LiteralArgumentBuilder<ServerCommandSource> {
        return currentNode as LiteralArgumentBuilder<ServerCommandSource>
    }

    /**
     * Gives access to the current node in the tree being built
     *
     * This exists to fill in gaps where Aegis doesn't have a replacement.
     * Note there is no way to escape a raw block, once you enter one all code must be raw brigadier
     */
    fun raw(method: ArgumentBuilder<ServerCommandSource, *>.()->Unit) {
        method(currentNode)
    }

    /**
     * Allows for attaching custom arguments to the tree
     *
     * @param argument The custom argument
     */
    fun custom(argument: ArgumentBuilder<ServerCommandSource, *>, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, argument)
    }

    /**
     * Creates a literal argument
     *
     * @param literalValue the value of the literal argument
     * @see CommandManager.literal
     */
    fun literal(literalValue: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.literal(literalValue))
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
    fun integer(name: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, IntegerArgumentType.integer(min, max)))
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
    fun long(name: String, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, LongArgumentType.longArg(min, max)))
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
    fun float(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, FloatArgumentType.floatArg(min, max)))
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
    fun double(name: String, min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, DoubleArgumentType.doubleArg(min, max)))
    }

    /**
     * Creates a boolean argument
     *
     * Values are retrieved with [BoolArgumentType.getBool]
     *
     * @param name the name of the argument
     * @see BoolArgumentType
     */
    fun bool(name: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, BoolArgumentType.bool()))
    }

    /**
     * Creates a string argument (single word or with quotes)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    fun string(name: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, StringArgumentType.string()))
    }

    /**
     * Creates a word argument (single word, no quotes)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    fun word(name: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, StringArgumentType.word()))
    }

    /**
     * Creates a greedy string argument (captures ***ALL*** text after previous argument)
     *
     * Values are retrieved with [StringArgumentType.getString]
     *
     * @param name the name of the argument
     * @see StringArgumentType
     */
    fun greedyString(name: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, StringArgumentType.greedyString()))
    }

    /**
     * Creates a block pos argument
     *
     * Values are retrieved with [BlockPosArgumentType.getBlockPos]
     *
     * @param name the name of the argument
     * @see BlockPosArgumentType
     */
    fun blockPos(name: String, method: AegisCommandBuilder.()->Unit) {
        runThenAttach(method, CommandManager.argument(name, BlockPosArgumentType.blockPos()))
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
    fun vec3(name: String, centered: Boolean = true, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, Vec3ArgumentType.vec3(centered)))
    }

    /**
     * Creates a entity argument
     *
     * Values are retrieved with [EntityArgumentType.getEntity]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    fun entity(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, EntityArgumentType.entity()))
    }

    /**
     * Creates a multiple entity argument
     *
     * Values are retrieved with [EntityArgumentType.getEntities]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    fun entities(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, EntityArgumentType.entities()))
    }

    /**
     * Creates a player argument
     *
     * Values are retrieved with [EntityArgumentType.getEntity]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    fun player(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, EntityArgumentType.player()))
    }

    /**
     * Creates a multiple player argument
     *
     * Values are retrieved with [EntityArgumentType.getEntities]
     *
     * @param name the name of the argument
     * @see EntityArgumentType
     */
    fun players(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, EntityArgumentType.players()))
    }

    /**
     * Creates an angle argument
     *
     * Values are retrieved with [AngleArgumentType.getAngle]
     *
     * @param name the name of the argument
     * @see AngleArgumentType
     */
    fun angle(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, AngleArgumentType.angle()))
    }

    /**
     * Creates a rotation argument
     *
     * Values are retrieved with [RotationArgumentType.getRotation]
     *
     * @param name the name of the argument
     * @see RotationArgumentType
     */
    fun rotation(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, RotationArgumentType.rotation()))
    }

    /**
     * Creates a dimension argument
     *
     * Values are retrieved with [DimensionArgumentType.getDimensionArgument]
     *
     * @param name the name of the argument
     * @see DimensionArgumentType
     */
    fun dimension(name: String, method: AegisCommandBuilder.() -> Unit) {
        runThenAttach(method, CommandManager.argument(name, DimensionArgumentType.dimension()))
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
     * @param method a lambda that takes in a CommandContext&lt;ServerCommandSource&gt; and returns an int, with the number showing success count, or 1 for generic success
     * @see ArgumentBuilder.executes
     */
    fun executes(method: (CommandContext<ServerCommandSource>)->Int) {
        currentNode.executes(method)
    }

    /**
     * Adds suggestions to a node
     *
     * @param method a lambda that takes in a CommandContext&lt;ServerCommandSource&gt; and a SuggestionBuilder, returning a CompletableFuture&lt;Suggestions&gt;
     * @see RequiredArgumentBuilder.suggests
     */
    fun suggests(method: (CommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
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
