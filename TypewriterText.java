import greenfoot.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class TypewriterText extends Actor
{
    private Queue<String> lines=new LinkedList<String>();

    private String currentLine="";
    private int charIndex=0;

    private String shownText="";   
    private boolean finished=false;

    private int fontSize;
    private Color textColor;
    private int boxW, boxH;
    private int typingSpeed;
    private int typingTimer=0;

    public TypewriterText(String message, int fontSize, Color textColor,int boxW, int boxH, int typingSpeed)
    {
        this.fontSize=fontSize;
        this.boxW=boxW;
        this.boxH=boxH;
        this.typingSpeed=Math.max(1, typingSpeed);

        //fill the queue with lines
        loadLines(message);

        //transparent display box
        GreenfootImage img=new GreenfootImage(boxW, boxH);
        img.setColor(new Color(0,0,0,0));
        img.fill();
        setImage(img);

        //start with the first line
        nextLine();
        redraw();
    }

    public void act()
    {
        if (finished) return;

        typingTimer++;
        if (typingTimer < typingSpeed) return;
        typingTimer=0;

        //Type next character in currentLine
        if (charIndex < currentLine.length())
        {
            shownText += currentLine.charAt(charIndex);  //add character to the text string to be shown
            charIndex++;

            redraw();
            return;
        }

        //finished this line
        shownText += "\n";
        if (!nextLine())
        {
            finished=true; //no more lines
        }
        redraw();
    }

    public boolean isFinished()
    {
        return finished;
    }

    /*
     * when player skip the text
     */
    public void skipToEnd()
    {
        //dump everything instantly
        while (!lines.isEmpty())
        {
            String ln=lines.remove();
            shownText += ln + "\n";
        }
        finished=true;
        redraw();
    }

    /** 
     * Load message into queue line by line 
     */
    private void loadLines(String message)
    {
        if (message == null) message="";

        StringTokenizer tok=new StringTokenizer(message, "\n", true);
        String line="";

        while (tok.hasMoreTokens())
        {
            String t=tok.nextToken();

            if (t.equals("\n"))
            {
                lines.add(line);
                line="";
            }
            else
            {
                line += t;
            }
        }

        //last line
        lines.add(line);
    }

    /** 
     * Move queue -> currentLine. 
     * Returns false if no more lines. 
     */
    private boolean nextLine()
    {
        if (lines.isEmpty()) return false;

        currentLine=lines.remove();
        charIndex=0;
        return true;
    }

    private void redraw()
    {
        //clear image
        GreenfootImage img=getImage();
        img.setColor(new Color(0,0,0,0));
        img.fill();

        int lineH=fontSize + 6;
        int y=10;

        //draw shownText line by line
        StringTokenizer tok=new StringTokenizer(shownText, "\n", true);
        String line="";

        while (tok.hasMoreTokens())
        {
            String t=tok.nextToken();

            if (t.equals("\n"))
            {
                drawLine(img, line, y);
                y += lineH;
                line="";

                if (y > boxH - lineH) break; // stop if out of box
            }
            else
            {
                line += t;
            }
        }

        //last partial line
        if (y <= boxH - lineH)
        {
            drawLine(img, line, y);
        }
    }

    private void drawLine(GreenfootImage img, String line, int y)
    {
        GreenfootImage lineImg=new GreenfootImage(
            line, fontSize, textColor, new Color(0,0,0,0)
        );
        img.drawImage(lineImg, 10, y);
    }
}