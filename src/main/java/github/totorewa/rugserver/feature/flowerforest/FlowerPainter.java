package github.totorewa.rugserver.feature.flowerforest;

import github.totorewa.rugserver.feature.player.Augmentation;
import github.totorewa.rugserver.feature.player.PlayerController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FlowerPainter implements Augmentation {
    public static final String NAME = "flower_painter";
    private final int growRadius;
    private boolean disabled;
    private int cooldown;
    private BlockPos lastBlockPos;

    public FlowerPainter(int radius) {
        growRadius = MathHelper.clamp(radius, 1, 10);
    }

    @Override
    public void tick(PlayerController ctrl) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (disabled || shouldDisable(ctrl.player) || !isHoldingBrush(ctrl.player)) return;
        BlockPos testPos = ctrl.player.getBlockPos();
        if (testPos.equals(lastBlockPos)) {
            cooldown();
            return;
        }
        lastBlockPos = testPos;
        int maxX = testPos.getX() + growRadius;
        int maxZ = testPos.getZ() + growRadius;
        int minZ = testPos.getZ() - growRadius;
        boolean planted = false;
        BlockPos.Mutable pos = new BlockPos.Mutable(testPos.getX() - growRadius, testPos.getY(), minZ);
        for (; pos.getX() <= maxX; pos.setPosition(pos.getX() + 1, pos.getY(), minZ)) {
            for (; pos.getZ() <= maxZ; pos.setPosition(pos.getX(), pos.getY(), pos.getZ() + 1)) {
                if (ctrl.player.world.isAir(pos)) {
                    FlowerBlock.FlowerType flowerType = ctrl.player.world.getBiome(pos).pickFlower(ctrl.random, pos);
                    FlowerBlock flower = flowerType.getColor().getBlock();
                    BlockState state = flower.getDefaultState().with(flower.method_8781(), flowerType);
                    if (flower.method_8691(ctrl.player.world, pos, state)) {
                        ctrl.player.world.method_8506(pos, state, 2);
                        planted = true;
                    }
                }
            }
        }
        if (planted) {
            ctrl.player.world.syncGlobalEvent(2005, pos, 0);
            Block.Sound sound = Block.GRASS;
            ctrl.player.world.playSound((float)testPos.getX() + 0.5f, (float)testPos.getY() + 0.5f, (float)testPos.getZ() + 0.5f, sound.getSound(), (sound.getVolume() + 1.0f) / 2.0f, sound.getPitch() * 0.8f);
        }
        cooldown();
    }

    @Override
    public void scheduleDisable() {
        disabled = true;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean conflictsWith(Augmentation other) {
        return Augmentation.isSameAugmentation(this, other);
    }

    private boolean isHoldingBrush(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack != null) {
            Item item = stack.getItem();
            return item == Items.DYE && stack.getMeta() == DyeColor.WHITE.getSwappedId();
        }
        return false;
    }

    private boolean shouldDisable(ServerPlayerEntity player) {
        if (!player.interactionManager.getGameMode().isCreative()) {
            return disabled = true;
        }
        return false;
    }

    private void cooldown() {
        cooldown = 20;
    }
}
