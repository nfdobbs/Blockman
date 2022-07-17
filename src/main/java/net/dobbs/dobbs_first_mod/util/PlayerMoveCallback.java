package net.dobbs.dobbs_first_mod.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerMoveCallback {
    Event<PlayerMoveCallback> EVENT = EventFactory.createArrayBacked(PlayerMoveCallback.class,
            (listeners) -> (player) -> {
                for(PlayerMoveCallback listener : listeners){
                    ActionResult result = listener.interact(player);
                }
                return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player);
}
