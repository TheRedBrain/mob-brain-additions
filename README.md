# Mob Brain Additions

This mod adds features that give additional functionality to mobs that use the brain system.

This mod depends on the SmartBrainLib mod, which implements an optimised and expanded version of the vanilla brain system.

## Current features

## Blocks

- a new (operator only) "Path Finding Node" block, which can save a map of strings and block positions

## Memory Types

- "mob_brain_additions:is_near_home", a unit memory type

## Sensor Types

- "mob_brain_additions:is_near_home", a sensor type that sets the "mob_brain_additions:is_near_home" memory type when the entity is within a configurable radius of its home position. If the entity is further away the "mob_brain_additions:is_near_home" memory type is removed, along with the vanilla walk_target and attack_target memory types
- "mob_brain_additions:update_home_from_path_finding_block", a sensor type that sets home position of an entity according to a nearby "Path Finding Node Block". The Path Finding Node Block has block positions mapped to strings. If the entity is an instance of the "TracksPathFindingNodes" interface, it has a "trackedPathFindingNodeId", which is used to determine which of the block positions of the node block is set aas its new home position.

## Behaviours

- the "SetWalkTargetToHomePosition" behaviour sets the look_target and walk_target memories to the home position of the entity
- the "TargetOrRetaliateWhenNearHome" behaviour works very similar to the existing "TargetOrRetaliate" behaviour, but it only runs when the entity has the "mob_brain_additions:is_near_home" memory. Additionally, the target has to be near the entities home position. The maximum distance is configurable.