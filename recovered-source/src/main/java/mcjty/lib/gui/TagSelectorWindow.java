package mcjty.lib.gui;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Slider;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.WidgetList;
import mcjty.lib.gui.widgets.Widgets;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;

public class TagSelectorWindow {
   public static final String TYPE_BOTH = "both";
   public static final String TYPE_ITEM = "item";
   public static final String TYPE_BLOCK = "block";
   private String filter = "";
   private String type;
   private Supplier<String> tagGetter;
   private Consumer<String> tagSetter;

   public void create(final Window parentWindow, String type, Consumer<String> tagSetter, Supplier<String> tagGetter, final boolean onlyDoubleClick) {
      Screen gui = parentWindow.getWindowManager().getGui();
      this.tagSetter = tagSetter;
      this.tagGetter = tagGetter;
      this.type = type;
      Panel modalDialog = Widgets.positional().filledRectThickness(2);
      int wx = (int)(parentWindow.getToplevel().getBounds().getX() + 20.0);
      int wy = (int)(parentWindow.getToplevel().getBounds().getY() + 20.0);
      modalDialog.bounds(wx, wy, 200, 156);
      final WidgetList list = Widgets.list(5, 20, 180, 115).name("list");
      Slider slider = Widgets.slider(187, 20, 10, 115).desiredWidth(10).vertical().scrollableName("list");
      Button clear = Widgets.button(5, 136, 60, 15, "Clear");
      Button close = Widgets.button(135, 136, 60, 15, "Close");
      this.refreshList(list, gui);
      TextField filterField = Widgets.textfield(5, 5, 180, 14).text(this.filter).event(newText -> {
         this.filter = newText;
         this.refreshList(list, gui);
      });
      modalDialog.children(close, clear, list, slider, filterField);
      final Window modalWindow = parentWindow.getWindowManager().createModalWindow(modalDialog);
      list.event(new DefaultSelectionEvent() {
         {
            Objects.requireNonNull(TagSelectorWindow.this);
         }

         @Override
         public void select(int index) {
            if (!onlyDoubleClick) {
               TagSelectorWindow.this.selectTag(index, list);
            }
         }

         @Override
         public void doubleClick(int index) {
            TagSelectorWindow.this.selectTag(index, list);
            parentWindow.getWindowManager().closeWindow(modalWindow);
         }
      });
      close.event(() -> parentWindow.getWindowManager().closeWindow(modalWindow));
      clear.event(() -> {
         tagSetter.accept(null);
         parentWindow.getWindowManager().closeWindow(modalWindow);
      });
   }

   private void selectTag(int index, WidgetList list) {
      if (index >= 0 && index < list.getChildCount()) {
         Object t = list.getChild(index).getUserObject();
         this.tagSetter.accept((String)t);
      }
   }

   private List<String> getTags() {
      if ("both".equals(this.type)) {
         Stream<String> itemStream = BuiltInRegistries.ITEM.getTags().map(t -> t.key().location().toString());
         Stream<String> blockStream = BuiltInRegistries.BLOCK.getTags().map(t -> t.key().location().toString());
         Set<String> tags = itemStream.collect(Collectors.toSet());
         blockStream.forEach(tags::add);
         return tags.stream().sorted().collect(Collectors.toList());
      } else if ("item".equals(this.type)) {
         Stream<String> itemStream = BuiltInRegistries.ITEM.getTags().map(t -> t.key().location().toString());
         Set<String> tags = itemStream.collect(Collectors.toSet());
         return tags.stream().sorted().collect(Collectors.toList());
      } else {
         Stream<String> blockStream = BuiltInRegistries.BLOCK.getTags().map(t -> t.key().location().toString());
         Set<String> tags = blockStream.collect(Collectors.toSet());
         return tags.stream().sorted().collect(Collectors.toList());
      }
   }

   private void refreshList(WidgetList list, Screen gui) {
      list.removeChildren();
      String currentTag = this.tagGetter.get();
      int sel = -1;

      for (String tag : this.getTags()) {
         if (tag.contains(this.filter)) {
            Panel panel = Widgets.horizontal(0, 0);
            panel.userObject(tag);
            panel.children(Widgets.label(tag));
            list.children(panel);
            if (tag.equals(currentTag)) {
               sel = list.getChildCount() - 1;
            }
         }
      }

      list.selected(sel);
   }
}
