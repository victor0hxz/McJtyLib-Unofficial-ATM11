package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyBar extends AbstractWidget<EnergyBar> {
   public static final String TYPE_ENERGYBAR = "energybar";
   public static final boolean DEFAULT_HORIZONTAL = false;
   public static final boolean DEFAULT_SHOWTEXT = true;
   public static final boolean DEFAULT_SHOWRFPERTICK = false;
   private long value;
   private long maxValue;
   private Integer energyOnColor = null;
   private Integer energyOffColor = null;
   private Integer spacerColor = null;
   private Integer textColor = null;
   private boolean horizontal = false;
   private IEnergyStorage handler = null;
   private boolean showText = true;
   private boolean showRfPerTick = false;
   private long rfPerTick = 0L;

   public EnergyBar horizontal() {
      this.horizontal = true;
      return this;
   }

   public boolean isHorizontal() {
      return this.horizontal;
   }

   public EnergyBar handler(IEnergyStorage handler) {
      this.handler = handler;
      return this;
   }

   public IEnergyStorage getHandler() {
      return this.handler;
   }

   public EnergyBar vertical() {
      this.horizontal = false;
      return this;
   }

   public boolean isVertical() {
      return !this.horizontal;
   }

   @Override
   public List<String> getTooltips() {
      if (this.tooltips == null) {
         String s = this.getValue() + " / " + this.getMaxValue();
         List<String> tt = new ArrayList<>();
         tt.add(s);
         return tt;
      } else {
         return this.tooltips;
      }
   }

   public boolean isShowText() {
      return this.showText;
   }

   public EnergyBar showText(boolean showText) {
      this.showText = showText;
      return this;
   }

   public boolean isShowRfPerTick() {
      return this.showRfPerTick;
   }

   public EnergyBar showRfPerTick(boolean showRfPerTick) {
      this.showRfPerTick = showRfPerTick;
      return this;
   }

   public long getRfPerTick() {
      return this.rfPerTick;
   }

   public EnergyBar rfPerTick(long rfPerTick) {
      this.rfPerTick = rfPerTick;
      return this;
   }

   public long getValue() {
      return this.handler != null ? this.handler.getEnergyStored() : this.value;
   }

   public EnergyBar value(long value) {
      this.value = value;
      return this;
   }

   public long getMaxValue() {
      return this.handler != null ? this.handler.getMaxEnergyStored() : this.maxValue;
   }

   public EnergyBar maxValue(long maxValue) {
      this.maxValue = maxValue;
      return this;
   }

   public int getEnergyOnColor() {
      return this.energyOnColor == null ? StyleConfig.colorEnergyBarHighEnergy : this.energyOnColor;
   }

   public EnergyBar setEnergyOnColor(int energyOnColor) {
      this.energyOnColor = energyOnColor;
      return this;
   }

   public int getEnergyOffColor() {
      return this.energyOffColor == null ? StyleConfig.colorEnergyBarLowEnergy : this.energyOffColor;
   }

   public EnergyBar setEnergyOffColor(int leftColor) {
      this.energyOffColor = leftColor;
      return this;
   }

   public int getSpacerColor() {
      return this.spacerColor == null ? StyleConfig.colorEnergyBarSpacer : this.spacerColor;
   }

   public EnergyBar setSpacerColor(int rightColor) {
      this.spacerColor = rightColor;
      return this;
   }

   public int getTextColor() {
      return this.textColor == null ? StyleConfig.colorEnergyBarText : this.textColor;
   }

   public EnergyBar setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         int bx = x + this.bounds.x;
         int by = y + this.bounds.y;
         RenderHelper.drawThickBeveledBox(
            graphics,
            bx,
            by,
            bx + this.bounds.width - 1,
            by + this.bounds.height - 1,
            1,
            StyleConfig.colorEnergyBarTopLeft,
            StyleConfig.colorEnergyBarBottomRight,
            -10263709
         );
         long currentValue = this.getValue();
         long maximum = this.getMaxValue();
         if (maximum > 0L) {
            boolean on = false;
            if (this.horizontal) {
               int w = (int)((double)(this.bounds.width - 2) * currentValue / maximum);

               for (int xx = bx + 1; xx < bx + this.bounds.width - 2; xx++) {
                  int color = this.getColor(bx, w, on, xx);
                  RenderHelper.drawVerticalLine(graphics, xx, by + 1, by + this.bounds.height - 2, color);
                  on = !on;
               }
            } else {
               int h = (int)((double)(this.bounds.height - 2) * currentValue / maximum);

               for (int yy = y + 1; yy < y + this.bounds.height - 2; yy++) {
                  int color = this.getColorReversed(y, h, on, yy);
                  RenderHelper.drawHorizontalLine(graphics, bx + 1, y + by + this.bounds.height - yy - 2, bx + this.bounds.width - 2, color);
                  on = !on;
               }
            }
         }

         if (this.showText) {
            String s;
            if (this.showRfPerTick) {
               s = this.rfPerTick + "RF/t";
            } else {
               s = currentValue + "/" + maximum;
            }

            graphics.text(
               this.mc.font,
               this.mc.font.plainSubstrByWidth(s, this.getBounds().width),
               x + this.bounds.x + 5,
               y + this.bounds.y + (this.bounds.height - 9) / 2,
               this.getTextColor()
            );
         }
      }
   }

   private int getColor(int pos, int total, boolean on, int cur) {
      int color;
      if (on) {
         if (cur < pos + total) {
            color = this.getEnergyOnColor();
         } else {
            color = this.getEnergyOffColor();
         }
      } else {
         color = this.getSpacerColor();
      }

      return color;
   }

   private int getColorReversed(int pos, int total, boolean on, int cur) {
      int color;
      if (on) {
         if (cur < pos + total) {
            color = this.getEnergyOnColor();
         } else {
            color = this.getEnergyOffColor();
         }
      } else {
         color = this.getSpacerColor();
      }

      return color;
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.energyOnColor = GuiParser.get(command, "energyoncolor", null);
      this.energyOffColor = GuiParser.get(command, "energyoffcolor", null);
      this.spacerColor = GuiParser.get(command, "spacercolor", null);
      this.textColor = GuiParser.get(command, "textcolor", null);
      this.horizontal = GuiParser.get(command, "horizontal", false);
      this.showText = GuiParser.get(command, "showtext", true);
      this.showRfPerTick = GuiParser.get(command, "showrfpertick", false);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "energyoncolor", this.energyOnColor, null);
      GuiParser.put(command, "energyoffcolor", this.energyOffColor, null);
      GuiParser.put(command, "spacercolor", this.spacerColor, null);
      GuiParser.put(command, "textcolor", this.textColor, null);
      GuiParser.put(command, "horizontal", this.horizontal, false);
      GuiParser.put(command, "showtext", this.showText, true);
      GuiParser.put(command, "showrfpertick", this.showRfPerTick, false);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("energybar");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
