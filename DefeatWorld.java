import greenfoot.*;

/**
 * DefeatWorld (Game Over Screen)
 * 
 * It also display game related information for feedback.
 * 
 * @author:
 * @version
 */
public class DefeatWorld extends World
{

    private GameData data; //shared data that passed around

    public DefeatWorld()
    {
        this(null);
    }

    public DefeatWorld(GameData data)
    {
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        SoundManager.stopGameMusic();

        this.data = data;

        // background image
        GreenfootImage bg = new GreenfootImage("defeat_world_bg.png"); 
        bg.scale(getWidth(), getHeight());
        setBackground(bg);

        //Titles
        addObject(new TextLabel("GAME OVER", 48, Color.WHITE, -1), getWidth()/2, getHeight()/2 - 140);

        //story related line
        addObject(new TextLabel("Owen can't go home now. He needs to stay.", 22, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 - 95);
        addObject(new TextLabel("He needs to stay.", 22, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 - 70);

        //instructions
        addObject(new TextLabel("Press R to Try Again | Press S to go to Setting ", 22, Color.WHITE, -1), getWidth()/2, getHeight()/2 - 45);

        //Results
        addObject(new TextLabel("Results", 30, Color.YELLOW, -1), getWidth()/2, getHeight()/2 +10);
        addObject(new TextLabel("Total Enemies Killed: " + GameWorld.enemiesKilled, 22, Color.WHITE, -1),
                  getWidth()/2, getHeight()/2 + 40);
        if (data != null)
        {
            addObject(new TextLabel("Final Score: " + data.score, 22, Color.WHITE, -1),
                      getWidth()/2, getHeight()/2 + 70);

            addObject(new TextLabel("Coins Unused: " + data.coins, 22, Color.WHITE, -1),
                      getWidth()/2, getHeight()/2 + 100);

            addObject(new TextLabel("Rooms Cleared: " + data.roomsCleared, 22, Color.WHITE, -1),
                      getWidth()/2, getHeight()/2 + 130);

            addObject(new TextLabel("Unused Stone Skill: " + data.stones, 22, Color.WHITE, -1),
                      getWidth()/2, getHeight()/2 + 160);
        }
        
        SoundManager.playDefeatSound();
    }

    public void act()
    {
        //restart
        if (Greenfoot.isKeyDown("r"))
        {
            SaveManager.deleteSave();
            SoundManager.stopDefeatSound();
            SoundManager.playGameMusic(); //game music does not load from gameworld constructor
            Greenfoot.setWorld(new GameWorld(GameConfig.WARRIOR_AXE, false, null));
        }

        //go back to settings page
        if (Greenfoot.isKeyDown("s"))
        {
            //return to setting
            SoundManager.stopDefeatSound();
            SoundManager.playGameMusic();//game music does not load from setting constructor
            Greenfoot.setWorld(new SettingWorld(null));
        }
    }
}