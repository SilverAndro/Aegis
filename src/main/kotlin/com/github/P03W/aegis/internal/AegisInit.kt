/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.p03w.aegis.internal

import ca.stellardrift.colonel.api.ServerArgumentType
import com.github.p03w.aegis.internal.types.EnumArgument
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

class AegisInit : ModInitializer {
    override fun onInitialize() {
        ServerArgumentType.builder<EnumArgument<*>>(Identifier("aegis", "enum"))
            .type(EnumArgument::class.java)
            .serializer(EnumArgument.Serializer)
            .fallbackProvider { StringArgumentType.word() }
            .register()
    }
}
