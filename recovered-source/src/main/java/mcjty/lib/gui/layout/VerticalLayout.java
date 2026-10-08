package mcjty.lib.gui.layout;

import java.util.Collection;
import mcjty.lib.gui.widgets.AbstractWidget;
import mcjty.lib.gui.widgets.Widget;

public class VerticalLayout extends AbstractLayout<VerticalLayout> {
   @Override
   public void doLayout(Collection<Widget<?>> children, int width, int height) {
      int otherHeight = this.calculateDynamicSize(children, height, Widget.Dimension.DIMENSION_HEIGHT);
      int top = this.getVerticalMargin();

      for (Widget<?> child : children) {
         int h = child.getDesiredHeight();
         if (h == -1) {
            h = otherHeight;
         }

         ((AbstractWidget)child).setBounds(this.align(this.getHorizontalMargin(), top, width - this.getHorizontalMargin() * 2, h, child));
         top += h;
         top += this.getSpacing();
      }
   }
}
