package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum RedstoneMode implements StringRepresentable {
   REDSTONE_IGNORED("Ignored"),
   REDSTONE_OFFREQUIRED("Off"),
   REDSTONE_ONREQUIRED("On");

   private static final Map<String, RedstoneMode> modeToMode = new HashMap<>();
   public static final Codec<RedstoneMode> CODEC = StringRepresentable.fromEnum(RedstoneMode::values);
   public static final StreamCodec<FriendlyByteBuf, RedstoneMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(RedstoneMode.class);
   private final String description;

   private RedstoneMode(String description) {
      this.description = description;
   }

   public String getDescription() {
      return this.description;
   }

   public static RedstoneMode getMode(String mode) {
      return modeToMode.get(mode);
   }

   public String getSerializedName() {
      return this.getDescription();
   }

   static {
      for (RedstoneMode mode : values()) {
         modeToMode.put(mode.description, mode);
      }
   }
}
