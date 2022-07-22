package net.dobbs.dobbs_first_mod.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public interface PlayerMoveCallback {
    Event<PlayerMoveCallback> EVENT = EventFactory.createArrayBacked(PlayerMoveCallback.class,
            (listeners) -> (player, vector) -> {
                Vec3d result = Vec3d.ZERO;
                for(PlayerMoveCallback listener : listeners){
                    result = listener.interact(player, vector);
                }
                return result;
            });

    Vec3d interact(LivingEntity player, Vec3d vector);
}
