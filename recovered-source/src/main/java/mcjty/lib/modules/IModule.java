package mcjty.lib.modules;

import mcjty.lib.datagen.DataGen;
import net.minecraft.core.HolderLookup.Provider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IModule {
   void init(FMLCommonSetupEvent var1);

   void initClient(FMLClientSetupEvent var1);

   void initConfig(IEventBus var1);

   default void initDatagen(DataGen dataGen, Provider provider) {
   }
}
