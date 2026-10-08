package mcjty.lib.gui.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;

public class Widgets {
   private static final Map<String, Supplier<Widget<?>>> FACTORIES = new HashMap<>();

   @Nullable
   public static Widget<?> createWidget(String type) {
      Supplier<Widget<?>> function = FACTORIES.get(type);
      return function == null ? null : function.get();
   }

   public static Panel positional() {
      return new Panel().layout(new PositionalLayout());
   }

   public static Panel horizontal() {
      return new Panel().layout(new HorizontalLayout());
   }

   public static Panel horizontal(int margin, int spacing) {
      return new Panel().layout(new HorizontalLayout().setHorizontalMargin(margin).setSpacing(spacing));
   }

   public static Panel vertical() {
      return new Panel().layout(new VerticalLayout());
   }

   public static Panel vertical(int margin, int spacing) {
      return new Panel().layout(new VerticalLayout().setVerticalMargin(margin).setSpacing(spacing));
   }

   public static Button button(int x, int y, int w, int h, String text) {
      return new Button().hint(x, y, w, h).text(text);
   }

   public static Button button(String text) {
      return new Button().text(text);
   }

   public static TextField textfield(int x, int y, int w, int h) {
      return new TextField().hint(x, y, w, h);
   }

   public static Label label(int x, int y, int w, int h, String text) {
      return new Label().hint(x, y, w, h).text(text);
   }

   public static Label label(String text) {
      return new Label().text(text);
   }

   public static ImageChoiceLabel imageChoice(int x, int y, int w, int h) {
      return new ImageChoiceLabel().hint(x, y, w, h);
   }

   public static WidgetList list(int x, int y, int w, int h) {
      return new WidgetList().hint(x, y, w, h);
   }

   public static Slider slider(int x, int y, int w, int h) {
      return new Slider().hint(x, y, w, h);
   }

   static {
      FACTORIES.put("blockrender", BlockRender::new);
      FACTORIES.put("button", Button::new);
      FACTORIES.put("label", Label::new);
      FACTORIES.put("choicelabel", ChoiceLabel::new);
      FACTORIES.put("colorchoicelabel", ColorChoiceLabel::new);
      FACTORIES.put("colorselector", ColorSelector::new);
      FACTORIES.put("energybar", EnergyBar::new);
      FACTORIES.put("iconholder", IconHolder::new);
      FACTORIES.put("iconrender", IconRender::new);
      FACTORIES.put("imagechoicelabel", ImageChoiceLabel::new);
      FACTORIES.put("imagelabel", ImageLabel::new);
      FACTORIES.put("panel", Panel::new);
      FACTORIES.put("scrollablelabel", ScrollableLabel::new);
      FACTORIES.put("slider", Slider::new);
      FACTORIES.put("tabbedpanel", TabbedPanel::new);
      FACTORIES.put("textfield", TextField::new);
      FACTORIES.put("integerfield", IntegerField::new);
      FACTORIES.put("floatfield", FloatField::new);
      FACTORIES.put("togglebutton", ToggleButton::new);
      FACTORIES.put("widgetlist", WidgetList::new);
      FACTORIES.put("tagselector", TagSelector::new);
   }
}
