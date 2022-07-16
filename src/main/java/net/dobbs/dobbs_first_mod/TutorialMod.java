package net.dobbs.dobbs_first_mod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialMod implements ModInitializer {
	//test comment
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
