package github.totorewa.rugserver.helper;

import net.minecraft.util.math.MathHelper;

public class ExperienceHelper {
    public static int getExperienceToNextLevel(int currentLevel) {
        if (currentLevel >= 30) {
            return 112 + (currentLevel - 30) * 9;
        }
        if (currentLevel >= 15) {
            return 37 + (currentLevel - 15) * 5;
        }
        return 7 + currentLevel * 2;
    }

    public static int getExperience(int level, float progress) {
        int experience = 0;
        for (int i = 0; i < level; i++) {
            experience += getExperienceToNextLevel(i);
        }
        return experience + MathHelper.floor((float) getExperienceToNextLevel(level) * progress);
    }
}
