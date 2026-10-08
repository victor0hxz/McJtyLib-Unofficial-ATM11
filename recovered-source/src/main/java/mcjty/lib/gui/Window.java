package mcjty.lib.gui;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.events.ChannelEvent;
import mcjty.lib.gui.events.FocusEvent;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketAttachmentData;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.ValueHolder;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NamedCodec;
import mcjty.lib.varia.StringRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class Window {
   public static final Key<String> PARAM_ID = new Key<>("id", Type.STRING);
   private AbstractContainerWidget<?> toplevel;
   private final Screen gui;
   private Widget<?> textFocus = null;
   private Widget<?> hover = null;
   private GuiStyle currentStyle;
   private WindowManager windowManager;
   private final Map<String, List<ChannelEvent>> channelEvents = new HashMap<>();
   private final Map<Widget, Function> bindings = new HashMap<>();
   private final Set<Integer> activeFlags = new HashSet<>();
   private List<FocusEvent> focusEvents = null;

   public Screen getGui() {
      return this.gui;
   }

   public Window(Screen gui, AbstractContainerWidget<?> toplevel) {
      this.gui = gui;
      this.toplevel = toplevel;
   }

   public Window(Screen gui, Identifier guiDescription) {
      this.gui = gui;
      int[] dim = new int[]{-1, -1};
      int[] sidesize = new int[]{0, 0};
      this.parseInternal(null, null, guiDescription, dim, sidesize);
      if (dim[0] != -1 || dim[1] != -1) {
         if (gui instanceof GuiItemScreen container) {
            container.setWindowDimensions(dim[0], dim[1]);
         }

         int guiLeft = (gui.width - dim[0]) / 2;
         int guiTop = (gui.height - dim[1]) / 2;
         this.toplevel.bounds(guiLeft - sidesize[0], guiTop - sidesize[1], dim[0] + sidesize[0], dim[1] + sidesize[1]);
      }
   }

   public Window(Screen gui, GenericTileEntity tileEntity, Identifier guiDescription) {
      this.gui = gui;
      int[] dim = new int[]{-1, -1};
      int[] sidesize = new int[]{0, 0};
      this.parseInternal((GenericGuiContainer<?, ?>)gui, tileEntity, guiDescription, dim, sidesize);
      if (dim[0] != -1 || dim[1] != -1) {
         if (gui instanceof GenericGuiContainer container) {
            container.setWindowDimensions(dim[0], dim[1]);
         }

         int guiLeft = (gui.width - dim[0]) / 2;
         int guiTop = (gui.height - dim[1]) / 2;
         this.toplevel.bounds(guiLeft - sidesize[0], guiTop - sidesize[1], dim[0] + sidesize[0], dim[1] + sidesize[1]);
      }
   }

   private void parseInternal(
      @Nullable GenericGuiContainer<?, ?> gui, @Nullable GenericTileEntity tileEntity, Identifier guiDescription, int[] dim, int[] sidesize
   ) {
      try {
         WindowTools.parseAndHandleClient(
            guiDescription,
            command -> {
               if ("window".equals(command.getId())) {
                  command.findCommand("size").ifPresent(cmd -> {
                     dim[0] = cmd.getOptionalPar(0, -1);
                     dim[1] = cmd.getOptionalPar(1, -1);
                  });
                  command.findCommand("sidesize").ifPresent(cmd -> {
                     sidesize[0] = cmd.getOptionalPar(0, 0);
                     sidesize[1] = cmd.getOptionalPar(1, 0);
                  });
                  command.commands().filter(cmd -> "event".equals(cmd.getId())).forEach(cmd -> {
                     if (gui != null) {
                        String channel = cmd.getOptionalPar(0, "");
                        String teCommand = cmd.getOptionalPar(1, "");
                        this.event(channel, (source, params) -> gui.sendServerCommandTyped(teCommand, params));
                     }
                  });
                  command.commands()
                     .filter(cmd -> "cmdevent".equals(cmd.getId()))
                     .forEach(
                        cmd -> {
                           String channel = cmd.getOptionalPar(0, "");
                           String modidCmd = cmd.getOptionalPar(1, "");
                           Identifier rl = Identifier.parse(modidCmd);
                           this.event(
                              channel,
                              (source, params) -> Networking.sendToServer(PacketSendServerCommand.create(rl.getNamespace(), rl.getPath(), TypedMap.EMPTY))
                           );
                        }
                     );
                  command.findCommand("panel").ifPresent(cmd -> {
                     this.toplevel = new Panel();
                     this.toplevel.readFromGuiCommand(cmd);
                  });
                  command.commands().filter(cmd -> "bind".equals(cmd.getId())).forEach(cmd -> {
                     if (tileEntity != null) {
                        String component = cmd.getOptionalPar(0, "");
                        String value = cmd.getOptionalPar(1, "");
                        this.bind(component, tileEntity, value);
                     }
                  });
                  command.commands().filter(cmd -> "binddata".equals(cmd.getId())).forEach(cmd -> {
                     if (tileEntity != null) {
                        String component = cmd.getOptionalPar(0, "");
                        String attributeName = cmd.getOptionalPar(1, "");
                        String attachmentKey = cmd.getOptionalPar(2, "");
                        AttachmentType<?> type = (AttachmentType<?>)NeoForgeRegistries.ATTACHMENT_TYPES.getValue(Identifier.parse(attachmentKey));
                        if (type == null) {
                           Logging.logError("Could not find attachment type '" + attachmentKey + "'!");
                           return;
                        }

                        this.bindData(component, attributeName, tileEntity, type);
                     }
                  });
                  command.commands().filter(cmd -> "action".equals(cmd.getId())).forEach(cmd -> {
                     if (tileEntity != null) {
                        String component = cmd.getOptionalPar(0, "");
                        String key = cmd.getOptionalPar(1, "");
                        this.action(component, tileEntity, key);
                     }
                  });
               }
            }
         );
      } catch (Exception var9) {
         String message = var9.getMessage();
         if (message.length() > 70) {
            int i = message.lastIndexOf(58);
            if (i == -1) {
               message = message.substring(message.length() - 70);
            } else {
               message = message.substring(i + 1);
            }
         }

         this.toplevel = Widgets.positional()
            .desiredWidth(400)
            .desiredHeight(50)
            .filledBackground(-1)
            .filledRectThickness(2)
            .children(Widgets.label(message).hint(5, 5, 390, 40));
         dim[0] = 400;
         dim[1] = 50;
         var9.printStackTrace();
      }
   }

   public <T extends Widget<T>> T findChild(String name) {
      Widget<?> widget = this.toplevel.findChildRecursive(name);
      if (widget == null) {
         Logging.logError("Could not find widget '" + name + "'!");
      }

      return (T)widget;
   }

   public WindowManager getWindowManager() {
      return this.windowManager;
   }

   public void setWindowManager(WindowManager windowManager) {
      this.windowManager = windowManager;
   }

   public boolean isWidgetOnWindow(Widget<?> w) {
      return this.toplevel.containsWidget(w);
   }

   public Widget<?> getToplevel() {
      return this.toplevel;
   }

   public void setFlag(String flag) {
      if (flag.startsWith("!")) {
         this.activeFlags.remove(StringRegister.STRINGS.get(flag.substring(1)));
      } else {
         this.activeFlags.remove(StringRegister.STRINGS.get("!" + flag));
      }

      this.activeFlags.add(StringRegister.STRINGS.get(flag));
      this.enableDisableWidgets(this.toplevel);
   }

   public void clearFlag(String flag) {
      this.activeFlags.remove(StringRegister.STRINGS.get(flag));
      this.activeFlags.add(StringRegister.STRINGS.get("!" + flag));
      this.enableDisableWidgets(this.toplevel);
   }

   public void setFlag(String flag, boolean v) {
      if (v) {
         this.activeFlags.remove(StringRegister.STRINGS.get("!" + flag));
         this.activeFlags.add(StringRegister.STRINGS.get(flag));
      } else {
         this.activeFlags.remove(StringRegister.STRINGS.get(flag));
         this.activeFlags.add(StringRegister.STRINGS.get("!" + flag));
      }

      this.enableDisableWidgets(this.toplevel);
   }

   private void enableDisableWidgets(Widget<?> widget) {
      Set<Integer> enabledFlags = widget.getEnabledFlags();
      if (!enabledFlags.isEmpty()) {
         boolean enable = this.activeFlags.containsAll(enabledFlags);
         widget.enabled(enable);
      }

      if (widget instanceof AbstractContainerWidget) {
         for (Widget<?> child : ((AbstractContainerWidget)widget).getChildren()) {
            this.enableDisableWidgets(child);
         }
      }
   }

   public Widget<?> getWidgetAtPosition(double x, double y) {
      return this.toplevel.in(x, y) && this.toplevel.isVisible() ? this.toplevel.getWidgetAtPosition(x, y) : null;
   }

   public void mouseClicked(double x, double y, int button) {
      if (this.textFocus != null) {
         this.textFocus = null;
         this.fireFocusEvents(null);
      }

      if (this.toplevel.in(x, y) && this.toplevel.isVisible()) {
         this.toplevel.setWindow(this);
         this.toplevel.mouseClick(x, y, button);
      }
   }

   public void mouseDragged(double x, double y, int button) {
      this.toplevel.setWindow(this);
      this.toplevel.mouseMove(x, y);
   }

   public void mouseScrolled(double x, double y, double dx, double dy) {
      this.toplevel.setWindow(this);
      this.toplevel.mouseScrolled(x, y, dx, dy);
   }

   public void mouseReleased(double x, double y, int button) {
      this.toplevel.setWindow(this);
      this.toplevel.mouseRelease(x, y, button);
   }

   public void setTextFocus(Widget<?> focus) {
      if (this.windowManager != null) {
         this.windowManager.clearFocus();
      }

      this.setFocus(focus);
   }

   void setFocus(Widget<?> focus) {
      if (this.textFocus != focus) {
         this.textFocus = focus;
         this.fireFocusEvents(focus);
      }
   }

   public Widget<?> getTextFocus() {
      return this.textFocus;
   }

   public boolean charTyped(char codePoint) {
      return this.textFocus != null ? this.textFocus.charTyped(codePoint) : false;
   }

   public boolean keyTyped(int keyCode, int scanCode) {
      if (keyCode == 301) {
         GuiParser.GuiCommand windowCmd = this.createWindowCommand();
         GuiParser.GuiCommand command = this.toplevel.createGuiCommand();
         this.toplevel.fillGuiCommand(command);
         windowCmd.command(command);

         try (PrintWriter writer = new PrintWriter(new File("output.gui"))) {
            windowCmd.write(writer, 0);
            writer.flush();
         } catch (FileNotFoundException var10) {
            Logging.logError("Problem writing output.gui!", var10);
         }
      }

      return this.textFocus != null ? this.textFocus.keyTyped(keyCode, scanCode) : false;
   }

   private GuiParser.GuiCommand createWindowCommand() {
      GuiParser.GuiCommand windowCmd = new GuiParser.GuiCommand("window");
      windowCmd.command(
         new GuiParser.GuiCommand("size").parameter((int)this.toplevel.getBounds().getWidth()).parameter((int)this.toplevel.getBounds().getHeight())
      );
      return windowCmd;
   }

   public <T extends GenericTileEntity> void syncBindings(T te) {
      this.bindings.entrySet().forEach(entry -> entry.getKey().setGenericValue(entry.getValue().apply(te)));
   }

   public void draw(GuiGraphicsExtractor graphics) {
      int x = this.getRelativeX();
      int y = this.getRelativeY();
      if (this.hover != null) {
         this.hover.hovering(false);
      }

      this.hover = this.toplevel.getWidgetAtPosition(x, y);
      if (this.hover != null) {
         this.hover.hovering(true);
      }

      int dwheelX;
      int dwheelY;
      if (this.windowManager == null) {
         dwheelX = 0;
         dwheelY = 0;
      } else {
         dwheelX = this.windowManager.getMouseWheelX();
         if (dwheelX == -1) {
            dwheelX = 0;
         }

         dwheelY = this.windowManager.getMouseWheelY();
         if (dwheelY == -1) {
            dwheelY = 0;
         }
      }

      if (dwheelX != 0 || dwheelY != 0) {
         this.toplevel.setWindow(this);
         this.toplevel.mouseScrolled(x, y, dwheelX, dwheelY);
      }

      PreferencesProperties preferencesProperties = McJtyLib.getPreferencesProperties(this.gui.getMinecraft().player);
      this.currentStyle = preferencesProperties != null ? preferencesProperties.getStyle() : GuiStyle.STYLE_FLAT_GRADIENT;
      this.toplevel.setWindow(this);
      this.toplevel.draw(this.gui, graphics, 0, 0);
      this.toplevel.drawPhase2(this.gui, graphics, 0, 0);
   }

   public GuiStyle getCurrentStyle() {
      return this.currentStyle;
   }

   @Nullable
   public List<String> getTooltips() {
      int x = this.getRelativeX();
      int y = this.getRelativeY();
      if (this.toplevel.in(x, y) && this.toplevel.isVisible()) {
         Widget<?> w = this.toplevel.getWidgetAtPosition(x, y);
         List<String> tooltips = w.getTooltips();
         if (tooltips != null) {
            return tooltips;
         }
      }

      return null;
   }

   @Nullable
   public List<ItemStack> getTooltipItems() {
      int x = this.getRelativeX();
      int y = this.getRelativeY();
      if (this.toplevel.in(x, y) && this.toplevel.isVisible()) {
         Widget<?> w = this.toplevel.getWidgetAtPosition(x, y);
         return w.getTooltipItems();
      } else {
         return null;
      }
   }

   private int getRelativeX() {
      int windowWidth = this.gui.getMinecraft().getWindow().getScreenWidth();
      return windowWidth == 0 ? 0 : (int)this.gui.getMinecraft().mouseHandler.xpos() * this.gui.width / windowWidth;
   }

   private int getRelativeY() {
      int windowHeight = this.gui.getMinecraft().getWindow().getScreenHeight();
      return windowHeight == 0 ? 0 : (int)this.gui.getMinecraft().mouseHandler.ypos() * this.gui.height / windowHeight;
   }

   public Window addFocusEvent(FocusEvent event) {
      if (this.focusEvents == null) {
         this.focusEvents = new ArrayList<>();
      }

      this.focusEvents.add(event);
      return this;
   }

   private void fireFocusEvents(Widget<?> widget) {
      if (this.focusEvents != null) {
         for (FocusEvent event : this.focusEvents) {
            event.focus(widget);
         }
      }
   }

   public Window event(String channel, ChannelEvent event) {
      if (!this.channelEvents.containsKey(channel)) {
         this.channelEvents.put(channel, new ArrayList<>());
      }

      this.channelEvents.get(channel).add(event);
      return this;
   }

   public <T extends GenericTileEntity> Window action(String componentName, T te, Command<?> command) {
      return this.action(componentName, te, command.name());
   }

   public <T extends GenericTileEntity> Window action(String componentName, T te, String keyName) {
      IRunnable<?> serverCommand = te.findServerCommand(keyName);
      if (serverCommand != null) {
         this.initializeAction(componentName, keyName, te);
         return this;
      } else {
         Logging.message(Minecraft.getInstance().player, "Could not find action '" + keyName + "' in supplied TE!");
         return this;
      }
   }

   private <T extends GenericTileEntity> void initializeAction(String componentName, String command, T te) {
      this.event(
         componentName, (source, params) -> Networking.sendToServer(PacketServerCommandTyped.create(te.getBlockPos(), te.getDimension(), command, params))
      );
   }

   public void sendServerCommand(Command<?> action, @Nonnull TypedMap params) {
      GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>)this.gui;
      guiContainer.sendServerCommandTyped(action, params);
   }

   public <T extends GenericTileEntity, O> Window bindData(String componentName, String attributeName, T te, AttachmentType<O> type) {
      GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>)this.gui;
      GenericContainer menu = (GenericContainer)guiContainer.getMenu();
      Codec<O> codec = menu.getCodecForType(type);
      if (codec == null) {
         Logging.message(Minecraft.getInstance().player, "Could not find codec for type '" + type + "'!");
         return this;
      } else {
         Widget<?> component = this.findChild(componentName);
         if (component == null) {
            Logging.message(Minecraft.getInstance().player, "Could not find component '" + componentName + "'!");
            return this;
         } else {
            O dt = (O)te.getData(type);
            Object value = NamedCodec.get(codec, dt, attributeName);
            component.setGenericValue(value);
            this.bindings.put(component, t -> NamedCodec.get(codec, te.getData(type), attributeName));
            this.event(componentName, (source, params) -> {
               O data = (O)te.getData(type);
               NamedCodec<O> ncOut = NamedCodec.map(codec, data);
               Type<?> attributeType = ncOut.getType(attributeName);
               Object oldValue = component.getGenericValue(attributeType);
               O newValue = ncOut.set(attributeName, oldValue);
               te.setData(type, newValue);
               Identifier id = NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type);
               StreamCodec<RegistryFriendlyByteBuf, O> streamCodec = menu.getStreamCodecForType(type);
               if (streamCodec == null) {
                  Logging.message(Minecraft.getInstance().player, "Could not find stream codec for type '" + id + "'!");
               } else {
                  ByteBuf newbuf = Unpooled.buffer();
                  RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, te.getLevel().registryAccess(), ConnectionType.OTHER);
                  streamCodec.encode(buffer, newValue);
                  Networking.sendToServer(PacketAttachmentData.create(id, buffer));
               }
            });
            return this;
         }
      }
   }

   public <T> void syncDataToServer(AttachmentType<T> type, GenericTileEntity be) {
      GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>)this.gui;
      GenericContainer menu = (GenericContainer)guiContainer.getMenu();
      StreamCodec<RegistryFriendlyByteBuf, T> codec = menu.getStreamCodecForType(type);
      ByteBuf newbuf = Unpooled.buffer();
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, be.getLevel().registryAccess(), ConnectionType.OTHER);
      T data = (T)be.getData(type);
      codec.encode(buffer, data);
      Networking.sendToServer(PacketAttachmentData.create(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type), buffer));
   }

   public <T extends GenericTileEntity> Window bind(String componentName, T te, String keyName) {
      Map<String, ValueHolder<?, ?>> valueMap = te.getValueMap();
      if (valueMap.containsKey(keyName)) {
         ValueHolder<?, ?> value = valueMap.get(keyName);
         this.initializeBinding(te.getDimension(), componentName, te, value);
         return this;
      } else {
         Logging.message(Minecraft.getInstance().player, "Could not find value '" + keyName + "' in supplied TE!");
         return this;
      }
   }

   private <T extends GenericTileEntity, V> void initializeBinding(@Nonnull ResourceKey<Level> dimensionType, String componentName, T te, ValueHolder value) {
      V v = (V)value.getter().apply(te);
      Widget<?> component = this.findChild(componentName);
      if (component == null) {
         Logging.message(Minecraft.getInstance().player, "Could not find component '" + componentName + "'!");
      } else {
         component.setGenericValue(v);
         this.bindings.put(component, value.getter());
         this.event(
            componentName,
            (source, params) -> {
               Type<V> type = value.key().type();
               V converted = type.convert(component.getGenericValue(type));
               value.setter().accept(te, converted);
               GenericGuiContainer<?, ?> guiContainer = (GenericGuiContainer<?, ?>)this.gui;
               guiContainer.sendServerCommandTyped(
                  dimensionType, GenericTileEntity.COMMAND_SYNC_BINDING.name(), TypedMap.builder().put(value.key(), converted).build()
               );
            }
         );
      }
   }

   public void fireChannelEvents(String channel, Widget<?> widget, @Nonnull TypedMap params) {
      if (this.channelEvents.containsKey(channel)) {
         for (ChannelEvent event : this.channelEvents.get(channel)) {
            event.fire(widget, params);
         }
      }
   }
}
