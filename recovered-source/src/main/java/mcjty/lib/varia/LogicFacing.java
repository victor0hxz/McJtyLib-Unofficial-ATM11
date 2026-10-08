package mcjty.lib.varia;

import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum LogicFacing implements StringRepresentable {
   DOWN_TONORTH("down_tonorth", Direction.NORTH, 0),
   DOWN_TOSOUTH("down_tosouth", Direction.SOUTH, 1),
   DOWN_TOWEST("down_towest", Direction.WEST, 2),
   DOWN_TOEAST("down_toeast", Direction.EAST, 3),
   UP_TONORTH("up_tonorth", Direction.NORTH, 1),
   UP_TOSOUTH("up_tosouth", Direction.SOUTH, 0),
   UP_TOWEST("up_towest", Direction.WEST, 2),
   UP_TOEAST("up_toeast", Direction.EAST, 3),
   NORTH_TOWEST("north_towest", Direction.WEST, 2),
   NORTH_TOEAST("north_toeast", Direction.EAST, 3),
   NORTH_TOUP("north_toup", Direction.UP, 0),
   NORTH_TODOWN("north_todown", Direction.DOWN, 1),
   SOUTH_TOWEST("south_towest", Direction.WEST, 2),
   SOUTH_TOEAST("south_toeast", Direction.EAST, 3),
   SOUTH_TOUP("south_toup", Direction.UP, 1),
   SOUTH_TODOWN("south_todown", Direction.DOWN, 0),
   WEST_TONORTH("west_tonorth", Direction.NORTH, 2),
   WEST_TOSOUTH("west_tosouth", Direction.SOUTH, 3),
   WEST_TOUP("west_toup", Direction.UP, 1),
   WEST_TODOWN("west_todown", Direction.DOWN, 0),
   EAST_TONORTH("east_tonorth", Direction.NORTH, 3),
   EAST_TOSOUTH("east_tosouth", Direction.SOUTH, 2),
   EAST_TOUP("east_toup", Direction.UP, 1),
   EAST_TODOWN("east_todown", Direction.DOWN, 0);

   public static final LogicFacing[] VALUES = values();
   public static final Direction[] DIRECTIONS = Direction.values();
   private final String name;
   private final Direction inputSide;
   private final int rotationStep;

   private LogicFacing(String name, Direction inputSide, int rotationStep) {
      this.name = name;
      this.inputSide = inputSide;
      this.rotationStep = rotationStep;
   }

   @Nonnull
   public String getSerializedName() {
      return this.name;
   }

   public Direction getInputSide() {
      return this.inputSide;
   }

   public Direction getSide() {
      return DIRECTIONS[this.ordinal() / 4];
   }

   public int getRotationStep() {
      return this.rotationStep;
   }

   public static LogicFacing rotate(LogicFacing facing) {
      int current = facing.ordinal() & 3;
      if (++current > 3) {
         current = 0;
      }

      return VALUES[(facing.ordinal() & -4) + current];
   }

   @Override
   public String toString() {
      return this.name;
   }
}
