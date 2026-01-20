import greenfoot.*;
/**
 * Decoration that uses animated images
 * 
 * @author:     Owen Kung
 * @version:    Jan 2026
 */

public class AnimatedDecoration extends Decoration
{
    private GreenfootImage[] frames;
    private int frameIndex=0;
    private int animDelay=20;
    private int animTimer=0;

    /**
     * @param basePath:     basePath of the images
     * @param frameCount:   number of frames
     * @param w,h:          scaled size
     */
    public AnimatedDecoration(String basePath, int frameCount, int w, int h)
    {
        super(basePath + "1.png", w, h); // set initial image using Decoration constructor

        this.animDelay=animDelay;

        //animation frames
        frames=new GreenfootImage[frameCount];
        for (int i=0; i < frameCount; i++)
        {
            GreenfootImage img=new GreenfootImage(basePath + (i + 1) + ".png");
            img.scale(w, h);
            frames[i]=img;
        }
    }

    public void act()
    {
        animTimer++;
        if (animTimer >= animDelay)
        {
            animTimer=0;
            frameIndex=(frameIndex + 1) % frames.length;
            setImage(frames[frameIndex]);
        }
    }
}