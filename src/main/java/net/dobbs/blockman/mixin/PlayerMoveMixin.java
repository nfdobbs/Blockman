package net.dobbs.blockman.mixin;

import net.dobbs.blockman.util.PlayerMoveCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.*;


@Mixin(Entity.class)
public abstract class PlayerMoveMixin {

    @ModifyVariable(method = "move", at = @At("STORE"), ordinal = 1)
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
}
