package mcjty.lib.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.varia.LogicFacing;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LogicSlabBlock extends BaseBlock {
   public static final EnumProperty<LogicFacing> LOGIC_FACING = EnumProperty.create("logic_facing", LogicFacing.class);
   public static final VoxelShape BLOCK_DOWN = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
   public static final VoxelShape BLOCK_UP = Shapes.box(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
   public static final VoxelShape BLOCK_NORTH = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
   public static final VoxelShape BLOCK_SOUTH = Shapes.box(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
   public static final VoxelShape BLOCK_WEST = Shapes.box(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
   public static final VoxelShape BLOCK_EAST = Shapes.box(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);

   public LogicSlabBlock(BlockBuilder builder) {
      super(builder);
   }

   public static Direction rotateLeft(Direction downSide, Direction inputSide) {
      return switch (downSide) {
         case DOWN -> inputSide.getClockWise();
         case UP -> inputSide.getCounterClockWise();
         case NORTH -> OrientationTools.rotateAround(inputSide, Axis.Z);
         case SOUTH -> OrientationTools.rotateAround(inputSide.getOpposite(), Axis.Z);
         case WEST -> OrientationTools.rotateAround(inputSide, Axis.X);
         case EAST -> OrientationTools.rotateAround(inputSide.getOpposite(), Axis.X);
         default -> throw new MatchException(null, null);
      };
   }

   public static Direction rotateRight(Direction downSide, Direction inputSide) {
      return rotateLeft(downSide.getOpposite(), inputSide);
   }

   @Override
   public RotationType getRotationType() {
      return RotationType.NONE;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Vec3 hit = context.getClickLocation();
      BlockPos pos = context.getClickedPos();
      double hx = hit.x - pos.getX();
      double hy = hit.y - pos.getY();
      double hz = hit.z - pos.getZ();
      double dx = Math.abs(0.5 - hx);
      double dy = Math.abs(0.5 - hy);
      double dz = Math.abs(0.5 - hz);
      Direction side = context.getClickedFace().getOpposite();

      return (BlockState)super.getStateForPlacement(context).setValue(LOGIC_FACING, switch (side) {
         case DOWN -> {
            if (dx < dz) {
               yield hz < 0.5 ? LogicFacing.DOWN_TOSOUTH : LogicFacing.DOWN_TONORTH;
            } else {
               yield hx < 0.5 ? LogicFacing.DOWN_TOEAST : LogicFacing.DOWN_TOWEST;
            }
         }
         case UP -> {
            if (dx < dz) {
               yield hz < 0.5 ? LogicFacing.UP_TOSOUTH : LogicFacing.UP_TONORTH;
            } else {
               yield hx < 0.5 ? LogicFacing.UP_TOEAST : LogicFacing.UP_TOWEST;
            }
         }
         case NORTH -> {
            if (dx < dy) {
               yield hy < 0.5 ? LogicFacing.NORTH_TOUP : LogicFacing.NORTH_TODOWN;
            } else {
               yield hx < 0.5 ? LogicFacing.NORTH_TOEAST : LogicFacing.NORTH_TOWEST;
            }
         }
         case SOUTH -> {
            if (dx < dy) {
               yield hy < 0.5 ? LogicFacing.SOUTH_TOUP : LogicFacing.SOUTH_TODOWN;
            } else {
               yield hx < 0.5 ? LogicFacing.SOUTH_TOEAST : LogicFacing.SOUTH_TOWEST;
            }
         }
         case WEST -> {
            if (dy < dz) {
               yield hz < 0.5 ? LogicFacing.WEST_TOSOUTH : LogicFacing.WEST_TONORTH;
            } else {
               yield hy < 0.5 ? LogicFacing.WEST_TOUP : LogicFacing.WEST_TODOWN;
            }
         }
         case EAST -> {
            if (dy < dz) {
               yield hz < 0.5 ? LogicFacing.EAST_TOSOUTH : LogicFacing.EAST_TONORTH;
            } else {
               yield hy < 0.5 ? LogicFacing.EAST_TOUP : LogicFacing.EAST_TODOWN;
            }
         }
         default -> LogicFacing.DOWN_TOWEST;
      });
   }

   @Nonnull
   public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return switch (((LogicFacing)state.getValue(LOGIC_FACING)).getSide()) {
         case DOWN -> BLOCK_DOWN;
         case UP -> BLOCK_UP;
         case NORTH -> BLOCK_NORTH;
         case SOUTH -> BLOCK_SOUTH;
         case WEST -> BLOCK_WEST;
         case EAST -> BLOCK_EAST;
         default -> throw new MatchException(null, null);
      };
   }

   protected int getInputStrength(Level world, BlockPos pos, Direction side) {
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

   @Deprecated
   @Override
   protected void checkRedstone(Level world, BlockPos pos) {
      super.checkRedstone(world, pos);
      if (world.getBlockEntity(pos) instanceof GenericTileEntity generic) {
         Direction inputSide = LogicSupport.getFacing(world.getBlockState(pos)).getInputSide();
         int power = this.getInputStrength(world, pos, inputSide);
         generic.setPowerInput(power);
      }
   }

   public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
      BlockEntity te = world.getBlockEntity(pos);
      if (state.getBlock() instanceof LogicSlabBlock && te instanceof GenericTileEntity) {
         Direction direction = LogicSupport.getFacing(state).getInputSide();

         return switch (direction) {
            case DOWN, UP -> side == Direction.DOWN || side == Direction.UP;
            case NORTH, SOUTH -> side == Direction.NORTH || side == Direction.SOUTH;
            case WEST, EAST -> side == Direction.WEST || side == Direction.EAST;
            default -> throw new MatchException(null, null);
         };
      } else {
         return false;
      }
   }

   protected int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
      return state.getBlock() instanceof LogicSlabBlock && world.getBlockEntity(pos) instanceof GenericTileEntity generic
         ? generic.getRedstoneOutput(state, world, pos, side)
         : 0;
   }

   @Override
   public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
      if (state.getBlock() instanceof LogicSlabBlock) {
         LogicFacing facing = (LogicFacing)state.getValue(LOGIC_FACING);
         LogicFacing newfacing = LogicFacing.rotate(facing);
         BlockState newstate = (BlockState)state.getBlock().defaultBlockState().setValue(LOGIC_FACING, newfacing);
         world.setBlock(pos, newstate, 3);
         BlockEntity te = world.getBlockEntity(pos);
         if (te instanceof GenericTileEntity) {
            ((GenericTileEntity)te).rotateBlock(rot);
         }

         return newstate;
      } else {
         return state;
      }
   }

   public boolean isSignalSource(@Nonnull BlockState state) {
      return true;
   }

   public int getSignal(@Nonnull BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
      return this.getRedstoneOutput(blockState, blockAccess, pos, side);
   }

   @Override
   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LOGIC_FACING});
   }
}
