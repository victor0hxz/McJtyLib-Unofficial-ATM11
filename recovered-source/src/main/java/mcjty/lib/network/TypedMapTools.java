package mcjty.lib.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TypedMapTools {
   private static Map<Type<?>, TypedMapTools.ArgumentType> typeToIndex = null;

   private static void setupTypeMapping() {
      if (typeToIndex == null) {
         typeToIndex = new HashMap<>();
         registerMapping(Type.STRING, TypedMapTools.ArgumentType.TYPE_STRING);
         registerMapping(Type.UUID, TypedMapTools.ArgumentType.TYPE_UUID);
         registerMapping(Type.INTEGER, TypedMapTools.ArgumentType.TYPE_INTEGER);
         registerMapping(Type.BLOCKPOS, TypedMapTools.ArgumentType.TYPE_BLOCKPOS);
         registerMapping(Type.DIMENSION_TYPE, TypedMapTools.ArgumentType.TYPE_DIMENSION_TYPE);
         registerMapping(Type.BOOLEAN, TypedMapTools.ArgumentType.TYPE_BOOLEAN);
         registerMapping(Type.DOUBLE, TypedMapTools.ArgumentType.TYPE_DOUBLE);
         registerMapping(Type.FLOAT, TypedMapTools.ArgumentType.TYPE_FLOAT);
         registerMapping(Type.ITEMSTACK, TypedMapTools.ArgumentType.TYPE_STACK);
         registerMapping(Type.LONG, TypedMapTools.ArgumentType.TYPE_LONG);
         registerMapping(Type.STRING_LIST, TypedMapTools.ArgumentType.TYPE_STRING_LIST);
         registerMapping(Type.ITEMSTACK_LIST, TypedMapTools.ArgumentType.TYPE_ITEMSTACK_LIST);
         registerMapping(Type.POS_LIST, TypedMapTools.ArgumentType.TYPE_POS_LIST);
      }
   }

   private static void registerMapping(Type<?> type, TypedMapTools.ArgumentType argumentType) {
      typeToIndex.put(type, argumentType);
   }

   private static TypedMapTools.ArgumentType getArgumentType(Type<?> type) {
      setupTypeMapping();
      return typeToIndex.get(type);
   }

   public static TypedMap readArguments(RegistryFriendlyByteBuf buf) {
      TypedMap.Builder args = TypedMap.builder();
      int size = buf.readInt();
      if (size != 0) {
         for (int i = 0; i < size; i++) {
            readArgument(buf, args::put);
         }
      }

      return args.build();
   }

   public static void readArgument(RegistryFriendlyByteBuf buf, BiConsumer<Key, Object> args) {
      String key = buf.readUtf(32767);
      TypedMapTools.ArgumentType type = TypedMapTools.ArgumentType.getType(buf.readByte());
      switch (type) {
         case TYPE_STRING:
            args.accept(new Key<>(key, Type.STRING), NetworkTools.readStringUTF8(buf));
            break;
         case TYPE_INTEGER:
            args.accept(new Key<>(key, Type.INTEGER), buf.readInt());
            break;
         case TYPE_BLOCKPOS:
            if (buf.readBoolean()) {
               args.accept(new Key<>(key, Type.BLOCKPOS), buf.readBlockPos());
            } else {
               args.accept(new Key<>(key, Type.BLOCKPOS), null);
            }
            break;
         case TYPE_BOOLEAN:
            args.accept(new Key<>(key, Type.BOOLEAN), buf.readBoolean());
            break;
         case TYPE_DOUBLE:
            args.accept(new Key<>(key, Type.DOUBLE), buf.readDouble());
            break;
         case TYPE_STACK:
            if (buf.readBoolean()) {
               args.accept(new Key<>(key, Type.ITEMSTACK), NetworkTools.readItemStack(buf));
            } else {
               args.accept(new Key<>(key, Type.ITEMSTACK), null);
            }
            break;
         case TYPE_LONG:
            args.accept(new Key<>(key, Type.LONG), buf.readLong());
            break;
         case TYPE_STRING_LIST:
            int sxx = buf.readInt();
            if (sxx == -1) {
               args.accept(new Key<>(key, Type.STRING_LIST), null);
            } else {
               List<String> list = new ArrayList<>(sxx);

               for (int j = 0; j < sxx; j++) {
                  list.add(NetworkTools.readStringUTF8(buf));
               }

               args.accept(new Key<>(key, Type.STRING_LIST), list);
            }
            break;
         case TYPE_ITEMSTACK_LIST:
            int sx = buf.readInt();
            if (sx == -1) {
               args.accept(new Key<>(key, Type.ITEMSTACK_LIST), null);
            } else {
               List<ItemStack> list = new ArrayList<>(sx);

               for (int j = 0; j < sx; j++) {
                  list.add(NetworkTools.readItemStack(buf));
               }

               args.accept(new Key<>(key, Type.ITEMSTACK_LIST), list);
            }
            break;
         case TYPE_POS_LIST:
            int s = buf.readInt();
            if (s == -1) {
               args.accept(new Key<>(key, Type.POS_LIST), null);
            } else {
               List<BlockPos> list = new ArrayList<>(s);

               for (int j = 0; j < s; j++) {
                  list.add(buf.readBlockPos());
               }

               args.accept(new Key<>(key, Type.POS_LIST), list);
            }
            break;
         case TYPE_UUID:
            args.accept(new Key<>(key, Type.UUID), buf.readUUID());
            break;
         case TYPE_DIMENSION_TYPE:
            if (buf.readBoolean()) {
               args.accept(new Key<>(key, Type.DIMENSION_TYPE), LevelTools.getId(buf.readIdentifier()));
            } else {
               args.accept(new Key<>(key, Type.DIMENSION_TYPE), null);
            }
            break;
         case TYPE_FLOAT:
            args.accept(new Key<>(key, Type.FLOAT), buf.readFloat());
            break;
         default:
            throw new RuntimeException("Unsupported type for key '" + key + "'!");
      }
   }

   public static void writeArguments(RegistryFriendlyByteBuf buf, TypedMap args) {
      buf.writeInt(args.size());

      for (Key key : args.getKeys()) {
         writeArgument(buf, key, args.get(key));
      }
   }

   public static <T> void writeArgument(RegistryFriendlyByteBuf buf, Key<T> key, T value) {
      buf.writeUtf(key.name());
      TypedMapTools.ArgumentType argumentType = getArgumentType(key.type());
      buf.writeByte(argumentType.ordinal());
      switch (argumentType) {
         case TYPE_STRING:
            NetworkTools.writeStringUTF8(buf, (String)value);
            break;
         case TYPE_INTEGER:
            buf.writeInt((Integer)value);
            break;
         case TYPE_BLOCKPOS:
            BlockPos pos = (BlockPos)value;
            if (pos != null) {
               buf.writeBoolean(true);
               buf.writeBlockPos(pos);
            } else {
               buf.writeBoolean(false);
            }
            break;
         case TYPE_BOOLEAN:
            buf.writeBoolean((Boolean)value);
            break;
         case TYPE_DOUBLE:
            buf.writeDouble((Double)value);
            break;
         case TYPE_STACK:
            ItemStack stack = (ItemStack)value;
            if (stack != null) {
               buf.writeBoolean(true);
               NetworkTools.writeItemStack(buf, stack);
            } else {
               buf.writeBoolean(false);
            }
            break;
         case TYPE_LONG:
            buf.writeLong((Long)value);
            break;
         case TYPE_STRING_LIST:
            List<String> listxx = (List<String>)value;
            if (listxx != null) {
               buf.writeInt(listxx.size());

               for (String s : listxx) {
                  NetworkTools.writeStringUTF8(buf, s);
               }
            } else {
               buf.writeInt(-1);
            }
            break;
         case TYPE_ITEMSTACK_LIST:
            List<ItemStack> listx = (List<ItemStack>)value;
            if (listx != null) {
               buf.writeInt(listx.size());

               for (ItemStack s : listx) {
                  NetworkTools.writeItemStack(buf, s);
               }
            } else {
               buf.writeInt(-1);
            }
            break;
         case TYPE_POS_LIST:
            List<BlockPos> list = (List<BlockPos>)value;
            if (list != null) {
               buf.writeInt(list.size());

               for (BlockPos s : list) {
                  buf.writeBlockPos(s);
               }
            } else {
               buf.writeInt(-1);
            }
            break;
         case TYPE_UUID:
            buf.writeUUID((UUID)value);
            break;
         case TYPE_DIMENSION_TYPE:
            ResourceKey<Level> type = (ResourceKey<Level>)value;
            if (type != null) {
               buf.writeBoolean(true);
               buf.writeIdentifier(type.identifier());
            } else {
               buf.writeBoolean(false);
            }
            break;
         case TYPE_FLOAT:
            buf.writeFloat((Float)value);
      }
   }

   static enum ArgumentType {
      TYPE_STRING(0),
      TYPE_INTEGER(1),
      TYPE_BLOCKPOS(2),
      TYPE_BOOLEAN(3),
      TYPE_DOUBLE(4),
      TYPE_STACK(5),
      TYPE_LONG(6),
      TYPE_STRING_LIST(7),
      TYPE_ITEMSTACK_LIST(8),
      TYPE_POS_LIST(9),
      TYPE_UUID(10),
      TYPE_DIMENSION_TYPE(11),
      TYPE_FLOAT(12);

      private final int index;
      private static final Map<Integer, TypedMapTools.ArgumentType> mapping = new HashMap<>();

      private ArgumentType(int index) {
         this.index = index;
      }

      public int getIndex() {
         return this.index;
      }

      public static TypedMapTools.ArgumentType getType(int index) {
         return mapping.get(index);
      }

      static {
         for (TypedMapTools.ArgumentType type : values()) {
            mapping.put(type.index, type);
         }
      }
   }
}
