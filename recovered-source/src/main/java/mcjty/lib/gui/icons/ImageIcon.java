package mcjty.lib.gui.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ImageIcon implements IIcon, Cloneable {
   private Identifier image = null;
   private int u;
   private int v;
   private int width;
   private int height;
   private final String id;
   private List<IIcon> overlays;
   private Map<String, Object> dataMap;

   public Identifier getImage() {
      return this.image;
   }

   public ImageIcon(String id) {
      this.id = id;
   }

   @Override
   public void addOverlay(IIcon icon) {
      if (this.overlays == null) {
         this.overlays = new ArrayList<>();
      }

      this.overlays.add(icon);
   }

   @Override
   public void removeOverlay(String id) {
      if (this.overlays != null) {
         IIcon toRemove = null;

         for (IIcon icon : this.overlays) {
            if (id.equals(icon.getID())) {
               toRemove = icon;
               break;
            }
         }

         if (toRemove != null) {
            this.overlays.remove(toRemove);
         }
      }
   }

   @Override
   public boolean hasOverlay(String id) {
      if (this.overlays == null) {
         return false;
      } else {
         for (IIcon icon : this.overlays) {
            if (id.equals(icon.getID())) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void clearOverlays() {
      this.overlays = null;
   }

   @Override
   public void addData(String name, Object data) {
      if (this.dataMap == null) {
         this.dataMap = new HashMap<>();
      }

      this.dataMap.put(name, data);
   }

   @Override
   public void removeData(String name) {
      if (this.dataMap != null) {
         this.dataMap.remove(name);
      }
   }

   @Override
   public void clearData() {
      this.dataMap = null;
   }

   @Override
   public Map<String, Object> getData() {
      return this.dataMap;
   }

   public ImageIcon setImage(Identifier image, int u, int v) {
      this.image = image;
      this.u = u;
      this.v = v;
      return this;
   }

   public ImageIcon setDimensions(int w, int h) {
      this.width = w;
      this.height = h;
      return this;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.image, x, y, this.u, this.v, this.width, this.height, 256, 256);
      if (this.overlays != null) {
         for (IIcon icon : this.overlays) {
            icon.draw(gui, graphics, x, y);
         }
      }
   }

   @Override
   public String getID() {
      return this.id;
   }

   @Override
   public IIcon clone() {
      ImageIcon imageIcon = new ImageIcon(this.id).setImage(this.image, this.u, this.v).setDimensions(this.width, this.height);
      if (this.overlays != null) {
         imageIcon.overlays = new ArrayList<>();

         for (IIcon icon : this.overlays) {
            imageIcon.overlays.add(icon.clone());
         }
      }

      if (this.dataMap != null) {
         imageIcon.dataMap = new HashMap<>(this.dataMap);
      }

      return imageIcon;
   }
}
