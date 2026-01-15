import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * World for the story of the main character
 * 
 * @author Clifton Lin
 * @version Jan, 2026
 */
public class StoryWorld extends World
{
    
    /**
     * Constructor for objects of class StoryWorld.
     * 
     */
    public StoryWorld()
    {    
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        prepare();
    }
    
    private void prepare(){
        StoryPlayer player = new StoryPlayer();
        
        int startX = GameConfig.ROOM_X + 100;
        int startY = GameConfig.roomBottom() - 60;
        
        addObject(player, startX, startY);
    }
}
