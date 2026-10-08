package mcjty.lib.typed;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import mcjty.lib.network.TypedMapTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedMap(Map<Key<?>, Object> map) {
   public static final TypedMap EMPTY = builder().build();
   public static final StreamCodec<RegistryFriendlyByteBuf, TypedMap> STREAM_CODEC = StreamCodec.of(TypedMapTools::writeArguments, TypedMapTools::readArguments);

   public Set<Key<?>> getKeys() {
      return this.map.keySet();
   }

   public int size() {
      return this.map.size();
   }

   public <V> V get(@Nonnull Key<V> key) {
      return (V)this.map.get(key);
   }

   public <V> Optional<V> getOptional(@Nonnull Key<V> key) {
      return Optional.ofNullable((V)this.map.get(key));
   }

   public static TypedMap.Builder builder() {
      return new TypedMap.Builder();
   }

   public static class Builder {
      private final Map<Key<?>, Object> map = new HashMap<>();

      Builder() {
      }

      public <V> TypedMap.Builder put(@Nonnull Key<V> key, V value) {
         this.map.put(key, value);
         return this;
      }

      public TypedMap build() {
         return new TypedMap(this.map);
      }
   }
}
