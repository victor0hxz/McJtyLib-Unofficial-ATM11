package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.events.ValueEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;

public class ScrollableLabel extends AbstractLabel<ScrollableLabel> implements Scrollable {
   public static final String TYPE_SCROLLABLELABEL = "scrollablelabel";
   public static final Key<Integer> PARAM_VALUE = new Key<>("value", Type.INTEGER);
   public static final int DEFAULT_REALMIN = 0;
   public static final int DEFAULT_REALMAX = 100;
   public static final String DEFAULT_SUFFIX = "";
   private int realmin = 0;
   private int realmax = 100;
   private int first = 0;
   private String suffix = "";
   private List<ValueEvent> valueEvents = null;

   public ScrollableLabel() {
      this.setFirstSelected(0);
   }

   public String getSuffix() {
      return this.suffix;
   }

   public ScrollableLabel suffix(String suffix) {
      this.suffix = suffix;
      return this;
   }

   public ScrollableLabel realMaximum(int realmax) {
      this.realmax = realmax;
      if (this.first > realmax - this.realmin) {
         this.first = realmax - this.realmin;
      }

      this.setFirstSelected(this.first);
      return this;
   }

   public int getRealMaximum() {
      return this.realmax;
   }

   public int getRealValue() {
      return this.first + this.realmin;
   }

   public ScrollableLabel realValue(int value) {
      int f = value - this.realmin;
      if (f < 0) {
         f = 0;
      }

      this.setFirstSelected(f);
      return this;
   }

   public ScrollableLabel realMinimum(int realmin) {
      this.realmin = realmin;
      if (this.first < 0) {
         this.first = 0;
      }

      this.setFirstSelected(this.first);
      return this;
   }

   public int getRealMinimum() {
      return this.realmin;
   }

   @Override
   public int getMaximum() {
      return this.realmax - this.realmin + 1;
   }

   @Override
   public int getCountSelected() {
      return 1;
   }

   @Override
   public int getFirstSelected() {
      return this.first;
   }

   @Override
   public void setFirstSelected(int first) {
      if (this.first != first) {
         this.first = first;
         this.text(this.getRealValue() + this.suffix);
         this.fireValueEvents(this.getRealValue());
      }
   }

   public ScrollableLabel event(ValueEvent event) {
      if (this.valueEvents == null) {
         this.valueEvents = new ArrayList<>();
      }

      this.valueEvents.add(event);
      return this;
   }

   public void removeValueEvent(ValueEvent event) {
      if (this.valueEvents != null) {
         this.valueEvents.remove(event);
      }
   }

   private void fireValueEvents(int value) {
      this.fireChannelEvents(TypedMap.builder().put(PARAM_VALUE, value).build());
      if (this.valueEvents != null) {
         for (ValueEvent event : this.valueEvents) {
            event.valueChanged(value);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.suffix = GuiParser.get(command, "suffix", "");
      this.realmin = GuiParser.get(command, "realmin", 0);
      this.realmax = GuiParser.get(command, "realmax", 100);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "suffix", this.suffix, "");
      GuiParser.put(command, "realmin", this.realmin, 0);
      GuiParser.put(command, "realmax", this.realmax, 100);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("scrollablelabel");
   }

   @Override
   public <T> void setGenericValue(T value) {
      this.realValue(TypeConvertors.toInt(value));
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.getRealValue();
   }
}
