package mcjty.lib.api.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemModule(GlobalPos pos, String name) {
   public static final Codec<ItemModule> ITEM_MODULE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(GlobalPos.CODEC.fieldOf("pos").forGetter(ItemModule::pos), Codec.STRING.fieldOf("name").forGetter(ItemModule::name))
         .apply(instance, ItemModule::new)
   );
   public static final StreamCodec<ByteBuf, ItemModule> ITEM_MODULE_STREAM_CODEC = StreamCodec.composite(
      GlobalPos.STREAM_CODEC, ItemModule::pos, ByteBufCodecs.STRING_UTF8, ItemModule::name, ItemModule::new
   );
}
