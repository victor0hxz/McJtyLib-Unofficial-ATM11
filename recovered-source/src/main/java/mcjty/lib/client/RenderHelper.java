package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.base.StyleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderHelper {
   public static float rot = 0.0F;
   public static final int MAX_BRIGHTNESS = 15728880;
   public static final RenderSettings DEFAULT_SETTINGS = RenderSettings.builder().color(255, 255, 255).alpha(128).build();
   public static final RenderSettings FULLBRIGHT_SETTINGS = RenderSettings.builder().color(255, 255, 255).alpha(15728880).build();

   public static void renderItemGround(PoseStack matrixStack, MultiBufferSource buffer, ItemStack stack, int brightness, int combinedOverlay) {
   }

   public static void renderItemGround(
      @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, @Nonnull RenderType renderType, ItemStack stack, int lightmap, int overlay
   ) {
   }

   public static void renderItemGui(
      PoseStack matrixStack, MultiBufferSource buffer, RenderType renderType, ItemStack stack, int brightness, int combinedOverlay
   ) {
   }

   public static void renderItemGui(PoseStack matrixStack, MultiBufferSource buffer, ItemStack stack, int brightness, int combinedOverlay) {
   }

   public static void renderAndDecorateItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
      graphics.item(stack, x, y, x * y * 31);
      graphics.itemDecorations(Minecraft.getInstance().font, stack, x, y, null);
   }

   public static void renderStaticFixed(PoseStack matrixStack, MultiBufferSource buffer, ItemStack stack, int brightness, int combinedOverlay) {
   }

   public static void renderText(Font font, String text, int x, int y, int color, PoseStack poseStack, MultiBufferSource buffer, int lightmapValue) {
      font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), buffer, DisplayMode.NORMAL, 0, lightmapValue);
   }

   public static void renderModel(
      Object renderer,
      PoseStack stack,
      VertexConsumer buffer,
      BlockState state,
      Object model,
      float r,
      float g,
      float b,
      int combinedLight,
      int combinedOverlay,
      Object modelData,
      RenderType renderType
   ) {
   }

   public static void line(
      VertexConsumer builder,
      PoseStack matrixStack,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      builder.addVertex(matrixStack.last().pose(), x1, y1, 0.0F).setColor(red, green, blue, alpha);
      builder.addVertex(matrixStack.last().pose(), x2, y2, 0.0F).setColor(red, green, blue, alpha);
   }

   public static void adjustTransformToDirection(PoseStack matrixStack, Direction facing) {
      matrixStack.translate(0.5F, 0.5F, 0.5F);
      switch (facing) {
         case DOWN:
            matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            break;
         case UP:
            matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            break;
         case NORTH:
            matrixStack.mulPose(Axis.YP.rotationDegrees(-180.0F));
         case SOUTH:
         default:
            break;
         case WEST:
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            break;
         case EAST:
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      }

      matrixStack.translate(-0.5F, -0.5F, -0.5F);
   }

   public static void renderNorthSouthQuad(PoseStack poseStack, VertexConsumer builder, TextureAtlasSprite sprite, Object rotation, float offset) {
   }

   private static void renderEntity(GuiGraphicsExtractor graphics, Entity entity, int xPos, int yPos, float scale) {
   }

   public static boolean renderObject(GuiGraphicsExtractor graphics, int x, int y, Object itm, boolean highlight) {
      if (itm instanceof Entity) {
         renderEntity(graphics, (Entity)itm, x, y, 10.0F);
         return true;
      } else {
         Object itemRenderer = null;
         return renderObject(graphics, itemRenderer, x, y, itm, highlight, 100.0F);
      }
   }

   public static boolean renderObject(GuiGraphicsExtractor graphics, Object itemRender, int x, int y, Object itm, boolean highlight, float lvl) {
      return renderObjectInternal(graphics, itemRender, x, y, itm, highlight, lvl);
   }

   private static boolean renderObjectInternal(GuiGraphicsExtractor graphics, Object itemRender, int x, int y, Object itm, boolean highlight, float lvl) {
      if (itm instanceof ItemStack stack && !stack.isEmpty()) {
         graphics.item(stack, x, y, x * y * 31);
         return true;
      } else if (itm instanceof Item item) {
         graphics.item(new ItemStack(item), x, y, x * y * 31);
         return true;
      } else {
         return false;
      }
   }

   private static boolean renderIcon(GuiGraphicsExtractor graphics, Object itemRender, TextureAtlasSprite itm, int xo, int yo, boolean highlight) {
      return false;
   }

   public static boolean renderFluidStack(FluidStack fluidStack, int x, int y, boolean highlight) {
      return false;
   }

   private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, double zLevel) {
   }

   private static void setGLColorFromInt(int color) {
   }

   public static boolean renderItemStackWithCount(GuiGraphicsExtractor graphics, Object itemRender, ItemStack itm, int xo, int yo, boolean highlight) {
      if (itm.isEmpty()) {
         return true;
      } else {
         graphics.item(itm, xo, yo, xo * yo * 31);
         graphics.itemDecorations(Minecraft.getInstance().font, itm, xo, yo, null);
         return true;
      }
   }

   public static boolean renderItemStack(GuiGraphicsExtractor graphics, Object itemRender, ItemStack itm, int x, int y, String txt, boolean highlight) {
      if (itm.isEmpty()) {
         return true;
      } else {
         graphics.item(itm, x, y, x * y * 31);
         graphics.itemDecorations(Minecraft.getInstance().font, itm, x, y, txt);
         return true;
      }
   }

   private static void renderGuiItemDecorations(Object itemRender, Font font, ItemStack stack, int x, int y, @Nullable String text, int scaled) {
   }

   private static void fillRect(int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue, int pAlpha) {
   }

   private static void draw(int x, int y, int width, int height, int red, int green, int blue, int alpha) {
   }

   public static void drawVerticalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
   }

   public static void drawHorizontalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
   }

   public static void drawHorizontalGradientRect(
      GuiGraphicsExtractor graphics, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int color1, int color2, int lightmap
   ) {
      graphics.fill(x1, y1, x2, y2, color1);
   }

   public static void drawHorizontalLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int color) {
      graphics.fill(x1, y1, x2, y1 + 1, color);
   }

   public static void drawVerticalLine(GuiGraphicsExtractor graphics, int x1, int y1, int y2, int color) {
      graphics.fill(x1, y1, x1 + 1, y2, color);
   }

   public static void drawLeftTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
      drawVerticalLine(graphics, x, y, y, color);
      drawVerticalLine(graphics, x + 1, y - 1, y + 1, color);
      drawVerticalLine(graphics, x + 2, y - 2, y + 2, color);
   }

   public static void drawRightTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
      drawVerticalLine(graphics, x, y, y, color);
      drawVerticalLine(graphics, x - 1, y - 1, y + 1, color);
      drawVerticalLine(graphics, x - 2, y - 2, y + 2, color);
   }

   public static void drawUpTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
      drawHorizontalLine(graphics, x, y, x, color);
      drawHorizontalLine(graphics, x - 1, y + 1, x + 1, color);
      drawHorizontalLine(graphics, x - 2, y + 2, x + 2, color);
   }

   public static void drawDownTriangle(GuiGraphicsExtractor graphics, int x, int y, int color) {
      drawHorizontalLine(graphics, x, y, x, color);
      drawHorizontalLine(graphics, x - 1, y - 1, x + 1, color);
      drawHorizontalLine(graphics, x - 2, y - 2, x + 2, color);
   }

   public static void drawColorLogic(int x, int y, int width, int height, int red, int green, int blue, Object colorLogic) {
      draw(x, y, width, height, red, green, blue, 255);
   }

   public static void drawThickButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
      graphics.fill(x1 + 2, y1 + 2, x2 - 2, y2 - 2, average);
      drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 1, bright);
      drawHorizontalLine(graphics, x1 + 2, y1 + 2, x2 - 2, bright);
      drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 2, bright);
      drawVerticalLine(graphics, x1 + 2, y1 + 3, y2 - 3, bright);
      drawHorizontalLine(graphics, x1 + 3, y2 - 3, x2 - 2, dark);
      drawHorizontalLine(graphics, x1 + 2, y2 - 2, x2 - 1, dark);
      drawVerticalLine(graphics, x2 - 2, y1 + 2, y2 - 2, dark);
      drawVerticalLine(graphics, x2 - 3, y1 + 3, y2 - 3, dark);
   }

   public static void drawThinButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
      graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average);
      drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 2, bright);
      drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 3, bright);
      drawHorizontalLine(graphics, x1 + 1, y2 - 2, x2 - 1, dark);
      drawVerticalLine(graphics, x2 - 2, y1 + 1, y2 - 2, dark);
   }

   public static void drawThinButtonBoxGradient(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
      drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
      drawHorizontalLine(graphics, x1 + 1, y1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y2 - 1, x2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawVerticalLine(graphics, x2 - 1, y1 + 1, y2 - 1, StyleConfig.colorButtonExternalBorder);
      drawHorizontalLine(graphics, x1 + 1, y1 + 1, x2 - 2, bright);
      drawVerticalLine(graphics, x1 + 1, y1 + 2, y2 - 3, bright);
      drawHorizontalLine(graphics, x1 + 1, y2 - 2, x2 - 1, dark);
      drawVerticalLine(graphics, x2 - 2, y1 + 1, y2 - 2, dark);
   }

   public static void drawFlatButtonBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average, int dark) {
      drawBeveledBox(graphics, x1, y1, x2, y2, bright, dark, average);
   }

   public static void drawFlatButtonBox(
      GuiGraphicsExtractor graphics, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int bright, int average, int dark, int lightmap
   ) {
      drawBeveledBox(graphics, buffer, x1, y1, x2, y2, bright, dark, average, lightmap);
   }

   public static void drawFlatButtonBoxGradient(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
      drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
      drawHorizontalLine(graphics, x1, y1, x2 - 1, bright);
      drawVerticalLine(graphics, x1, y1, y2 - 1, bright);
      drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, dark);
      drawHorizontalLine(graphics, x1, y2 - 1, x2, dark);
   }

   public static void drawBeveledBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
      if (fillcolor != -1) {
         graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
      }

      drawHorizontalLine(graphics, x1, y1, x2 - 1, topleftcolor);
      drawVerticalLine(graphics, x1, y1, y2 - 1, topleftcolor);
      drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, botrightcolor);
      drawHorizontalLine(graphics, x1, y2 - 1, x2, botrightcolor);
   }

   public static void drawBeveledBox(
      GuiGraphicsExtractor graphics, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor, int lightmap
   ) {
      if (fillcolor != -1) {
         fill(graphics, buffer, x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor, lightmap);
      }

      fill(graphics, buffer, x1, y1, x2 - 1, y1 + 1, topleftcolor, lightmap);
      fill(graphics, buffer, x1, y1, x1 + 1, y2 - 1, topleftcolor, lightmap);
      fill(graphics, buffer, x2 - 1, y1, x2 - 1 + 1, y2 - 1, botrightcolor, lightmap);
      fill(graphics, buffer, x1, y2 - 1, x2, y2 - 1 + 1, botrightcolor, lightmap);
   }

   public static void drawThickBeveledBox(
      GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor
   ) {
      if (fillcolor != -1) {
         graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
      }

      graphics.fill(x1, y1, x2 - 1, y1 + thickness, topleftcolor);
      graphics.fill(x1, y1, x1 + thickness, y2 - 1, topleftcolor);
      graphics.fill(x2 - thickness, y1, x2, y2 - 1, botrightcolor);
      graphics.fill(x1, y2 - thickness, x2, y2, botrightcolor);
   }

   public static void drawFlatBox(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int border, int fill) {
      if (fill != -1) {
         graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fill);
      }

      drawHorizontalLine(graphics, x1, y1, x2 - 1, border);
      drawVerticalLine(graphics, x1, y1, y2 - 1, border);
      drawVerticalLine(graphics, x2 - 1, y1, y2 - 1, border);
      drawHorizontalLine(graphics, x1, y2 - 1, x2, border);
   }

   public static void drawTexturedModalRect(
      PoseStack poseStack,
      VertexConsumer builder,
      int x,
      int y,
      int textureX,
      int textureY,
      int width,
      int height,
      int totw,
      int toth,
      float parentU,
      float parentV
   ) {
      Matrix4f matrix = poseStack.last().pose();
      float f = 1.0F / totw;
      float f1 = 1.0F / toth;
      float zLevel = 50.0F;
      builder.addVertex(matrix, x + 0, y + height, zLevel).setUv(parentU + (textureX + 0) * f, parentV + (textureY + height) * f1);
      builder.addVertex(matrix, x + width, y + height, zLevel).setUv(parentU + (textureX + width) * f, parentV + (textureY + height) * f1);
      builder.addVertex(matrix, x + width, y + 0, zLevel).setUv(parentU + (textureX + width) * f, parentV + (textureY + 0) * f1);
      builder.addVertex(matrix, x + 0, y + 0, zLevel).setUv(parentU + (textureX + 0) * f, parentV + (textureY + 0) * f1);
   }

   public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
   }

   public static void renderSplitBillboard(PoseStack matrixStack, VertexConsumer buffer, float scale, Vec3 offset, Identifier texture) {
   }

   public static void renderBillboardQuadBright(PoseStack matrixStack, MultiBufferSource buffer, float scale, Identifier texture) {
      renderBillboardQuadBright(matrixStack, buffer, scale, texture, DEFAULT_SETTINGS);
   }

   public static void renderBillboardQuadBright(PoseStack matrixStack, VertexConsumer builder, float scale, Identifier texture, RenderSettings settings) {
   }

   public static void renderBillboardQuadBright(PoseStack poseStack, MultiBufferSource buffer, float scale, Identifier texture, RenderSettings settings) {
      renderBillboardQuadBright(poseStack, buffer.getBuffer(settings.renderType()), scale, texture, settings);
   }

   public static void rotateToPlayer(PoseStack poseStack) {
      Quaternionf cameraRotation = new Quaternionf(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
      poseStack.mulPose(cameraRotation);
      poseStack.mulPose(new Quaternionf().rotateY((float) Math.PI));
   }

   public static int renderText(GuiGraphicsExtractor graphics, int x, int y, String txt) {
      Minecraft mc = Minecraft.getInstance();
      graphics.text(mc.font, txt, x, y, -1);
      return mc.font.width(txt);
   }

   public static int renderText(GuiGraphicsExtractor graphics, int x, int y, String txt, int color) {
      Minecraft mc = Minecraft.getInstance();
      graphics.text(mc.font, txt, x, y, color);
      return mc.font.width(txt);
   }

   public static void drawBeam(PoseStack matrix, VertexConsumer builder, TextureAtlasSprite sprite, Vec3 S, Vec3 E, Vec3 P, float width) {
      Vec3 PS = S.subtract(P);
      Vec3 SE = E.subtract(S);
      Vec3 normal = PS.cross(SE).normalize();
      Vec3 half = normal.multiply(width, width, width);
      Vec3 p1 = S.add(half);
      Vec3 p2 = S.subtract(half);
      Vec3 p3 = E.add(half);
      Vec3 p4 = E.subtract(half);
      drawQuad(matrix.last().pose(), builder, sprite, p1, p3, p4, p2, DEFAULT_SETTINGS);
   }

   public static void drawBeam(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 S, Vec3 E, Vec3 P, RenderSettings settings) {
      Vec3 PS = S.subtract(P);
      Vec3 SE = E.subtract(S);
      Vec3 normal = PS.cross(SE).normalize();
      Vec3 half = normal.multiply(settings.width(), settings.width(), settings.width());
      Vec3 p1 = S.add(half);
      Vec3 p2 = S.subtract(half);
      Vec3 p3 = E.add(half);
      Vec3 p4 = E.subtract(half);
      drawQuad(poseStack.last().pose(), buffer, sprite, p1, p3, p4, p2, settings);
   }

   public static void renderQuadGui(PoseStack matrixStack, TextureAtlasSprite sprite, int packedLight, VertexConsumer builder, float zfront, float size) {
      float u0 = sprite.getU0();
      float v0 = sprite.getV0();
      float u1 = sprite.getU1();
      float v1 = sprite.getV1();
      Matrix4f matrix = matrixStack.last().pose();
      vt(builder, matrix, -size, size, zfront, u0, v0, packedLight);
      vt(builder, matrix, size, size, zfront, u1, v0, packedLight);
      vt(builder, matrix, size, -size, zfront, u1, v1, packedLight);
      vt(builder, matrix, -size, -size, zfront, u0, v1, packedLight);
   }

   public static void drawQuadGui(PoseStack poseStack, VertexConsumer builder, float x1, float x2, float y1, float y2, float z, int color, int packedLightIn) {
      Matrix4f matrix = poseStack.last().pose();
      float a = (color >> 24 & 0xFF) / 255.0F;
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      builder.addVertex(matrix, x1, y2, z).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z).setColor(r, g, b, a).setLight(packedLightIn);
   }

   private static void drawQuad(Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, RenderSettings settings) {
      int b1 = settings.brightness() >> 16 & 65535;
      int b2 = settings.brightness() & 65535;
      vt(
         buffer,
         matrix,
         (float)p1.x(),
         (float)p1.y(),
         (float)p1.z(),
         sprite.getU0(),
         sprite.getV0(),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p2.x(),
         (float)p2.y(),
         (float)p2.z(),
         sprite.getU1(),
         sprite.getV0(),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p3.x(),
         (float)p3.y(),
         (float)p3.z(),
         sprite.getU1(),
         sprite.getV1(),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p4.x(),
         (float)p4.y(),
         (float)p4.z(),
         sprite.getU0(),
         sprite.getV1(),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
   }

   private static void drawQuadUnit(
      Matrix4f matrix,
      VertexConsumer buffer,
      TextureAtlasSprite sprite,
      Vec3 p1,
      Vec3 p2,
      Vec3 p3,
      Vec3 p4,
      double u0Par,
      double u1Par,
      double v0Par,
      double v1Par,
      RenderSettings settings
   ) {
      int b1 = settings.brightness() >> 16 & 65535;
      int b2 = settings.brightness() & 65535;
      u0Par = u0Par < 0.0 ? 1.0 + u0Par % 1.0 : u0Par % 1.0;
      u1Par = u1Par < 0.0 ? -(u1Par % 1.0) : 1.0 - u1Par % 1.0;
      v0Par = v0Par < 0.0 ? 1.0 + v0Par % 1.0 : v0Par % 1.0;
      v1Par = v1Par < 0.0 ? -(v1Par % 1.0) : 1.0 - v1Par % 1.0;
      float du = sprite.getU1() - sprite.getU0();
      float dv = sprite.getV1() - sprite.getV0();
      vt(
         buffer,
         matrix,
         (float)p1.x(),
         (float)p1.y(),
         (float)p1.z(),
         sprite.getU0() + (float)(du * u0Par),
         sprite.getV0() + (float)(dv * v0Par),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p2.x(),
         (float)p2.y(),
         (float)p2.z(),
         sprite.getU1() - (float)(du * u1Par),
         sprite.getV0() + (float)(dv * v0Par),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p3.x(),
         (float)p3.y(),
         (float)p3.z(),
         sprite.getU1() - (float)(du * u1Par),
         sprite.getV1() - (float)(dv * v1Par),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
      vt(
         buffer,
         matrix,
         (float)p4.x(),
         (float)p4.y(),
         (float)p4.z(),
         sprite.getU0() + (float)(du * u0Par),
         sprite.getV1() - (float)(dv * v1Par),
         b1,
         b2,
         settings.r(),
         settings.g(),
         settings.b(),
         settings.a()
      );
   }

   private static void drawQuad(
      Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, boolean opposite, RenderSettings settings
   ) {
      int b1 = settings.brightness() >> 16 & 65535;
      int b2 = settings.brightness() & 65535;
      if (opposite) {
         vt(
            buffer,
            matrix,
            (float)p1.x(),
            (float)p1.y(),
            (float)p1.z(),
            sprite.getU0(),
            sprite.getV0(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p2.x(),
            (float)p2.y(),
            (float)p2.z(),
            sprite.getU1(),
            sprite.getV0(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p3.x(),
            (float)p3.y(),
            (float)p3.z(),
            sprite.getU1(),
            sprite.getV1(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p4.x(),
            (float)p4.y(),
            (float)p4.z(),
            sprite.getU0(),
            sprite.getV1(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
      } else {
         vt(
            buffer,
            matrix,
            (float)p4.x(),
            (float)p4.y(),
            (float)p4.z(),
            sprite.getU0(),
            sprite.getV1(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p3.x(),
            (float)p3.y(),
            (float)p3.z(),
            sprite.getU1(),
            sprite.getV1(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p2.x(),
            (float)p2.y(),
            (float)p2.z(),
            sprite.getU1(),
            sprite.getV0(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
         vt(
            buffer,
            matrix,
            (float)p1.x(),
            (float)p1.y(),
            (float)p1.z(),
            sprite.getU0(),
            sprite.getV0(),
            b1,
            b2,
            settings.r(),
            settings.g(),
            settings.b(),
            settings.a()
         );
      }
   }

   public static void renderRect(PoseStack poseStack, VertexConsumer buffer, RenderHelper.Rect rect, BlockPos p, float r, float g, float b, float a) {
      Matrix4f matrix = poseStack.last().pose();
      buffer.addVertex(matrix, (float)(p.getX() + rect.v1.x), (float)(p.getY() + rect.v1.y), (float)(p.getZ() + rect.v1.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v2.x), (float)(p.getY() + rect.v2.y), (float)(p.getZ() + rect.v2.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v2.x), (float)(p.getY() + rect.v2.y), (float)(p.getZ() + rect.v2.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v3.x), (float)(p.getY() + rect.v3.y), (float)(p.getZ() + rect.v3.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v3.x), (float)(p.getY() + rect.v3.y), (float)(p.getZ() + rect.v3.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v4.x), (float)(p.getY() + rect.v4.y), (float)(p.getZ() + rect.v4.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v4.x), (float)(p.getY() + rect.v4.y), (float)(p.getZ() + rect.v4.z)).setColor(r, g, b, a);
      buffer.addVertex(matrix, (float)(p.getX() + rect.v1.x), (float)(p.getY() + rect.v1.y), (float)(p.getZ() + rect.v1.z)).setColor(r, g, b, a);
   }

   public static void drawBox(
      PoseStack matrixStack, VertexConsumer builder, float x1, float x2, float y1, float y2, float z1, float z2, float r, float g, float b, int packedLightIn
   ) {
      drawBox(matrixStack, builder, x1, x2, y1, y2, z1, z2, r, g, b, 1.0F, packedLightIn);
   }

   public static void drawBox(
      PoseStack matrixStack,
      VertexConsumer builder,
      float x1,
      float x2,
      float y1,
      float y2,
      float z1,
      float z2,
      float r,
      float g,
      float b,
      float a,
      int packedLightIn
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setLight(packedLightIn);
      builder.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setLight(packedLightIn);
   }

   public static void drawBox(
      PoseStack matrixStack,
      VertexConsumer builder,
      TextureAtlasSprite sprite,
      boolean down,
      boolean up,
      boolean north,
      boolean south,
      boolean west,
      boolean east,
      float x1,
      float x2,
      float y1,
      float y2,
      float z1,
      float z2,
      RenderSettings settings
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      Vec3 c111 = new Vec3(x1, y1, z1);
      Vec3 c112 = new Vec3(x1, y1, z2);
      Vec3 c121 = new Vec3(x1, y2, z1);
      Vec3 c122 = new Vec3(x1, y2, z2);
      Vec3 c211 = new Vec3(x2, y1, z1);
      Vec3 c212 = new Vec3(x2, y1, z2);
      Vec3 c221 = new Vec3(x2, y2, z1);
      Vec3 c222 = new Vec3(x2, y2, z2);
      if (down) {
         drawQuad(matrix, builder, sprite, c211, c212, c112, c111, settings);
      }

      if (up) {
         drawQuad(matrix, builder, sprite, c121, c122, c222, c221, settings);
      }

      if (north) {
         drawQuad(matrix, builder, sprite, c121, c221, c211, c111, settings);
      }

      if (south) {
         drawQuad(matrix, builder, sprite, c112, c212, c222, c122, settings);
      }

      if (west) {
         drawQuad(matrix, builder, sprite, c112, c122, c121, c111, settings);
      }

      if (east) {
         drawQuad(matrix, builder, sprite, c211, c221, c222, c212, settings);
      }
   }

   public static void drawBox(
      PoseStack matrixStack,
      VertexConsumer builder,
      TextureAtlasSprite sprite,
      float x1,
      float x2,
      float y1,
      float y2,
      float z1,
      float z2,
      RenderSettings settings
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      Vec3 c111 = new Vec3(x1, y1, z1);
      Vec3 c112 = new Vec3(x1, y1, z2);
      Vec3 c121 = new Vec3(x1, y2, z1);
      Vec3 c122 = new Vec3(x1, y2, z2);
      Vec3 c211 = new Vec3(x2, y1, z1);
      Vec3 c212 = new Vec3(x2, y1, z2);
      Vec3 c221 = new Vec3(x2, y2, z1);
      Vec3 c222 = new Vec3(x2, y2, z2);
      drawQuad(matrix, builder, sprite, c211, c212, c112, c111, settings);
      drawQuad(matrix, builder, sprite, c121, c122, c222, c221, settings);
      drawQuad(matrix, builder, sprite, c121, c221, c211, c111, settings);
      drawQuad(matrix, builder, sprite, c112, c212, c222, c122, settings);
      drawQuad(matrix, builder, sprite, c112, c122, c121, c111, settings);
      drawQuad(matrix, builder, sprite, c211, c221, c222, c212, settings);
   }

   public static void drawBoxInside(
      PoseStack matrixStack,
      VertexConsumer builder,
      TextureAtlasSprite sprite,
      float x1,
      float x2,
      float y1,
      float y2,
      float z1,
      float z2,
      RenderSettings settings
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      Vec3 c111 = new Vec3(x1, y1, z1);
      Vec3 c112 = new Vec3(x1, y1, z2);
      Vec3 c121 = new Vec3(x1, y2, z1);
      Vec3 c122 = new Vec3(x1, y2, z2);
      Vec3 c211 = new Vec3(x2, y1, z1);
      Vec3 c212 = new Vec3(x2, y1, z2);
      Vec3 c221 = new Vec3(x2, y2, z1);
      Vec3 c222 = new Vec3(x2, y2, z2);
      drawQuad(matrix, builder, sprite, c111, c112, c212, c211, settings);
      drawQuad(matrix, builder, sprite, c221, c222, c122, c121, settings);
      drawQuad(matrix, builder, sprite, c111, c211, c221, c121, settings);
      drawQuad(matrix, builder, sprite, c122, c222, c212, c112, settings);
      drawQuad(matrix, builder, sprite, c111, c121, c122, c112, settings);
      drawQuad(matrix, builder, sprite, c212, c222, c221, c211, settings);
   }

   public static void drawBoxUnit(
      PoseStack matrixStack,
      VertexConsumer builder,
      TextureAtlasSprite sprite,
      boolean down,
      boolean up,
      boolean north,
      boolean south,
      boolean west,
      boolean east,
      float x1,
      float x2,
      float y1,
      float y2,
      float z1,
      float z2,
      RenderSettings settings
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      Vec3 c111 = new Vec3(x1, y1, z1);
      Vec3 c112 = new Vec3(x1, y1, z2);
      Vec3 c121 = new Vec3(x1, y2, z1);
      Vec3 c122 = new Vec3(x1, y2, z2);
      Vec3 c211 = new Vec3(x2, y1, z1);
      Vec3 c212 = new Vec3(x2, y1, z2);
      Vec3 c221 = new Vec3(x2, y2, z1);
      Vec3 c222 = new Vec3(x2, y2, z2);
      if (down) {
         drawQuadUnit(matrix, builder, sprite, c211, c212, c112, c111, x1, x2, z1, z2, settings);
      }

      if (up) {
         drawQuadUnit(matrix, builder, sprite, c121, c122, c222, c221, x1, x2, z1, z2, settings);
      }

      if (north) {
         drawQuadUnit(matrix, builder, sprite, c121, c221, c211, c111, x1, x2, y1, y2, settings);
      }

      if (south) {
         drawQuadUnit(matrix, builder, sprite, c112, c212, c222, c122, x1, x2, y1, y2, settings);
      }

      if (west) {
         drawQuadUnit(matrix, builder, sprite, c112, c122, c121, c111, y1, y2, z1, z2, settings);
      }

      if (east) {
         drawQuadUnit(matrix, builder, sprite, c211, c221, c222, c212, y1, y2, z1, z2, settings);
      }
   }

   public static void drawQuad(
      PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, Direction side, boolean opposite, float offset, RenderSettings settings
   ) {
      Matrix4f matrix = poseStack.last().pose();
      switch (side) {
         case DOWN:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(0.0, offset, 1.0),
               new Vec3(1.0, offset, 1.0),
               new Vec3(1.0, offset, 0.0),
               new Vec3(0.0, offset, 0.0),
               opposite,
               settings
            );
            break;
         case UP:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(1.0, 1.0F - offset, 1.0),
               new Vec3(0.0, 1.0F - offset, 1.0),
               new Vec3(0.0, 1.0F - offset, 0.0),
               new Vec3(1.0, 1.0F - offset, 0.0),
               opposite,
               settings
            );
            break;
         case NORTH:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(0.0, 0.0, offset),
               new Vec3(1.0, 0.0, offset),
               new Vec3(1.0, 1.0, offset),
               new Vec3(0.0, 1.0, offset),
               opposite,
               settings
            );
            break;
         case SOUTH:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(0.0, 1.0, 1.0F - offset),
               new Vec3(1.0, 1.0, 1.0F - offset),
               new Vec3(1.0, 0.0, 1.0F - offset),
               new Vec3(0.0, 0.0, 1.0F - offset),
               opposite,
               settings
            );
            break;
         case WEST:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(offset, 0.0, 0.0),
               new Vec3(offset, 1.0, 0.0),
               new Vec3(offset, 1.0, 1.0),
               new Vec3(offset, 0.0, 1.0),
               opposite,
               settings
            );
            break;
         case EAST:
            drawQuad(
               matrix,
               buffer,
               sprite,
               new Vec3(1.0F - offset, 0.0, 1.0),
               new Vec3(1.0F - offset, 1.0, 1.0),
               new Vec3(1.0F - offset, 1.0, 0.0),
               new Vec3(1.0F - offset, 0.0, 0.0),
               opposite,
               settings
            );
      }
   }

   public static void vt(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float r, float g, float b, int packedLight) {
      renderer.addVertex(stack.last().pose(), x, y, z).setColor(r, g, b, 1.0F).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
   }

   public static void vt(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int packedLight) {
      renderer.addVertex(stack.last().pose(), x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(u, v).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
   }

   private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int packedLight) {
      renderer.addVertex(matrix, x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(u, v).setLight(packedLight).setNormal(1.0F, 0.0F, 0.0F);
   }

   public static void vt(VertexConsumer renderer, PoseStack matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
      renderer.addVertex(matrix.last().pose(), x, y, z).setColor(r, g, b, a).setUv(u, v).setUv2(lu, lv).setNormal(1.0F, 0.0F, 0.0F);
   }

   private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v) {
      renderer.addVertex(matrix, x, y, z).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(u, v).setLight(15728880).setNormal(1.0F, 0.0F, 0.0F);
   }

   private static void vt(VertexConsumer renderer, Matrix4f matrix, float x, float y, float z, float u, float v, int lu, int lv, int r, int g, int b, int a) {
      renderer.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u, v).setUv2(lu, lv).setNormal(1.0F, 0.0F, 0.0F);
   }

   public static void putVertex(
      VertexConsumer builder, Position normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite, float r, float g, float b, float a
   ) {
      float iu = sprite.getU(u);
      float iv = sprite.getV(v);
      builder.addVertex((float)x, (float)y, (float)z)
         .setUv(iu, iv)
         .setUv2(0, 0)
         .setColor(r, g, b, a)
         .setNormal((float)normal.x(), (float)normal.y(), (float)normal.z());
   }

   public static void renderHighLightedBlocksOutline(
      PoseStack poseStack, VertexConsumer buffer, float mx, float my, float mz, float r, float g, float b, float a
   ) {
      Matrix4f matrix = poseStack.last().pose();
      buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my + 1.0F, mz).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx + 1.0F, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my, mz + 1.0F).setColor(r, g, b, a);
      buffer.addVertex(matrix, mx, my + 1.0F, mz + 1.0F).setColor(r, g, b, a);
   }

   public static void fill(GuiGraphicsExtractor graphics, MultiBufferSource buffer, int x1, int y1, int x2, int y2, int color, int lightmap) {
      graphics.fill(x1, y1, x2, y2, color);
   }

   public static void rotateXP(PoseStack stack, float degrees) {
      stack.mulPose(Axis.XP.rotationDegrees(degrees));
   }

   public static void rotateYP(PoseStack stack, float degrees) {
      stack.mulPose(Axis.YP.rotationDegrees(degrees));
   }

   public static void rotateZP(PoseStack stack, float degrees) {
      stack.mulPose(Axis.ZP.rotationDegrees(degrees));
   }

   public record Rect(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
   }
}
