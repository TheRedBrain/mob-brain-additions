# Mob Brain Additions

This API can be used by other mods to give additional functionality to mobs that use the brain system. It depends on the SmartBrainLib mod, which implements an optimised and expanded version of the vanilla brain system.

## Current features

## Memory Types

- "mob_brain_additions:is_near_home", a unit memory type
- "mob_brain_additions:old_home", a GlobalPos memory type
- "mob_brain_additions:path_end_position", a GlobalPos memory type

## Sensor Types

- "mob_brain_additions:is_near_home", a sensor type that sets the "mob_brain_additions:is_near_home" memory type when the entity is within a configurable radius of its home position. If the entity is further away the "mob_brain_additions:is_near_home" memory type is removed.
    - optionally checks if the OLD_HOME position is within the defined reach. In that case only one of the two positions has to be in reach for the entity to be considered "near home".
    - optionally removes the "ATTACK_TARGET" and/or the "WALK_TARGET" memories, if the entity is not "near home"
- "mob_brain_additions:is_near_path_end", a sensor type that sets the "mob_brain_additions:is_near_path_end" memory type to the block position of a "Path Finding End Node" block that is within a configurable radius around the entity. If no "Path End Node" blocks are found, the "mob_brain_additions:is_near_path_end" memory type is removed.
- "mob_brain_additions:update_home_from_path_finding_block", a sensor type that sets home position of an entity according to a nearby "ProvidesPathFindingNode" block. Blocks that implement the "ProvidesPathFindingNode" interface provide the sensor with a block position. The position can depend on an id, which is unique for each entity type, that implements the "TracksPathFindingNodes" interface.
    - optionally saves the previous home position as the "OLD_HOME" position

## Behaviours

- the "ReactToNearbyEndOfPath" behaviour "triggers" a nearby "Path Finding End Node" block and optionally discards the entity
- the "SetWalkTargetToHomePosition" behaviour sets the look_target and walk_target memories to the home position of the entity
- the "TargetOrRetaliateWhenNearHome" behaviour works very similar to the existing "TargetOrRetaliate" behaviour, but it only runs when the entity has the "mob_brain_additions:is_near_home" memory. Additionally, the target has to be near the entities home position. The maximum distance is configurable.

## Blocks

Blocks that provide a block position to the "mob_brain_additions:update_home_from_path_finding_block" sensor:

- "Path Finding Node" (operator only), which provides a configurable and individual position for specific "path finding ids"
- "Path Finding Branching Node" (operator only), which provides a position for one specific "path finding id". The position is randomly chosen from a weighted list of positions.
- "Path Finding End Node" (operator only), which gets "triggered" when mobs with specific "path finding ids" are near the block. This has one of the following effects:
  - if the end node block is in the "redstone mode", the block will emit a redstone signal
  - if the end node block is in the "Script Blocks mode" and the "Script Blocks" mod is installed, another block gets triggered (using the Script Blocks triggering mechanic)