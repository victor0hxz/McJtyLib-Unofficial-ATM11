package mcjty.lib.gui.widgets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ImageEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public abstract class AbstractImageLabel<P extends AbstractImageLabel<P>> extends AbstractWidget<P> {
   public static final String TYPE_IMAGELABEL = "imagelabel";
   public static final Key<Integer> PARAM_U = new Key<>("u", Type.INTEGER);
   public static final Key<Integer> PARAM_V = new Key<>("v", Type.INTEGER);
   public static final int DEFAULT_TXTDIM = 256;
   private boolean dragging = false;
   private Identifier image = null;
   private int u;
   private int v;
   private int txtWidth = 256;
   private int txtHeight = 256;
   private List<ImageEvent> imageEvents = null;
   private BufferedImage bufferedImage;

   public Identifier getImage() {
      return this.image;
   }

   public P image(Identifier image, int u, int v) {
      this.image = image;
      this.u = u;
      this.v = v;
      return this.getThis();
   }

   public P setTextureDimensions(int tw, int th) {
      this.txtWidth = tw;
      this.txtHeight = th;
      return this.getThis();
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.dragging = true;
         int u = (int)(x - this.bounds.x);
         int v = (int)(y - this.bounds.y);
         this.fireImageEvents("click", u, v, this.pickColor(u, v));
         return this;
      } else {
         return null;
      }
   }

   @Override
   public void mouseMove(double x, double y) {
      if (this.dragging && this.in(x, y) && this.isEnabledAndVisible()) {
         int u = (int)(x - this.bounds.x);
         int v = (int)(y - this.bounds.y);
         this.fireImageEvents("move", u, v, this.pickColor(u, v));
      }
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      super.mouseRelease(x, y, button);
      if (this.dragging) {
         this.dragging = false;
         if (this.in(x, y)) {
            int u = (int)(x - this.bounds.x);
            int v = (int)(y - this.bounds.y);
            this.fireImageEvents("release", u, v, this.pickColor(u, v));
         }
      }
   }

   private int pickColor(int u, int v) {
      if (this.bufferedImage == null) {
         try {
            Resource resource = (Resource)Minecraft.getInstance().getResourceManager().getResource(this.image).get();
            this.bufferedImage = ImageIO.read(resource.open());
         } catch (IOException var4) {
         }
      }

      int color = 0;
      if (this.bufferedImage != null) {
         color = this.bufferedImage.getRGB(u, v);
      }

      return color;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         if (this.image != null) {
            int xx = x + this.bounds.x;
            int yy = y + this.bounds.y;
            graphics.blit(
               RenderPipelines.GUI_TEXTURED, this.image, xx, yy, this.u, this.v, this.bounds.width, this.bounds.height, this.txtWidth, this.txtHeight
            );
         }
      }
   }

   public AbstractImageLabel<P> event(ImageEvent event) {
      if (this.imageEvents == null) {
         this.imageEvents = new ArrayList<>();
      }

      this.imageEvents.add(event);
      return this;
   }

   public void removeImageEvent(ImageEvent event) {
      if (this.imageEvents != null) {
         this.imageEvents.remove(event);
      }
   }

   private void fireImageEvents(String id, int u, int v, int color) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, id).put(PARAM_U, u).put(PARAM_V, v).build());
      if (this.imageEvents != null) {
         if (u < 0 || v < 0 || u >= this.txtWidth || v >= this.txtHeight) {
            return;
         }

         for (ImageEvent event : this.imageEvents) {
            event.imageClicked(u, v, color);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      command.findCommand("image").ifPresent(cmd -> this.image = Identifier.parse(cmd.getOptionalPar(0, "")));
      command.findCommand("dim").ifPresent(cmd -> {
         this.u = cmd.getOptionalPar(0, 0);
         this.v = cmd.getOptionalPar(1, 0);
         this.txtWidth = cmd.getOptionalPar(2, 256);
         this.txtHeight = cmd.getOptionalPar(3, 256);
      });
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      if (this.image != null) {
         command.command(new GuiParser.GuiCommand("image").parameter(this.image.toString()));
      }

      GuiParser.GuiCommand dimCmd = new GuiParser.GuiCommand("dim").parameter(this.u).parameter(this.v).parameter(this.txtWidth).parameter(this.txtHeight);
      command.command(dimCmd);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("imagelabel");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
