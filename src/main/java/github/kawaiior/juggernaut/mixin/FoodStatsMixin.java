package github.kawaiior.juggernaut.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(PlayerEntity player, CallbackInfo callbackInfo){
        callbackInfo.cancel();
    }

}
