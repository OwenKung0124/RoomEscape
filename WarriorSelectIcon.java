import greenfoot.*;
/**
 * WarriorSelectIcon is a clickable image used on the setup screen.
 *
 * - Shows animated warrior picture
 * - When clicked, it tells SetupWorld which warrior type was chosen
 * - If selected, it draws a highlight border
 */

public class WarriorSelectIcon extends Actor
{
    private int type;
    private boolean selected = false;

    //animation
    private GreenfootImage[] frames;
    private int frameIndex = 0;
    private int animTick = 0;
    private int animDelay = 35; 

    //sizes
    private int w, h;

    /**
     * prefix example: setting/axe_  loads axe_1.png, axe_2.png ...
     */
    public WarriorSelectIcon(String framePrefix, int frameCount, int type, int w, int h)
    {
        this.type = type;
        this.w = w;
        this.h = h;

        frames = loadFrames(framePrefix, frameCount, w, h);
        setImage(frames[0]);
    }

    public void act()
    {
        animate();

        if (Greenfoot.mouseClicked(this))
        {
            SettingWorld sw = (SettingWorld)getWorld();
            sw.chooseWarrior(type);
        }
    }

    private void animate()
    {
        if (frames == null || frames.length == 0) return;

        animTick++;
        if (animTick >= animDelay)
        {
            animTick = 0;
            frameIndex = (frameIndex + 1) % frames.length;
            setImage(frames[frameIndex]);

            if (selected) drawSelectedBorder();
        }
    }

    public void setSelected(boolean sel)
    {
        selected = sel;

        // refresh current frame + border
        setImage(frames[frameIndex]);
        if (selected) drawSelectedBorder();
    }

    private void drawSelectedBorder()
    {
        GreenfootImage img = new GreenfootImage(getImage()); // copy
        img.setColor(Color.CYAN);
        img.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        img.drawRect(2, 2, img.getWidth() - 5, img.getHeight() - 5);
        setImage(img);
    }

    private GreenfootImage[] loadFrames(String prefix, int count, int w, int h)
    {
        GreenfootImage[] arr = new GreenfootImage[count];
        for (int i = 0; i < count; i++)
        {
            GreenfootImage img = new GreenfootImage(prefix + (i + 1) + ".png");
            img.scale(w, h);
            arr[i] = img;
        }
        return arr;
    }
}