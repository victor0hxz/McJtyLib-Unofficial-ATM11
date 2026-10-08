package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.FloatEnterEvent;
import mcjty.lib.gui.events.FloatEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class FloatField extends AbstractWidget<FloatField> {
   public static final String TYPE_FLOATFIELD = "floatfield";
   public static final Key<Float> PARAM_FLOAT = new Key<>("float", Type.FLOAT);
   public static final boolean DEFAULT_EDITABLE = true;
   private String text = "";
   private int cursor = 0;
   private int startOffset = 0;
   private int selection = -1;
   private List<FloatEvent> floatEvents = null;
   private List<FloatEnterEvent> floatEnterEvents = null;
   private boolean editable = true;
   private final DecimalFormat fmt = new DecimalFormat("#.#");
   private static final Pattern COMPILE = Pattern.compile(",", 16);

   public boolean isEditable() {
      return this.editable;
   }

   private static float safeFloat(String f) {
      try {
         f = COMPILE.matcher(f).replaceAll(Matcher.quoteReplacement("."));
         return Float.parseFloat(f);
      } catch (NumberFormatException var2) {
         return 0.0F;
      }
   }

   public FloatField editable(boolean editable) {
      this.editable = editable;
      return this;
   }

   public float getFloat() {
      try {
         return safeFloat(this.text);
      } catch (NumberFormatException var2) {
         return 0.0F;
      }
   }

   public FloatField value(float value) {
      if (this.getFloat() == value) {
         return this;
      } else {
         this.text = this.fmt.format(value);
         if (this.cursor > this.text.length()) {
            this.cursor = this.text.length();
         }

         if (this.startOffset >= this.cursor) {
            this.startOffset = this.cursor - 1;
            if (this.startOffset < 0) {
               this.startOffset = 0;
            }
         }

         return this;
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible() && this.editable) {
         this.window.setTextFocus(this);
         if (button == 1) {
            this.value(0.0F);
            this.fireFloatEvents(0.0F);
         }

         return this;
      } else {
         return null;
      }
   }

   private static boolean isControlDown() {
      Window handle = Minecraft.getInstance().getWindow();
      return InputConstants.isKeyDown(handle, 341) || InputConstants.isKeyDown(handle, 345);
   }

   @Override
   public boolean keyTyped(int keyCode, int scanCode) {
      boolean rc = super.keyTyped(keyCode, scanCode);
      if (rc) {
         return true;
      } else if (this.isEnabledAndVisible() && this.editable) {
         Window handle = Minecraft.getInstance().getWindow();
         if (isControlDown()) {
            if (keyCode == 86) {
               String data = GLFW.glfwGetClipboardString(handle.handle());
               if (data != null) {
                  if (this.isRegionSelected()) {
                     this.replaceSelectedRegion(data);
                  } else {
                     this.text = this.text.substring(0, this.cursor) + data + this.text.substring(this.cursor);
                  }

                  this.cursor = this.cursor + data.length();
                  this.fireFloatEvents(this.getFloat());
               }
            } else if (keyCode == 67) {
               if (this.isRegionSelected()) {
                  GLFW.glfwSetClipboardString(handle.handle(), this.getSelectedText());
               }
            } else if (keyCode == 88) {
               if (this.isRegionSelected()) {
                  GLFW.glfwSetClipboardString(handle.handle(), this.getSelectedText());
                  this.replaceSelectedRegion("");
                  this.fireFloatEvents(this.getFloat());
               }
            } else if (keyCode == 65) {
               this.selectAll();
            } else if (keyCode == 263) {
               this.updateSelection();
               if (this.cursor > 0) {
                  this.cursor = this.findNextWord(true);
               }
            } else if (keyCode == 262) {
               this.updateSelection();
               if (this.cursor < this.text.length()) {
                  this.cursor = this.findNextWord(false);
               }
            }
         } else {
            if (keyCode == 257) {
               this.fireIntegerEnterEvents(this.getFloat());
               return false;
            }

            if (keyCode == 256) {
               return false;
            }

            if (keyCode == 259) {
               if (this.isRegionSelected()) {
                  this.replaceSelectedRegion("");
                  this.fireFloatEvents(this.getFloat());
               } else if (!this.text.isEmpty() && this.cursor > 0) {
                  this.text = this.text.substring(0, this.cursor - 1) + this.text.substring(this.cursor);
                  this.cursor--;
                  this.fireFloatEvents(this.getFloat());
               }
            } else if (keyCode == 261) {
               if (this.isRegionSelected()) {
                  this.replaceSelectedRegion("");
                  this.fireFloatEvents(this.getFloat());
               } else if (this.cursor < this.text.length()) {
                  this.text = this.text.substring(0, this.cursor) + this.text.substring(this.cursor + 1);
                  this.fireFloatEvents(this.getFloat());
               }
            } else if (keyCode == 268) {
               this.updateSelection();
               this.cursor = 0;
            } else if (keyCode == 269) {
               this.updateSelection();
               this.cursor = this.text.length();
            } else if (keyCode == 263) {
               this.updateSelection();
               if (this.cursor > 0) {
                  this.cursor--;
               }
            } else if (keyCode == 262) {
               this.updateSelection();
               if (this.cursor < this.text.length()) {
                  this.cursor++;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char codePoint) {
      if (this.isEnabledAndVisible() && this.editable) {
         if (this.isRegionSelected()) {
            this.replaceSelectedRegion(Character.toString(codePoint));
            this.cursor++;
            this.fireFloatEvents(this.getFloat());
            return true;
         }

         if (Character.isDigit(codePoint) || codePoint == '-' || codePoint == '.' || codePoint == ',') {
            this.text = this.text.substring(0, this.cursor) + codePoint + this.text.substring(this.cursor);
            this.cursor++;
            this.fireFloatEvents(this.getFloat());
            return true;
         }
      }

      return false;
   }

   private int calculateVerticalOffset() {
      int h = 9;
      return (this.bounds.height - h) / 2;
   }

   private void ensureVisible() {
      if (this.cursor > this.text.length()) {
         this.cursor = this.text.length();
      }

      if (this.cursor < this.startOffset) {
         this.startOffset = this.cursor;
      } else {
         for (int w = this.mc.font.width(this.text.substring(this.startOffset, this.cursor));
            w > this.bounds.width - 12;
            w = this.mc.font.width(this.text.substring(this.startOffset, this.cursor))
         ) {
            this.startOffset++;
         }
      }
   }

   public void selectAll() {
      this.setSelection(0, this.text.length());
   }

   public void setSelection(int start, int end) {
      this.selection = start;
      this.cursor = end;
   }

   public void clearSelection() {
      this.selection = -1;
   }

   public boolean isRegionSelected() {
      return this.selection != -1;
   }

   public int getSelectionStart() {
      return Math.min(this.cursor, this.selection);
   }

   public int getSelectionEnd() {
      return Math.max(this.cursor, this.selection);
   }

   public String getSelectedText() {
      return this.text.substring(this.getSelectionStart(), this.getSelectionEnd());
   }

   public void replaceSelectedRegion(String replacement) {
      int selectionStart = this.getSelectionStart();
      this.text = this.text.substring(0, selectionStart) + replacement + this.text.substring(this.getSelectionEnd());
      this.cursor = selectionStart;
      this.clearSelection();
   }

   private void updateSelection() {
      if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344)) {
         this.clearSelection();
      } else if (!this.isRegionSelected()) {
         this.selection = this.cursor;
      }
   }

   private int findNextWord(boolean reversed) {
      int change = reversed ? -1 : 1;
      int i = this.cursor;
      char last = ' ';

      while (true) {
         i += change;
         if (i < 0 || i >= this.text.length()) {
            break;
         }

         char c = this.text.charAt(i);
         if (c == ' ' && last != ' ') {
            break;
         }

         last = c;
      }

      return reversed ? i - change : i;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      super.draw(gui, graphics, x, y);
      int xx = x + this.bounds.x;
      int yy = y + this.bounds.y;
      this.ensureVisible();
      int col = StyleConfig.colorTextFieldFiller;
      if (this.window.getTextFocus() == this) {
         col = StyleConfig.colorTextFieldFocusedFiller;
      } else if (this.isHovering()) {
         col = StyleConfig.colorTextFieldHoveringFiller;
      }

      RenderHelper.drawThickBeveledBox(
         graphics,
         xx,
         yy,
         xx + this.bounds.width - 1,
         yy + this.bounds.height - 1,
         1,
         StyleConfig.colorTextFieldTopLeft,
         StyleConfig.colorTextFieldBottomRight,
         col
      );
      String renderedText = this.mc.font.plainSubstrByWidth(this.text.substring(this.startOffset), this.bounds.width - 10);
      int textX = x + 5 + this.bounds.x;
      int textY = y + this.calculateVerticalOffset() + this.bounds.y;
      if (this.isEnabled()) {
         if (this.isEditable()) {
            graphics.text(this.mc.font, renderedText, textX, textY, -16777216);
         } else {
            graphics.text(this.mc.font, renderedText, textX, textY, -13421773);
         }

         if (this.isRegionSelected()) {
            int selectionStart = this.getSelectionStart();
            int selectionEnd = this.getSelectionEnd();
            int renderedStart = Mth.clamp(selectionStart - this.startOffset, 0, renderedText.length());
            int renderedEnd = Mth.clamp(selectionEnd - this.startOffset, 0, renderedText.length());
            String renderedSelection = renderedText.substring(renderedStart, renderedEnd);
            String renderedPreSelection = renderedText.substring(0, renderedStart);
            int selectionX = textX + this.mc.font.width(renderedPreSelection);
            int selectionWidth = this.mc.font.width(renderedSelection);
            RenderHelper.drawColorLogic(selectionX - 1, textY, selectionWidth + 1, 9, 60, 147, 242, null);
         }
      } else {
         graphics.text(this.mc.font, renderedText, textX, textY, -6250336);
      }

      if (this.window.getTextFocus() == this) {
         int w = this.mc.font.width(this.text.substring(this.startOffset, this.cursor));
         graphics.fill(xx + 5 + w, yy + 2, xx + 5 + w + 1, yy + this.bounds.height - 3, StyleConfig.colorTextFieldCursor);
      }
   }

   public FloatField event(FloatEvent event) {
      if (this.floatEvents == null) {
         this.floatEvents = new ArrayList<>();
      }

      this.floatEvents.add(event);
      return this;
   }

   public void removeFloatEvent(FloatEvent event) {
      if (this.floatEvents != null) {
         this.floatEvents.remove(event);
      }
   }

   private void fireFloatEvents(float newValue) {
      this.fireChannelEvents(TypedMap.builder().put(mcjty.lib.gui.Window.PARAM_ID, "integer").put(PARAM_FLOAT, newValue).build());
      if (this.floatEvents != null) {
         for (FloatEvent event : this.floatEvents) {
            event.floatChanged(newValue);
         }
      }
   }

   public FloatField addFloatEnterEvent(FloatEnterEvent event) {
      if (this.floatEnterEvents == null) {
         this.floatEnterEvents = new ArrayList<>();
      }

      this.floatEnterEvents.add(event);
      return this;
   }

   public void removeFloatEnterEvent(FloatEnterEvent event) {
      if (this.floatEnterEvents != null) {
         this.floatEnterEvents.remove(event);
      }
   }

   private void fireIntegerEnterEvents(float newValue) {
      this.fireChannelEvents(TypedMap.builder().put(mcjty.lib.gui.Window.PARAM_ID, "enter").put(PARAM_FLOAT, newValue).build());
      if (this.floatEnterEvents != null) {
         for (FloatEnterEvent event : this.floatEnterEvents) {
            event.floatEntered(newValue);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.editable = GuiParser.get(command, "editable", true);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "editable", this.editable, true);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("floatfield");
   }

   @Override
   public <T> void setGenericValue(T value) {
      if (value == null) {
         this.value(0.0F);
      } else {
         try {
            this.value(safeFloat(value.toString()));
         } catch (NumberFormatException var3) {
            this.value(0.0F);
         }
      }
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      if (Type.INTEGER.equals(type)) {
         return this.getFloat();
      } else if (Type.STRING.equals(type)) {
         return this.text;
      } else if (Type.DOUBLE.equals(type)) {
         try {
            return Double.parseDouble(this.text);
         } catch (NumberFormatException var3) {
            return 0.0;
         }
      } else if (Type.FLOAT.equals(type)) {
         try {
            return safeFloat(this.text);
         } catch (NumberFormatException var4) {
            return 0.0F;
         }
      } else {
         return this.getFloat();
      }
   }
}
