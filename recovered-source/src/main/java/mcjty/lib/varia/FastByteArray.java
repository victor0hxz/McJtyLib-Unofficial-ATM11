package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;

public class FastByteArray {
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private List<byte[]> buffers;
   private byte[] buffer;
   private final int blockSize;
   private int index;
   private int size;

   public FastByteArray() {
      this(8192);
   }

   public FastByteArray(int aSize) {
      this.blockSize = aSize;
      this.buffer = new byte[this.blockSize];
   }

   public int getSize() {
      return this.size + this.index;
   }

   public byte[] toByteArray() {
      byte[] data = new byte[this.getSize()];
      int pos = 0;
      if (this.buffers != null) {
         for (byte[] bytes : this.buffers) {
            System.arraycopy(bytes, 0, data, pos, this.blockSize);
            pos += this.blockSize;
         }
      }

      System.arraycopy(this.buffer, 0, data, pos, this.index);
      return data;
   }

   @Override
   public String toString() {
      return new String(this.toByteArray());
   }

   public void write(int datum) {
      if (this.index == this.blockSize) {
         this.addBuffer();
      }

      this.buffer[this.index++] = (byte)datum;
   }

   private void addBuffer() {
      if (this.buffers == null) {
         this.buffers = new ArrayList<>();
      }

      this.buffers.add(this.buffer);
      this.buffer = new byte[this.blockSize];
      this.size = this.size + this.index;
      this.index = 0;
   }
}
