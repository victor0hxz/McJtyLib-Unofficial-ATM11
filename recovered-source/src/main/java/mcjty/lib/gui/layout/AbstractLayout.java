package mcjty.lib.gui.layout;

import java.awt.Rectangle;
import java.util.Collection;
import mcjty.lib.gui.widgets.Widget;

public abstract class AbstractLayout<P extends AbstractLayout<P>> implements Layout {
   public static final int DEFAULT_SPACING = 5;
   public static final int DEFAULT_HORIZONTAL_MARGIN = 5;
   public static final int DEFAULT_VERTICAL_MARGIN = 2;
   public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGN = HorizontalAlignment.ALIGN_CENTER;
   public static final VerticalAlignment DEFAULT_VERTICAL_ALIGN = VerticalAlignment.ALIGN_CENTER;
   private int spacing = 5;
   private int horizontalMargin = 5;
   private int verticalMargin = 2;
   private HorizontalAlignment horizontalAlignment = DEFAULT_HORIZONTAL_ALIGN;
   private VerticalAlignment verticalAlignment = DEFAULT_VERTICAL_ALIGN;

   protected int calculateDynamicSize(Collection<Widget<?>> children, int totalSize, Widget.Dimension dimension) {
      int totalFixed = 0;
      int countFixed = 0;

      for (Widget<?> child : children) {
         int s = child.getDesiredSize(dimension);
         if (s != -1) {
            totalFixed += s;
            countFixed++;
         }
      }

      totalFixed += this.getSpacing() * (children.size() - 1);
      if (dimension == Widget.Dimension.DIMENSION_WIDTH) {
         totalFixed += this.getHorizontalMargin() * 2;
      } else {
         totalFixed += this.getVerticalMargin() * 2;
      }

      int otherSize = 0;
      if (countFixed < children.size()) {
         otherSize = (totalSize - totalFixed) / (children.size() - countFixed);
         if (otherSize <= 0) {
            otherSize = 1;
         }
      }

      return otherSize;
   }

   protected Rectangle align(int x, int y, int width, int height, Widget<?> child) {
      int desiredWidth = child.getDesiredWidth();
      if (desiredWidth == -1) {
         desiredWidth = width;
      }
      x = switch (this.horizontalAlignment) {
         case ALIGN_RIGHT -> width - desiredWidth;
         case ALIGN_CENTER -> (width - desiredWidth) / 2;
      };
      int desiredHeight = child.getDesiredHeight();
      if (desiredHeight == -1) {
         desiredHeight = height;
      }
      return new Rectangle(x, switch (this.verticalAlignment) {
         case ALIGN_BOTTOM -> height - desiredHeight;
         case ALIGN_CENTER -> (height - desiredHeight) / 2;
      }, desiredWidth, desiredHeight);
   }

   public int getSpacing() {
      return this.spacing;
   }

   public P setSpacing(int spacing) {
      this.spacing = spacing;
      return this.getThis();
   }

   private P getThis() {
      return (P)this;
   }

   public int getHorizontalMargin() {
      return this.horizontalMargin;
   }

   public P setHorizontalMargin(int horizontalMargin) {
      this.horizontalMargin = horizontalMargin;
      return this.getThis();
   }

   public int getVerticalMargin() {
      return this.verticalMargin;
   }

   public P setVerticalMargin(int verticalMargin) {
      this.verticalMargin = verticalMargin;
      return this.getThis();
   }

   public HorizontalAlignment getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public P setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
      if (horizontalAlignment == null) {
         throw new RuntimeException("Invalid horizontal alignment!");
      } else {
         this.horizontalAlignment = horizontalAlignment;
         return this.getThis();
      }
   }

   public VerticalAlignment getVerticalAlignment() {
      return this.verticalAlignment;
   }

   public P setVerticalAlignment(VerticalAlignment verticalAlignment) {
      if (this.horizontalAlignment == null) {
         throw new RuntimeException("Invalid vertical alignment!");
      } else {
         this.verticalAlignment = verticalAlignment;
         return this.getThis();
      }
   }
}
