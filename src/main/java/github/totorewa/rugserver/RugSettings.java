package github.totorewa.rugserver;

import github.totorewa.rugserver.feature.player.FakePlayerManager;
import github.totorewa.rugserver.settings.OpEnableOption;
import github.totorewa.rugserver.settings.RugRule;
import github.totorewa.rugserver.settings.Rule;
import github.totorewa.rugserver.settings.Validator;
import net.minecraft.command.CommandSource;
import net.minecraft.server.world.ServerWorld;

public class RugSettings {
    public static final String COMMAND = "command";
    public static final String CREATIVE = "creative";
    public static final String EXPERIMENTAL = "experimental";
    public static final String FEATURE = "feature";
    public static final String SURVIVAL = "survival";

    @Rule(desc = "Announce when a player has skipped the night", categories = {SURVIVAL})
    public static boolean announceSleep = true;

    @Rule(desc = "Disables some anti-cheat checks", categories = {EXPERIMENTAL, SURVIVAL})
    public static boolean antiCheatDisabled = false;

    @Rule(desc = "Barrier block will suppress any further neighboring block updates", categories = {CREATIVE})
    public static boolean barrierBlockSuppressesUpdates = false;

    @Rule(desc = "Allows entering camera mode even if damage was taken recently", categories = {SURVIVAL})
    public static OpEnableOption cameraModeDisableDamageCooldown = OpEnableOption.FALSE;

    @Rule(
            desc = "Instantly pick-up mined items if sneaking", categories = {FEATURE, SURVIVAL},
            remarks = "Only applicable if the player is subscribed to carefulBreak")
    public static boolean carefulBreak = false;

    @Rule(desc = "Enables /cs for entering/exiting camera mode", categories = {FEATURE, COMMAND, SURVIVAL})
    public static OpEnableOption commandCamera = OpEnableOption.FALSE;

    @Rule(
            desc = "Enables /player for player spawning", categories = {FEATURE, COMMAND, EXPERIMENTAL},
            validator = HandlePlayerChange.class)
    public static OpEnableOption commandPlayer = OpEnableOption.FALSE;

    @Rule(desc = "Enables /tick for tick manipulation", categories = {COMMAND, CREATIVE})
    public static OpEnableOption commandTick = OpEnableOption.OP;

    @Rule(desc = "Disables enderman griefing (i.e. block pick-up)", categories = {SURVIVAL})
    public static boolean endermanNoGriefing = false;

    @Rule(desc = "Specify the block limit for /clone and /fill", categories = {CREATIVE})
    public static int fillLimit = 32768;

    @Rule(desc = "Disables neighboring block updates when using /clone, /fill, and /setblock", categories = {CREATIVE})
    public static boolean fillUpdates = true;

    @Rule(desc = "Skip the end credits when leaving The End", categories = {SURVIVAL})
    public static boolean noEndCredits = false;

    @Rule(desc = "Prevent mobs spawning if it were to immediately despawn", categories = {EXPERIMENTAL, SURVIVAL})
    public static boolean mobsOnlySpawnNearPlayers = false;

    @Rule(desc = "Save log subscriptions to disk and load on start-up", categories = {CREATIVE, SURVIVAL})
    public static boolean persistLogSubscriptions = false;

    @Rule(
            desc = "Percentage of players required to sleep to skip the night", categories = {FEATURE, SURVIVAL},
            options = {"0", "25", "50", "75", "100"})
    public static int sleepPercentage = 100;

    @Rule(desc = "Disables pick-up cooldown for XP orbs", categories = {SURVIVAL})
    public static boolean xpNoCooldown = false;

    public static class HandlePlayerChange implements Validator<OpEnableOption> {
        @Override
        public OpEnableOption validate(CommandSource source, RugRule<OpEnableOption> rule, OpEnableOption parsedValue, String rawValue) {
            if (parsedValue == OpEnableOption.FALSE && rule.current() != parsedValue && source.getWorld() instanceof ServerWorld) {
                FakePlayerManager.getInstance(((ServerWorld) source.getWorld()).getServer()).purge();
            }
            return parsedValue;
        }

        @Override
        public String errorDescription() {
            return null;
        }
    }
}
