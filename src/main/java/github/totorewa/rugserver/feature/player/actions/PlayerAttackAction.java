package github.totorewa.rugserver.feature.player.actions;

import github.totorewa.rugserver.feature.player.PlayerController;
import github.totorewa.rugserver.helper.RayTraceHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class PlayerAttackAction implements PlayerAction {
    @Override
    public void execute(PlayerController ctrl, ActionParameters parameters) {
        BlockHitResult hit = RayTraceHelper.rayTrace(ctrl.player, ctrl.getMaxReach());
        if (hit == null) return;
        if (hit.type == BlockHitResult.Type.BLOCK)
            handleBlockHit(ctrl, hit);
        else if (hit.type == BlockHitResult.Type.ENTITY)
            handleEntityHit(ctrl, hit, parameters.continuous);
    }

    private void handleBlockHit(PlayerController ctrl, BlockHitResult hit) {
        if (ctrl.miningCooldown > 0) return;
        BlockPos pos = hit.getBlockPos();
        BlockState state = ctrl.player.world.getBlockState(pos);
        if (Blocks.AIR.isEqualTo(state.getBlock())) {
            ctrl.miningPos = null;
            return;
        }

        if (ctrl.player.interactionManager.getGameMode().isCreative()) {
            ctrl.player.interactionManager.processBlockBreakingAction(pos, hit.direction);
            ctrl.enterMiningCooldown();
        } else if (ctrl.miningPos == null || !ctrl.miningPos.equals(pos)) {
            if (ctrl.miningPos != null)
                ctrl.player.interactionManager.method_10769();

            if (ctrl.player.server.isSpawnProtected(ctrl.player.world, pos, ctrl.player) ||
                    !ctrl.player.world.getWorldBorder().contains(pos))
                ctrl.miningPos = null;
            else {
                ctrl.player.interactionManager.processBlockBreakingAction(pos, hit.direction);
                ctrl.miningPos = pos;
                ctrl.miningTick = ctrl.player.server.getTicks();
            }
        } else {
            int ticksElapsed = ctrl.player.server.getTicks() - ctrl.miningTick;
            if (state.getBlock().calcBlockBreakingData(ctrl.player, ctrl.player.world, ctrl.miningPos) * (float)(ticksElapsed + 1) >= 1.0f) {
                ctrl.player.interactionManager.method_10764(ctrl.miningPos);
                ctrl.miningPos = null;
                ctrl.enterMiningCooldown();
            }
        }
        ctrl.swingHand();
    }

    private void handleEntityHit(PlayerController ctrl, BlockHitResult hit, boolean continuous) {
        if (ctrl.attackCooldown > 0) return;
        if (!continuous) {
            ctrl.player.method_3216(hit.entity);
            ctrl.swingHand();
        }
        ctrl.enterAttackCooldown();
    }
}
