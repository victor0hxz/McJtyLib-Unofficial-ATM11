package mcjty.lib.tileentity;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.varia.LogicFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LogicSupport {
   private int powerOutput = 0;

   public static LogicFacing getFacing(BlockState state) {
      return (LogicFacing)state.getValue(LogicSlabBlock.LOGIC_FACING);
   }

   public void setPowerOutput(int powerOutput) {
      this.powerOutput = powerOutput;
   }

   public int getPowerOutput() {
      return this.powerOutput;
   }

   public void setRedstoneState(GenericTileEntity te, int newout) {
      if (this.powerOutput != newout) {
         this.powerOutput = newout;
         te.setChanged();
         BlockState state = te.getBlockState();
         Direction outputSide = getFacing(state).getInputSide().getOpposite();
         te.getLevel().neighborChanged(te.getBlockPos().relative(outputSide), state.getBlock(), null);
      }
   }

   public void checkRedstone(GenericTileEntity te, Level world, BlockPos pos) {
      Direction inputSide = getFacing(world.getBlockState(pos)).getInputSide();
      int power = this.getInputStrength(world, pos, inputSide);
      te.setPowerInput(power);
   }

   public int getInputStrength(Level world, BlockPos pos, Direction side) {
      int power = world.getSignal(pos.relative(side), side);
      if (power < 15) {
         BlockState blockState = world.getBlockState(pos.relative(side));
         Block b = blockState.getBlock();
         if (b == Blocks.REDSTONE_WIRE) {
            power = Math.max(power, (Integer)blockState.getValue(RedStoneWireBlock.POWER));
         }
      }

      return power;
   }

   public int getRedstoneOutput(BlockState state, Direction side) {
      return side == getFacing(state).getInputSide() ? this.getPowerOutput() : 0;
   }
}
