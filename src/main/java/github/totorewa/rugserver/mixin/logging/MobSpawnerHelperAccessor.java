package github.totorewa.rugserver.mixin.logging;

import net.minecraft.entity.MobSpawnerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobSpawnerHelper.class)
public interface MobSpawnerHelperAccessor {
    @Accessor("field_9221")
    static int getMagicNumber() {
        throw new AssertionError();
    }
}
