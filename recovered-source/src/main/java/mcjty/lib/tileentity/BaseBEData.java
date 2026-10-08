package mcjty.lib.tileentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BaseBEData(String ownerName, UUID ownerUUID, int securityChannel, RedstoneMode rsMode) {
   public static final Codec<BaseBEData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("ownerName").forGetter(BaseBEData::ownerName),
            UUIDUtil.CODEC.optionalFieldOf("ownerUUID").forGetter(d -> Optional.ofNullable(d.ownerUUID())),
            Codec.INT.fieldOf("securityChannel").forGetter(BaseBEData::securityChannel),
            RedstoneMode.CODEC.fieldOf("rsMode").forGetter(BaseBEData::rsMode)
         )
         .apply(instance, (owner, uuid, channel, redstoneMode) -> new BaseBEData(owner, (UUID)uuid.orElse(null), channel, redstoneMode))
   );
   public static final StreamCodec<FriendlyByteBuf, BaseBEData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      BaseBEData::ownerName,
      ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
      s -> Optional.ofNullable(s.ownerUUID),
      ByteBufCodecs.INT,
      BaseBEData::securityChannel,
      RedstoneMode.STREAM_CODEC,
      BaseBEData::rsMode,
      (ownerName, uuid, channel, redstoneMode) -> new BaseBEData(ownerName, (UUID)uuid.orElse(null), channel, redstoneMode)
   );

   public BaseBEData withRedstoneMode(RedstoneMode mode) {
      return new BaseBEData(this.ownerName, this.ownerUUID, this.securityChannel, mode);
   }

   public BaseBEData withOwner(UUID owner, String ownerName) {
      return new BaseBEData(ownerName, owner, this.securityChannel, this.rsMode);
   }

   public BaseBEData withSecurityChannel(int securityChannel) {
      return new BaseBEData(this.ownerName, this.ownerUUID, securityChannel, this.rsMode);
   }
}
