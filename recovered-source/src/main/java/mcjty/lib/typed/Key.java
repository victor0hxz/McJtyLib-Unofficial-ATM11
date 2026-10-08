package mcjty.lib.typed;

import javax.annotation.Nonnull;

public record Key<T>(@Nonnull String name, @Nonnull Type<T> type) {
}
