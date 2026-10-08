package mcjty.lib.crafting;

import java.util.Objects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CopyComponentsRecipeBuilder implements IRecipeBuilder<CopyComponentsRecipeBuilder> {
   private final Item result;
   private final int count;
   private final Builder advancementBuilder = Builder.advancement();
   private ShapedRecipeBuilder builder;

   private CopyComponentsRecipeBuilder(ItemLike resultIn, int countIn) {
      this.result = resultIn.asItem();
      this.count = countIn;
      this.builder = ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, this.result, this.count);
   }

   public static CopyComponentsRecipeBuilder shapedRecipe(ItemLike resultIn) {
      return shapedRecipe(resultIn, 1);
   }

   public static CopyComponentsRecipeBuilder shapedRecipe(ItemLike resultIn, int countIn) {
      return new CopyComponentsRecipeBuilder(resultIn, countIn);
   }

   public CopyComponentsRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
      this.builder = this.builder.define(symbol, tagIn);
      return this;
   }

   public CopyComponentsRecipeBuilder define(Character symbol, ItemLike itemIn) {
      this.builder = this.builder.define(symbol, itemIn);
      return this;
   }

   public CopyComponentsRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
      this.builder = this.builder.define(symbol, ingredientIn);
      return this;
   }

   public CopyComponentsRecipeBuilder patternLine(String patternIn) {
      this.builder = this.builder.pattern(patternIn);
      return this;
   }

   public CopyComponentsRecipeBuilder unlockedBy(String name, Criterion<? extends CriterionTriggerInstance> criterionIn) {
      this.builder = this.builder.unlockedBy(name, criterionIn);
      return this;
   }

   public CopyComponentsRecipeBuilder setGroup(String groupIn) {
      this.builder = this.builder.group(groupIn);
      return this;
   }

   @Override
   public void build(RecipeOutput consumerIn) {
      Identifier id = BuiltInRegistries.ITEM.getKey(this.result);
      this.build(consumerIn, id);
   }

   @Override
   public void build(RecipeOutput consumerIn, String save) {
      this.build(consumerIn, Identifier.parse(save));
   }

   @Override
   public void build(RecipeOutput consumerIn, Identifier id) {
      ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, id);
      this.builder.save(this.wrap(consumerIn), recipeKey);
   }

   private RecipeOutput wrap(final RecipeOutput consumerIn) {
      final Builder localAdvancementBuilder = this.advancementBuilder;
      return new RecipeOutput() {
         {
            Objects.requireNonNull(CopyComponentsRecipeBuilder.this);
         }

         public Builder advancement() {
            return localAdvancementBuilder;
         }

         public void includeRootAdvancement() {
            consumerIn.includeRootAdvancement();
         }

         public void accept(ResourceKey<Recipe<?>> id, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
            consumerIn.accept(id, new CopyComponentsRecipe((ShapedRecipe)recipe), advancementHolder);
         }

         public void accept(ResourceKey<Recipe<?>> id, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... conditions) {
            consumerIn.accept(id, new CopyComponentsRecipe((ShapedRecipe)recipe), advancementHolder, conditions);
         }
      };
   }
}
