package mcjty.lib.setup;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class DeferredItems {
   private final Items register;

   private DeferredItems(String modid) {
      this.register = DeferredRegister.createItems(modid);
   }

   public void register(IEventBus bus) {
      this.register.register(bus);
   }

   public <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
      return this.register.register(name, location -> RegistrationContext.withItemId(location, supplier));
   }

   public static DeferredItems create(String modid) {
      return new DeferredItems(modid);
   }

   public Items getRegister() {
      return this.register;
   }
}
