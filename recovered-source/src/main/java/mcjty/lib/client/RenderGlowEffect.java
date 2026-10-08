package mcjty.lib.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RenderGlowEffect {
   private static final RenderGlowEffect.Quad[] QUADS = new RenderGlowEffect.Quad[]{
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(0.0F, 0.0F, 0.0F),
         new RenderGlowEffect.Vt(1.0F, 0.0F, 0.0F),
         new RenderGlowEffect.Vt(1.0F, 0.0F, 1.0F),
         new RenderGlowEffect.Vt(0.0F, 0.0F, 1.0F)
      ),
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(0.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(1.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(1.0F, 1.0F, 0.0F),
         new RenderGlowEffect.Vt(0.0F, 1.0F, 0.0F)
      ),
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(1.0F, 1.0F, 0.0F),
         new RenderGlowEffect.Vt(1.0F, 0.0F, 0.0F),
         new RenderGlowEffect.Vt(0.0F, 0.0F, 0.0F),
         new RenderGlowEffect.Vt(0.0F, 1.0F, 0.0F)
      ),
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(1.0F, 0.0F, 1.0F),
         new RenderGlowEffect.Vt(1.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(0.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(0.0F, 0.0F, 1.0F)
      ),
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(0.0F, 0.0F, 1.0F),
         new RenderGlowEffect.Vt(0.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(0.0F, 1.0F, 0.0F),
         new RenderGlowEffect.Vt(0.0F, 0.0F, 0.0F)
      ),
      new RenderGlowEffect.Quad(
         new RenderGlowEffect.Vt(1.0F, 0.0F, 0.0F),
         new RenderGlowEffect.Vt(1.0F, 1.0F, 0.0F),
         new RenderGlowEffect.Vt(1.0F, 1.0F, 1.0F),
         new RenderGlowEffect.Vt(1.0F, 0.0F, 1.0F)
      )
   };

   public static void renderGlow(PoseStack matrixStack, MultiBufferSource buffer, Identifier texture) {
   }

   public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset, Vec3 offs) {
      int b1 = 15728880;
      int b2 = 15728880;
      RenderGlowEffect.Quad quad = QUADS[side];
      buffer.addVertex((float)(offs.x + quad.v1.x * mult + offset), (float)(offs.y + quad.v1.y * mult + offset), (float)(offs.z + quad.v1.z * mult + offset))
         .setUv(0.0F, 0.0F)
         .setUv2(b1, b2)
         .setColor(255, 255, 255, 128);
      buffer.addVertex((float)(offs.x + quad.v2.x * mult + offset), (float)(offs.y + quad.v2.y * mult + offset), (float)(offs.z + quad.v2.z * mult + offset))
         .setUv(0.0F, 1.0F)
         .setUv2(b1, b2)
         .setColor(255, 255, 255, 128);
      buffer.addVertex((float)(offs.x + quad.v3.x * mult + offset), (float)(offs.y + quad.v3.y * mult + offset), (float)(offs.z + quad.v3.z * mult + offset))
         .setUv(1.0F, 1.0F)
         .setUv2(b1, b2)
         .setColor(255, 255, 255, 128);
      buffer.addVertex((float)(offs.x + quad.v4.x * mult + offset), (float)(offs.y + quad.v4.y * mult + offset), (float)(offs.z + quad.v4.z * mult + offset))
         .setUv(1.0F, 0.0F)
         .setUv2(b1, b2)
         .setColor(255, 255, 255, 128);
   }

   public static void addSideFullTexture(Matrix4f positionMatrix, VertexConsumer buffer, TextureAtlasSprite sprite, int side, float mult, float offset) {
      int b1 = 15728880;
      int b2 = 15728880;
      RenderGlowEffect.Quad quad = QUADS[side];
      buffer.addVertex(positionMatrix, quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset)
         .setColor(255, 255, 255, 128)
         .setUv(sprite.getU0(), sprite.getV0())
         .setUv2(b1, b2)
         .setNormal(1.0F, 0.0F, 0.0F);
      buffer.addVertex(positionMatrix, quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset)
         .setColor(255, 255, 255, 128)
         .setUv(sprite.getU0(), sprite.getV1())
         .setUv2(b1, b2)
         .setNormal(1.0F, 0.0F, 0.0F);
      buffer.addVertex(positionMatrix, quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset)
         .setColor(255, 255, 255, 128)
         .setUv(sprite.getU1(), sprite.getV1())
         .setUv2(b1, b2)
         .setNormal(1.0F, 0.0F, 0.0F);
      buffer.addVertex(positionMatrix, quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset)
         .setColor(255, 255, 255, 128)
         .setUv(sprite.getU1(), sprite.getV0())
         .setUv2(b1, b2)
         .setNormal(1.0F, 0.0F, 0.0F);
   }

   private record Quad(RenderGlowEffect.Vt v1, RenderGlowEffect.Vt v2, RenderGlowEffect.Vt v3, RenderGlowEffect.Vt v4) {
      public RenderGlowEffect.Quad rotate(Direction direction) {
         return switch (direction) {
            case NORTH -> new RenderGlowEffect.Quad(this.v4, this.v1, this.v2, this.v3);
            case EAST -> new RenderGlowEffect.Quad(this.v3, this.v4, this.v1, this.v2);
            case SOUTH -> new RenderGlowEffect.Quad(this.v2, this.v3, this.v4, this.v1);
            case WEST -> this;
            default -> this;
         };
      }
   }

   private record Vt(float x, float y, float z) {
   }
}
