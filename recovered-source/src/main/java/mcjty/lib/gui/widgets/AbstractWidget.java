package mcjty.lib.gui.widgets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.LayoutHint;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.ItemStackTools;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractWidget<P extends AbstractWidget<P>> implements Widget<P> {
   public static final int DEFAULT_BACKGROUND_OFFSET = 256;
   public static final int DEFAULT_FILLED_RECT_THICKNESS = 0;
   public static final int DEFAULT_FILLED_BACKGROUND = -1;
   public static final boolean DEFAULT_BACKGROUND_HORIZONTAL = true;
   protected Rectangle bounds;
   protected int desiredWidth = -1;
   protected int desiredHeight = -1;
   protected Minecraft mc;
   protected Window window;
   private LayoutHint layoutHint = null;
   private boolean enabled = true;
   private boolean hovering = false;
   protected boolean visible = true;
   protected List<String> tooltips = null;
   protected List<ItemStack> items = null;
   private final Set<Integer> enableFlags = new HashSet<>();
   private String name;
   private String channel;
   private boolean layoutDirty = true;
   private Object userObject = null;
   private Identifier background1 = null;
   private Identifier background2 = null;
   private boolean background2Horizontal = true;
   private int backgroundOffset = 256;
   private int filledRectThickness = 0;
   private int filledBackground = -1;
   private int filledBackground2 = -1;

   @Override
   public boolean containsWidget(Widget<?> w) {
      return w == this;
   }

   protected AbstractWidget() {
      this.mc = Minecraft.getInstance();
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Window getWindow() {
      return this.window;
   }

   @Override
   public void setWindow(Window window) {
      this.window = window;
   }

   public P name(String name) {
      this.name = name;
      if (this.channel == null) {
         this.channel = name;
      }

      return this.getThis();
   }

   protected P getThis() {
      return (P)this;
   }

   public P channel(String channel) {
      this.channel = channel;
      return this.getThis();
   }

   protected void fireChannelEvents() {
      this.fireChannelEvents(TypedMap.EMPTY);
   }

   protected void fireChannelEvents(String id) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, id).build());
   }

   protected void fireChannelEvents(@Nonnull TypedMap params) {
      if (this.window != null && this.channel != null) {
         this.window.fireChannelEvents(this.channel, this, params);
      }
   }

   protected void drawBox(GuiGraphicsExtractor graphics, int xx, int yy, int color) {
      graphics.fill(xx, yy, xx, yy + this.bounds.height, color);
      graphics.fill(xx + this.bounds.width, yy, xx + this.bounds.width, yy + this.bounds.height, color);
      graphics.fill(xx, yy, xx + this.bounds.width, yy, color);
      graphics.fill(xx, yy + this.bounds.height, xx + this.bounds.width, yy + this.bounds.height, color);
   }

   @Override
   public Widget<?> getWidgetAtPosition(double x, double y) {
      return this;
   }

   public P tooltips(String... tooltips) {
      if (tooltips.length > 0) {
         this.tooltips = new ArrayList<>(tooltips.length);
         Collections.addAll(this.tooltips, tooltips);
      } else {
         this.tooltips = null;
      }

      return this.getThis();
   }

   public P tooltipItems(ItemStack... items) {
      if (items.length > 0) {
         this.items = new ArrayList<>(items.length);
         Collections.addAll(this.items, items);
      } else {
         this.items = Collections.emptyList();
      }

      return this.getThis();
   }

   @Override
   public List<String> getTooltips() {
      return this.tooltips;
   }

   @Override
   public List<ItemStack> getTooltipItems() {
      return this.items;
   }

   @Override
   public boolean isHovering() {
      return this.hovering;
   }

   public P hovering(boolean hovering) {
      this.hovering = hovering;
      return this.getThis();
   }

   public P enabled(boolean enabled) {
      this.enabled = enabled;
      return this.getThis();
   }

   public P enabledFlags(String... flags) {
      for (String flag : flags) {
         this.enableFlags.add(StringRegister.STRINGS.get(flag));
      }

      return this.getThis();
   }

   @Nonnull
   @Override
   public Set<Integer> getEnabledFlags() {
      return this.enableFlags;
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   @Override
   public boolean isEnabledAndVisible() {
      return this.enabled && this.visible;
   }

   public P visible(boolean visible) {
      this.visible = visible;
      return this.getThis();
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public int getDesiredSize(Widget.Dimension dimension) {
      return dimension == Widget.Dimension.DIMENSION_WIDTH ? this.getDesiredWidth() : this.getDesiredHeight();
   }

   @Override
   public int getDesiredWidth() {
      return this.desiredWidth;
   }

   public P desiredWidth(int desiredWidth) {
      this.desiredWidth = desiredWidth;
      return this.getThis();
   }

   @Override
   public int getDesiredHeight() {
      return this.desiredHeight;
   }

   public P desiredHeight(int desiredHeight) {
      this.desiredHeight = desiredHeight;
      return this.getThis();
   }

   public P background(Identifier bg) {
      return this.setBackgrounds(bg, null);
   }

   public P setBackgrounds(Identifier bg1, Identifier bg2) {
      this.background1 = bg1;
      this.background2 = bg2;
      this.background2Horizontal = true;
      this.backgroundOffset = 256;
      return this.getThis();
   }

   public P setBackgroundLayout(boolean horizontal, int offset) {
      this.background2Horizontal = horizontal;
      this.backgroundOffset = offset;
      return this.getThis();
   }

   public P filledRectThickness(int thickness) {
      this.filledRectThickness = thickness;
      return this.getThis();
   }

   public int getFilledBackground() {
      return this.filledBackground;
   }

   public int getFilledBackground2() {
      return this.filledBackground2;
   }

   public P filledBackground(int filledBackground) {
      this.filledBackground = filledBackground;
      this.filledBackground2 = -1;
      return this.getThis();
   }

   public P filledBackground(int filledBackground, int filledBackground2) {
      this.filledBackground = filledBackground;
      this.filledBackground2 = filledBackground2;
      return this.getThis();
   }

   public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   @Override
   public void bounds(int x, int y, int w, int h) {
      this.bounds = new Rectangle(x, y, w, h);
   }

   @Override
   public Rectangle getBounds() {
      return this.bounds;
   }

   @Override
   public boolean in(double x, double y) {
      return this.bounds == null ? false : this.bounds.contains(x, y);
   }

   protected void drawBackground(Screen gui, GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.background1 != null) {
            if (this.background2 == null) {
               graphics.blit(RenderPipelines.GUI_TEXTURED, this.background1, xx, yy, 0.0F, 0.0F, w, h, 256, 256);
            } else if (this.background2Horizontal) {
               graphics.blit(RenderPipelines.GUI_TEXTURED, this.background1, xx, yy, 0.0F, 0.0F, this.backgroundOffset, h, 256, 256);
               graphics.blit(RenderPipelines.GUI_TEXTURED, this.background2, xx + this.backgroundOffset, yy, 0.0F, 0.0F, w - this.backgroundOffset, h, 256, 256);
            } else {
               graphics.blit(RenderPipelines.GUI_TEXTURED, this.background1, xx, yy, 0.0F, 0.0F, w, this.backgroundOffset, 256, 256);
               graphics.blit(RenderPipelines.GUI_TEXTURED, this.background2, xx, yy + this.backgroundOffset, 0.0F, 0.0F, w, h - this.backgroundOffset, 256, 256);
            }
         } else if (this.filledRectThickness > 0) {
            RenderHelper.drawThickBeveledBox(
               graphics,
               xx,
               yy,
               xx + w - 1,
               yy + h - 1,
               this.filledRectThickness,
               StyleConfig.colorBackgroundBevelBright,
               StyleConfig.colorBackgroundBevelDark,
               this.filledBackground == -1 ? StyleConfig.colorBackgroundFiller : this.filledBackground
            );
         } else if (this.filledRectThickness < 0) {
            RenderHelper.drawThickBeveledBox(
               graphics,
               xx,
               yy,
               xx + w - 1,
               yy + h - 1,
               -this.filledRectThickness,
               StyleConfig.colorBackgroundBevelDark,
               StyleConfig.colorBackgroundBevelBright,
               this.filledBackground == -1 ? StyleConfig.colorBackgroundFiller : this.filledBackground
            );
         } else if (this.filledBackground != -1) {
            RenderHelper.drawHorizontalGradientRect(
               xx, yy, xx + w - 1, yy + h - 1, this.filledBackground, this.filledBackground2 == -1 ? this.filledBackground : this.filledBackground2
            );
         }
      }
   }

   protected void drawBackground(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         this.drawBackground(gui, graphics, x, y, this.bounds.width, this.bounds.height);
      }
   }

   protected void drawStyledBoxNormal(Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
      this.drawStyledBox(
         window,
         graphics,
         x1,
         y1,
         x2,
         y2,
         StyleConfig.colorButtonBorderTopLeft,
         StyleConfig.colorButtonFiller,
         StyleConfig.colorButtonFillerGradient1,
         StyleConfig.colorButtonFillerGradient2,
         StyleConfig.colorButtonBorderBottomRight
      );
   }

   protected void drawStyledBoxNormal(Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int averageOverride) {
      this.drawStyledBox(
         window,
         graphics,
         x1,
         y1,
         x2,
         y2,
         StyleConfig.colorButtonBorderTopLeft,
         averageOverride,
         averageOverride,
         averageOverride,
         StyleConfig.colorButtonBorderBottomRight
      );
   }

   protected void drawStyledBoxSelected(Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
      this.drawStyledBox(
         window,
         graphics,
         x1,
         y1,
         x2,
         y2,
         StyleConfig.colorButtonSelectedBorderTopLeft,
         StyleConfig.colorButtonSelectedFiller,
         StyleConfig.colorButtonSelectedFillerGradient1,
         StyleConfig.colorButtonSelectedFillerGradient2,
         StyleConfig.colorButtonSelectedBorderBottomRight
      );
   }

   protected void drawStyledBoxHovering(Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
      this.drawStyledBox(
         window,
         graphics,
         x1,
         y1,
         x2,
         y2,
         StyleConfig.colorButtonHoveringBorderTopLeft,
         StyleConfig.colorButtonHoveringFiller,
         StyleConfig.colorButtonHoveringFillerGradient1,
         StyleConfig.colorButtonHoveringFillerGradient2,
         StyleConfig.colorButtonHoveringBorderBottomRight
      );
   }

   protected void drawStyledBoxDisabled(Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
      this.drawStyledBox(
         window,
         graphics,
         x1,
         y1,
         x2,
         y2,
         StyleConfig.colorButtonDisabledBorderTopLeft,
         StyleConfig.colorButtonDisabledFiller,
         StyleConfig.colorButtonDisabledFillerGradient1,
         StyleConfig.colorButtonDisabledFillerGradient2,
         StyleConfig.colorButtonDisabledBorderBottomRight
      );
   }

   private void drawStyledBox(
      Window window, GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int average1, int average2, int dark
   ) {
      switch (window.getCurrentStyle()) {
         case STYLE_BEVEL:
            RenderHelper.drawThinButtonBox(graphics, x1, y1, x2, y2, bright, average, dark);
            break;
         case STYLE_BEVEL_GRADIENT:
            RenderHelper.drawThinButtonBoxGradient(graphics, x1, y1, x2, y2, bright, average1, average2, dark);
            break;
         case STYLE_FLAT:
            RenderHelper.drawFlatButtonBox(graphics, x1, y1, x2, y2, bright, average, dark);
            break;
         case STYLE_FLAT_GRADIENT:
            RenderHelper.drawFlatButtonBoxGradient(graphics, x1, y1, x2, y2, bright, average1, average2, dark);
            break;
         case STYLE_THICK:
            RenderHelper.drawThickButtonBox(graphics, x1, y1, x2, y2, bright, average, dark);
      }
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      this.drawBackground(gui, graphics, x, y);
   }

   @Override
   public void drawPhase2(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      return null;
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
   }

   @Override
   public void mouseMove(double x, double y) {
   }

   @Override
   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      return false;
   }

   @Override
   public boolean keyTyped(int keyCode, int scanCode) {
      return false;
   }

   @Override
   public boolean charTyped(char codePoint) {
      return false;
   }

   @Override
   public void markLayoutDirty() {
      this.layoutDirty = true;
   }

   void markClean() {
      this.layoutDirty = false;
   }

   boolean isDirty() {
      return this.layoutDirty;
   }

   public P hint(LayoutHint hint) {
      this.layoutHint = hint;
      this.layoutDirty = true;
      return this.getThis();
   }

   @Override
   public LayoutHint getLayoutHint() {
      return this.layoutHint;
   }

   @Override
   public Object getUserObject() {
      return this.userObject;
   }

   public P userObject(Object obj) {
      this.userObject = obj;
      return this.getThis();
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      this.name = command.getOptionalPar(0, null);
      if (this.name != null && this.name.isEmpty()) {
         this.name = null;
      }

      this.channel = GuiParser.get(command, "channel", null);
      if (this.channel == null) {
         this.channel = this.name;
      }

      command.findCommand("desired").ifPresent(cmd -> {
         this.desiredWidth = cmd.getOptionalPar(0, -1);
         this.desiredHeight = cmd.getOptionalPar(1, -1);
      });
      command.findCommand("enableon").ifPresent(cmd -> {
         this.enableFlags.clear();
         cmd.parameters().forEach(flag -> this.enableFlags.add(StringRegister.STRINGS.get((String)flag)));
      });
      command.findCommand("tooltips").ifPresent(cmd -> {
         this.tooltips = new ArrayList<>();

         for (Object par : cmd.getParameters()) {
            this.tooltips.add(par.toString());
         }
      });
      command.findCommand("items").ifPresent(cmd -> {
         this.items = new ArrayList<>();

         for (GuiParser.GuiCommand itemCmd : cmd.getGuiCommands()) {
            this.items.add(ItemStackTools.guiCommandToItemStack(itemCmd));
         }
      });
      command.findCommand("hint")
         .ifPresent(cmd -> this.hint(cmd.getOptionalPar(0, 0), cmd.getOptionalPar(1, 0), cmd.getOptionalPar(2, 40), cmd.getOptionalPar(3, 40)));
      command.findCommand("bg1").ifPresent(cmd -> this.background1 = Identifier.parse(cmd.getOptionalPar(0, "")));
      command.findCommand("bg2").ifPresent(cmd -> this.background2 = Identifier.parse(cmd.getOptionalPar(0, "")));
      this.background2Horizontal = GuiParser.get(command, "bghoriz", true);
      this.backgroundOffset = GuiParser.get(command, "bgoffset", 256);
      this.filledRectThickness = GuiParser.get(command, "bgthickness", 0);
      this.filledBackground = GuiParser.get(command, "bgfilled1", -1);
      this.filledBackground2 = GuiParser.get(command, "bgfilled2", -1);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      command.parameter(this.name != null ? this.name : "");
      GuiParser.put(command, "channel", this.channel, null);
      if (this.desiredWidth != -1 || this.desiredHeight != -1) {
         command.command(new GuiParser.GuiCommand("desired").parameter(this.desiredWidth).parameter(this.desiredHeight));
      }

      if (this.layoutHint instanceof PositionalLayout.PositionalHint hint) {
         command.command(new GuiParser.GuiCommand("hint").parameter(hint.getX()).parameter(hint.getY()).parameter(hint.getWidth()).parameter(hint.getHeight()));
      }

      if (!this.enableFlags.isEmpty()) {
         GuiParser.GuiCommand enabledCmd = new GuiParser.GuiCommand("enableon");
         command.command(enabledCmd);

         for (Integer flag : this.enableFlags) {
            enabledCmd.parameter(StringRegister.STRINGS.get(flag));
         }
      }

      if (this.tooltips != null && !this.tooltips.isEmpty()) {
         GuiParser.GuiCommand ttCmd = new GuiParser.GuiCommand("tooltips");
         command.command(ttCmd);

         for (String s : this.tooltips) {
            ttCmd.parameter(s);
         }
      }

      if (this.items != null && !this.items.isEmpty()) {
         GuiParser.GuiCommand itemsCmd = new GuiParser.GuiCommand("items");
         command.command(itemsCmd);

         for (ItemStack stack : this.items) {
            itemsCmd.command(ItemStackTools.itemStackToGuiCommand("item", stack));
         }
      }

      if (this.background1 != null) {
         command.command(new GuiParser.GuiCommand("bg1").parameter(this.background1.toString()));
      }

      if (this.background2 != null) {
         command.command(new GuiParser.GuiCommand("bg2").parameter(this.background2.toString()));
      }

      GuiParser.put(command, "bghoriz", this.background2Horizontal, true);
      GuiParser.put(command, "bgoffset", this.backgroundOffset, 256);
      GuiParser.put(command, "bgthickness", this.filledRectThickness, 0);
      GuiParser.put(command, "bgfilled1", this.filledBackground, -1);
      GuiParser.put(command, "bgfilled2", this.filledBackground2, -1);
   }
}
