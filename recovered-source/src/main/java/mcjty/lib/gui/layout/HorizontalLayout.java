package mcjty.lib.gui.layout;

import java.util.Collection;
import mcjty.lib.gui.widgets.AbstractWidget;
import mcjty.lib.gui.widgets.Widget;

public class HorizontalLayout extends AbstractLayout<HorizontalLayout> {
   @Override
   public void doLayout(Collection<Widget<?>> children, int width, int height) {
      int otherWidth = this.calculateDynamicSize(children, width, Widget.Dimension.DIMENSION_WIDTH);
      int left = this.getHorizontalMargin();

      for (Widget<?> child : children) {
         int w = child.getDesiredWidth();
         if (w == -1) {
            w = otherWidth;
         }

         ((AbstractWidget)child).setBounds(this.align(left, this.getVerticalMargin(), w, height - this.getVerticalMargin() * 2, child));
         left += w;
         left += this.getSpacing();
      }
   }
}
