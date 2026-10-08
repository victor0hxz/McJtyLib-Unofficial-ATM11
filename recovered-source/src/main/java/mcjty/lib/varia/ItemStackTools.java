package mcjty.lib.varia;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.gui.GuiParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemStackTools {
   private static Set<TagKey<Item>> commonTags = null;

   @Nonnull
   public static ItemStack extractItem(@Nullable BlockEntity tileEntity, int slot, int amount) {
      if (tileEntity == null) {
         return ItemStack.EMPTY;
      } else {
         IItemHandler handler = (IItemHandler)tileEntity.getLevel().getCapability(LegacyCapabilities.ITEM_BLOCK, tileEntity.getBlockPos(), null);
         return handler != null ? handler.extractItem(slot, amount, false) : ItemStack.EMPTY;
      }
   }

   @Nonnull
   public static ItemStack getStack(@Nullable BlockEntity tileEntity, int slot) {
      if (tileEntity == null) {
         return ItemStack.EMPTY;
      } else {
         IItemHandler h = (IItemHandler)tileEntity.getLevel().getCapability(LegacyCapabilities.ITEM_BLOCK, tileEntity.getBlockPos(), null);
         return h != null ? h.getStackInSlot(slot) : ItemStack.EMPTY;
      }
   }

   public static GuiParser.GuiCommand itemStackToGuiCommand(String name, ItemStack item) {
      GuiParser.GuiCommand object = new GuiParser.GuiCommand(name);
      object.parameter(Tools.getId(item).toString());
      object.parameter(item.getCount());
      return object;
   }

   public static ItemStack guiCommandToItemStack(GuiParser.GuiCommand obj) {
      String itemName = obj.getOptionalPar(0, "minecraft:stick");
      Item item = Tools.getItem(Identifier.parse(itemName));
      int amount = obj.getOptionalPar(1, 1);
      return new ItemStack(item, amount);
   }

   public static void addCommonTags(Collection<TagKey<Item>> fromItem, Set<TagKey<Item>> tags) {
      findCommonTags();

      for (TagKey<Item> id : fromItem) {
         if (commonTags.contains(id)) {
            tags.add(id);
         }
      }
   }

   public static boolean hasCommonTag(Collection<TagKey<Item>> fromItem) {
      findCommonTags();

      for (TagKey<Item> id : fromItem) {
         if (commonTags.contains(id)) {
            return true;
         }
      }

      return false;
   }

   private static void findCommonTags() {
      if (commonTags == null) {
         commonTags = new HashSet<>();
         commonTags.add(ItemTags.SAND);
         commonTags.add(ItemTags.FENCES);
         commonTags.add(ItemTags.SAPLINGS);
         commonTags.add(ItemTags.LEAVES);
         commonTags.add(ItemTags.LOGS);
         commonTags.add(ItemTags.RAILS);
         commonTags.add(ItemTags.SLABS);
         commonTags.add(ItemTags.WOOL);
         commonTags.add(ItemTags.WOOL_CARPETS);
         commonTags.add(ItemTags.PLANKS);
         commonTags.add(ItemTags.STAIRS);
         commonTags.add(ItemTags.DIRT);
         commonTags.add(Items.CROPS);
         commonTags.add(Items.GLASS_BLOCKS);
         commonTags.add(Items.GLASS_PANES);
         commonTags.add(Items.CHESTS);
         commonTags.add(Items.COBBLESTONES);
         commonTags.add(Items.NETHERRACKS);
         commonTags.add(Items.OBSIDIANS);
         commonTags.add(Items.GRAVELS);
         commonTags.add(Items.SANDS);
         commonTags.add(Items.END_STONES);
         commonTags.add(Items.STONES);
         commonTags.add(Items.ORES_COAL);
         commonTags.add(Items.ORES_DIAMOND);
         commonTags.add(Items.ORES_EMERALD);
         commonTags.add(Items.ORES_GOLD);
         commonTags.add(Items.ORES_REDSTONE);
         commonTags.add(Items.ORES_QUARTZ);
         commonTags.add(Items.ORES_IRON);
         commonTags.add(Items.ORES_LAPIS);
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/copper")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/tin")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/silver")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/manganese")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/platinum")));
         commonTags.add(Items.STORAGE_BLOCKS_COAL);
         commonTags.add(Items.STORAGE_BLOCKS_DIAMOND);
         commonTags.add(Items.STORAGE_BLOCKS_EMERALD);
         commonTags.add(Items.STORAGE_BLOCKS_GOLD);
         commonTags.add(Items.STORAGE_BLOCKS_REDSTONE);
         commonTags.add(Items.STORAGE_BLOCKS_IRON);
         commonTags.add(Items.STORAGE_BLOCKS_LAPIS);
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/copper")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/tin")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/silver")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/manganese")));
         commonTags.add(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/platinum")));
      }
   }
}
