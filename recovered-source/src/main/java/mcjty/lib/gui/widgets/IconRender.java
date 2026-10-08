package mcjty.lib.gui.widgets;

import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconRender extends AbstractWidget<IconRender> {
   public static final String TYPE_ICONRENDER = "iconrender";
   private TextureAtlasSprite icon = null;

   public IconRender() {
      this.desiredHeight(16);
      this.desiredWidth(16);
   }

   public TextureAtlasSprite getIcon() {
      return this.icon;
   }

   public IconRender icon(TextureAtlasSprite icon) {
      this.icon = icon;
      return this;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         if (this.icon != null) {
            RenderHelper.renderObject(graphics, x + this.bounds.x, y + this.bounds.y, this.icon, false);
         }
      }
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("iconrender");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
