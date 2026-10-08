package mcjty.lib.tileentity;

import java.util.function.BiConsumer;
import java.util.function.Function;
import mcjty.lib.typed.Key;

public record ValueHolder<T extends GenericTileEntity, V>(Key<V> key, Function<T, V> getter, BiConsumer<T, V> setter) {
}
