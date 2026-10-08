package mcjty.lib.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseScreen extends Screen {
   public BaseScreen(Component pTitle) {
      super(pTitle);
   }

   protected abstract void renderInternal(GuiGraphicsExtractor var1, int var2, int var3, float var4);

   public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
      this.renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
   }
}
