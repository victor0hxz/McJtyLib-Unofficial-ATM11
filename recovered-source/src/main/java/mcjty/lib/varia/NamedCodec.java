package mcjty.lib.varia;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.HashMap;
import java.util.Map;
import mcjty.lib.typed.Type;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NamedCodec<T> {
   private final Codec<T> codec;
   private final T value;

   private NamedCodec(Codec<T> codec, T value) {
      this.codec = codec;
      this.value = value;
   }

   public static <T> NamedCodec<T> map(Codec<T> codec, T value) {
      return new NamedCodec<>(codec, value);
   }

   public static <T> Object get(Codec<T> codec, T value, String name) {
      return map(codec, value).get(name);
   }

   public Object get(String name) {
      DataResult<Tag> result = this.codec.encodeStart(NbtOps.INSTANCE, this.value);
      Tag tag = (Tag)result.getOrThrow();
      Map<String, Object> map = new HashMap<>();
      this.scanTagForRead(tag, "", map);
      return map.get(name);
   }

   public T set(String name, Object v) {
      DataResult<Tag> result = this.codec.encodeStart(NbtOps.INSTANCE, this.value);
      Tag tag = (Tag)result.getOrThrow();
      this.scanTagForWrite((CompoundTag)tag, name, v);
      Pair<T, Tag> resultOut = (Pair<T, Tag>)this.codec.decode(NbtOps.INSTANCE, tag).getOrThrow();
      return (T)resultOut.getFirst();
   }

   private byte convertToByte(Object v) {
      if (v instanceof Byte) {
         return (Byte)v;
      } else if (v instanceof Integer) {
         return ((Integer)v).byteValue();
      } else if (v instanceof Long) {
         return ((Long)v).byteValue();
      } else if (v instanceof Short) {
         return ((Short)v).byteValue();
      } else if (v instanceof Float) {
         return ((Float)v).byteValue();
      } else if (v instanceof Double) {
         return ((Double)v).byteValue();
      } else if (v instanceof Boolean) {
         return (byte)((Boolean)v ? 1 : 0);
      } else if (v instanceof String) {
         try {
            return Byte.parseByte((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to byte");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to byte");
      }
   }

   private int convertToInt(Object v) {
      if (v instanceof Byte) {
         return (Byte)v;
      } else if (v instanceof Integer) {
         return (Integer)v;
      } else if (v instanceof Long) {
         return ((Long)v).intValue();
      } else if (v instanceof Short) {
         return (Short)v;
      } else if (v instanceof Float) {
         return (int)((Float)v).floatValue();
      } else if (v instanceof Double) {
         return (int)((Double)v).doubleValue();
      } else if (v instanceof Boolean) {
         return (Boolean)v ? 1 : 0;
      } else if (v instanceof String) {
         try {
            return Integer.parseInt((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to int");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to int");
      }
   }

   private short convertToShort(Object v) {
      if (v instanceof Byte) {
         return ((Byte)v).byteValue();
      } else if (v instanceof Integer) {
         return ((Integer)v).shortValue();
      } else if (v instanceof Long) {
         return ((Long)v).shortValue();
      } else if (v instanceof Short) {
         return (Short)v;
      } else if (v instanceof Float) {
         return (short)((Float)v).floatValue();
      } else if (v instanceof Double) {
         return (short)((Double)v).doubleValue();
      } else if (v instanceof Boolean) {
         return (short)((Boolean)v ? 1 : 0);
      } else if (v instanceof String) {
         try {
            return Short.parseShort((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to short");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to short");
      }
   }

   private long convertToLong(Object v) {
      if (v instanceof Byte) {
         return ((Byte)v).byteValue();
      } else if (v instanceof Integer) {
         return ((Integer)v).intValue();
      } else if (v instanceof Long) {
         return (Long)v;
      } else if (v instanceof Short) {
         return ((Short)v).shortValue();
      } else if (v instanceof Float) {
         return (long)((Float)v).floatValue();
      } else if (v instanceof Double) {
         return (long)((Double)v).doubleValue();
      } else if (v instanceof Boolean) {
         return (Boolean)v ? 1L : 0L;
      } else if (v instanceof String) {
         try {
            return Long.parseLong((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to long");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to long");
      }
   }

   private float convertToFloat(Object v) {
      if (v instanceof Byte) {
         return ((Byte)v).byteValue();
      } else if (v instanceof Integer) {
         return ((Integer)v).intValue();
      } else if (v instanceof Long) {
         return (float)((Long)v).longValue();
      } else if (v instanceof Short) {
         return ((Short)v).shortValue();
      } else if (v instanceof Float) {
         return (Float)v;
      } else if (v instanceof Double) {
         return (float)((Double)v).doubleValue();
      } else if (v instanceof Boolean) {
         return (Boolean)v ? 1.0F : 0.0F;
      } else if (v instanceof String) {
         try {
            return Float.parseFloat((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to float");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to float");
      }
   }

   private double convertToDouble(Object v) {
      if (v instanceof Byte) {
         return ((Byte)v).byteValue();
      } else if (v instanceof Integer) {
         return ((Integer)v).intValue();
      } else if (v instanceof Long) {
         return ((Long)v).longValue();
      } else if (v instanceof Short) {
         return ((Short)v).shortValue();
      } else if (v instanceof Float) {
         return ((Float)v).floatValue();
      } else if (v instanceof Double) {
         return (Double)v;
      } else if (v instanceof Boolean) {
         return (Boolean)v ? 1.0 : 0.0;
      } else if (v instanceof String) {
         try {
            return Double.parseDouble((String)v);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Cannot convert " + v + " to double");
         }
      } else {
         throw new IllegalArgumentException("Cannot convert " + v + " to double");
      }
   }

   public Type<?> getType(String attributeName) {
      DataResult<Tag> result = this.codec.encodeStart(NbtOps.INSTANCE, this.value);
      Tag tag = (Tag)result.getOrThrow();
      return this.scanTagForType((CompoundTag)tag, attributeName);
   }

   private Type<?> scanTagForType(CompoundTag tag, String key) {
      if (tag == null) {
         return Type.OBJECT;
      } else {
         if (tag.contains(key)) {
            Tag subTag = tag.get(key);
            switch (subTag.getId()) {
               case 1:
               case 2:
               case 3:
                  return Type.INTEGER;
               case 4:
                  return Type.LONG;
               case 5:
                  return Type.FLOAT;
               case 6:
                  return Type.DOUBLE;
               case 7:
               default:
                  break;
               case 8:
                  return Type.STRING;
            }
         }

         for (String k : tag.keySet()) {
            Tag subTag = tag.get(k);
            switch (subTag.getId()) {
               case 10:
                  return this.scanTagForType((CompoundTag)subTag, key);
            }
         }

         return Type.OBJECT;
      }
   }

   private void scanTagForRead(Tag tag, String key, Map<String, Object> map) {
      if (tag != null) {
         switch (tag.getId()) {
            case 1:
               map.put(key, ((ByteTag)tag).byteValue());
               break;
            case 2:
               map.put(key, ((ShortTag)tag).shortValue());
               break;
            case 3:
               map.put(key, ((IntTag)tag).intValue());
               break;
            case 4:
               map.put(key, ((LongTag)tag).longValue());
               break;
            case 5:
               map.put(key, ((FloatTag)tag).floatValue());
               break;
            case 6:
               map.put(key, ((DoubleTag)tag).doubleValue());
            case 7:
            case 9:
            default:
               break;
            case 8:
               map.put(key, ((StringTag)tag).value());
               break;
            case 10:
               for (String k : ((CompoundTag)tag).keySet()) {
                  this.scanTagForRead(((CompoundTag)tag).get(k), k, map);
               }
         }
      }
   }

   private boolean scanTagForWrite(CompoundTag parent, String key, Object v) {
      if (parent.contains(key)) {
         Tag tag = parent.get(key);
         switch (tag.getId()) {
            case 1:
               parent.putByte(key, this.convertToByte(v));
               return true;
            case 2:
               parent.putShort(key, this.convertToShort(v));
               return true;
            case 3:
               parent.putInt(key, this.convertToInt(v));
               return true;
            case 4:
               parent.putLong(key, this.convertToLong(v));
               return true;
            case 5:
               parent.putFloat(key, this.convertToFloat(v));
               return true;
            case 6:
               parent.putDouble(key, this.convertToDouble(v));
               return true;
            case 7:
            default:
               break;
            case 8:
               parent.putString(key, v.toString());
               return true;
         }
      }

      for (String k : parent.keySet()) {
         Tag tag = parent.get(k);
         switch (tag.getId()) {
            case 10:
               if (this.scanTagForWrite((CompoundTag)tag, key, v)) {
                  return true;
               }
         }
      }

      return false;
   }
}
