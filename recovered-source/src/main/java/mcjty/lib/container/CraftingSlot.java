package mcjty.lib.container;

import javax.annotation.Nonnull;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CraftingSlot extends SlotItemHandler {
   private final GenericTileEntity te;
   private final Player player;
   private TriConsumer<BlockEntity, Player, ItemStack> onCraft = (container, playerEntityx, stack) -> {};
   private int removeCount;

   public CraftingSlot(Player playerEntity, IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
      super(inventory, index, x, y);
      this.te = te;
      this.player = playerEntity;
   }

   public CraftingSlot onCraft(TriConsumer<BlockEntity, Player, ItemStack> onCraft) {
      this.onCraft = onCraft;
      return this;
   }

   public TriConsumer<BlockEntity, Player, ItemStack> getOnCraft() {
      return this.onCraft;
   }

   @Nonnull
   public ItemStack remove(int amount) {
      if (this.hasItem()) {
         this.removeCount = this.removeCount + Math.min(amount, this.getItem().getCount());
      }

      return super.remove(amount);
   }

   public void set(@Nonnull ItemStack stack) {
      if (this.te != null) {
         this.te.onSlotChanged(this.getSlotIndex(), stack);
      }

      super.set(stack);
   }

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return false;
   }

   public void onTake(@Nonnull Player thePlayer, @Nonnull ItemStack stack) {
      this.checkTakeAchievements(stack);
      super.onTake(thePlayer, stack);
   }

   protected void onQuickCraft(@Nonnull ItemStack stack, int amount) {
      this.removeCount += amount;
      this.checkTakeAchievements(stack);
   }

   protected void checkTakeAchievements(ItemStack stack) {
      stack.onCraftedBy(this.player, this.removeCount);
      this.onCraft.accept(this.te, this.player, stack);
      int craftedCount = this.removeCount;
      this.removeCount = 0;
      EventHooks.firePlayerSmeltedEvent(this.player, stack, craftedCount);
   }
}
