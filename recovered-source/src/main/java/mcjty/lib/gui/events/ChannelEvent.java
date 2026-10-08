package mcjty.lib.gui.events;

import javax.annotation.Nonnull;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.typed.TypedMap;

public interface ChannelEvent {
   void fire(@Nonnull Widget<?> var1, @Nonnull TypedMap var2);
}
