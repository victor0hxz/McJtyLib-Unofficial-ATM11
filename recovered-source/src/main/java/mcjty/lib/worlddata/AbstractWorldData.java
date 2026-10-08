package mcjty.lib.worlddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mcjty.lib.varia.LevelTools;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends SavedData {
   protected AbstractWorldData() {
   }

   public void save() {
      this.setDirty();
   }

   public abstract CompoundTag save(CompoundTag var1, Provider var2);

   @Nonnull
   public static <T extends AbstractWorldData<T>> T getData(Level world, Function<CompoundTag, T> loader, Supplier<T> supplier, String name) {
      if (world.isClientSide()) {
         throw new RuntimeException("Don't access this client-side!");
      } else {
         Provider provider = world.registryAccess();
         Codec<T> codec = Codec.PASSTHROUGH.xmap(dynamic -> {
            Tag tag = (Tag)dynamic.convert(NbtOps.INSTANCE).getValue();
            return loader.apply(tag instanceof CompoundTag compound ? compound : new CompoundTag());
         }, value -> new Dynamic(NbtOps.INSTANCE, value.save(new CompoundTag(), provider)));
         Identifier id = name.indexOf(58) >= 0 ? Identifier.parse(name) : Identifier.fromNamespaceAndPath("mcjtylib", name.toLowerCase(Locale.ROOT));
         SavedDataType<T> type = new SavedDataType(id, supplier, codec, null);
         SavedDataStorage storage = LevelTools.getOverworld(world).getDataStorage();
         return (T)storage.computeIfAbsent(type);
      }
   }
}
