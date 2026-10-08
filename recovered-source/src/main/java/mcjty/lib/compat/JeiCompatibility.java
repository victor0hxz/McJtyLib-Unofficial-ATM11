package mcjty.lib.compat;

import java.util.List;
import javax.annotation.Nonnull;
import mcjty.lib.gui.GenericGuiContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {
   @Nonnull
   public Identifier getPluginUid() {
      return Identifier.fromNamespaceAndPath("mcjtylib", "mcjtylib");
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addGenericGuiContainerHandler(GenericGuiContainer.class, new JeiCompatibility.Handler());
   }

   static class Handler<T extends AbstractContainerMenu> implements IGuiContainerHandler<GenericGuiContainer<?, T>> {
      @Nonnull
      public List<Rect2i> getGuiExtraAreas(GenericGuiContainer containerScreen) {
         return containerScreen.getExtraWindowBounds();
      }
   }
}
