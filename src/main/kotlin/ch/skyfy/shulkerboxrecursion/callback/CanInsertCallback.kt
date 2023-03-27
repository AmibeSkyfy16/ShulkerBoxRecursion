package ch.skyfy.shulkerboxrecursion.callback

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult

fun interface CanInsertCallback {

    companion object {
        @JvmField
        val EVENT: Event<CanInsertCallback> = EventFactory.createArrayBacked(CanInsertCallback::class.java){ listeners ->
            CanInsertCallback { instance ->
                for(listener in listeners) {
                    val result = listener.canInsert(instance)
                    if(result.result != ActionResult.FAIL)
                        return@CanInsertCallback result
                }
                TypedActionResult.pass(true)
            }
        }
    }

    fun canInsert(instance: Inventory): TypedActionResult<Boolean>

}