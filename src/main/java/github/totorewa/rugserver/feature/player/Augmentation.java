package github.totorewa.rugserver.feature.player;

public interface Augmentation {
    void tick(PlayerController controller);
    void scheduleDisable();
    boolean isDisabled();
    String getName();
    default boolean conflictsWith(Augmentation other) {
        return false;
    }
    static boolean isSameAugmentation(Augmentation source, Augmentation other) {
        return source.getClass().equals(other.getClass());
    }
}
