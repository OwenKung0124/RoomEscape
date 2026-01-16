import greenfoot.*;
/**
 * 
 * Helper TextLebel to help display texts
 */
public class TextLabel extends Actor
{
    private String text = "";
    private int size = 24;
    private Color textColor = Color.WHITE;
    private Color bgColor = new Color(0,0,0,0); //transparent
    private int life=0;  //-1, don't remove
    private int timer=0;

    public TextLabel(String text, int size, Color textColor, int life)
    {
        this.text=text;
        this.size = size;
        this.textColor = textColor;
        this.life=life;
        
        setImage(new GreenfootImage(text, size, textColor, bgColor));
    }

    public void act() 
    {
        timer++;
        
        if (timer>=life && life!=-1) 
        {
             getWorld().removeObject(this);
        }
    }
}