package mcjty.lib.varia;

import javax.annotation.Nullable;
import mcjty.lib.blocks.BaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class OrientationTools {
   public static final Direction[] DIRECTION_VALUES = Direction.values();
   public static final Direction[] HORIZONTAL_DIRECTION_VALUES = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public static Direction rotateAround(Direction input, Axis axis) {
      switch (axis) {
         case X:
            if (input != Direction.WEST && input != Direction.EAST) {
               return rotateX(input);
            }

            return input;
         case Y:
            if (input != Direction.UP && input != Direction.DOWN) {
               return input.getClockWise();
            }

            return input;
         case Z:
            if (input != Direction.NORTH && input != Direction.SOUTH) {
               return rotateZ(input);
            }

            return input;
         default:
            throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   private static Direction rotateX(Direction input) {
      return switch (input) {
         case NORTH -> Direction.DOWN;
         case SOUTH -> Direction.UP;
         case UP -> Direction.NORTH;
         case DOWN -> Direction.SOUTH;
         case EAST, WEST -> throw new IllegalStateException("Unable to get X-rotated facing of " + input);
         default -> throw new MatchException(null, null);
      };
   }

   private static Direction rotateZ(Direction input) {
      return switch (input) {
         case NORTH, SOUTH -> throw new IllegalStateException("Unable to get Z-rotated facing of " + input);
         case UP -> Direction.EAST;
         case DOWN -> Direction.WEST;
         case EAST -> Direction.DOWN;
         case WEST -> Direction.UP;
         default -> throw new MatchException(null, null);
      };
   }

   public static Direction getOrientationHoriz(BlockState state) {
      return (Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public static Direction getOrientation(BlockState state) {
      return ((BaseBlock)state.getBlock()).getFrontDirection(state);
   }

   public static Direction getFacingFromEntity(BlockPos clickedBlock, @Nullable Entity entityIn) {
      if (entityIn == null) {
         return Direction.UP;
      } else {
         if (Mth.abs((float)entityIn.getX() - clickedBlock.getX()) < 2.0F && Mth.abs((float)entityIn.getZ() - clickedBlock.getZ()) < 2.0F) {
            double d0 = entityIn.getY() + entityIn.getEyeHeight();
            if (d0 - clickedBlock.getY() > 2.0) {
               return Direction.UP;
            }

            if (clickedBlock.getY() - d0 > 0.0) {
               return Direction.DOWN;
            }
         }

         return entityIn.getDirection().getOpposite();
      }
   }
}
