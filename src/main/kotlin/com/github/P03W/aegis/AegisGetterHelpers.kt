@file:Suppress("unused")

package com.github.p03w.aegis

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.*
import net.minecraft.entity.Entity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.util.*

fun CommandContext<ServerCommandSource>.getInt(name: String): Int {
    return IntegerArgumentType.getInteger(this, name)
}

fun CommandContext<ServerCommandSource>.getLong(name: String): Long {
    return LongArgumentType.getLong(this, name)
}

fun CommandContext<ServerCommandSource>.getFloat(name: String): Float {
    return FloatArgumentType.getFloat(this, name)
}

fun CommandContext<ServerCommandSource>.getDouble(name: String): Double {
    return DoubleArgumentType.getDouble(this, name)
}

fun CommandContext<ServerCommandSource>.getBool(name: String): Boolean {
    return BoolArgumentType.getBool(this, name)
}

fun CommandContext<ServerCommandSource>.getString(name: String): String {
    return StringArgumentType.getString(this, name)
}

fun CommandContext<ServerCommandSource>.getBlockPos(name: String): BlockPos {
    return BlockPosArgumentType.getBlockPos(this, name)
}

fun CommandContext<ServerCommandSource>.getVec2(name: String): Vec2f {
    return Vec2ArgumentType.getVec2(this, name)
}

fun CommandContext<ServerCommandSource>.getVec3(name: String): Vec3d {
    return Vec3ArgumentType.getVec3(this, name)
}

fun CommandContext<ServerCommandSource>.getEntity(name: String): Entity {
    return EntityArgumentType.getEntity(this, name)
}

fun CommandContext<ServerCommandSource>.getEntities(name: String): MutableCollection<out Entity> {
    return EntityArgumentType.getEntities(this, name)
}

fun CommandContext<ServerCommandSource>.getPlayer(name: String): ServerPlayerEntity {
    return EntityArgumentType.getPlayer(this, name)
}

fun CommandContext<ServerCommandSource>.getPlayers(name: String): MutableCollection<ServerPlayerEntity> {
    return EntityArgumentType.getPlayers(this, name)
}

fun CommandContext<ServerCommandSource>.getAngle(name: String): Float {
    return AngleArgumentType.getAngle(this, name)
}

fun CommandContext<ServerCommandSource>.getRotation(name: String): PosArgument {
    return RotationArgumentType.getRotation(this, name)
}

fun CommandContext<ServerCommandSource>.getDimension(name: String): ServerWorld {
    return DimensionArgumentType.getDimensionArgument(this, name)
}

fun CommandContext<ServerCommandSource>.getIdentifier(name: String): Identifier {
    return IdentifierArgumentType.getIdentifier(this, name)
}

fun CommandContext<ServerCommandSource>.getText(name: String): Text {
    return TextArgumentType.getTextArgument(this, name)
}

fun CommandContext<ServerCommandSource>.getUUID(name: String): UUID {
    return UuidArgumentType.getUuid(this, name)
}

fun CommandContext<ServerCommandSource>.getGameProfile(name: String): MutableCollection<GameProfile> {
    return GameProfileArgumentType.getProfileArgument(this, name)
}

fun CommandContext<ServerCommandSource>.getSwizzle(name: String): EnumSet<Direction.Axis> {
    return SwizzleArgumentType.getSwizzle(this, name)
}

fun CommandContext<ServerCommandSource>.getNbtTag(name: String): Tag {
    return NbtTagArgumentType.getTag(this, name)
}

fun CommandContext<ServerCommandSource>.getNbtCompoundTag(name: String): CompoundTag {
    return NbtCompoundTagArgumentType.getCompoundTag(this, name)
}

fun CommandContext<ServerCommandSource>.getColor(name: String): Formatting {
    return ColorArgumentType.getColor(this, name)
}
