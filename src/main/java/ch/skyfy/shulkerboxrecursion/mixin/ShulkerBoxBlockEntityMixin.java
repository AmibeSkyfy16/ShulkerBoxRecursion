package ch.skyfy.shulkerboxrecursion.mixin;

import ch.skyfy.shulkerboxrecursion.callback.CanInsertCallback;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    @Inject(at = @At("HEAD"), method = "canInsert", cancellable = true)
    public void init(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        var result = CanInsertCallback.EVENT.invoker().canInsert((ShulkerBoxBlockEntity) (Object) this);
        if (result.getResult() == ActionResult.PASS) {
            cir.setReturnValue(result.getValue());
            cir.cancel();
        }
    }
}