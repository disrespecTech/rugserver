package github.totorewa.rugserver.mixin.logging;

import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
    @Accessor("field_6728")
    MobSpawnerHelper getMobSpawnerHelper();
}
