# 1.1.0

## Additions

- added "Path Finding Branching Node Block"
  - if the "mob_brain_additions:update_home_from_path_finding_block" sensor detects this block, one block position out of a list (randomly chosen based on a configurable weight) is set as the new HOME
- added "Path Finding End Node Block"
  - saves a list of Strings (used for matching against path finding ids)
  - can be "triggered". This can either be a redstone pulse or (of Script Blocks is installed and this option is chosen for the block) a Script Blocks trigger
- added "OLD_HOME" memory type
- added "PATH_END_POSITION" memory type
- added "mob_brain_additions:is_near_path_end" sensor type
  - saves the position of a nearby and matching "Path Finding End Node Block" to the "PATH_END_POSITION" memory
- added "ReactToNearbyEndOfPath" behaviour
  - if the block placed at the "PATH_END_POSITION" is a "Path Finding End Node Block", that block gets "triggered"
  - optionally discards the entity

## Changes

- updated "mob_brain_additions:is_near_home" sensor type
  - now optionally checks if the OLD_HOME position is within the defined reach. In that case only one of the two positions has to be in reach for the entity to be considered "near home".
  - can now be configured to not remove the "ATTACK_TARGET" and/or the "WALK_TARGET" memory
- updated "mob_brain_additions:update_home_from_path_finding_block" sensor type
    - now optionally updates the OLD_HOME memory to the previous HOME memory

# 1.0.0

This API can be used by other mods to give additional functionality to mobs that use the brain system.

This first release includes:

- a block based path system, where mobs follow a path created by "node blocks"

#