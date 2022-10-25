package github.totorewa.rugserver.mixin.feature.mobspawnnearplayer;

import github.totorewa.rugserver.fake.IMobCheckDespawn;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements IMobCheckDespawn {
    @Shadow
    protected abstract void checkDespawn();

    @Override
    public void rug$checkDespawn() {
        checkDespawn();
    }
}
