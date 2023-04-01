package ch.skyfy.shulkerboxrecursion

import ch.skyfy.json5configlib.ConfigManager
import ch.skyfy.shulkerboxrecursion.callback.CanInsertCallback
import ch.skyfy.shulkerboxrecursion.command.ReloadFilesCmd
import ch.skyfy.shulkerboxrecursion.config.Configs
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
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
        CanInsertCallback.EVENT.register { stack, _ ->
            val count = countShulker(stack)
//            println("count: $count")

            if (count > Configs.CONFIG.serializableData.maximumRecursion) {
                return@register TypedActionResult.pass(false)
            } else {
                return@register TypedActionResult.pass(true)
            }

        }
    }

    private fun countShulker(stack: ItemStack, count: Int = 0): Int {
        var counter = count
        if (stack.item is BlockItem && (stack.item as BlockItem).block is ShulkerBoxBlock) {
            counter++
            val content = mutableListOf<ItemStack>()
            val blockEntityTag = stack.getSubNbt("BlockEntityTag")
            if (blockEntityTag != null) {
                (blockEntityTag.get("Items") as NbtList).forEach { nbtElement ->
                    if (nbtElement is NbtCompound) {
                        val stack2 = ItemStack.fromNbt(nbtElement)
                        content.add(stack2)
                    }
                }
            }

            content.forEach {
                if (it.item is BlockItem && (it.item as BlockItem).block is ShulkerBoxBlock) {
                    return countShulker(it, counter)
                }
            }
        }
        return counter
    }

}


