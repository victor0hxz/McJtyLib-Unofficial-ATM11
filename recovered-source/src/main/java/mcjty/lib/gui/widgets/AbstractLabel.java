package mcjty.lib.gui.widgets;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalAlignment;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public abstract class AbstractLabel<P extends AbstractLabel<P>> extends AbstractWidget<P> {
   public static final String TYPE_LABEL = "label";
   public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGN = HorizontalAlignment.ALIGN_CENTER;
   public static final VerticalAlignment DEFAULT_VERTICAL_ALIGN = VerticalAlignment.ALIGN_CENTER;
   public static final boolean DEFAULT_DYNAMIC = false;
   private String text;
   private Integer color = null;
   private Integer disabledColor = null;
   private HorizontalAlignment horizontalAlignment = DEFAULT_HORIZONTAL_ALIGN;
   private VerticalAlignment verticalAlignment = DEFAULT_VERTICAL_ALIGN;
   private boolean dynamic = false;
   private int txtDx = 0;
   private int txtDy = 0;
   private Identifier image = null;
   private int u;
   private int v;
   private int iw;
   private int ih;

   public Identifier getImage() {
      return this.image;
   }

   public P image(Identifier image, int u, int v, int iw, int ih) {
      this.image = image;
      this.u = u;
      this.v = v;
      this.iw = iw;
      this.ih = ih;
      return this.getThis();
   }

   public boolean isDynamic() {
      return this.dynamic;
   }

   public P dynamic(boolean dynamic) {
      this.dynamic = dynamic;
      return this.getThis();
   }

   @Override
   public int getDesiredWidth() {
      int w = super.getDesiredWidth();
      if (this.dynamic) {
         return w;
      } else {
         if (w == -1) {
            w = this.mc.font.width(this.text) + 6;
         }

         return w;
      }
   }

   @Override
   public int getDesiredHeight() {
      int h = super.getDesiredHeight();
      if (this.dynamic) {
         return h;
      } else {
         if (h == -1) {
            h = 9 + 2;
         }

         return h;
      }
   }

   public String getText() {
      return this.text;
   }

   public P text(String text) {
      this.text = text;
      return this.getThis();
   }

   public P textOffset(int ox, int oy) {
      this.txtDx = ox;
      this.txtDy = oy;
      return this.getThis();
   }

   public int getColor() {
      return this.color == null ? StyleConfig.colorTextNormal : this.color;
   }

   public P color(int color) {
      this.color = color;
      return this.getThis();
   }

   public int getDisabledColor() {
      return this.disabledColor == null ? StyleConfig.colorTextDisabled : this.disabledColor;
   }

   public P disabledColor(int disabledColor) {
      this.disabledColor = disabledColor;
      return this.getThis();
   }

   public HorizontalAlignment getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public P horizontalAlignment(HorizontalAlignment horizontalAlignment) {
      this.horizontalAlignment = horizontalAlignment;
      return this.getThis();
   }

   public VerticalAlignment getVerticalAlignment() {
      return this.verticalAlignment;
   }

   public P verticalAlignment(VerticalAlignment verticalAlignment) {
      this.verticalAlignment = verticalAlignment;
      return this.getThis();
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      this.drawOffset(gui, graphics, x, y, 0, 0);
   }

   public void drawOffset(Screen gui, GuiGraphicsExtractor graphics, int x, int y, int offsetx, int offsety) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         int dx = this.calculateHorizontalOffset() + offsetx + this.txtDx;
         int dy = this.calculateVerticalOffset() + offsety + this.txtDy;
         if (this.image != null) {
            int xx = x + this.bounds.x + (this.bounds.width - this.iw) / 2;
            int yy = y + this.bounds.y + (this.bounds.height - this.ih) / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, xx, yy, this.u, this.v, this.iw, this.ih, 256, 256);
         }

         int col = this.getColor();
         if (!this.isEnabled()) {
            col = this.getDisabledColor();
         }

         if (this.text == null) {
            graphics.text(this.mc.font, "", x + dx + this.bounds.x, y + dy + this.bounds.y, col);
         } else {
            graphics.text(this.mc.font, this.mc.font.plainSubstrByWidth(this.text, this.bounds.width), x + dx + this.bounds.x, y + dy + this.bounds.y, col);
         }
      }
   }

   private int calculateVerticalOffset() {
      if (this.verticalAlignment != VerticalAlignment.ALIGN_TOP) {
         int h = 9;
         return this.verticalAlignment == VerticalAlignment.ALIGN_BOTTOM ? this.bounds.height - h : (this.bounds.height - h) / 2;
      } else {
         return 0;
      }
   }

   private int calculateHorizontalOffset() {
      if (this.horizontalAlignment != HorizontalAlignment.ALIGN_LEFT) {
         int w = this.mc.font.width(this.text);
         return this.horizontalAlignment == HorizontalAlignment.ALIGN_RIGHT ? this.bounds.width - w : (this.bounds.width - w) / 2;
      } else {
         return 0;
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.text = command.getOptionalPar(1, "");
      this.color = GuiParser.get(command, "color", null);
      this.disabledColor = GuiParser.get(command, "disabledcolor", null);
      this.horizontalAlignment = HorizontalAlignment.getByName(GuiParser.get(command, "horizalign", DEFAULT_HORIZONTAL_ALIGN.name()));
      this.verticalAlignment = VerticalAlignment.getByName(GuiParser.get(command, "vertalign", DEFAULT_VERTICAL_ALIGN.name()));
      this.dynamic = GuiParser.get(command, "dynamic", false);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.parameter(this.text);
      GuiParser.put(command, "color", this.color, null);
      GuiParser.put(command, "disabledcolor", this.disabledColor, null);
      GuiParser.put(command, "horizalign", this.horizontalAlignment.name(), DEFAULT_HORIZONTAL_ALIGN.name());
      GuiParser.put(command, "vertalign", this.verticalAlignment.name(), DEFAULT_VERTICAL_ALIGN.name());
      GuiParser.put(command, "dynamic", this.dynamic, false);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("label");
   }

   @Override
   public <T> void setGenericValue(T value) {
      if (value == null) {
         this.text("");
      } else {
         this.text(value.toString());
      }
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.getText();
   }
}
