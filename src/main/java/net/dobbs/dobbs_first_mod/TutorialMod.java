package net.dobbs.dobbs_first_mod;

import net.dobbs.dobbs_first_mod.item.ModItems;
import net.dobbs.dobbs_first_mod.util.PlayerMoveCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialMod implements ModInitializer {
	//test comment
	public static final String MOD_ID = "dobbs_first_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		LOGGER.info("Hello Fabric world!");

		PlayerMoveCallback.EVENT.register((player) -> {
			LOGGER.info(player.getEntityName() + " Moved: " + player.getPos());
			return ActionResult.PASS;
		});
	}
}
