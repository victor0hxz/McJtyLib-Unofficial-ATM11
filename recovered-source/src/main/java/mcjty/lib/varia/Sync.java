package mcjty.lib.varia;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.ValueHolder;
import mcjty.lib.typed.Key;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.DataSlot;

public class Sync {
   public static IContainerDataListener values(final Identifier id, final GenericTileEntity te) {
      return new IContainerDataListener() {
         private Map<Key, Object> oldValues = new HashMap<>();

         @Override
         public Identifier getId() {
            return id;
         }

         private void copyToOld() {
            this.oldValues.clear();

            for (ValueHolder value : te.getValueMap().values()) {
               Object v = value.getter().apply(te);
               this.oldValues.put(value.key(), v);
            }
         }

         @Override
         public boolean isDirtyAndClear() {
            for (ValueHolder value : te.getValueMap().values()) {
               Object v = value.getter().apply(te);
               Key<?> key = value.key();
               if (!this.oldValues.containsKey(key) || !Objects.equals(this.oldValues.get(key), v)) {
                  this.copyToOld();
                  return true;
               }
            }

            return false;
         }

         @Override
         public void toBytes(RegistryFriendlyByteBuf buf) {
            for (ValueHolder value : te.getValueMap().values()) {
               Object v = value.getter().apply(te);
               value.key().type().serialize(buf, v);
            }
         }

         @Override
         public void readBuf(RegistryFriendlyByteBuf buf) {
            for (ValueHolder value : te.getValueMap().values()) {
               value.key().type().deserialize(buf, value, te);
            }
         }
      };
   }

   public static IContainerDataListener string(final Identifier id, final Supplier<String> getter, final Consumer<String> setter) {
      return new IContainerDataListener() {
         private String oldString = null;

         @Override
         public Identifier getId() {
            return id;
         }

         @Override
         public boolean isDirtyAndClear() {
            String newValue = getter.get();
            if (!Objects.equals(newValue, this.oldString)) {
               this.oldString = newValue;
               return true;
            } else {
               return false;
            }
         }

         @Override
         public void toBytes(RegistryFriendlyByteBuf buf) {
            buf.writeUtf(getter.get());
         }

         @Override
         public void readBuf(RegistryFriendlyByteBuf buf) {
            setter.accept(buf.readUtf(32767));
         }
      };
   }

   public static IContainerDataListener flt(final Identifier id, final Supplier<Float> getter, final Consumer<Float> setter) {
      return new IContainerDataListener() {
         private Float oldFloat = null;

         @Override
         public Identifier getId() {
            return id;
         }

         @Override
         public boolean isDirtyAndClear() {
            Float newValue = getter.get();
            if (!Objects.equals(newValue, this.oldFloat)) {
               this.oldFloat = newValue;
               return true;
            } else {
               return false;
            }
         }

         @Override
         public void toBytes(RegistryFriendlyByteBuf buf) {
            buf.writeFloat(getter.get());
         }

         @Override
         public void readBuf(RegistryFriendlyByteBuf buf) {
            setter.accept(buf.readFloat());
         }
      };
   }

   public static DataSlot integer(final Supplier<Integer> getter, final Consumer<Integer> setter) {
      return new DataSlot() {
         public int get() {
            return getter.get();
         }

         public void set(int v) {
            setter.accept(v);
         }
      };
   }

   public static DataSlot shortint(final Supplier<Short> getter, final Consumer<Short> setter) {
      return new DataSlot() {
         public int get() {
            return getter.get();
         }

         public void set(int v) {
            setter.accept((short)v);
         }
      };
   }

   public static <T extends Enum<T>> DataSlot enumeration(final Supplier<T> getter, final Consumer<T> setter, final T[] values) {
      return new DataSlot() {
         public int get() {
            return getter.get().ordinal();
         }

         public void set(int v) {
            setter.accept(values[v]);
         }
      };
   }

   public static DataSlot bool(final Supplier<Boolean> getter, final Consumer<Boolean> setter) {
      return new DataSlot() {
         public int get() {
            return getter.get() ? 1 : 0;
         }

         public void set(int v) {
            setter.accept(v != 0);
         }
      };
   }
}
