package net.dobbs.blockman.item;

import net.dobbs.blockman.Blockman;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item RAW_DOBBSZANITE = registerItem("raw_dobbszanite",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item DOBBSZANITE_INGOT = registerItem("dobbszanite_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(Blockman.MOD_ID, name), item);
    }

    public static void registerModItems(){
        Blockman.LOGGER.debug("Registering mod items for " + Blockman.MOD_ID);
    }
}
