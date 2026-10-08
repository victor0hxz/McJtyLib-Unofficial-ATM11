package mcjty.lib.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import mcjty.lib.datagen.DataGen;
import net.minecraft.core.HolderLookup.Provider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class Modules {
   private final List<IModule> modules = new ArrayList<>();

   public void register(IModule module) {
      this.modules.add(module);
   }

   public void init(FMLCommonSetupEvent event) {
      this.modules.forEach(m -> m.init(event));
   }

   public void initClient(FMLClientSetupEvent event) {
      this.modules.forEach(m -> m.initClient(event));
   }

   public void initConfig(IEventBus bus) {
      this.modules.forEach(iModule -> iModule.initConfig(bus));
   }

   public void datagen(DataGen dataGen, CompletableFuture<Provider> provider) {
      this.modules.forEach(m -> {
         try {
            m.initDatagen(dataGen, provider.get());
         } catch (InterruptedException var4) {
            throw new RuntimeException(var4);
         } catch (ExecutionException var5) {
            throw new RuntimeException(var5);
         }
      });
   }
}
