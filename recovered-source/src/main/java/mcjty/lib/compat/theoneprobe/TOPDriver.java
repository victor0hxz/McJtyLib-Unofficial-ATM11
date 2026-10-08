package mcjty.lib.compat.theoneprobe;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface TOPDriver {
   void addProbeInfo(ProbeMode var1, IProbeInfo var2, Player var3, Level var4, BlockState var5, IProbeHitData var6);
}
