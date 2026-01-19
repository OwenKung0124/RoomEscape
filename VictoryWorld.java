import greenfoot.*;

/**
 * VictoryWorld (Win Screen)
 * It allows player to go back to setting or go play a new game directly
 * It also shows same game states for feedback
 * 
 * @author:
 * @version:
 * 
 */
public class VictoryWorld extends World
{
    private GameData data; //shared data that passed around

    public VictoryWorld()
    {
        this(null);
    }

    public VictoryWorld(GameData data)
    {
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        SoundManager.stopGameMusic();
    
        this.data = data;
    
        //scale background
        GreenfootImage bg = new GreenfootImage("victory.png"); // change if needed
        bg.scale(getWidth(), getHeight());
    
        //Draw a readable box, so text can be read properly
        int boxW = 760;
        int boxH = 440;
        int boxX = (getWidth() - boxW) / 2;
        int boxY = (getHeight() - boxH) / 2;
    
        //semi transparent black color
        bg.setColor(new Color(0, 0, 0, 170));
        bg.fillRect(boxX, boxY, boxW, boxH);
    
        //add text and states
        setBackground(bg);
    
        //textLabels
        addObject(new TextLabel("Successfully Escaped", 56, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 - 140);
    
        addObject(new TextLabel("Press R to Go Back To Rooms", 22, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 - 70);
    
        addObject(new TextLabel("Press S to Go to Setting", 22, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 - 45);
    
        addObject(new TextLabel("Results", 30, Color.YELLOW, -1),
                  getWidth()/2, getHeight()/2 + 5);
    
                  
        //results lines
        int y = getHeight()/2 + 40;
        int step = 25;

        addObject(new TextLabel("Total Enemies Killed: " + GameWorld.enemiesKilled, 22, Color.WHITE, -1),
                  getWidth()/2, y);
        y+=step;
        addObject(new TextLabel("Health Upgraded " + GameWorld.numOfHealthUpgrade, 22, Color.WHITE, -1),
                getWidth()/2, y);
                
        y+=step;         
        addObject(new TextLabel("Attack Power Upgraded " + GameWorld.numOfAttackUpgrade, 22, Color.WHITE, -1),
                getWidth()/2, y);
        
        y+=step;         
        addObject(new TextLabel("Stone Skilled Aquired " + GameWorld.numOfStoneUpgrade, 22, Color.WHITE, -1),
                getWidth()/2, y);
                
        y+=step;         
        addObject(new TextLabel("Stone Skill Used " + GameWorld.numOfStoneUsed, 22, Color.WHITE, -1),
                getWidth()/2, y);
                
        y+= step;
        addObject(new TextLabel("Total Time Taken to Escape: " + (GameWorld.playerTimeFrames/60) + " Seconds", 22, Color.WHITE, -1),
                  getWidth()/2, y);
                  
        y+= step;
        if (data != null)
        {
            addObject(new TextLabel("Final Score: " + data.score, 22, Color.WHITE, -1),
                      getWidth()/2, y);
                      
            //y += step;

            //addObject(new TextLabel("Unused Stone Skill: " + data.stones, 22, Color.WHITE, -1),
            //          getWidth()/2, y);
            //y += step;
        }
        
        SoundManager.playVictorySound();
    }

    public void act()
    {
        SoundManager.playVictorySound();
        
        //restart
        if (Greenfoot.isKeyDown("r"))
        {
            SaveManager.deleteSave();
            SoundManager.stopVictorySound();
            SoundManager.playGameMusic();
            Greenfoot.setWorld(new GameWorld(GameConfig.WARRIOR_AXE, false, null));
        }

        // go back to settings page
        if (Greenfoot.isKeyDown("s"))
        {
            SoundManager.stopVictorySound();
            SoundManager.playGameMusic();
            Greenfoot.setWorld(new SettingWorld(null));
        }
        
        
    }
      /**
     * Make sure game music starts after restarted
     */
    public void started()
    {
        //SoundManager.playVictorySound();
    }
    /**
     * Make sure game music stops when paused
     */
    public void stopped()
    {
        //SoundManager.stopVictorySound();
    }
}