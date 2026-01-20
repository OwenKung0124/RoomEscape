import greenfoot.*; 

/**
 * World for the story of the main character
 * 
 * @author Clifton Lin
 * @version Jan, 2026
 */
public class StoryWorld extends World
{

    private TypeWriterText storyText;
    //after typing finishes, wait a bit then switch worlds
    private int switchTimer = 0;
    private static final int SWITCH_DELAY = 300; 
    
    /**
     * Constructor for objects of class StoryWorld.
     * 
     */
    public StoryWorld()
    {    
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        
        //set background image
        GreenfootImage bg = new GreenfootImage("setting_bg.jpg");
        bg.scale(getWidth(), getHeight()); //fit the world size
        setBackground(bg);
        
        
        //draw a transparent background, so that text can be shown clearly
        int boxW = 600;
        int boxH = 600;

        // center of the world
        int boxCenterX = getWidth() / 2;
        int boxCenterY = getHeight() / 2;

        // convert center -> top-left for drawing the rectangle
        int boxX = boxCenterX - boxW / 2;
        int boxY = boxCenterY - boxH / 2;

        // semi-transparent dark rectangle
        bg.setColor(new Color(0, 0, 0, 160));
        bg.fillRect(boxX, boxY, boxW, boxH);

        // optional border
        bg.setColor(new Color(255, 255, 255, 200));
        bg.drawRect(boxX, boxY, boxW, boxH);

        setBackground(bg);
 
        
        String msg =
            "Owen walks home after a good day,\n" +
            "scrolling on his phone.\n" +
            "Near an old stone well, the ground opens—\n" +
            "and he falls into darkness.\n\n" +

            "He wakes in a cold underground castle.\n" +
            "A voice warns:\n" +
            "\"No one can leave until the castle is satisfied.\"\n\n" +

            "Each room locks until Owen defeats the guards.\n" +
            "He finds a weapon, earns coins and upgrades,\n" +
            "and fights deeper as enemies grow stronger.\n\n" +

            "At the center, Owen faces the Summoner Boss.\n" +
            "To go back to the world above,\n"+
            "he must defeat the boss and clear all the rooms";

        //msg size, color, box width, box height, speedf
        storyText=new TypeWriterText(msg, 20, Color.WHITE, boxW - 40, boxH - 40, 2);
        addObject(storyText,boxCenterX, boxCenterY);
        
        addObject(new TextLabel("Press Esc to Skip Intro.",30,Color.WHITE,-1),getWidth()/2, getHeight()-30);
        
        //SoundManager.playTypingSound();
        
    }
    public void act()
    {
        if (Greenfoot.isKeyDown("escape"))
        {
            SoundManager.stopTypingSound();
            SoundManager.playGameMusic();
            Greenfoot.setWorld(new SettingWorld());
            return;
        }

        //when finished typing, start countdown
        //then switch world
        if (storyText != null && storyText.isFinished())
        {
            switchTimer++;
            if (switchTimer >= SWITCH_DELAY)
            {
                //go to setting world
                SoundManager.playGameMusic();
                Greenfoot.setWorld(new SettingWorld()); 
            }
        }
    }
    /**
     * Make sure game music starts after restarted
     */
    public void started()
    {
        SoundManager.playTypingSound();
    }
    /**
     * Make sure game music stops when paused
     */
    public void stopped()
    {
        SoundManager.stopTypingSound();
    }
}