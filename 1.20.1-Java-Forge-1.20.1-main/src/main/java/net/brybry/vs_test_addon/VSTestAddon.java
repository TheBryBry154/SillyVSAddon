package net.brybry.vs_test_addon;

import net.brybry.vs_test_addon.registry.ModBlocks;
import net.brybry.vs_test_addon.registry.ModCreativeTabs;
import net.brybry.vs_test_addon.registry.ModItems;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VSTestAddon.MOD_ID)
public class VSTestAddon {
    public static final String MOD_ID = "vstestaddon";

    public VSTestAddon() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);


    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        IEventBus clientBus = FMLJavaModLoadingContext.get().getModEventBus();
        clientBus.addListener(this::onClientSetup);
    }
    private void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {

    }
}
