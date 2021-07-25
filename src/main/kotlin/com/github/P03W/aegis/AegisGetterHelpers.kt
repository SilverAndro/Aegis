/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.github.p03w.aegis

import com.github.p03w.aegis.internal.types.EnumArgument
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import net.minecraft.command.argument.*
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.function.CommandFunction
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.Tag
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.reflect.KClass

inline fun CommandContext<ServerCommandSource>.getInt(name: String): Int {
    return IntegerArgumentType.getInteger(this, name)
}

inline fun CommandContext<ServerCommandSource>.getLong(name: String): Long {
    return LongArgumentType.getLong(this, name)
}

inline fun CommandContext<ServerCommandSource>.getFloat(name: String): Float {
    return FloatArgumentType.getFloat(this, name)
}

inline fun CommandContext<ServerCommandSource>.getDouble(name: String): Double {
    return DoubleArgumentType.getDouble(this, name)
}

inline fun CommandContext<ServerCommandSource>.getBool(name: String): Boolean {
    return BoolArgumentType.getBool(this, name)
}

inline fun CommandContext<ServerCommandSource>.getString(name: String): String {
    return StringArgumentType.getString(this, name)
}

inline fun CommandContext<ServerCommandSource>.getBlockPos(name: String): BlockPos {
    return BlockPosArgumentType.getBlockPos(this, name)
}

inline fun CommandContext<ServerCommandSource>.getVec2(name: String): Vec2f {
    return Vec2ArgumentType.getVec2(this, name)
}

inline fun CommandContext<ServerCommandSource>.getVec3(name: String): Vec3d {
    return Vec3ArgumentType.getVec3(this, name)
}

inline fun CommandContext<ServerCommandSource>.getEntity(name: String): Entity {
    return EntityArgumentType.getEntity(this, name)
}

inline fun CommandContext<ServerCommandSource>.getEntities(name: String): MutableCollection<out Entity> {
    return EntityArgumentType.getEntities(this, name)
}

inline fun CommandContext<ServerCommandSource>.getPlayer(name: String): ServerPlayerEntity {
    return EntityArgumentType.getPlayer(this, name)
}

inline fun CommandContext<ServerCommandSource>.getPlayers(name: String): MutableCollection<ServerPlayerEntity> {
    return EntityArgumentType.getPlayers(this, name)
}

inline fun CommandContext<ServerCommandSource>.getAngle(name: String): Float {
    return AngleArgumentType.getAngle(this, name)
}

inline fun CommandContext<ServerCommandSource>.getRotation(name: String): PosArgument {
    return RotationArgumentType.getRotation(this, name)
}

inline fun CommandContext<ServerCommandSource>.getDimension(name: String): ServerWorld {
    return DimensionArgumentType.getDimensionArgument(this, name)
}

inline fun CommandContext<ServerCommandSource>.getIdentifier(name: String): Identifier {
    return IdentifierArgumentType.getIdentifier(this, name)
}

inline fun CommandContext<ServerCommandSource>.getText(name: String): Text {
    return TextArgumentType.getTextArgument(this, name)
}

inline fun CommandContext<ServerCommandSource>.getUUID(name: String): UUID {
    return UuidArgumentType.getUuid(this, name)
}

inline fun CommandContext<ServerCommandSource>.getGameProfile(name: String): MutableCollection<GameProfile> {
    return GameProfileArgumentType.getProfileArgument(this, name)
}

inline fun CommandContext<ServerCommandSource>.getSwizzle(name: String): EnumSet<Direction.Axis> {
    return SwizzleArgumentType.getSwizzle(this, name)
}

inline fun CommandContext<ServerCommandSource>.getNbtCompoundTag(name: String): NbtCompound {
    return NbtCompoundArgumentType.getNbtCompound(this, name)
}

inline fun CommandContext<ServerCommandSource>.getColor(name: String): Formatting {
    return ColorArgumentType.getColor(this, name)
}

inline fun CommandContext<ServerCommandSource>.getOperation(name: String): OperationArgumentType.Operation {
    return OperationArgumentType.getOperation(this, name)
}

inline fun CommandContext<ServerCommandSource>.getFunctionsOnly(name: String): MutableCollection<CommandFunction> {
    return CommandFunctionArgumentType.getFunctions(this, name)
}

inline fun CommandContext<ServerCommandSource>.getFunctionOrTag(name: String): Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> {
    return CommandFunctionArgumentType.getFunctionOrTag(this, name)
}

inline fun <reified T: Enum<T>> CommandContext<ServerCommandSource>.getEnum(name: String, enum: KClass<T>): T {
    return EnumArgument.getEnum(this, name, enum)
}
