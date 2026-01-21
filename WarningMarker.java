import greenfoot.*;


public class WarningMarker extends Actor
{
    private int timer;

    public WarningMarker(int time)
    {
        timer = time;
        GreenfootImage img = new GreenfootImage(80, 80);
        img.setColor(new Color(255, 0, 0, 90));
        img.fillOval(0, 0, 80, 80);
        setImage(img);
    }

    public void act()
    {
        timer--;

        //blink
        if (timer % 10 < 5) getImage().setTransparency(40);
        else getImage().setTransparency(140);

        if (timer <= 0 && getWorld() != null) getWorld().removeObject(this);
    }
}