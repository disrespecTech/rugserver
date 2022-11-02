# Rugserver

Rugserver is a low-effort carpetmod rip-off for Minecraft 1.8.9.

This was created for the *disrespecTech* server which current plays in 1.8 and this mod tries to bring some modern-day tooling/information such as TPS and mobcap logging to old MC.

## Rule

### allowAutoEating
Players can automatically consume food  
_Action must be attached via /player._  
_Intended for use by bots since 1.8 doesn't have off-hand._  
_Super cheaty, don't use lol._

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `CHEATY`, `SURVIVAL`

### allowXpDumping
Players can drop all its experience on the ground  
_Action must be performed via /player. When set to limited, players only drop the amount they would have if they'd died. 
Intended for use on bots to drop the XP they gain from mob farms._

* Type: `BotExperienceDropType`
* Default value: `false`
* Required options: `true`, `false`, `limited`
* Categories: `CHEATY`, `SURVIVAL`

### announceSleep
Announce when a player has skipped the night  
_Only applicable when `sleepPercentage` is `0`_

* Type: `boolean`
* Default value: `true`
* Required options: `true`, `false`
* Categories: `SURVIVAL`

### antiCheatDisabled
Relaxes some anti-cheat checks to ease high-ping symptoms

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `EXPERIMENTAL`, `SURVIVAL`

### barrierBlockSuppressesUpdates
Barrier block will suppress any further neighboring block updates

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `CREATIVE`, `EXPERIMENTAL`

### cameraModeDisableDamageCooldown 
Allows entering camera mode even if damage was taken recently

* Type: `OpEnableOption`
* Default value: `false`
* Required options: `true`, `false`, `op`
* Categories: `SURVIVAL`

### carefulBreak  
Instantly pick-up mined items if sneaking
_Only applicable if the player is subscribed to `carefulBreak`_

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `FEATURE`, `SURVIVAL`

### commandCamera 
Enables `/cs` for entering/exiting camera mode

* Type: `OpEnableOption`
* Default value: `false`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `FEATURE`, `SURVIVAL`

### commandFlower
Enables `/flower` for painting flowers in flower forests

* Type: `OpEnableOption`
* Default value: `op`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `CREATIVE`

### commandPlayer 
Enables `/player` for player spawning

* Type: `OpEnableOption`
* Default value: `false`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `FEATURE`, `EXPERIMENTAL`

### commandTick 
Enables `/tick` for tick manipulation

* Type: `OpEnableOption`
* Default value: `op`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `CREATIVE`

### endermanNoGriefing
Disables enderman griefing (i.e. block pick-up)

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `CHEATY`, `SURVIVAL`

### fillLimit
Specify the block limit for `/clone` and `/fill`

* Type: `integer`
* Default: `32768`
* Categories: `CREATIVE`

### fillUpdates
Disables neighboring block updates when using /clone, /fill, and /setblock

* Type: `boolean`
* Default value: `true`
* Required options: `true`, `false`
* Categories: `CREATIVE`

### noEndCredits
Skip the end credits when leaving The End

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `SURVIVAL`

### mobsOnlySpawnNearPlayers
Prevent mobs spawning if it were to immediately despawn

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `EXPERIMENTAL`, `SURVIVAL`

### opNoCheating
Disable /tp when an operator is in survival mode

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `SURVIVAL`

### persistLogSubscriptions
Save log subscriptions to disk and load on start-up

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `CREATIVE`, `SURVIVAL`

### sleepPercentage
Percentage of players required to sleep to skip the night

* Type: `integer`
* Default: `100`
* Options: `0`, `25`, `50`, `75`, `100`
* Categories: `FEATURE`, `SURVIVAL`

### xpNoCooldown
Disables pick-up cooldown for XP orbs

* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `SURVIVAL`


## Commands

A list of supported commands.

### Bots
Spawn fake players at your current position. Requires `commandPlayer` rule to execute.

* `/player <name> spawn` - Spawns a fake player at your current location
* `/player <yourname> shadow` - Spawns a shadow player to replace you
* `/player <name> kill` - Kicks a fake player
* `/player <name> attack [once/interval/continuous] [ticks]` - Attack at a set tick interval, continuously, or once
* `/player <name> use  [once/interval/continuous] [ticks]` - Use item at a set tick interval, continuously, or once
* `/player <name> sneak/unsneak` - Make player sneak/unsneak
* `/player <name> mount/dismount` - Make player mount nearest vehicle, or dismount current
* `/player <name> drop [once/interval/continuous] [ticks]` - Drop held item one at a time at a set tick interval, continuously, or once
* `/player <name> dropStack [once/interval/continuous] [ticks]` - Drop entire held item stack at a set tick interval, continuously, or once
* `/player <name> drop all` - Drop entire inventory on to the ground
* `/player <name> drop slot <slot> [true]` - Drop an item from the specified inventory slot, or entire stack if `true` is provided
* `/player <name> drop xp` - Dump players experience on to the ground
    * Requires `allowXpDumping` to be `true` or `limited`
* `/player <name> eat [once/interval/continuous] [ticks]` - Eats a valid food item from inventory when required, checking at a set tick interval, continuously, or once
    * Requires `allowAutoEating` to be `true`

### Logging
Subscribe/unsubscribe to different log streams.

* `/log carefulBreak` - Instantly pick-up mined items if sneaking. Requires `carefulBreak` rule.
* `/log tps` - Show TPS/MSPT in tab footer
* `/log mobcaps` - Show mob count and mob cap for current dimension in tab footer

### Rules
Modify mod rules/features

* `/rug` - List all rules and their current values
* `/rug <rule>` - Describe the target rule and its current value
* `/rug <rule> <value>` - Set the target rule to specified value
* `/rug setDefault <rule>` - Reset default value to its initial value for the target rule
* `/rug setDefault <rule> <value>` - Set the default value for the target rule to persist across restarts

### Utilty

* `/cs` - Toggle camera mode
* `/flower paint [radius] [player]` - Attaches a flower forest painter with set radius to target player
* `/flower stop [player]` - Removes flower forest painter from target player
* `/tick rate` - Show current tick rate (default: 20)
* `/tick rate <rate>` - Set tick rate
