package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BlockPosTools {
   public static final BlockPos INVALID = new BlockPos(-1, -1000, -1);

   public static boolean isValid(BlockPos pos) {
      return pos != null && pos.getY() != -1000;
   }

   public static BlockPos read(CompoundTag tagCompound, String tagName) {
      int[] array = tagCompound.getIntArray(tagName).orElseGet(() -> new int[0]);
      return array.length == 0 ? null : new BlockPos(array[0], array[1], array[2]);
   }

   public static void write(CompoundTag tagCompound, String tagName, BlockPos coordinate) {
      if (coordinate == null) {
         tagCompound.putIntArray(tagName, new int[0]);
      } else {
         tagCompound.putIntArray(tagName, new int[]{coordinate.getX(), coordinate.getY(), coordinate.getZ()});
      }
   }

   public static BlockPos read(ValueInput input, String tagName) {
      int[] array = input.getIntArray(tagName).orElseGet(() -> new int[0]);
      return array.length < 3 ? null : new BlockPos(array[0], array[1], array[2]);
   }

   public static void write(ValueOutput output, String tagName, BlockPos coordinate) {
      if (coordinate == null) {
         output.putIntArray(tagName, new int[0]);
      } else {
         output.putIntArray(tagName, new int[]{coordinate.getX(), coordinate.getY(), coordinate.getZ()});
      }
   }

   public static CompoundTag write(BlockPos coordinate) {
      CompoundTag tagCompound = new CompoundTag();
      write(tagCompound, "c", coordinate);
      return tagCompound;
   }

   public static String toString(BlockPos pos) {
      return pos.getX() + "," + pos.getY() + "," + pos.getZ();
   }

   public static String toString(GlobalPos pos) {
      return toString(pos.pos()) + " (" + pos.dimension().identifier().getPath() + ")";
   }
}
