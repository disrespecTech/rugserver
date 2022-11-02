package github.totorewa.rugserver.feature.player.actions;

import github.totorewa.rugserver.feature.player.FakeServerPlayerEntity;
import github.totorewa.rugserver.feature.player.PlayerController;
import github.totorewa.rugserver.helper.RayTraceHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerUseAction implements PlayerAction {
    @Override
    public void execute(PlayerController ctrl, ActionParameters parameters) {
        if (ctrl.useCooldown > 0 || ctrl.player.isUsingItem() || ctrl.player.isSleeping()) return;
        BlockHitResult hit = RayTraceHelper.rayTrace(ctrl.player, ctrl.getMaxReach());
        if (hit == null) return;
        if (hit.type == BlockHitResult.Type.ENTITY)
            handleEntityInteraction(ctrl, hit);
        else if (hit.type == BlockHitResult.Type.BLOCK)
            handleBlockInteraction(ctrl, hit);

        ItemStack heldStack = ctrl.player.getStackInHand();
        if (heldStack != null) {
            Item heldItem = heldStack.getItem();
            boolean eat = heldItem.getUseAction(heldStack) == UseAction.EAT;
            heldStack.onStartUse(ctrl.player.world, ctrl.player);
            if (ctrl.player.isUsingItem()) {
                if (eat) ctrl.eat();
                ctrl.enterUseCooldown();
            }
        }
    }

    private void handleEntityInteraction(PlayerController ctrl, BlockHitResult hit) {
        if (hit.entity.interactAt(ctrl.player, hit.pos) || ctrl.player.method_3215(hit.entity)) {
            ctrl.swingHand();
            ctrl.enterUseCooldown();
        }
    }

    private void handleBlockInteraction(PlayerController ctrl, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        ServerPlayerEntity player = ctrl.player;
        MinecraftServer server = player.server;
        if (pos.getY() < server.getWorldHeight() - (hit.direction == Direction.UP ? 1 : 0) &&
                player.squaredDistanceTo(
                        (double) pos.getX() + 0.5,
                        (double) pos.getY() + 0.5,
                        (double) pos.getZ() + 0.5) < 64.0 &&
                !server.isSpawnProtected(player.world, pos, player) && player.world.getWorldBorder().contains(pos)) {
            float dx = (float)(hit.pos.x - (double)pos.getX());
            float dy = (float)(hit.pos.y - (double)pos.getY());
            float dz = (float)(hit.pos.z - (double)pos.getZ());
            if (player.interactionManager.interactBlock(player, player.world, player.getStackInHand(), pos, hit.direction, dx, dy, dz)) {
                ctrl.swingHand();
                ItemStack heldStack;
                if ((heldStack = player.inventory.getMainHandStack()) != null && heldStack.count == 0) {
                    player.inventory.main[player.inventory.selectedSlot] = null;
                    heldStack = null;
                }
                if (heldStack == null || heldStack.getMaxUseTime() == 0) {
                    player.skipPacketSlotUpdates = true;
                    player.inventory.main[player.inventory.selectedSlot] = ItemStack.copyOf(player.inventory.main[player.inventory.selectedSlot]);
                    Slot slot = player.openScreenHandler.method_3255(player.inventory, player.inventory.selectedSlot);
                    player.openScreenHandler.sendContentUpdates();
                    player.skipPacketSlotUpdates = false;
                    if (!(player instanceof FakeServerPlayerEntity) && player.networkHandler != null) {
                        player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(player.openScreenHandler.syncId, slot.id, player.inventory.getMainHandStack()));
                    }
                }
            }
            ctrl.enterUseCooldown();
        }
    }
}
