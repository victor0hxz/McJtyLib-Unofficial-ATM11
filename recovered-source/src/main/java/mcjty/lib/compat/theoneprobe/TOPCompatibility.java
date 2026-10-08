package mcjty.lib.compat.theoneprobe;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import mcjty.lib.varia.Logging;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.InterModComms;

public class TOPCompatibility {
   private static boolean registered;

   public static void register() {
      if (!registered) {
         registered = true;
         InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPCompatibility.GetTheOneProbe::new);
      }
   }

   public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {
      public static ITheOneProbe probe;

      @Nullable
      public Void apply(ITheOneProbe theOneProbe) {
         probe = theOneProbe;
         Logging.log("Enabled support for The One Probe");
         probe.registerProvider(new IProbeInfoProvider() {
            {
               Objects.requireNonNull(GetTheOneProbe.this);
            }

            public Identifier getID() {
               return Identifier.parse("mcjtylib:default");
            }

            public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
               if (blockState.getBlock() instanceof TOPInfoProvider) {
                  TOPInfoProvider provider = (TOPInfoProvider)blockState.getBlock();
                  TOPDriver driver = provider.getProbeDriver();
                  if (driver != null) {
                     driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
                  }
               }
            }
         });
         return null;
      }
   }
}
