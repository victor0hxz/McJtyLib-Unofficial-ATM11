package mcjty.lib.compat.theoneprobe;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class McJtyLibTOPDriver implements TOPDriver {
   public static final McJtyLibTOPDriver DRIVER = new McJtyLibTOPDriver();

   @Override
   public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
      this.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
   }

   public void addStandardProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
      if (mode == ProbeMode.EXTENDED && world.getBlockEntity(data.getPos()) instanceof GenericTileEntity generic) {
         IInfusable h = (IInfusable)world.getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY, data.getPos(), null);
         if (h != null) {
            int infused = h.getInfused();
            int pct = infused * 100 / (Integer)GeneralConfig.maxInfuse.get();
            probeInfo.text(CompoundText.create().style(TextStyleClass.HIGHLIGHTED).text("Infused: " + pct + "%"));
         }

         if ((Boolean)GeneralConfig.manageOwnership.get() && generic.getOwnerName() != null && !generic.getOwnerName().isEmpty()) {
            int securityChannel = generic.getSecurityChannel();
            if (securityChannel == -1) {
               probeInfo.text(CompoundText.create().style(TextStyleClass.HIGHLIGHTED).text("Owned by: " + generic.getOwnerName()));
            } else {
               probeInfo.text(
                  CompoundText.create().style(TextStyleClass.HIGHLIGHTED).text("Owned by: " + generic.getOwnerName() + " (channel " + securityChannel + ")")
               );
            }

            if (generic.getOwnerUUID() == null) {
               probeInfo.text(CompoundText.create().style(TextStyleClass.ERROR).text("Warning! Ownership not correctly set! Please place block again!"));
            }
         }
      }
   }
}
