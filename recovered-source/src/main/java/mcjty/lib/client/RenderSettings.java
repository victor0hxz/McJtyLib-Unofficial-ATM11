package mcjty.lib.client;

import net.minecraft.client.renderer.rendertype.RenderType;

public record RenderSettings(int brightness, int r, int g, int b, int a, float width, RenderType renderType) {
   private RenderSettings(RenderSettings.Builder builder) {
      this(builder.brightness, builder.r, builder.g, builder.b, builder.a, builder.width, builder.renderType);
   }

   public static RenderSettings.Builder builder() {
      return new RenderSettings.Builder();
   }

   public static class Builder {
      private int brightness = 15728880;
      private int r = 255;
      private int g = 255;
      private int b = 255;
      private int a = 255;
      private float width = 1.0F;
      private RenderType renderType = CustomRenderTypes.translucent();

      public RenderSettings.Builder brightness(int brightness) {
         this.brightness = brightness;
         return this;
      }

      public RenderSettings.Builder red(int r) {
         this.r = r;
         return this;
      }

      public RenderSettings.Builder green(int g) {
         this.g = g;
         return this;
      }

      public RenderSettings.Builder blue(int b) {
         this.b = b;
         return this;
      }

      public RenderSettings.Builder alpha(int a) {
         this.a = a;
         return this;
      }

      public RenderSettings.Builder color(int color) {
         this.r = color >> 16 & 0xFF;
         this.g = color >> 8 & 0xFF;
         this.b = color & 0xFF;
         this.a = color >> 24 & 0xFF;
         return this;
      }

      public RenderSettings.Builder color(int r, int g, int b) {
         this.r = r;
         this.g = g;
         this.b = b;
         return this;
      }

      public RenderSettings.Builder width(float width) {
         this.width = width;
         return this;
      }

      public RenderSettings.Builder renderType(RenderType renderType) {
         this.renderType = renderType;
         return this;
      }

      public RenderSettings build() {
         return new RenderSettings(this);
      }
   }
}
