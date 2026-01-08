import greenfoot.*;

/**
 * AnimatedDecoration: a decoration that plays an animation using
 * files like basePath + index + extension
 * e.g. "decor/lion" + 0 + ".png"  -> "decor/lion0.png"
 */
public class AnimatedDecoration extends SuperSmoothMover
{
    private GreenfootImage[] frames;
    private int frameIndex = 0;

    private int animDelay=20;  // bigger.slower
    private int animTick = 0;


    /**
     * Room display that has animation
     *
     * @param  baseFileName
     * @param frameCount:   how many frames total
     * @param w:            scaled width
     * @param h:            scaled height
    */
    public AnimatedDecoration(String baseFileName, int frameCount, int w, int h)
    {
        frames = new GreenfootImage[frameCount];

        for (int i = 0; i < frameCount; i++)
        {
            String filename = baseFileName + (i+1) + ".png"; 
            GreenfootImage img = new GreenfootImage(filename);
            img.scale(w, h);
            frames[i] = img;
        }

        setImage(frames[0]);
    }

    public void act()
    {
        super.act();

        if (frames == null || frames.length <= 1) return;

        animTick++;
        if (animTick >= animDelay)
        {
            animTick = 0;
            frameIndex++;

            if (frameIndex >= frames.length)
            {
                frameIndex = 0;
            }

            setImage(frames[frameIndex]);
        }
    }
}