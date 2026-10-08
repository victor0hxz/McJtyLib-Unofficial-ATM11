package mcjty.lib.varia;

public class Check32 {
   private static final int POLY = -306674912;
   private int crc = -1;

   public void add(int c) {
      this.crc ^= c;

      for (int j = 0; j < 8; j++) {
         if ((this.crc & 1) != 0) {
            this.crc = this.crc >> 1 ^ -306674912;
         } else {
            this.crc >>= 1;
         }
      }
   }

   public int get() {
      return ~this.crc;
   }

   public static int test(int... c) {
      Check32 check = new Check32();
      check.add(c.length);

      for (int i : c) {
         check.add(i);
      }

      return check.get();
   }

   public static void main(String[] args) {
      System.out.println("test(1,2,1000,4,393,120) = " + test(1, 2, 1000, 4, 393, 120));
      System.out.println("test(1,5,1000,4,393,120) = " + test(1, 5, 1000, 4, 393, 120));
      System.out.println("test(1,2,1000,4,193,120) = " + test(1, 2, 1000, 4, 193, 120));
      System.out.println("test(1,2,1000,4,393,121) = " + test(1, 2, 1000, 4, 393, 121));
      System.out.println("test(1,2,1000,1,393,120) = " + test(1, 2, 1000, 1, 393, 120));
      System.out.println("test(54,2,1000,4,393,120) = " + test(54, 2, 1000, 4, 393, 120));
      System.out.println("test(30,2,1000,4,393,120) = " + test(30, 2, 1000, 4, 393, 120));
   }
}
