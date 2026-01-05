import greenfoot.*;
import java.util.*;

/**
 * DoorSystem controls door rules and the physical blockers.
 *
 * Door rules:
 * - If the current room is cleared (enemies = 0): all doors are usable.
 * - If not cleared: ONLY the "back door" (the room you came from) is usable.
 * - Locked door gaps are physically blocked using invisible Wall objects.
 */
public class DoorSystem 
{

    private final GameWorld world;
    private final GameMap map;

    //Invisible wall blocks added across door gaps while locked.
    private ArrayList<Blocker> doorBlockers = new ArrayList<Blocker>();

    //Used to rebuild door blockers
    //when lock/unlock state changes
    private boolean lastUnlocked = true;

    public DoorSystem(GameWorld world, GameMap map) 
    {
        this.world = world;
        this.map = map;
    }

    /**
     * Call this when a new room is loaded.
     * Clear the old blocker list 
     * because the old Walls were removed from the world.
     */
    public void onRoomLoaded() 
    {
        doorBlockers.clear();

        // IMPORTANT:
        // Reset so the next syncDoorBlockers() call rebuilds correctly in the new room.
        lastUnlocked = true;
    }

    /**
     * Updates which doors are usable (unlocked) in the current room.
     *
     * Rule:
     * - If the room is cleared (no enemies), unlock all doors.
     * - If the room is NOT cleared, only unlock the door that goes back to the previous room.
     *
     * @param roomR current room row
     * @param roomC current room col
     * @param lastRoomR previous room row
     * @param lastRoomC previous room col
     * @param unlockedNow true if the current room has 0 enemies
     */
    public void updateDoorStates(int roomR, int roomC, int lastRoomR, int lastRoomC, boolean unlockedNow) 
    {
        //Calculate which direction would take us back to the previous room.
        //Example: if last room was above, backDr = -1 and backDc = 0.
        int backDr = lastRoomR - roomR;
        int backDc = lastRoomC - roomC;
    
        //Loop through every door currently in this room and decide if it should be unlocked.
        for (Door d : world.getObjects(Door.class)) 
        {
            //A "back door" is the door whose (dr, dc) points 
            //to the last room the player came from.
            boolean isBackDoor = (d.getDr() == backDr && d.getDc() == backDc);
    
            // Unlock logic:
            // - If the room is cleared -> unlock everything
            // - If not cleared -> only unlock the back door
            d.setUnlocked(unlockedNow || isBackDoor);
        }
    }

    /**
     * Adds/removes invisible Wall objects that physically block the doorway gaps.
     *
     * Rule:
     * - If the room is unlocked (enemies = 0): do not block any door gaps.
     * - If the room is locked (enemies > 0): block every door gap except the back door gap.
     *
     * IMPORTANT UPDATE:
     * - Only block a direction if a Door actor actually exists in that direction.
     * - Uses DoorBlocker so blockers are never visible.
     *
     * @param roomR current room row
     * @param roomC current room col
     * @param lastRoomR previous room row
     * @param lastRoomC previous room col
     * @param unlocked true if the current room has 0 enemies
     */
    public void syncDoorBlockers(int roomR, int roomC, int lastRoomR, int lastRoomC, boolean unlocked) 
    {
        //If the unlocked/locked state didn't change 
        //since last frame do nothing.
        if (unlocked == lastUnlocked) 
        {
            return;
        }
        
        lastUnlocked = unlocked;
    
        //Remove old blockers from the world
        for (Blocker b : doorBlockers) 
        {
            if (b != null && b.getWorld() != null) 
            {
                world.removeObject(b);   
            }
        }
        doorBlockers.clear();
    
        // If unlocked
        //need no more blockers
        if (unlocked)
        {
            return;
        }
    
        //Calculate which direction would take us back to the previous room.
        int backDr = lastRoomR - roomR;
        int backDc = lastRoomC - roomC;

        //Block every door EXCEPT the back door.
        blockDoor(-1, 0, backDr, backDc); // UP
        blockDoor( 1, 0, backDr, backDc); // DOWN
        blockDoor( 0,-1, backDr, backDc); // LEFT
        blockDoor( 0, 1, backDr, backDc); // RIGHT
    }

    /**
     * Adds a blocker for a Door in direction (dr,dc) unless it's the back door.
     */
    private void blockDoor(int dr, int dc, int backDr, int backDc)
    {
        // don't block the back door
        if (dr == backDr && dc == backDc) return;

        Door door = findDoor(dr, dc);
        if (door == null) 
        {
             // no actual door here -> don't block
             return;
        }

        int w = (dc != 0) ? GameConfig.BORDER_THICK : GameConfig.DOOR_GAP_W;
        int h = (dc != 0) ? GameConfig.DOOR_GAP_H    : GameConfig.BORDER_THICK;

        Blocker b = new DoorBlocker(w, h);
        world.addObject(b, door.getX(), door.getY());
        doorBlockers.add(b);
    }

    /**
     * Finds the Door in this room that matches (dr,dc).
     */
    private Door findDoor(int dr, int dc)
    {
        for (Door d : world.getObjects(Door.class)) 
        {
            if (d.getDr() == dr && d.getDc() == dc) return d;
        }
        return null;
    }
}