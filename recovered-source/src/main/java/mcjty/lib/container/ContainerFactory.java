package mcjty.lib.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.neoforged.neoforge.common.util.Lazy;

public class ContainerFactory {
   public static final Lazy<ContainerFactory> EMPTY = Lazy.of(() -> new ContainerFactory(0));
   private Map<Integer, SlotDefinition> indexToType = new HashMap<>();
   private List<SlotFactory> slots = new ArrayList<>();
   public static final String CONTAINER_CONTAINER = "container";
   public static final String CONTAINER_PLAYER = "player";
   private final int containerSlots;

   public ContainerFactory(int containerSlots) {
      this.containerSlots = containerSlots;
   }

   public int getContainerSlots() {
      return this.containerSlots;
   }

   public Iterable<SlotFactory> getSlots() {
      return this.slots;
   }

   public SlotType getSlotType(int index) {
      SlotDefinition slotDefinition = this.indexToType.get(index);
      return slotDefinition == null ? SlotType.SLOT_UNKNOWN : slotDefinition.getType();
   }

   @Nonnull
   public SlotDefinition getSlotDefinition(int index) {
      SlotDefinition slotDefinition = this.indexToType.get(index);
      if (slotDefinition == null) {
         throw new IllegalStateException("Bad slot specified!");
      } else {
         return slotDefinition;
      }
   }

   public boolean isGenericSlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_GENERIC;
   }

   public boolean isOutputSlot(int index) {
      return this.getSlotDefinition(index).isOutput();
   }

   public boolean isInputSlot(int index) {
      return this.getSlotDefinition(index).isInput();
   }

   public boolean isGhostSlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_GHOST;
   }

   public boolean isGhostOutputSlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_GHOSTOUT;
   }

   public boolean isCraftResultSlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_CRAFTRESULT;
   }

   public boolean isPlayerInventorySlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_PLAYERINV;
   }

   public boolean isSpecificItemSlot(int index) {
      return this.getSlotDefinition(index).isSpecific();
   }

   public boolean isPlayerHotbarSlot(int index) {
      return this.getSlotType(index) == SlotType.SLOT_PLAYERHOTBAR;
   }

   public SlotRanges getRanges(Predicate<SlotDefinition> matcher) {
      SlotRanges ranges = new SlotRanges();
      int idx = 0;

      for (SlotFactory slot : this.slots) {
         if (matcher.test(slot.slotDefinition())) {
            ranges.addSingle(idx);
         }

         idx++;
      }

      return ranges;
   }

   public ContainerFactory slot(SlotDefinition slotDefinition, int index, int x, int y) {
      return this.slot(slotDefinition, "container", index, x, y);
   }

   public ContainerFactory slot(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y) {
      SlotFactory slotFactory = new SlotFactory(slotDefinition, inventoryName, index, x, y);
      int slotIndex = this.slots.size();
      this.slots.add(slotFactory);
      this.indexToType.put(slotIndex, slotDefinition);
      return this;
   }

   public ContainerFactory range(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int amount, int dx) {
      for (int i = 0; i < amount; i++) {
         this.slot(slotDefinition, inventoryName, index, x, y);
         x += dx;
         index++;
      }

      return this;
   }

   public ContainerFactory box(SlotDefinition slotDefinition, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      return this.box(slotDefinition, "container", index, x, y, horAmount, dx, verAmount, dy);
   }

   public ContainerFactory box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      for (int j = 0; j < verAmount; j++) {
         this.range(slotDefinition, inventoryName, index, x, y, horAmount, dx);
         index += horAmount;
         y += dy;
      }

      return this;
   }

   public ContainerFactory box(SlotDefinition slotDefinition, String inventoryName, int index, int x, int y, int horAmount, int verAmount) {
      return this.box(slotDefinition, inventoryName, index, x, y, horAmount, 18, verAmount, 18);
   }

   public ContainerFactory box(SlotDefinition slotDefinition, int index, int x, int y, int horAmount, int verAmount) {
      return this.box(slotDefinition, "container", index, x, y, horAmount, 18, verAmount, 18);
   }

   public ContainerFactory playerSlots(int leftCol, int topRow) {
      this.box(new SlotDefinition(SlotType.SLOT_PLAYERINV), "player", 9, leftCol, topRow, 9, 3);
      topRow += 58;
      this.range(new SlotDefinition(SlotType.SLOT_PLAYERHOTBAR), "player", 0, leftCol, topRow, 9, 18);
      return this;
   }
}
