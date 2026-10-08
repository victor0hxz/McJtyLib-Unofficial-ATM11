package mcjty.lib.multiblock;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

public class MultiblockDriver<T extends IMultiblock> {
   private final Map<Integer, MultiblockHolder<T>> multiblocks = new HashMap<>();
   private int lastId = 0;
   private final Function<CompoundTag, T> loader;
   private final BiConsumer<CompoundTag, T> saver;
   private final Consumer<MultiblockDriver<T>> dirtySetter;
   private final BiPredicate<T, T> mergeChecker;
   private final IMultiblockFixer<T> fixer;
   private final BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter;

   private MultiblockDriver(MultiblockDriver.Builder<T> builder) {
      this.loader = builder.loader;
      this.saver = builder.saver;
      this.dirtySetter = builder.dirtySetter;
      this.mergeChecker = builder.mergeChecker;
      this.fixer = builder.fixer;
      this.holderGetter = builder.holderGetter;
   }

   public void clear() {
      this.multiblocks.clear();
      this.lastId = 0;
   }

   public IMultiblockFixer<T> getFixer() {
      return this.fixer;
   }

   public BiFunction<Level, BlockPos, IMultiblockConnector> getHolderGetter() {
      return this.holderGetter;
   }

   public BiPredicate<T, T> getMergeChecker() {
      return this.mergeChecker;
   }

   public void createOrUpdate(int id, T mb) {
      this.multiblocks.put(id, new MultiblockHolder<>(mb));
   }

   @Nullable
   public T get(int id) {
      MultiblockHolder<T> holder = this.multiblocks.get(id);
      return holder == null ? null : holder.getMb();
   }

   public void delete(int id) {
      this.multiblocks.remove(id);
      this.dirtySetter.accept(this);
   }

   public void modify(int id, Consumer<MultiblockHolder<T>> consumer) {
      MultiblockHolder<T> holder = this.multiblocks.get(id);
      if (holder != null) {
         consumer.accept(holder);
         this.dirtySetter.accept(this);
      }
   }

   public int createId() {
      this.lastId++;
      this.dirtySetter.accept(this);
      return this.lastId;
   }

   public void load(CompoundTag tagCompound) {
      this.clear();
      ListTag lst = (ListTag)tagCompound.getList("mb").orElseGet(ListTag::new);

      for (int i = 0; i < lst.size(); i++) {
         CompoundTag tc = (CompoundTag)lst.getCompound(i).orElseGet(CompoundTag::new);
         int id = tc.getInt("id").orElse(0);
         T value = this.loader.apply(tc);
         MultiblockHolder<T> holder = new MultiblockHolder<>(value);
         holder.load(tc);
         this.multiblocks.put(id, holder);
      }

      this.lastId = tagCompound.getInt("lastId").orElse(0);
   }

   public CompoundTag save(CompoundTag tagCompound) {
      ListTag lst = new ListTag();

      for (Entry<Integer, MultiblockHolder<T>> entry : this.multiblocks.entrySet()) {
         CompoundTag tc = new CompoundTag();
         tc.putInt("id", entry.getKey());
         this.saver.accept(tc, entry.getValue().getMb());
         entry.getValue().save(tc);
         lst.add(tc);
      }

      tagCompound.put("mb", lst);
      tagCompound.putInt("lastId", this.lastId);
      return tagCompound;
   }

   public static <T extends IMultiblock> MultiblockDriver.Builder<T> builder() {
      return new MultiblockDriver.Builder<>();
   }

   public static class Builder<T extends IMultiblock> {
      private Function<CompoundTag, T> loader;
      private BiConsumer<CompoundTag, T> saver;
      private Consumer<MultiblockDriver<T>> dirtySetter;
      private BiPredicate<T, T> mergeChecker;
      private IMultiblockFixer<T> fixer;
      private BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter;

      public MultiblockDriver.Builder<T> loader(Function<CompoundTag, T> loader) {
         this.loader = loader;
         return this;
      }

      public MultiblockDriver.Builder<T> saver(BiConsumer<CompoundTag, T> saver) {
         this.saver = saver;
         return this;
      }

      public MultiblockDriver.Builder<T> dirtySetter(Consumer<MultiblockDriver<T>> dirtySetter) {
         this.dirtySetter = dirtySetter;
         return this;
      }

      public MultiblockDriver.Builder<T> mergeChecker(BiPredicate<T, T> mergeChecker) {
         this.mergeChecker = mergeChecker;
         return this;
      }

      public MultiblockDriver.Builder<T> fixer(IMultiblockFixer<T> fixer) {
         this.fixer = fixer;
         return this;
      }

      public MultiblockDriver.Builder<T> holderGetter(BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter) {
         this.holderGetter = holderGetter;
         return this;
      }

      public MultiblockDriver<T> build() {
         return new MultiblockDriver<>(this);
      }
   }
}
