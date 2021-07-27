/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.p03w.aegis.internal.types

import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.LiteralText
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

class EnumArgument<T : Enum<T>>(val enumClass: Class<T>) : ArgumentType<T> {
    private val values: HashMap<String, T> = hashMapOf()

    init {
        enumClass.enumConstants.forEach {
            val cleanName = it.name
            values[cleanName] = it
        }
    }

    override fun parse(reader: StringReader): T {
        val name = reader.readUnquotedString()
        return values[name] ?: throw illegalEnumValue.createWithContext(reader, name, enumClass)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(values.keys, builder)
    }

    override fun getExamples(): Collection<String> {
        return values.keys
    }

    internal class Serializer : ArgumentSerializer<EnumArgument<*>> {
        override fun toPacket(argumentType: EnumArgument<*>, packetByteBuf: PacketByteBuf) {
            packetByteBuf.writeString(argumentType.enumClass.name, MAX_LENGTH)
        }

        override fun fromPacket(packetByteBuf: PacketByteBuf): EnumArgument<*> {
            val className = packetByteBuf.readString(MAX_LENGTH)
            val value = Class.forName(className)
            require(value.isEnum) { "Class $value is not an enum!" }
            return EnumArgument(value.asSubclass(Enum::class.java))
        }

        override fun toJson(argumentType: EnumArgument<*>, jsonObject: JsonObject) {
            jsonObject.addProperty("enum", argumentType.enumClass.name)
        }

        companion object {
            const val MAX_LENGTH = 128 * 4
        }
    }

    companion object {
        val illegalEnumValue = Dynamic2CommandExceptionType { value: Any, `class`: Any ->
            LiteralText("Unknown enum '$value' in ${(`class` as Class<*>).simpleName}")
        }

        fun <T: Enum<T>> getEnum(ctx: CommandContext<*>, argKey: String, enumClazz: KClass<T>): T {
            return ctx.getArgument(argKey, enumClazz.java)
        }
    }
}