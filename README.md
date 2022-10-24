# Rugserver

Rugserver is a low-effort carpetmod rip-off for Minecraft 1.8.9.

This was created for the *disrespecTech* server which current plays in 1.8 and this mod tries to bring some modern-day tooling/information such as TPS and mobcap logging to old MC.

## Rule

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

* Type: `enum`
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

* Type: `enum`
* Default value: `false`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `FEATURE`, `SURVIVAL`

### commandPlayer 
Enables `/player` for player spawning

* Type: `enum`
* Default value: `false`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `FEATURE`, `EXPERIMENTAL`

### commandTick 
Enables `/tick` for tick manipulation

* Type: `enum`
* Default value: `op`
* Required options: `true`, `false`, `op`
* Categories: `COMMAND`, `CREATIVE`, `FEATURE`

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

* `/player <name> spawn` - Spawns a fake player at your curent location
* `/player <yourname> shadow` - Spawns a shadow player to replace you

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
* `/tick rate` - Show current tick rate (default: 20)
* `/tick rate <rate>` - Set tick rate
