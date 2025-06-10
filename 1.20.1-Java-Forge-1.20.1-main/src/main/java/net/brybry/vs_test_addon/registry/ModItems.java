package net.brybry.vs_test_addon.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.brybry.vs_test_addon.VSTestAddon.MOD_ID;


    public class ModItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);



        public static void register(IEventBus eventBus) {
            ITEMS.register(eventBus);}
}
