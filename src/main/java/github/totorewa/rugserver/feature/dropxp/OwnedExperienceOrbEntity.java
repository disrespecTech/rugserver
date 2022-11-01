package github.totorewa.rugserver.feature.dropxp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class OwnedExperienceOrbEntity extends ExperienceOrbEntity {
    private final Entity owner;
    public int ownerDelay;

    public OwnedExperienceOrbEntity(World world, double x, double y, double z, int amount, Entity owner) {
        super(world, x, y, z, amount);
        this.owner = owner;
        ownerDelay = 300;
    }

    @Override
    public void tick() {
        super.tick();
        if (ownerDelay > 0)
            ownerDelay--;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (player == owner && ownerDelay > 0) return;
        super.onPlayerCollision(player);
    }
}
