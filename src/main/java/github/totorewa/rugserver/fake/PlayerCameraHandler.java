package github.totorewa.rugserver.fake;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public interface PlayerCameraHandler {
    String cameraModeBlockReason();

    boolean enterCameraMode();

    boolean exitCameraMode();

    boolean isInCameraMode();

    Vec3d getSurvivalPos();

    float getSurvivalPitch();

    float getSurvivalYaw();

    int getSurvivalDimension();

    Set<StatusEffectInstance> getSurvivalEffects();
}
