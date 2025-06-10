package net.brybry.vs_test_addon.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.brybry.vs_test_addon.VSTestAddon.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> VSTEST_ADDON_TAB =
            TABS.register("vstestaddon", () -> CreativeModeTab.builder()
                    .title(Component.literal("Brys VS Test Addon "))
                    .icon(() -> new ItemStack(ModBlocks.GOOBER_BLOCK.get()))
                    .displayItems((ItemDisplayParameters parameters, Output output) -> {
                        output.accept(ModBlocks.GOOBER_BLOCK.get());
                        output.accept(ModBlocks.SILLY_BLOCK.get());
                        output.accept(ModBlocks.EVIL_GOOBER_BLOCK.get());
                        output.accept(ModBlocks.SIILY_FUCKING_BOMB.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}