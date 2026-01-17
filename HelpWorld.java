import greenfoot.*;
/**
 * This world simply display information on 
 *  how to play the game
 *  how scaling, coins, scores work
 * 
 * @author:
 * @version:
 */
public class HelpWorld extends World
{
    //usee background images
    private String[] pages =
    {
        "help/help1.png",
        "help/help2.png"
    };

    private int pageIndex = 0;

    //key edge detection
    private boolean upWasDown = false;
    private boolean downWasDown = false;
    private boolean escWasDown = false;

    private World returnWorld;
    
    /**
     * Creates the HelpWorld and displays the first help page.
     *
     * @param returnWorld the world to go back to when Esc is pressed
     */
    public HelpWorld(World returnWorld)
    {
        super(GameConfig.WORLD_W, GameConfig.WORLD_H, 1);
        this.returnWorld = returnWorld;
        
        //show first page
        showPage(0);
        
        SoundManager.playHelpSound();
    }
    /**
     * Checks for Up/Down/Esc key presses each frame.
     */
    public void act()
    {
        boolean upDown = Greenfoot.isKeyDown("up");
        boolean downDown = Greenfoot.isKeyDown("down");
        boolean escDown = Greenfoot.isKeyDown("escape");

        //UP
        if (upDown && !upWasDown)
        {
            showPage(pageIndex - 1);
        }

        //DOWN
        if (downDown && !downWasDown)
        {
            showPage(pageIndex + 1);
        }

        //ESC
        if (escDown && !escWasDown)
        {
            if (returnWorld != null) 
            {
                SoundManager.stopHelpSound();
                SoundManager.playGameMusic();
                Greenfoot.setWorld(returnWorld);   
            }
            
        }

        upWasDown = upDown;
        downWasDown = downDown;
        escWasDown = escDown;
    }
    /**
     * Displays the requested page index by setting the world background image.
     *
     * @param index the requested page number
     */
    private void showPage(int index)
    {
        if (index < 0) index = 0;
        if (index >= pages.length) index = pages.length - 1;

        pageIndex = index;

        GreenfootImage bg = new GreenfootImage(pages[pageIndex]);
        bg.scale(getWidth(), getHeight());
        setBackground(bg);
    }
}