package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.BlockRenderEvent;
import mcjty.lib.gui.events.ItemStackDraggedEvent;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.ItemStackTools;
import mcjty.lib.varia.Tools;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;

public class BlockRender extends AbstractWidget<BlockRender> {
   public static final String TYPE_BLOCKRENDER = "blockrender";
   public static final int DEFAULT_OFFSET = 0;
   public static final boolean DEFAULT_HILIGHT_ON_HOVER = false;
   public static final boolean DEFAULT_SHOW_LABEL = false;
   private Object renderItem = null;
   private int offsetX = 0;
   private int offsetY = 0;
   private long prevTime = -1L;
   private boolean hilightOnHover = false;
   private boolean showLabel = false;
   private Integer labelColor = null;
   private List<BlockRenderEvent> selectionEvents = null;
   private List<ItemStackDraggedEvent> draggedEvents = null;

   public Object getRenderItem() {
      return this.renderItem;
   }

   public BlockRender renderItem(Object renderItem) {
      this.renderItem = renderItem;
      return this;
   }

   public BlockRender() {
      this.desiredHeight(16);
      this.desiredWidth(16);
   }

   public boolean isShowLabel() {
      return this.showLabel;
   }

   public BlockRender showLabel(boolean showLabel) {
      this.showLabel = showLabel;
      return this;
   }

   public int getLabelColor() {
      return this.labelColor == null ? StyleConfig.colorTextNormal : this.labelColor;
   }

   public BlockRender labelColor(int labelColor) {
      this.labelColor = labelColor;
      return this;
   }

   public int getOffsetX() {
      return this.offsetX;
   }

   public BlockRender offsetX(int offsetX) {
      this.offsetX = offsetX;
      return this;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   public BlockRender offsetY(int offsetY) {
      this.offsetY = offsetY;
      return this;
   }

   public boolean isHilightOnHover() {
      return this.hilightOnHover;
   }

   public BlockRender hilightOnHover(boolean hilightOnHover) {
      this.hilightOnHover = hilightOnHover;
      return this;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         if (this.showLabel) {
            this.drawBackground(gui, graphics, x, y, this.bounds.height, this.bounds.height);
         } else {
            super.draw(gui, graphics, x, y);
         }

         if (this.renderItem != null) {
            int xx = x + this.bounds.x + this.offsetX;
            int yy = y + this.bounds.y + this.offsetY;
            RenderHelper.renderObject(graphics, xx, yy, this.renderItem, false);
            if (this.hilightOnHover && this.isHovering()) {
               RenderHelper.drawVerticalGradientRect(xx, yy, xx + 16, yy + 16, -2130706433, -2130706433);
            }

            if (this.showLabel) {
               String name;
               if (this.renderItem instanceof ItemStack) {
                  name = ((ItemStack)this.renderItem).getHoverName().getString();
               } else if (this.renderItem instanceof FluidStack) {
                  name = ((FluidStack)this.renderItem).getHoverName().getString();
               } else if (this.renderItem instanceof Item) {
                  name = new ItemStack((Item)this.renderItem).getHoverName().getString();
               } else if (this.renderItem instanceof Block) {
                  name = new ItemStack((Block)this.renderItem).getHoverName().getString();
               } else {
                  name = "";
               }

               int h = 9;
               int dy = (this.bounds.height - h) / 2;
               graphics.text(this.mc.font, name, xx + 20, yy + dy, this.getLabelColor());
            }
         }
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.fireSelectionEvents();
         long t = System.currentTimeMillis();
         if (this.prevTime != -1L && t - this.prevTime < 250L) {
            this.fireDoubleClickEvent();
         }

         this.prevTime = t;
         return this;
      } else {
         return null;
      }
   }

   public BlockRender event(BlockRenderEvent event) {
      if (this.selectionEvents == null) {
         this.selectionEvents = new ArrayList<>();
      }

      this.selectionEvents.add(event);
      return this;
   }

   public BlockRender event(ItemStackDraggedEvent event) {
      if (this.draggedEvents == null) {
         this.draggedEvents = new ArrayList<>();
      }

      this.draggedEvents.add(event);
      return this;
   }

   public void removeSelectionEvent(BlockRenderEvent event) {
      if (this.selectionEvents != null) {
         this.selectionEvents.remove(event);
      }
   }

   public void fireDraggedEvents(ItemStack itemStack) {
      this.fireChannelEvents("itemdragged");
      if (this.draggedEvents != null) {
         for (ItemStackDraggedEvent event : this.draggedEvents) {
            event.setItemStack(itemStack);
         }
      }
   }

   private void fireSelectionEvents() {
      this.fireChannelEvents("select");
      if (this.selectionEvents != null) {
         for (BlockRenderEvent event : this.selectionEvents) {
            event.select();
         }
      }
   }

   private void fireDoubleClickEvent() {
      this.fireChannelEvents("doubleclick");
      if (this.selectionEvents != null) {
         for (BlockRenderEvent event : this.selectionEvents) {
            event.doubleClick();
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      command.findCommand("offset").ifPresent(cmd -> {
         this.offsetX = cmd.getOptionalPar(0, 0);
         this.offsetY = cmd.getOptionalPar(1, 0);
      });
      this.hilightOnHover = GuiParser.get(command, "highlighthover", false);
      this.showLabel = GuiParser.get(command, "showlabel", false);
      this.labelColor = GuiParser.get(command, "labelColor", null);
      command.findCommand("render").ifPresent(cmd -> this.renderItem = ItemStackTools.guiCommandToItemStack(cmd));
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      if (this.offsetX != 0 || this.offsetY != 0) {
         command.command(new GuiParser.GuiCommand("offset").parameter(this.offsetX).parameter(this.offsetY));
      }

      GuiParser.put(command, "highlighthover", this.hilightOnHover, false);
      GuiParser.put(command, "showlabel", this.showLabel, false);
      GuiParser.put(command, "labelColor", this.labelColor, null);
      if (this.renderItem != null && this.renderItem instanceof ItemStack) {
         command.command(ItemStackTools.itemStackToGuiCommand("render", (ItemStack)this.renderItem));
      }
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("blockrender");
   }

   @Override
   public <T> void setGenericValue(T value) {
      if (value == null) {
         this.renderItem(null);
      } else {
         Item item = Tools.getItem(Identifier.parse(value.toString()));
         if (item != null) {
            this.renderItem(new ItemStack(item));
         } else {
            Block block = Tools.getBlock(Identifier.parse(value.toString()));
            if (block != null) {
               this.renderItem(new ItemStack(block));
            } else {
               this.renderItem(null);
            }
         }
      }
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.renderItem instanceof ItemStack ? Tools.getId((ItemStack)this.renderItem).toString() : null;
   }
}
