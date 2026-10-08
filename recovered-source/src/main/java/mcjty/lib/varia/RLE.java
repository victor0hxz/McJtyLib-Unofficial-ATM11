package mcjty.lib.varia;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class RLE {
   private final FastByteArray stream = new FastByteArray();
   private byte[] data = null;
   private int cnt = 0;
   private int prev = -1;
   private int pos;
   private int readcnt;
   private int readvalue;
   public static final StreamCodec<FriendlyByteBuf, RLE> OPTIONAL_STREAM_CODEC = StreamCodec.of((buf, rle) -> {
      if (rle == null) {
         buf.writeBoolean(false);
      } else {
         buf.writeBoolean(true);
         byte[] data = rle.getData();
         buf.writeVarInt(data.length);
         buf.writeBytes(data);
      }
   }, buf -> {
      if (!buf.readBoolean()) {
         return null;
      } else {
         int length = buf.readVarInt();
         byte[] data = new byte[length];
         buf.readBytes(data);
         RLE rle = new RLE();
         rle.setData(data);
         return rle;
      }
   });

   public void add(int c) {
      if (this.prev == -1) {
         this.prev = c;
         this.cnt = 1;
      } else if (this.prev == c && this.cnt < 255) {
         this.cnt++;
      } else {
         this.stream.write(this.cnt);
         this.stream.write(this.prev);
         this.prev = c;
         this.cnt = 1;
      }
   }

   public void reset() {
      this.pos = 0;
      this.readcnt = 0;
   }

   public int read() {
      if (this.readcnt == 0) {
         if (this.pos >= this.data.length) {
            return 0;
         }

         this.readcnt = this.data[this.pos++] & 255;
         this.readvalue = this.data[this.pos++] & 255;
      }

      this.readcnt--;
      return this.readvalue;
   }

   public byte[] getData() {
      if (this.data == null) {
         if (this.prev != -1) {
            this.stream.write(this.cnt);
            this.stream.write(this.prev);
            this.prev = -1;
            this.cnt = 0;
         }

         this.data = this.stream.toByteArray();
      }

      return this.data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }
}
