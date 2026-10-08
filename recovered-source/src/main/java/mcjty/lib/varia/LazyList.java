package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;

public class LazyList<T> {
   private List<T> internalList = new ArrayList<>();
   private List<T> list;
   private boolean needsCopy = false;

   public LazyList() {
      this.list = this.internalList;
   }

   public void copyList(List<T> list) {
      this.list = list;
      this.needsCopy = true;
   }

   public void add(T object) {
      this.resolve();
      this.list.add(object);
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public void clear() {
      this.needsCopy = false;
      this.list = this.internalList;
      this.list.clear();
   }

   public List<T> getList() {
      return this.list;
   }

   public List<T> extractList() {
      if (this.needsCopy) {
         List<T> extracted = new ArrayList<>(this.list);
         this.clear();
         return extracted;
      } else {
         List<T> extracted = this.internalList;
         this.internalList = new ArrayList<>();
         return extracted;
      }
   }

   private void resolve() {
      if (this.needsCopy) {
         this.needsCopy = false;
         this.list = new ArrayList<>(this.list);
      }
   }
}
