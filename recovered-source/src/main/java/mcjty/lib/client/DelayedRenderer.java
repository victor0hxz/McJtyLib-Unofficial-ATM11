package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import mcjty.lib.varia.TriConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class DelayedRenderer {
   private static final Map<RenderType, List<Pair<BlockPos, BiConsumer<PoseStack, VertexConsumer>>>> RENDERS = new HashMap<>();
   private static final Map<BlockPos, TriConsumer<PoseStack, Vec3, RenderType>> DELAYED_RENDERS = new HashMap<>();
   private static final Map<BlockPos, BiFunction<Level, BlockPos, Boolean>> RENDER_VALIDATIONS = new HashMap<>();

   public static void render(PoseStack matrixStack) {
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().position();
      Set<BlockPos> todelete = new HashSet<>();
      DELAYED_RENDERS.forEach((posx, consumer) -> {
         if (RENDER_VALIDATIONS.getOrDefault(posx, (level, blockPos) -> false).apply(Minecraft.getInstance().level, posx)) {
            consumer.accept(matrixStack, projectedView, null);
         } else {
            todelete.add(posx);
         }
      });

      for (BlockPos pos : todelete) {
         DELAYED_RENDERS.remove(pos);
         RENDER_VALIDATIONS.remove(pos);
      }

      RENDERS.forEach((type, renderlist) -> {
         VertexConsumer consumer = buffer.getBuffer(type);
         renderlist.forEach(r -> {
            BlockPos posx = (BlockPos)r.getKey();
            matrixStack.pushPose();
            matrixStack.translate(posx.getX() - projectedView.x, posx.getY() - projectedView.y, posx.getZ() - projectedView.z);
            ((BiConsumer)r.getValue()).accept(matrixStack, consumer);
            matrixStack.popPose();
         });
         buffer.endBatch(type);
      });
      RENDERS.clear();
      buffer.endLastBatch();
   }

   public static void addRender(BlockPos pos, TriConsumer<PoseStack, Vec3, RenderType> renderer, BiFunction<Level, BlockPos, Boolean> validator) {
      DELAYED_RENDERS.put(pos, renderer);
      RENDER_VALIDATIONS.put(pos, validator);
   }

   public static void removeRender(BlockPos pos) {
      DELAYED_RENDERS.remove(pos);
      RENDER_VALIDATIONS.remove(pos);
   }

   public static void addRender(RenderType type, BlockPos pos, BiConsumer<PoseStack, VertexConsumer> renderer) {
      RENDERS.computeIfAbsent(type, renderType -> new ArrayList<>()).add(Pair.of(pos, renderer));
   }
}
