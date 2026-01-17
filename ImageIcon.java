import greenfoot.*;

/**
 * ImageIcon: a simple actor that only displays an image.
 */
public class ImageIcon extends Actor
{
    //the name of the icon, when user mouse click shows
    private String name;
    /**
     * Create an ImageActor with an image file and scale it.
     * @param filename e.g. "stoneIcon.png"
     * @param w image width
     * @param h image height
     * @param alpha transparency level
     */
    public ImageIcon(String filename, String name,int w, int h, int alpha)
    {
        this.name=name;
        GreenfootImage img = new GreenfootImage(filename);
        img.scale(w, h);
        img.setTransparency(alpha);
        setImage(img);
    }
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) 
        {
            //for directing to help
            if(name.equals("How to Play"))
            {
                Greenfoot.setWorld(new HelpWorld( getWorld()));
                return;
            }
            
            //for icons in the gameworld
            getWorld().addObject(new FloatingText(name,Color.CYAN,false),getX()+150,getY());
        }
        
    }
}