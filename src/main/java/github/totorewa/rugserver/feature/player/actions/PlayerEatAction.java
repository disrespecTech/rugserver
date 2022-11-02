package github.totorewa.rugserver.feature.player.actions;

import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.feature.player.PlayerController;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;

public class PlayerEatAction implements PlayerAction {
    @Override
    public void execute(PlayerController ctrl, ActionParameters parameters) {
        if (ctrl.eatCooldown > 0 || !ctrl.canEat()) return;
        ctrl.enterEatCooldown(parameters.interval);
        ItemStack food = findAppropriateFoodItem(ctrl);
        if (food != null) {
            FoodItem item = (FoodItem)food.getItem();
            item.onFinishUse(food, ctrl.player.world, ctrl.player);
        }
    }

    private ItemStack findAppropriateFoodItem(PlayerController ctrl) {
        int foodLevel = ctrl.player.getHungerManager().getFoodLevel();
        int remainder = 20 - foodLevel;
        Item item;
        ItemStack bestItem = null;
        int bestHungerPoints = 0;
        float bestSaturation = 0;
        for (ItemStack stack : ctrl.player.inventory.main) {
            if (stack == null) continue;
            if ((item = stack.getItem()) != null && item.getUseAction(stack) == UseAction.EAT && item instanceof FoodItem) {
                FoodItem foodItem = (FoodItem) item;
                int points = foodItem.getHungerPoints(stack);
                if (points > remainder) continue;
                if (points > bestHungerPoints) {
                    bestItem = stack;
                    bestHungerPoints = points;
                    bestSaturation = foodItem.getSaturation(stack);
                } else if (points == bestHungerPoints) {
                    float saturation = foodItem.getSaturation(stack);
                    if (saturation > bestSaturation) {
                        bestItem = stack;
                        bestSaturation = saturation;
                    }
                }
            }
        }

        return bestItem;
    }

    @Override
    public boolean isEnabled() {
        return RugSettings.allowAutoEating;
    }
}
