package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public record FurnaceOperation(boolean success, @Nullable ItemStack result,
                               float experience, int amount) {

    /**
     * Creates a new failed furnace operation.
     *
     * @return The failed furnace operation
     */
    public static FurnaceOperation fail() {
        return new FurnaceOperation(false, null,
                0F, 0);
    }

    /**
     * Creates a new furnace operation with the given result.
     * It will be considered a success and the passed ItemStack
     * is the result of the operation.
     *
     * @param result The result of the operation
     * @return The furnace operation
     */
    public static FurnaceOperation of(ItemStack result,
                                      float experience,
                                      int amount) {
        return new FurnaceOperation(true, result,
                experience, amount);
    }

    /**
     * Get the result of a vanilla furnace recipe passing
     * input as the smelting input ingredient.
     * If no recipe is found, it will return a failed operation.
     * Otherwise, it will return a successful operation with
     * the result of the recipe.
     *
     * @param input The input ingredient
     * @return The result of the vanilla furnace recipe
     */
    public static FurnaceOperation vanilla(ItemStack input) {
        FurnaceRecipe recipe = getFurnaceRecipe(input);
        if (recipe == null) {
            return fail();
        }
        return of(recipe.getResult(),
                recipe.getExperience(),
                input.getAmount());
    }

    private static FurnaceRecipe getFurnaceRecipe(ItemStack input) {
        for (Recipe recipe : Bukkit.getServer().getRecipesFor(input)) {
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                return furnaceRecipe;
            }
        }
        return null;
    }

    @Override
    public float experience() {
        return experience;
    }
}