package mcjty.lib.gui.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.TextEnterEvent;
import mcjty.lib.gui.events.TextEvent;
import mcjty.lib.gui.events.TextSpecialKeyEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class TextField extends AbstractWidget<TextField> {
   public static final String TYPE_TEXTFIELD = "textfield";
   public static final Key<String> PARAM_TEXT = new Key<>("text", Type.STRING);
   public static final boolean DEFAULT_EDITABLE = true;
   private String text = "";
   private int cursor = 0;
   private int startOffset = 0;
   private int selection = -1;
   private List<TextEvent> textEvents = null;
   private List<TextEnterEvent> textEnterEvents = null;
   private List<TextSpecialKeyEvent> textSpecialKeyEvents = null;
   private boolean editable = true;

   public boolean isEditable() {
      return this.editable;
   }

   public TextField editable(boolean editable) {
      this.editable = editable;
      return this;
   }

   public String getText() {
      return this.text;
   }

   public TextField text(String text) {
      if (Objects.equals(this.text, text)) {
         return this;
      } else {
         this.text = text;
         this.cursor = text.length();
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
            this.text("");
            this.fireTextEvents("");
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
                  this.fireTextEvents(this.text);
               }
            } else if (keyCode == 67) {
               if (this.isRegionSelected()) {
                  GLFW.glfwSetClipboardString(handle.handle(), this.getSelectedText());
               }
            } else if (keyCode == 88) {
               if (this.isRegionSelected()) {
                  GLFW.glfwSetClipboardString(handle.handle(), this.getSelectedText());
                  this.replaceSelectedRegion("");
                  this.fireTextEvents(this.text);
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
               this.fireTextEnterEvents(this.text);
               return false;
            }

            if (keyCode == 256) {
               this.fireTextEnterEvents(this.text);
               return false;
            }

            if (keyCode == 259) {
               if (this.isRegionSelected()) {
                  this.replaceSelectedRegion("");
                  this.fireTextEvents(this.text);
               } else if (!this.text.isEmpty() && this.cursor > 0) {
                  this.text = this.text.substring(0, this.cursor - 1) + this.text.substring(this.cursor);
                  this.cursor--;
                  this.fireTextEvents(this.text);
               }
            } else if (keyCode == 261) {
               if (this.isRegionSelected()) {
                  this.replaceSelectedRegion("");
                  this.fireTextEvents(this.text);
               } else if (this.cursor < this.text.length()) {
                  this.text = this.text.substring(0, this.cursor) + this.text.substring(this.cursor + 1);
                  this.fireTextEvents(this.text);
               }
            } else if (keyCode == 268) {
               this.updateSelection();
               this.cursor = 0;
            } else if (keyCode == 269) {
               this.updateSelection();
               this.cursor = this.text.length();
            } else if (keyCode == 264) {
               this.fireArrowDownEvents();
            } else if (keyCode == 265) {
               this.fireArrowUpEvents();
            } else if (keyCode == 258) {
               this.fireTabEvents();
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
         } else {
            this.text = this.text.substring(0, this.cursor) + codePoint + this.text.substring(this.cursor);
         }

         this.cursor++;
         this.fireTextEvents(this.text);
         return true;
      } else {
         return false;
      }
   }

   private int calculateVerticalOffset() {
      int h = 9;
      return (this.bounds.height - h) / 2;
   }

   private void ensureVisible() {
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

   public TextField event(TextEvent event) {
      if (this.textEvents == null) {
         this.textEvents = new ArrayList<>();
      }

      this.textEvents.add(event);
      return this;
   }

   public void removeTextEvent(TextEvent event) {
      if (this.textEvents != null) {
         this.textEvents.remove(event);
      }
   }

   private void fireTextEvents(String newText) {
      this.fireChannelEvents(TypedMap.builder().put(mcjty.lib.gui.Window.PARAM_ID, "text").put(PARAM_TEXT, newText).build());
      if (this.textEvents != null) {
         for (TextEvent event : this.textEvents) {
            event.textChanged(newText);
         }
      }
   }

   public TextField specialKeyEvent(TextSpecialKeyEvent event) {
      if (this.textSpecialKeyEvents == null) {
         this.textSpecialKeyEvents = new ArrayList<>();
      }

      this.textSpecialKeyEvents.add(event);
      return this;
   }

   private void fireArrowUpEvents() {
      this.fireChannelEvents("arrowup");
      if (this.textSpecialKeyEvents != null) {
         for (TextSpecialKeyEvent event : this.textSpecialKeyEvents) {
            event.arrowUp();
         }
      }
   }

   private void fireArrowDownEvents() {
      this.fireChannelEvents("arrowdown");
      if (this.textSpecialKeyEvents != null) {
         for (TextSpecialKeyEvent event : this.textSpecialKeyEvents) {
            event.arrowDown();
         }
      }
   }

   private void fireTabEvents() {
      this.fireChannelEvents("tab");
      if (this.textSpecialKeyEvents != null) {
         for (TextSpecialKeyEvent event : this.textSpecialKeyEvents) {
            event.tab();
         }
      }
   }

   public TextField addTextEnterEvent(TextEnterEvent event) {
      if (this.textEnterEvents == null) {
         this.textEnterEvents = new ArrayList<>();
      }

      this.textEnterEvents.add(event);
      return this;
   }

   public void removeTextEnterEvent(TextEnterEvent event) {
      if (this.textEnterEvents != null) {
         this.textEnterEvents.remove(event);
      }
   }

   private void fireTextEnterEvents(String newText) {
      this.fireChannelEvents(TypedMap.builder().put(mcjty.lib.gui.Window.PARAM_ID, "enter").put(PARAM_TEXT, newText).build());
      if (this.textEnterEvents != null) {
         for (TextEnterEvent event : this.textEnterEvents) {
            event.textEntered(newText);
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
      return new GuiParser.GuiCommand("textfield");
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
      if (Type.INTEGER.equals(type)) {
         try {
            return Integer.parseInt(this.getText());
         } catch (NumberFormatException var3) {
            return 0;
         }
      } else if (Type.DOUBLE.equals(type)) {
         try {
            return Double.parseDouble(this.getText());
         } catch (NumberFormatException var4) {
            return 0.0;
         }
      } else if (Type.FLOAT.equals(type)) {
         try {
            return Float.parseFloat(this.getText());
         } catch (NumberFormatException var5) {
            return 0.0F;
         }
      } else {
         return this.getText();
      }
   }
}
