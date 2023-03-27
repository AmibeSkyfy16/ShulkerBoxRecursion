package ch.skyfy.shulkerboxrecursion.config

import ch.skyfy.json5configlib.Validatable
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialComment("How many times a shulkerbox can be put inside another one")
    val maximumRecursion: Int = 2
) : Validatable
