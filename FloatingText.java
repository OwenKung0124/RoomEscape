import greenfoot.*;

/**
 * Floating Text that fades out after appearing.
 * 
 * This is particularly helpful to display the status of a player and create visual effects
 * 
 * @author: Owen Kung (edited by Joe)
 * @version: Oct 2025  //reused from vehicle simulation
 */
public class FloatingText extends Actor
{
    private int timer = 0;
    private boolean floating=true;

    /*
     * Without specifying color, 
     * Use Color.Green
     * Font 20
     * Transparent background
     * 
     * @param messages: message to display
     */
    public FloatingText(String message) 
    {
        GreenfootImage img = new GreenfootImage(message, 20, Color.GREEN, new Color(0,0,0,0)); //green text, transparent bakground
        setImage(img);
    }
    /*
     * Without specifying color, 
     * Font 20
     * Transparent background
     * 
     * @param String messages: message to display
     * @param Color color: color for displaying the text
     */
    public FloatingText(String message, Color color) 
    {
        GreenfootImage img = new GreenfootImage(message, 20, color, new Color(0,0,0,0));//custom color, transparent bakground
        setImage(img);
    }
    /*
     * Without specifying color, 
     * Font 20
     * Transparent background
     * 
     * @param String messages: message to display
     * @param Color color: color for displaying the text
     */
    public FloatingText(String message, Color color,boolean floating) 
    {
        this.floating=floating;
        GreenfootImage img = new GreenfootImage(message, 20, color, new Color(0,0,0,0));//custom color, transparent bakground
        setImage(img);
    }
    /*
     * Determine how long the text will stay on the screen
     */
    public void act() 
    {
        timer++;
        getImage().setTransparency(255 - (timer * 4)); //fade out
        
        if(floating)
        {
             if (timer % 6 == 0) setLocation(getX(), getY() + 1); // move slightly up
        }
       
        if (timer > 60) {
             getWorld().removeObject(this);
        }
    }
}