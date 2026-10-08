package mcjty.lib.blockcommands;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;

public record CommandInfo<T>(Class<T> type, Function<FriendlyByteBuf, T> deserializer, BiConsumer<FriendlyByteBuf, T> serializer) {
}
