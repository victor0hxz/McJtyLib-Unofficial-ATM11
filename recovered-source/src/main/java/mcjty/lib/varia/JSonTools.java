package mcjty.lib.varia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public class JSonTools {
   public static Optional<JsonElement> getElement(JsonObject element, String name) {
      JsonElement el = element.get(name);
      return el != null ? Optional.of(el) : Optional.empty();
   }

   public static int get(JsonObject object, String name, int def) {
      return object.has(name) ? object.get(name).getAsInt() : def;
   }

   public static boolean get(JsonObject object, String name, boolean def) {
      return object.has(name) ? object.get(name).getAsBoolean() : def;
   }

   public static String get(JsonObject object, String name, String def) {
      return object.has(name) ? object.get(name).getAsString() : def;
   }

   public static void put(JsonObject object, String name, Integer value, Integer def) {
      if (value != null) {
         if (!value.equals(def)) {
            object.add(name, new JsonPrimitive(value));
         }
      }
   }

   public static void put(JsonObject object, String name, Boolean value, Boolean def) {
      if (value != null) {
         if (!value.equals(def)) {
            object.add(name, new JsonPrimitive(value));
         }
      }
   }

   public static void put(JsonObject object, String name, String value, String def) {
      if (value != null) {
         if (!value.equals(def)) {
            object.add(name, new JsonPrimitive(value));
         }
      }
   }

   public static void putStringList(JsonObject object, String name, @Nullable List<String> list) {
      if (list != null) {
         if (list.size() == 1) {
            object.add(name, new JsonPrimitive(list.get(0)));
         } else {
            JsonArray array = new JsonArray();

            for (String s : list) {
               array.add(new JsonPrimitive(s));
            }

            object.add(name, array);
         }
      }
   }

   @Nullable
   public static List<String> getStringList(JsonObject object, String name) {
      if (!object.has(name)) {
         return null;
      } else if (!object.get(name).isJsonArray()) {
         return Collections.singletonList(object.get(name).getAsString());
      } else {
         JsonArray array = object.getAsJsonArray(name);
         List<String> result = new ArrayList<>();

         for (JsonElement element : array) {
            result.add(element.getAsString());
         }

         return result;
      }
   }

   public static JsonElement itemStackToJson(ItemStack item) {
      return (JsonElement)ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, item).result().orElseThrow(RuntimeException::new);
   }

   public static ItemStack jsonToItemStack(JsonObject obj) {
      return (ItemStack)ItemStack.CODEC.parse(JsonOps.INSTANCE, obj).result().orElseThrow(RuntimeException::new);
   }
}
