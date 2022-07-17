package net.dobbs.dobbs_first_mod.mixin;

import net.dobbs.dobbs_first_mod.util.PlayerMoveCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public class PlayerMoveMixin {
    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"))
    protected void injectPlayerMovedEvent(double dx, double dy, double dz, CallbackInfo info)
    {
        ActionResult result = PlayerMoveCallback.EVENT.invoker().interact((PlayerEntity) (Object) this);
    }
}
