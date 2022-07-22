package net.dobbs.dobbs_first_mod.mixin;

import net.dobbs.dobbs_first_mod.util.PlayerMoveCallback;
import net.minecraft.block.LichenGrower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class PlayerMoveMixin {

    @ModifyVariable(method = "move", at = @At("STORE"), ordinal = -1)
    private Vec3d inject(Vec3d i)
    {
        Vec3d result = i;
        Entity entity = (Entity) (Object) this;
        if(entity instanceof PlayerEntity)
        {
            result = PlayerMoveCallback.EVENT.invoker().interact((PlayerEntity) (Object) this, i);
        }

        return result;
    }


    //@Shadow public abstract void enterCombat();

    //Complex Working Model
    /*
    @Inject(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            ordinal = 1),
            slice = @Slice( from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V",
                    shift = At.Shift.AFTER)))

    protected void injectPlayerMovedEvent(CallbackInfo ci)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity instanceof PlayerEntity)
        {
            ActionResult result = PlayerMoveCallback.EVENT.invoker().interact((PlayerEntity) (Object) this);
        }
    } */

    //Works for playerInput

    /*
    @Inject(method = "applyMovementInput", at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    shift = At.Shift.AFTER))

    protected void injectPlayerMovedEvent(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity instanceof PlayerEntity)
        {
            ActionResult result = PlayerMoveCallback.EVENT.invoker().interact((PlayerEntity) (Object) this);
        }
    }*/






}
