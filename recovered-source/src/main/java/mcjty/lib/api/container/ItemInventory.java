package mcjty.lib.api.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ItemInventory(List<ItemStack> items) {
   public static final Codec<ItemInventory> ITEM_INVENTORY_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").forGetter(ItemInventory::items)).apply(instance, ItemInventory::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemInventory> ITEM_INVENTORY_STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_LIST_STREAM_CODEC, ItemInventory::items, ItemInventory::new
   );

   @Override
   public boolean equals(Object o) {
      return o instanceof ItemInventory that ? ItemStack.listMatches(this.items, that.items) : false;
   }

   @Override
   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }
}
