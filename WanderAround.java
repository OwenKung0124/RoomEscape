import greenfoot.*;

/**
 * WanderAround helps wondring actor move in the world
 *  pick a random direction
 *  Keep moving in that direction for a random time
 *  then pick again

 */
public class WanderAround
{
    private int dirX = 0;
    private int dirY = 1;

    private int timer = 0;

    private int minHoldFrames;
    private int extraHoldFrames;

    /**
     * @param minHoldFrames:    minimum frames to keep a direction
     * @param extraHoldFrames:  additional random frames
     */
    public WanderAround(int minHoldFrames, int extraHoldFrames)
    {
        this.minHoldFrames = minHoldFrames;
        this.extraHoldFrames = extraHoldFrames;
    }

    /**
     *   By default
     *   minHoldFramees=30
     *   extraHoldFrames=60;
     */
    public WanderAround()
    {
        this(30, 60);
    }

    /**
     * @param speed:    movement speed
     * @return:         int[]{dx, dy} 
     */
    public int[] nextMove(int speed)
    {
        if (timer <= 0)
        {
            pickNewDirection();
            timer = minHoldFrames + Greenfoot.getRandomNumber(extraHoldFrames);
        }

        timer--;
        return new int[]{ dirX * speed, dirY * speed };
    }

    /**
     * Chooses a new random direction and a random duration to walk.
     */
    private void pickNewDirection() 
    {
        int newDir = Greenfoot.getRandomNumber(6); //0-5

        //0..3 = 4 directions, 
        //4..5 = pause
        if (newDir == 0) 
        { 
            dirX =  1; 
            dirY =  0; 
        }
        else if (newDir == 1) 
        { 
            dirX = -1; 
            dirY =  0; 
        }
        else if (newDir == 2) 
        { 
            dirX =  0; 
            dirY =  1;
        }
        else if (newDir == 3) 
        { 
            dirX =  0; 
            dirY = -1; 
        }
        else 
        { 
            dirX = 0; 
            dirY = 0; 
        }
    }
}