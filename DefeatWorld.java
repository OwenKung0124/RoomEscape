import greenfoot.*;

/**
 * Defeat World: The world that appears when the player dies, stating that the game is over. 
 * 
 * @author Clifton Lin
 * @version Jan, 2026
 */
public class DefeatWorld extends World
{
    private GameData data; //shared data that passed around

    public DefeatWorld(){
        this(null);
    }
    
    public DefeatWorld(GameData data){
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        SoundManager.stopGameMusic();
        
        this.data = data;
        
        //dark background
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(0, 0, 0, 220));
        bg.fill();
        setBackground(bg);

        //show exit
        showText("GAME OVER", getWidth()/2, getHeight()/2 - 40);
        showText("Enemies Killed:" + GameWorld.enemiesKilled, getWidth()/2, getHeight()/2 +10);
        showText("Press R to Restart", getWidth()/2, getHeight()/2 + 40);
        showText("Press S to go to Setting", getWidth()/2, getHeight()/2 + 70);
    }

    public void act(){
        //restart
        if (Greenfoot.isKeyDown("r")){
            SaveManager.deleteSave();
            Greenfoot.setWorld(new GameWorld(GameConfig.WARRIOR_AXE, false, null));
        }

        //go back to settings page
        if (Greenfoot.isKeyDown("s")){
            //return to setting
            Greenfoot.setWorld(new SettingWorld(data));
        }
    }
}