import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
/**
 * Room Escape is a 2D room escape game.
 * The story:
 * Owen likes to browse the phone while walking.
 * He steps into a pothole and drops into an underground world.
 * He must combat the enemies there in order to come back out.
 * 
 * The Design:
 * Room Escape game is a room-to-room game based on a 2D array.
 * 2D array is used in:
 * The game layout (4x4 grid, indicating if there's a room or not)
 * The room types (4x4 grid, indicating room type of each room in the map)
 * The room layout (9x16 grid, indicating how each room should be rendered: exterior wall, interior wall, floor, coin, decoration, etc.)
 * 
 * ArrayList:
 * ArrayList of Actors is used to manage room transition cleanup on room load. Only UI objects remain.
 * DoorSystem uses ArrayList to store and manage all DoorBlocker objects in the current room, to manage Door lock and unlock.
 * ArrayList of Enemies to manage gameplay state in combat rooms.
 * 
 * Queue:
 * SummonerBoss spawns enemies in waves.
 * waveQueue is used for each summoning wave:
 * the intervals between summoning, how many minions to spawn, and what type of minions to spawn.
 * 
 * Customizable on Setting:
 * Player can choose which warrior to use for gameplay.
 * Player can also choose if they want music on/off or sound effect on/off.
 * 
 * The Engines:
 * GameWorld uses GameMap, RoomRenderer, DoorSystem, SpawnSystem for main logics.
 * 
 * The player is initially placed in the centre of the first room (row=1, col=1, for a new game),
 *            is placed on the left side of the room where player is left off (for resume game).
 *            
 * Initially all doors are locked. If all enemies are cleared, doors to neighboring rooms will open.
 * The door going back to the previous room remains open. Player can always go back to the previous room
 * while the current room is not cleared and then re-enter. But then the enemies will be respawned again.
 * 
 * For Treasure and shop rooms, doors are always open.
 * 
 * Room Types:
 * C - Combat room (where enemies are spawned). To successfully clear the room, player must defeat all the enemies in a specified time.
 *     If failed, the player is defeated directly and must start a new game.
 * T - Treasure room (mainly for player to collect coins).
 * S - Shop (where player can buy health, attack power, or stone skill).
 * R - Rush room, player must exit the room to the other end of the room before time is up, or a penalty on health will be taken.
 *     If finished within time, score is rewarded.
 * D - Dodge room, player must avoid colliding with the enemy wandering in the room.
 *     The enemy in the room does not take damage from player, neither can player kill the enemy.
 *     If the player successfully prevents collision with the enemy in the dodge room for 10 seconds,
 *     then the room is cleared. Whenever player has contact with enemy in the room, timer resets.
 * 
 * MiniMap
 * is placed at the side panel to help player navigate.
 * T, S, D, R room types will have a little indicator in the MiniMap.
 * 
 * SidePanel:
 * displays which room player is currently in,
 * number of enemies currently in the room,
 * number of rooms cleared / total rooms to clear,
 * MiniMap,
 * Prompt message requiring player input,
 * current game level,
 * player states (coins, stones, health, attacks fired in the session, attack power, etc.)
 * There's also game message that pops up from time to time requiring player attention.
 * 
 * Control:
 * player hits space to attack,
 *        hits k to cast stone skill, so that enemy can't move for about 2 seconds,
 *        hits ESC to exit game,
 *        WSAD and 4 arrows to navigate directions,
 *        when prompted, hits u to upgrade, hits d to decline.
 *        
 * Health:
 * player is initially given a default health of 100.
 * for each enemy defeated, player receives the score equal to the enemy's maxHealth.
 * player can also heal/increase health by purchasing potion using coins; each coin allows player to have 10 health.
 * 
 * Attack Power:
 * Different warrior type is given different default attack power.
 * AxeWarrior and SwordWarrior are given 2, BulletWarrior is given 5 attack power to start.
 * While game is playing, player can choose between different warrior type and increase the warrior's attack power.
 * The attack power for each warrior is then saved.
 * Regardless of which warrior player chooses to use, other attributes such as HP remain the same,
 * while attack power can be different for each warrior type.
 * Tips: use score to increase attack power at shop.
 * 
 * Coin:
 * player can collect coins in rush or dodge room and a lot in treasure rooms.
 * once the coin is collected, it will disappear from the room.
 * when player returns the room, the coin will not be spawned again.
 * if a game is saved and player resumes again, it will not be spawned again; each room data is saved.
 * 
 * Stone:
 * Stone Skill is something warrior can use to freeze enemy for a number of frames.
 * It costs 10 coins to acquire a stone skill.
 * 
 * Warrior Types:
 * AxeWarrior: uses Axe to kill enemy, by default attack power is 3; stone skill=2.
 * SwordWarrior: uses Sword to kill enemy, by default attack power is 3; stone skill=2.
 * BulletWarrior: uses Bullet to kill enemy, by default attack power is 5; stone skill=2.
 * All warriors blink after being hit by the enemy.
 * While being hit, it should not take further damage for a number of frames.
 * But if enemy and player stay touching, it would look like player keeps losing health while blinking.
 * 
 * Enemies:
 * All enemies wander around the room using computeMove to make it move itself.
 * It would not go through walls and would turn around when it hits wall.
 * Skeleton Enemy -> wanders around and fires arrow when warrior is in range. The arrow takes damage of the player, contact too.
 * Zombie Enemy -> targets at the player; when in contact with the player, it sticks to it and takes damage continuously.
 * Wander Enemy -> it wanders in the room as well, but does not follow the player.
 * it also takes damage when in contact with the player.
 * HazardEnemy -> it's only spawned in dodge room. It simply wanders around in the room. It does not take damage from player.
 * Boss Enemy has the maximum health of around 150 defined in the GameConfig.SUMMONER_BOSS_MAX_HEALTH.
 * --> It does not attack; it spawns minions at a random interval.
 * --> Before it spawns minions, it does animation to indicate it's about to spawn minions.
 * --> After spawning minions, it relocates to prevent player staying in the same spot hitting the boss.
 * --> It does not take contact damage from player.
 * --> Appears when all the other rooms are cleared; it's the last room player needs to clear.
 *    
 * Difficulty Level:
 * The difficulty level is based on the number of rooms cleared.
 * The difficulty level increases every two rooms cleared.
 * 
 * Enemies' health and contact damage and damage power get scaled with difficulty.
 * Number of enemies spawned in the combat room also increases with difficulty level.
 * 
 * Player can use the coins collected or score to upgrade their level, such as health, attack power, etc.
 *  
 * File Save Happens When:
 * Player presses ESC and chooses Q, data is saved to file called "save.txt".
 * When player is defeated or wins the game, no data is saved; save.txt is deleted.
 * 
 * File Loads When:
 * In SettingWorld, if a save.txt exists, it will try to load that data, so player can resume; if not, player can only start a new game.
 * In GameWorld, if coming from setting world indicating it's a resume game, then data gets loaded from save.txt.
 * 
 * SaveManager and GameData:
 * Uses SaveManager to handle saving GameData into save.txt or load data from save.txt.
 * 
 * What gets saved:
 * roomR, roomC,
 * lastRoomR, lastRoomC,
 * visited grid,
 * cleared grid,
 * player's health,
 * player's collected coin counts,
 * player's stone skills,
 * player's score,
 * axeWarriorAttack,
 * swordWarriorAttack,
 * bulletWarriorAttack,
 * each room's tile information (to correctly rebuild the room, particularly the coins collected).
 * 
 * Known Bugs:
 * PauseOverLay:
 * If user follows instruction on the PauseOverLay, then things work as expected.
 * But if user stops or pauses or resets in some situations,
 * when user resumes the game, the user must click ESC again to properly start the game,
 * or the game would look like frozen.
 *  
 * Music:
 * The game's music that are called in started and stopped methods in World classes
 * tends to throw error after pressing reset.
 * Tried to find ways to resolve,
 * such as converting to wav file,
 * or use shorter file or file from other source,
 * all lead to the same problem: music jitter or throw error after pressing reset.
 * When coming back from Victory World to setting and starts a new game then music jitters.
 *  
 * Credits:
 * sounds: https://pixabay.com/
 * images: canva
 * 
 * Class Diagram:
 * https://app.diagrams.net/#G1rkrVNgZZXxsKq4-FtFWi-zAjNDvGVG6X#%7B%22pageId%22%3A%22uU1-nqpaO3d9bgY2zWd6%22%7D
 * 
 * @author Owen Kung, Clifton Lin, Cartis Lee
 * @version Jan 2026
 */
public class BlockComment extends World
{

    /**
     * Constructor for objects of class BlockComment.
     * 
     */
    public BlockComment()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 
    }
}
