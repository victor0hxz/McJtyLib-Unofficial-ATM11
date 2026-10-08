package mcjty.lib.setup;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class DeferredBlocks {
   private final Blocks register;

   private DeferredBlocks(String modid) {
      this.register = DeferredRegister.createBlocks(modid);
   }

   public void register(IEventBus bus) {
      this.register.register(bus);
   }

   public <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier) {
      return this.register.register(name, location -> RegistrationContext.withBlockId(location, supplier));
   }

   public static DeferredBlocks create(String modid) {
      return new DeferredBlocks(modid);
   }
}
