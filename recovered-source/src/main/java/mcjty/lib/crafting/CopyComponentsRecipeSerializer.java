package mcjty.lib.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class CopyComponentsRecipeSerializer {
   public static final MapCodec<CopyComponentsRecipe> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ShapedRecipe.MAP_CODEC.fieldOf("recipe").forGetter(AbstractRecipeAdaptor::getRecipe))
         .apply(instance, CopyComponentsRecipe::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, CopyComponentsRecipe> STREAM_CODEC = ShapedRecipe.STREAM_CODEC
      .map(CopyComponentsRecipe::new, AbstractRecipeAdaptor::getRecipe);
   public static final RecipeSerializer<CopyComponentsRecipe> INSTANCE = new RecipeSerializer(CODEC, STREAM_CODEC);

   private CopyComponentsRecipeSerializer() {
   }
}
