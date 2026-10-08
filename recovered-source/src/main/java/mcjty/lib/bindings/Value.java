package mcjty.lib.bindings;

import java.util.function.BiConsumer;
import java.util.function.Function;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.NamedEnum;

public record Value<T extends GenericTileEntity, V>(Key<V> key, Function<T, V> supplier, BiConsumer<T, V> consumer) {
   public static <TT extends GenericTileEntity, VV> Value<TT, VV> create(String name, Type<VV> type, Function<TT, VV> supplier, BiConsumer<TT, VV> consumer) {
      return new Value<>(new Key<>(name, type), supplier, consumer);
   }

   public static <TT extends GenericTileEntity, E extends NamedEnum<E>> Value<TT, String> createEnum(
      String name, E[] values, Function<TT, E> supplier, BiConsumer<TT, E> consumer
   ) {
      return create(name, Type.STRING, te -> supplier.apply(te).getName(), (te, v) -> consumer.accept(te, NamedEnum.getEnumByName(v, values)));
   }
}
