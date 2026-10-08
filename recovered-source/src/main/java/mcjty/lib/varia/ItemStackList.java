package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;

public class ItemStackList extends NonNullList<ItemStack> {
   public static final ItemStackList EMPTY = create(0);

   public static ItemStackList create(int size) {
      Validate.notNull(ItemStack.EMPTY);
      ItemStack[] aobject = new ItemStack[size];
      Arrays.fill(aobject, ItemStack.EMPTY);
      return new ItemStackList(Arrays.asList(aobject), ItemStack.EMPTY);
   }

   public static ItemStackList create() {
      return new ItemStackList(new ArrayList<>(), ItemStack.EMPTY);
   }

   public ItemStackList(List<ItemStack> delegateIn, @Nullable ItemStack stack) {
      super(delegateIn, stack);
   }
}
