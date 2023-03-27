package ch.skyfy.shulkerboxrecursion

import ch.skyfy.json5configlib.ConfigManager
import ch.skyfy.shulkerboxrecursion.callback.CanInsertCallback
import ch.skyfy.shulkerboxrecursion.command.ReloadFilesCmd
import ch.skyfy.shulkerboxrecursion.config.Configs
import ch.skyfy.shulkerboxrecursion.data.Node
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.TypedActionResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import kotlin.io.path.*

@Suppress("MemberVisibilityCanBePrivate")
class ShulkerBoxRecursionMod : ModInitializer {

    companion object {
        const val MOD_ID: String = "shulkerbox_recursion"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(ShulkerBoxRecursionMod::class.java)

        val NODES = mutableListOf<Node>()
    }

    init {
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))
    }

    override fun onInitialize() {
        registerCommands()
        registerCallbacks()
    }

    private fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> ReloadFilesCmd.register(dispatcher) }

    private fun registerCallbacks() {
        CanInsertCallback.EVENT.register { instance ->

//            val n = findChild(instance.hashCode().toString(), NODES)
//            if (n != null) {
//                println("not null")
//            } else {
//
//            }

            TypedActionResult.pass(true)
        }
    }

    private fun findChild(id: String, list: MutableList<Node>, count: Int = 0): Node? {
        val n = list.firstOrNull { it.id == id }
        if (n != null) return n
        else {
            list.forEach {
                return findChild(it.id, it.childs)
            }
        }
        return null
    }

}


