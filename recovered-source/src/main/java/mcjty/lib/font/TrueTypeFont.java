package mcjty.lib.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import mcjty.lib.varia.Logging;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TrueTypeFont {
   public static final int ALIGN_LEFT = 0;
   public static final int ALIGN_RIGHT = 1;
   public static final int ALIGN_CENTER = 2;
   private TrueTypeFont.FloatObject[] charArray = new TrueTypeFont.FloatObject[256];
   private Map<Character, TrueTypeFont.FloatObject> customChars = new HashMap<>();
   protected boolean antiAlias;
   private float fontSize = 0.0F;
   private float fontHeight = 0.0F;
   private int fontTextureID;
   private int textureWidth = 1024;
   private int textureHeight = 1024;
   protected Font font;
   private FontMetrics fontMetrics;
   private int correctL = 9;
   private int correctR = 8;

   public TrueTypeFont(Font font, boolean antiAlias, char[] additionalChars) {
      this.font = font;
      this.fontSize = font.getSize() + 3;
      this.antiAlias = antiAlias;
      this.createSet(additionalChars);
      System.out.println("TrueTypeFont loaded: " + font + " - AntiAlias = " + antiAlias);
      this.fontHeight--;
      if (this.fontHeight <= 0.0F) {
         this.fontHeight = 1.0F;
      }
   }

   public TrueTypeFont(Font font, boolean antiAlias) {
      this(font, antiAlias, null);
   }

   public void setCorrection(boolean on) {
      if (on) {
         this.correctL = 2;
         this.correctR = 1;
      } else {
         this.correctL = 0;
         this.correctR = 0;
      }
   }

   private BufferedImage getFontImage(char ch) {
      BufferedImage tempfontImage = new BufferedImage(1, 1, 2);
      Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
      if (this.antiAlias) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }

      g.setFont(this.font);
      this.fontMetrics = g.getFontMetrics();
      float charwidth = this.fontMetrics.charWidth(ch) + 8;
      if (charwidth <= 0.0F) {
         charwidth = 7.0F;
      }

      float charheight = this.fontMetrics.getHeight() + 3;
      if (charheight <= 0.0F) {
         charheight = this.fontSize;
      }

      BufferedImage fontImage = new BufferedImage((int)charwidth, (int)charheight, 2);
      Graphics2D gt = (Graphics2D)fontImage.getGraphics();
      if (this.antiAlias) {
         gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }

      gt.setFont(this.font);
      gt.setColor(Color.WHITE);
      int charx = 3;
      int chary = 1;
      gt.drawString(String.valueOf(ch), charx, chary + this.fontMetrics.getAscent());
      return fontImage;
   }

   private void createSet(char[] customCharsArray) {
      if (customCharsArray != null && customCharsArray.length > 0) {
         this.textureWidth *= 2;
      }

      try {
         BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
         Graphics2D g = (Graphics2D)imgTemp.getGraphics();
         g.setColor(new Color(0, 0, 0, 1));
         g.fillRect(0, 0, this.textureWidth, this.textureHeight);
         float rowHeight = 0.0F;
         float positionX = 0.0F;
         float positionY = 0.0F;
         int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;

         for (int i = 0; i < 256 + customCharsLength; i++) {
            char ch = i < 256 ? (char)i : customCharsArray[i - 256];
            BufferedImage fontImage = this.getFontImage(ch);
            TrueTypeFont.FloatObject newIntObject = new TrueTypeFont.FloatObject();
            newIntObject.width = fontImage.getWidth();
            newIntObject.height = fontImage.getHeight();
            if (positionX + newIntObject.width >= this.textureWidth) {
               positionX = 0.0F;
               positionY += rowHeight;
               rowHeight = 0.0F;
            }

            newIntObject.storedX = positionX;
            newIntObject.storedY = positionY;
            if (newIntObject.height > this.fontHeight) {
               this.fontHeight = newIntObject.height;
            }

            if (newIntObject.height > rowHeight) {
               rowHeight = newIntObject.height;
            }

            g.drawImage(fontImage, (int)positionX, (int)positionY, null);
            positionX += newIntObject.width;
            if (i < 256) {
               this.charArray[i] = newIntObject;
            } else {
               this.customChars.put(ch, newIntObject);
            }

            Object var13 = null;
         }

         this.fontTextureID = loadImage(imgTemp);
      } catch (RuntimeException var12) {
         Logging.logError("Failed to create font!", var12);
      }
   }

   private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2) {
      float DrawWidth = drawX2 - drawX;
      float DrawHeight = drawY2 - drawY;
      float TextureSrcX = srcX / this.textureWidth;
      float TextureSrcY = srcY / this.textureHeight;
      float SrcWidth = srcX2 - srcX;
      float SrcHeight = srcY2 - srcY;
      float RenderWidth = SrcWidth / this.textureWidth;
      float RenderHeight = SrcHeight / this.textureHeight;
      GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
      GL11.glVertex2f(drawX, drawY);
      GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
      GL11.glVertex2f(drawX, drawY + DrawHeight);
      GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
      GL11.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
      GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
      GL11.glVertex2f(drawX + DrawWidth, drawY);
   }

   public float getWidth(String whatchars) {
      float totalwidth = 0.0F;
      int currentChar = 0;
      float lastWidth = -10.0F;

      for (int i = 0; i < whatchars.length(); i++) {
         int var7 = whatchars.charAt(i);
         TrueTypeFont.FloatObject floatObject;
         if (var7 < 256) {
            floatObject = this.charArray[var7];
         } else {
            floatObject = this.customChars.get((char)var7);
         }

         if (floatObject != null) {
            totalwidth += floatObject.width / 2.0F;
            lastWidth = floatObject.width;
         }
      }

      return this.fontMetrics.stringWidth(whatchars);
   }

   public float getHeight() {
      return this.fontHeight;
   }

   public float getHeight(String HeightString) {
      return this.fontHeight;
   }

   public float getLineHeight() {
      return this.fontHeight;
   }

   public String trimStringToWidth(String text, int width) {
      StringBuilder stringbuilder = new StringBuilder();
      int i = 0;
      int j = 0;
      int k = 1;
      boolean flag = false;
      boolean flag1 = false;

      for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
         char c0 = text.charAt(l);
         int i1 = (int)this.getWidth(String.valueOf(c0));
         if (flag) {
            flag = false;
            if (c0 == 'l' || c0 == 'L') {
               flag1 = true;
            } else if (c0 == 'r' || c0 == 'R') {
               flag1 = false;
            }
         } else if (i1 < 0) {
            flag = true;
         } else {
            i += i1;
            if (flag1) {
               i++;
            }
         }

         if (i > width) {
            break;
         }

         stringbuilder.append(c0);
      }

      return stringbuilder.toString();
   }

   public void drawString(float x, float y, String text, float scaleX, float scaleY, float yoffset, float... rgba) {
      if (rgba.length == 0) {
         rgba = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      }

      this.drawString(x, y, text, scaleX, scaleY, 0, yoffset, rgba);
   }

   public void drawString(float x, float y, String text, float scaleX, float scaleY, int format, float yoffset, float... rgba) {
      if (rgba.length == 0) {
         rgba = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      }

      for (int i = text.indexOf(167); i != -1 && i + 1 < text.length(); i = text.indexOf(167)) {
         String left = text.substring(0, i);
         if (!left.isEmpty()) {
            this.drawTextInternal(x, y, left, scaleX, scaleY, format, rgba);
            x += this.getWidth(left);
         }

         int colorCode = 0;
         if (colorCode != -1) {
            float r = (colorCode >> 16) / 255.0F;
            float g = (colorCode >> 8 & 0xFF) / 255.0F;
            float b = (colorCode & 0xFF) / 255.0F;
            rgba = new float[]{r, g, b, rgba[3]};
         }

         text = text.substring(i + 2);
      }

      this.drawTextInternal(x, y, text, scaleX, scaleY, format, rgba);
   }

   private void drawTextInternal(float x, float y, String whatchars, float scaleX, float scaleY, int format, float[] rgba) {
      int endIndex = whatchars.length() - 1;
      float totalwidth = 0.0F;
      int i = 0;
      float startY = 0.0F;
      int d;
      int c;
      switch (format) {
         case 1:
            d = -1;

            for (c = this.correctR; i < endIndex; i++) {
               if (whatchars.charAt(i) == '\n') {
                  startY -= this.fontHeight;
               }
            }
            break;
         case 2:
            for (int l = 0; l <= endIndex; l++) {
               int charCurrent = whatchars.charAt(l);
               if (charCurrent == 10) {
                  break;
               }

               TrueTypeFont.FloatObject floatObject;
               if (charCurrent < 256) {
                  floatObject = this.charArray[charCurrent];
               } else {
                  floatObject = this.customChars.get((char)charCurrent);
               }

               totalwidth += floatObject.width - this.correctL;
            }

            totalwidth /= -2.0F;
         case 0:
         default:
            d = 1;
            c = this.correctL;
      }

      if (rgba.length == 4) {
      }

      for (; i >= 0 && i <= endIndex; i += d) {
         int charCurrentx = whatchars.charAt(i);
         TrueTypeFont.FloatObject floatObject;
         if (charCurrentx < 256) {
            floatObject = this.charArray[charCurrentx];
         } else {
            floatObject = this.customChars.get((char)charCurrentx);
         }

         if (floatObject != null) {
            if (d < 0) {
               totalwidth += (floatObject.width - c) * d;
            }

            if (charCurrentx == 10) {
               startY -= this.fontHeight * d;
               totalwidth = 0.0F;
               if (format == 2) {
                  for (int l = i + 1; l <= endIndex; l++) {
                     int var18 = whatchars.charAt(l);
                     if (var18 == '\n') {
                        break;
                     }

                     if (var18 < 256) {
                        floatObject = this.charArray[var18];
                     } else {
                        floatObject = this.customChars.get((char)var18);
                     }

                     totalwidth += floatObject.width - this.correctL;
                  }

                  totalwidth /= -2.0F;
               }
            } else {
               this.drawQuad(
                  totalwidth + floatObject.width + x / scaleX,
                  startY + y / scaleY,
                  totalwidth + x / scaleX,
                  startY + floatObject.height + y / scaleY,
                  floatObject.storedX + floatObject.width,
                  floatObject.storedY + floatObject.height,
                  floatObject.storedX,
                  floatObject.storedY
               );
               if (d > 0) {
                  totalwidth += (floatObject.width - c) * d;
               }
            }
         }
      }
   }

   public static int loadImage(BufferedImage bufferedImage) {
      try {
         short width = (short)bufferedImage.getWidth();
         short height = (short)bufferedImage.getHeight();
         int bpp = (byte)bufferedImage.getColorModel().getPixelSize();
         DataBuffer db = bufferedImage.getData().getDataBuffer();
         ByteBuffer byteBuffer;
         if (db instanceof DataBufferInt) {
            int[] intI = ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
            byte[] newI = new byte[intI.length * 4];

            for (int i = 0; i < intI.length; i++) {
               byte[] b = intToByteArray(intI[i]);
               int newIndex = i * 4;
               newI[newIndex] = b[1];
               newI[newIndex + 1] = b[2];
               newI[newIndex + 2] = b[3];
               newI[newIndex + 3] = b[0];
            }

            byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(newI);
         } else {
            byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8))
               .order(ByteOrder.nativeOrder())
               .put(((DataBufferByte)bufferedImage.getData().getDataBuffer()).getData());
         }

         byteBuffer.flip();
         int internalFormat = 32856;
         int format = 6408;
         IntBuffer textureId = BufferUtils.createIntBuffer(1);
         GL11.glGenTextures(textureId);
         GL11.glBindTexture(3553, textureId.get(0));
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexEnvf(8960, 8704, 8448.0F);
         return textureId.get(0);
      } catch (RuntimeException var11) {
         Logging.logError("Failed to create font!", var11);
         return -1;
      }
   }

   public static boolean isSupported(String fontname) {
      Font[] font = getFonts();

      for (int i = font.length - 1; i >= 0; i--) {
         if (font[i].getName().equalsIgnoreCase(fontname)) {
            return true;
         }
      }

      return false;
   }

   public static Font[] getFonts() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
   }

   public static byte[] intToByteArray(int value) {
      return new byte[]{(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
   }

   public void destroy() {
      IntBuffer scratch = BufferUtils.createIntBuffer(1);
      scratch.put(0, this.fontTextureID);
      GL11.glBindTexture(3553, 0);
      GL11.glDeleteTextures(scratch);
   }

   private static class FloatObject {
      public float width;
      public float height;
      public float storedX;
      public float storedY;
   }
}
