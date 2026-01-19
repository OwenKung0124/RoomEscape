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
    private boolean visible=true;

    private GreenfootImage img=null;
    public TextLabel(String text, int size, Color textColor, int life)
    {
        this.text=text;
        this.size = size;
        this.textColor = textColor;
        this.life=life;
        
        redraw();
    }
    public void act() 
    {
        timer++;
        
        if (timer>=life && life!=-1) 
        {
             getWorld().removeObject(this);
        }
    }
    /**
     * redraw when text or visibility changes
     */
    private void redraw()
    {
        if (!visible)
        {
            GreenfootImage blank = new GreenfootImage(1, 1);
            blank.setTransparency(0);
            setImage(blank);
            return;
        }
        setImage(new GreenfootImage(text, size, textColor, bgColor));
    }
    /**
     * allows caller to reset text of a certain label
     * 
     * @param msg: message to display
     */
    public void setText(String msg)
    {
        this.text=msg;
        redraw();
    }
     /**
     * allows caller to reset visibility of the text label
     * default is true
     * 
     * @param visible: visibiity of the label
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        redraw();
    }
}