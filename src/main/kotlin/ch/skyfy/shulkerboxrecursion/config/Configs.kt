package ch.skyfy.shulkerboxrecursion.config

import ch.skyfy.json5configlib.ConfigData
import ch.skyfy.shulkerboxrecursion.ShulkerBoxRecursionMod


object Configs {
    @JvmField
    val CONFIG = ConfigData.invokeSpecial<Config>(ShulkerBoxRecursionMod.CONFIG_DIRECTORY.resolve("config.json5"), true)
}
