/*
 * StatCraft Bukkit Plugin
 *
 * Copyright (c) 2016 Kyle Wood (DemonWav)
 * https://www.demonwav.com
 *
 * MIT License
 */

package com.demonwav.statcraft.config

import com.md_5.config.ConfigComment
import com.md_5.config.NewLine

data class Config(

    @ConfigComment("MySQL Settings")
    var mysql: SqlConfig = SqlConfig(),

    @NewLine
    @ConfigComment("Statistics Settings")
    var stats: StatsConfig = StatsConfig(),

    @NewLine
    @ConfigComment("You can specify a server timezone for the last seen command, or just leave it as \"auto\".",
        "If you specify a timezone, it must be a proper three letter abbreviation.")
    var timezone: String = "auto",

    @NewLine
    @ConfigComment("Change the look of the plugin's command responses by modifying these values.",
        "For any of the color fields, this is the list of responses you can use:",
        "",
        " - BLACK",
        " - DARK_BLUE",
        " - DARK_GREEN",
        " - DARK_AQUA",
        " - DARK_RED",
        " - DARK_PURPLE",
        " - GOLD",
        " - GRAY",
        " - DARK_GRAY",
        " - BLUE",
        " - GREEN",
        " - AQUA",
        " - RED",
        " - LIGHT_PURPLE",
        " - YELLOW",
        " - WHITE")
    var colors: ColorConfig = ColorConfig()
)
