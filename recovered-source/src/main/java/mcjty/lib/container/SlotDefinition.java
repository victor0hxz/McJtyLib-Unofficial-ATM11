package mcjty.lib.container;

import java.util.Objects;
import java.util.function.Predicate;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SlotDefinition {
   private final SlotType type;
   private boolean input = false;
   private boolean output = false;
   private final Predicate<ItemStack> validItems;
   private TriConsumer<BlockEntity, Player, ItemStack> onCraft = (te, playerEntity, stack) -> {};

   SlotDefinition(SlotType type, ItemStack... itemStacks) {
      this.type = type;
      this.validItems = stack -> {
         for (ItemStack itemStack : itemStacks) {
            if (itemStack.getItem() == stack.getItem()) {
               return true;
            }
         }

         return false;
      };
   }

   public SlotDefinition onCraft(TriConsumer<BlockEntity, Player, ItemStack> onCraft) {
      this.onCraft = onCraft;
      return this;
   }

   public SlotDefinition in() {
      this.input = true;
      return this;
   }

   public SlotDefinition out() {
      this.output = true;
      return this;
   }

   public TriConsumer<BlockEntity, Player, ItemStack> getOnCraft() {
      return this.onCraft;
   }

   private SlotDefinition(SlotType type, Predicate<ItemStack> validItems) {
      this.type = type;
      this.validItems = validItems;
   }

   public static SlotDefinition specific(ItemStack... stacks) {
      return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, stacks);
   }

   public static SlotDefinition specific(Item... items) {
      ItemStack[] stacks = new ItemStack[items.length];

      for (int i = 0; i < items.length; i++) {
         stacks[i] = new ItemStack(items[i]);
      }

      return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, stacks);
   }

   public static SlotDefinition specific(Predicate<ItemStack> validItems) {
      return new SlotDefinition(SlotType.SLOT_SPECIFICITEM, validItems);
   }

   public static SlotDefinition generic() {
      return new SlotDefinition(SlotType.SLOT_GENERIC);
   }

   public static SlotDefinition ghost() {
      return new SlotDefinition(SlotType.SLOT_GHOST);
   }

   public static SlotDefinition ghostOut() {
      return new SlotDefinition(SlotType.SLOT_GHOSTOUT);
   }

   public static SlotDefinition craftResult() {
      return new SlotDefinition(SlotType.SLOT_CRAFTRESULT);
   }

   public SlotType getType() {
      return this.type;
   }

   public boolean itemStackMatches(ItemStack stack) {
      return this.validItems.test(stack);
   }

   public boolean isSpecific() {
      return this.type == SlotType.SLOT_SPECIFICITEM;
   }

   public boolean isInput() {
      return this.input;
   }

   public boolean isOutput() {
      return this.output;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SlotDefinition that = (SlotDefinition)o;
         return this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type);
   }
}
