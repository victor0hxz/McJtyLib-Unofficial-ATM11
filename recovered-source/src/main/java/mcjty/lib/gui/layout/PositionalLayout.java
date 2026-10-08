package mcjty.lib.gui.layout;

import java.util.Collection;
import mcjty.lib.gui.widgets.Widget;

public class PositionalLayout extends AbstractLayout<PositionalLayout> {
   @Override
   public void doLayout(Collection<Widget<?>> children, int width, int height) {
      for (Widget<?> child : children) {
         PositionalLayout.PositionalHint hint = (PositionalLayout.PositionalHint)child.getLayoutHint();
         if (hint != null) {
            int w;
            if (hint.width == -1) {
               w = child.getDesiredWidth();
            } else {
               w = hint.width;
            }

            int h;
            if (hint.height == -1) {
               h = child.getDesiredHeight();
            } else {
               h = hint.height;
            }

            child.bounds(hint.x, hint.y, w, h);
         }
      }
   }

   public static class PositionalHint implements LayoutHint {
      private int x;
      private int y;
      private int width;
      private int height;

      public PositionalHint(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      public PositionalHint(int x, int y) {
         this(x, y, -1, -1);
      }

      public int getX() {
         return this.x;
      }

      public void setX(int x) {
         this.x = x;
      }

      public int getY() {
         return this.y;
      }

      public void setY(int y) {
         this.y = y;
      }

      public int getWidth() {
         return this.width;
      }

      public void setWidth(int width) {
         this.width = width;
      }

      public int getHeight() {
         return this.height;
      }

      public void setHeight(int height) {
         this.height = height;
      }
   }
}
