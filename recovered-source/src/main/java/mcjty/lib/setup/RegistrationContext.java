package mcjty.lib.setup;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class RegistrationContext {
   private static final ThreadLocal<ResourceKey<Block>> CURRENT_BLOCK = new ThreadLocal<>();
   private static final ThreadLocal<ResourceKey<Item>> CURRENT_ITEM = new ThreadLocal<>();

   private RegistrationContext() {
   }

   public static <T> T withBlockId(Identifier location, Supplier<T> supplier) {
      ResourceKey<Block> old = CURRENT_BLOCK.get();
      CURRENT_BLOCK.set(ResourceKey.create(Registries.BLOCK, location));

      Object var3;
      try {
         var3 = supplier.get();
      } finally {
         if (old == null) {
            CURRENT_BLOCK.remove();
         } else {
            CURRENT_BLOCK.set(old);
         }
      }

      return (T)var3;
   }

   public static <T> T withItemId(Identifier location, Supplier<T> supplier) {
      ResourceKey<Item> old = CURRENT_ITEM.get();
      CURRENT_ITEM.set(ResourceKey.create(Registries.ITEM, location));

      Object var3;
      try {
         var3 = supplier.get();
      } finally {
         if (old == null) {
            CURRENT_ITEM.remove();
         } else {
            CURRENT_ITEM.set(old);
         }
      }

      return (T)var3;
   }

   public static Properties prepareBlockProperties(Properties properties) {
      ResourceKey<Block> id = CURRENT_BLOCK.get();
      if (id != null) {
         properties.setId(id);
      }

      return properties;
   }

   public static net.minecraft.world.item.Item.Properties prepareItemProperties(net.minecraft.world.item.Item.Properties properties) {
      ResourceKey<Item> id = CURRENT_ITEM.get();
      if (id != null) {
         properties.setId(id);
      }

      return properties;
   }
}
