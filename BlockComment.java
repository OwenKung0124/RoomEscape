import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Room Escape is a 2D room escaped game.
 * The story:
 * Owen likes to browse the phone while walking.
 * He steps into a pothole and drops into a underground world.
 * He must combat the enemies there in order to come back out.
 * 
 * The Design:
 * Room Escape game is a room to room game based on 2D array
 * 2D array is used in:
 * The game layout (4x4 grid, indicating if there's room or not,)
 * The room types (4x4 gridi, indicating room type of each room in the map)
 * The room layout (9x16 grid, indicating how each room should be renderered, exterior wall, interior wall, floor, coin, decoration etc)
 * 
 * The Engines:
 * GameWorld uses GameMap, RoomRenderer, DoorSystem, SpawnSystem for main logics
 * 
 * The player is initailly placed in the centre of the first room (row=1, col=1, for a new game)
 *            is placed in the centre of the room where player is left off (for resume game)
 *            
 * Initally all doors are locked, if all enemies are cleared door to neighboring rooms will open.
 * The door going back to the previous room remains open. Player can always go back to the previous room,
 * while the current room is not cleared and then re-enter.  But then the enemies will be resapwned again.
 * 
 * For Treasure and shop rooms, door always open.
 * 
 * Room Types:
 * C-combat room (where enemies are spawan)
 * B-boos room (spawns only boss, boss will spawn minions)
 * T-Treasusre room (main for player to collect coins now)
 * S-Shop (where player can buy health, attack power or stone skill)
 * 
 * MiniMap
 * is placed at the sidepane to help player navigate
 * B, T, S room types will have a little indicator in the Minimap
 * 
 * SidePanel:
 *  displays which room player is currently in
 *  number of enemies currently in the room
 *  number of rooms cleared vs. total rooms to clear
 *  MiniMap
 *  Proompt Message requiring player input,
 *  Current game level
 *  Player states(coins,stones,health,attacks fired in the session, attack power etc)
 * 
 * Control:
 * player hit space to attack
 *        hit k to cast stone skill, so that enemey can't move for about 2 seoncds
 *        hit ESC to exit game
 *        WSAD and 4 arrows to navigate dirrections
 *        when propmted, hit u to upgrade, hit d to decline
 * Health
 * player is initilaly given a default health of 100
 * for each enemy defeated, player receives the score equal to the enemy's maxhealth
 * player can also heal/increase health by purchsing potion using coins, each coin allows player to have 10 health
 * 
 * Attack Power
 * Different warrior type is given different default attack ppower
 * AxeWarrior and SwordWarrior is given 2, BulletWarrior is given 5 attack power to start
 * WHile game playing, player can choose between different warrior type and increase the warrior's attack power
 * the attack power for each warrior is then saved
 * Regardless which warrior player chooses to use, other attributes such hp remains the same,
 * while attack power can be different for each warrior type
 * 
 * Coin
 * player can collect coins in some combat rooms and a lot in treasure rooms
 * once the con is collected, it will disappear from the room
 * when player returns the room, the coin will not be spawned again
 * if a game is saved and player resume again, it will not be spawned again, each room data is saved
 * 
 * Stone:
 * Stone Skill is something warrior can use to freeze enemy for a number of frames
 * It costs 10 coins to aquire a stone skill
 * 
 * Warrior Types:
 * AxeWarrior:uses Axe to kill enemy, by default attack power is 2: stone skill=2
 * SowrdWarrior:uses Sword to kill enemy, by default attack power is 2: stone skill=2
 * BulletWarrior:uses Bullet to kill enemy, by default attack power is 5: stone skill=2
 * All warriors blinks after being hit by the enemy.
 * While being hit, it should not take furthr damage for a number of frames
 * But if enemy and player stay touching, it would look like player keep loosing health while blinks.
 * 
 * Enemies
 * All enemies wander around the room using computeMove to make it move itself
 * It would not go through walls and turn around when hits wall.
 * Skeleton Enemy -> wanders around and fires arrow when warrior is in range.  The arrow takes damager of the plaery, contact too.
 * Zombie Enemy->targets at the player, when in contact with the player, it sticks to it and takes damages continuously
 * Wander Enemy-> it wanders in the room as well, but does not follow the player, 
 * it also takes damges when in contact with the player. 
 * Boss Enemy has the maxium health of around 150 defined in the GameConfig.SUMMONER_BOSS_MAX_HEALTH
 * -->It does not attack, it spawns minions at a random interval.  
 * -->Before it spawns minions, it does animation to indicate it's about to spawn minions
 * -->After spawning minions, it relocates to prevent player staying in the same spot hitting the boss
 * -->It does not take contact damage from player.
 * -->appears when all the other rooms are cleared, it's the last room player need to clear 
 *    
 * Difficulty Level
 * 
 * 
 * 
 * 
 * File Save Happens When:
 *  Player press ESC and choose Q, data is saved to file called "save.txt"
 *  When player is defeated of wins the game, no data is saved. save.txt is deleted.
 * 
 * File Loads When:
 *  In SettingWorld, if a save.txt exitx, it will try to load that data, so player can resuem, it not player can only start a new game
 *  In GameWorld, if coming from setting world indicating it's a resume game, then data gets loaded from save.txt
 * 
 * SavaManager and GameData
 *  Uses SaveManager to handle saving GameData into save.txt or load data fro save.txt
 * 
 * What gets saved:
 *   roomR, roomC  
 *   lastRoomR,lastRoomC
 *   visited grid
 *   cleared grid
 *   player's health
 *   player's collected coin outns
 *   player's stone skills
 *   player's score
 *   axeWarriorAtack
 *   swordWarriorAttack
 *   bulletWarriorAttack
 *   each room's tile information (to correctly rebuilt the room, particularly the coins collected)
 * 
 * Known Bugs:
 *  EscapeOverLay
 *  If user follows instruction on the PauseOverLay, then things works as expected
 *  But if user stop or pause or reset in some situations
 *  When user resume the game, the user must click esc again to properly start the game
 *  or the game would look like frozen.
 *  
 *  Music:
 *  The game's music that are called in startted and stopped methods in World classes
 *  tends to throw error after pressing reset
 *  Tried totfind ways to resolve
 *  such as converting to wav file
 *  or use shorter or file from other source
 *  all lead to the same problem, music jitter or throw error after pressing reset/ and then play
 *  When play comes back from Victory World to setting and the starts a new game then music jitter
 *  
 * 
 * 
 * Credits:
 * sounds:all from https://pixabay.com/
 * images:canva,
 * 
 * @author Owen Kung, Cliffon Lin, Cartis Lee
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
