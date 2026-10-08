package mcjty.lib.crafting;

import java.util.Collection;
import net.minecraft.core.component.DataComponentType;

public interface IComponentsToPreserve {
   Collection<DataComponentType<?>> getComponentsToPreserve();
}
