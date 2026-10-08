package mcjty.lib.font;

public class Formatter {
   public static float[] getFormatted(char c) {
      int[] outrgba = null;

      outrgba = switch (c) {
         case '0' -> new int[]{0, 0, 0, 0, 255};
         case '1' -> new int[]{0, 0, 170, 255};
         case '2' -> new int[]{0, 170, 0, 255};
         case '3' -> new int[]{0, 170, 170, 255};
         case '4' -> new int[]{170, 0, 0, 255};
         case '5' -> new int[]{170, 0, 170, 255};
         case '6' -> new int[]{255, 170, 0, 255};
         case '7' -> new int[]{170, 170, 170, 255};
         case '8' -> new int[]{85, 85, 85, 255};
         case '9' -> new int[]{85, 85, 255, 255};
         default -> new int[]{255, 255, 255, 255};
         case 'a' -> new int[]{85, 255, 85, 255};
         case 'b' -> new int[]{85, 255, 255, 255};
         case 'c' -> new int[]{255, 85, 85, 255};
         case 'd' -> new int[]{85, 255, 255, 255};
         case 'e' -> new int[]{255, 255, 85, 255};
         case 'f' -> new int[]{255, 255, 255, 255};
      };
      float[] outfloat = new float[outrgba.length];

      for (int i = 0; i < outrgba.length; i++) {
         outfloat[i] = outrgba[i] > 0 ? outrgba[i] / 255 : 0.0F;
      }

      return outfloat;
   }
}
