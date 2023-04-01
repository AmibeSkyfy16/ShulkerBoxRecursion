package ch.skyfy.shulkerboxrecursion.mixin;

import ch.skyfy.shulkerboxrecursion.callback.CanInsertCallback;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public abstract class ShulkerBoxSlotMixin {
    @Inject(at = @At("HEAD"), method = "canInsert", cancellable = true)
    public void init(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var instance = (ShulkerBoxSlot) (Object) this;

        var result = CanInsertCallback.EVENT.invoker().canInsert(stack, instance.inventory);
        if (result.getResult() == ActionResult.PASS) {
            cir.setReturnValue(result.getValue());
            cir.cancel();
        }
    }
//        cir.setReturnValue(true);

}