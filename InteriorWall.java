import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class InteriorWall here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class InteriorWall extends Blocker
{
    private static final String WALL_SPRITE = "wall.jpg";
        
    public InteriorWall(int w, int h) 
    {
            super(w,h);
            
            GreenfootImage img = new GreenfootImage(w, h);
            VISIBLE=true;
            if (VISIBLE) 
            {
                //Try to use PNG sprite first
                try 
                {
                    GreenfootImage tex = new GreenfootImage(WALL_SPRITE);
                    tex.scale(w, h);
                    setImage(tex);
                    return;
                } 
                catch (IllegalArgumentException e) 
                {
                    //If sprite missing,, use drawn out rectangle
                    img.setColor(new Color(0, 200, 255, 120));
                    img.fill();
                    img.setColor(Color.BLUE);
                    img.drawRect(0, 0, w - 1, h - 1);
                    setImage(img);
                    return;
                }
            } 
            else 
            {
                //fully transparent, but still has size for collisions
                img.setColor(new Color(0, 0, 0, 0));
                img.fill();
                setImage(img);
            }
    } 
        
}
