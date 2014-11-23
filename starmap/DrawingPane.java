/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starmap;

import java.awt.*;
import java.awt.event.*;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 *
 * @author 7051665
 */
public class DrawingPane extends JPanel
{
    boolean Constellations_Visible = true;
    boolean Identifiers_Visible = true;
    boolean GridLines_Visible = true;
    public double zoom_factor = 1.0;
    public List<Star> smap = new ArrayList();
    public List<Constellation> cmap = new ArrayList();
    public HashMap<String, Star> NamedStars = new HashMap();
    public double gaze_alt = Math.toRadians(0);
    public double gaze_azi = Math.toRadians(0);
    public double gaze_lat = 44.08;
    public double gaze_lon = -103.23;
    public long now_msec;   //milliseconds elapsed since June 10, 2005 @ 6:45:14 GMT
    public String DateTime;
    private int xpressed, ypressed;
    private int xreleased, yreleased;
    private int xdragged, ydragged;
    public Star SelectedStar = new Star();
    public Double alt_pressed, azi_pressed;
    
    public double VMagCutoff = 99.99;
    
    public DrawingPane()
    {
        setBackground(Color.BLACK);
        
        Calendar cal = Calendar.getInstance();
        now_msec = cal.getTimeInMillis();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        DateTime = dateFormat.format(date); //2014/08/06 15:59:48
        
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent( g );	// call the base class 
        Graphics2D g2d = (Graphics2D)g;

        //because gridlines are BACKGROUND objects draw them first so everything else is on top
        if(GridLines_Visible)
        {
            DrawGridLines(g2d);
        }
        
        //since STARS are the most important draw them last/on top
         //only do this if the Contellations_Visible flag is set
        if(Constellations_Visible && smap.size() > 0)
        {
            for(Constellation item : cmap)
            {
                //now that we are loopoing through all of the constellations, we need to also loop through
                //each line in each constellation
                //for each line in the constellation
                if(ConstellationDrawable(item))
                {
                    Point p1 = new Point();
                    Point p2 = new Point();
                    g2d.setColor(item.ConstellationColor);
                    item.Lines.stream().forEach((_item) -> 
                    {
                        
                        int startx, starty, intensity1;
                        int endx, endy, intensity2;
                        startx = ConvertX(NamedStars.get(_item.start).x);
                        starty = ConvertY(NamedStars.get(_item.start).y);
                        endx = ConvertX(NamedStars.get(_item.end).x);
                        endy = ConvertY(NamedStars.get(_item.end).y);
                        intensity1 = ConvertVMag(NamedStars.get(_item.start).vmag);
                        intensity2 = ConvertVMag(NamedStars.get(_item.end).vmag);
                        p1.x = startx;
                        p1.y = starty;
                        p2.x = endx;
                        p2.y = endy;

                        for(int i = -1;i<1;i++)
                        {
                            for(int j = -1;j<1;j++)
                            {
                                g2d.drawLine(startx + intensity1/2 + i, starty +(intensity1/2)+j,
                                endx +(intensity2 / 2) + i, endy+(intensity2/2)+j);
                            }
                        }
                    });
                    
                    //we have the coords for the last contellation line drawn in p1, p2
                    //draw the name of the constellation halfway though the line
                    g2d.drawChars(item.name.toCharArray(), 0, item.name.length(), p1.x - (p1.x - p2.x)/2, p1.y - (p1.y -p2.y )/2);
                }
            }
        }
        
        //Stars are drawn no matter what
        for(Star item : smap)
        {
            if(item.visible)
            {
                int xpos = ConvertX(item.x);
                int ypos = ConvertY(item.y);
                //vmag goes from -1 to 10 with -1 being biggest 10 being smalles
                int rad = ConvertVMag(item.vmag);
                if(rad == 0)
                    continue;
                g2d.setColor(Color.WHITE);
                g2d.fillOval(xpos, ypos, rad, rad);
                if(Identifiers_Visible && (item.common_name.length() > 0 || !item.name.equalsIgnoreCase("???")))
                {
                    //print out the stars name next to it
                    String print_name = item.common_name;
                    if(print_name.length() == 0)
                        print_name = item.name;
                    
                    //draw the name
                    g2d.setColor(Color.ORANGE);
                    g2d.drawChars(print_name.toCharArray(), 0, print_name.length(), xpos, ypos);
                }
            }
        }

        //Draw the Selected Star as well as its information
        DrawSelectedStar(g2d);
        this.repaint();
        
    }
    
    private void DrawSelectedStar(Graphics2D g2d)
    {
        if(SelectedStar.name.length() == 0)
        {
            return;
        }
        if(SelectedStar.visible && Identifiers_Visible && (SelectedStar.common_name.length() > 0 || !SelectedStar.name.equalsIgnoreCase("???")))
        {
            //print out the stars name next to it
            String print_name = SelectedStar.common_name;
            if(print_name.length() == 0)
                print_name = SelectedStar.name;

            //draw the name
            g2d.setColor(Color.BLUE);
            g2d.drawChars(print_name.toCharArray(), 0, print_name.length(), ConvertX(SelectedStar.x), ConvertY(SelectedStar.y));
            g2d.fillOval(ConvertX(SelectedStar.x), ConvertY(SelectedStar.y), ConvertVMag(SelectedStar.vmag), ConvertVMag(SelectedStar.vmag));
        }
        else if(SelectedStar.visible)
        {
            g2d.setColor(Color.BLUE);
            g2d.fillOval(ConvertX(SelectedStar.x), ConvertY(SelectedStar.y), ConvertVMag(SelectedStar.vmag), ConvertVMag(SelectedStar.vmag));
        }
    }
    
    private boolean ConstellationDrawable(Constellation item)
    {
        //loop through every line
        for(ConstellationLine line :  item.Lines)
        {
            if(NamedStars.containsKey(line.start) && NamedStars.get(line.start).visible && NamedStars.get(line.start).vmag < VMagCutoff
               && NamedStars.containsKey(line.end) && NamedStars.get(line.end).visible && NamedStars.get(line.end).vmag < VMagCutoff)
            {
                //do nothing, keep checking
            }
            else
            {
                //if a star didnt exist, or was not visible dont draw the constellation
                return false;
            }
        }
        return true;
    }
    
    //Cartesian to Screen
    public int ConvertX(double x)
    {
        x *= zoom_factor;
        int screenWidth = this.getWidth() / 2;
        int retval;
        retval = (int)(screenWidth - ((-x)*screenWidth));
        return retval;
    }
    
    //Cartesian to Screen
    public int ConvertY(double y)
    {
        y *= -zoom_factor;
        int screenHeight = this.getHeight()/2;
        int retval;
        retval = (int)(screenHeight - ((-y)*screenHeight));
        return retval;
    }
    
    //Screen to Cartesian
    public double InvertX(int x)
    {
        double screenWidth = this.getWidth()/2;
        double retval = -(1 - (x / screenWidth));
        return (retval / zoom_factor);
    }
    
    //Screen to Cartesian
    public double InvertY(int y)
    {
        double screenHeight = this.getHeight()/2;
        double retval = -(1 - (y / screenHeight));
        return (retval / -zoom_factor);
    }
    
    public int ConvertVMag(double vmag)
    {
        if(vmag > VMagCutoff)
            return 0;
        else if(vmag >= 10)
            return 2;
        
        
        int retval = (int)((10 - vmag)*(10 - vmag))/5;
        if(retval < 2)
            retval = 2;
        return retval;
    }
    
    public void mouseDragged(MouseEvent e)
    {
        //when mouse is dragged get the new position
        xdragged = e.getX();
        ydragged = e.getY();

        //make sure we have at least two points

        int xdiff, ydiff;
        //move our gaze up/down and left/right
        xdiff = (xdragged - xpressed);
        ydiff = (ydragged - ypressed);
        if(xdiff < 0)
            gaze_azi = azi_pressed + ((Math.abs(xdiff)/500.0) / zoom_factor);
        else
            gaze_azi = azi_pressed - ((xdiff/500.0) / zoom_factor);  
        if(ydiff > 0)
        {
            gaze_alt = alt_pressed + (ydiff/500.0 / zoom_factor);
            if(gaze_alt >= Math.PI/2)
                gaze_alt = Math.PI/2;
        }
        else
        {
            gaze_alt = alt_pressed - (Math.abs(ydiff)/500.0 / zoom_factor);
            if(gaze_alt <= 0)
                gaze_alt = 0;
        }
        StarPos.CalculateCoords(smap, gaze_alt, gaze_azi, gaze_lat, gaze_lon, now_msec);
        this.repaint();
    }
    
    public void mousePressed(MouseEvent e) 
    {
        xpressed = e.getX();
        ypressed = e.getY();
        alt_pressed = gaze_alt;
        azi_pressed = gaze_azi;
    }

    public void mouseReleased(MouseEvent e) 
    {
        xreleased = e.getX();
        yreleased = e.getY();
        
        gaze_azi = FixRadians(gaze_azi);
        gaze_alt = FixRadians(gaze_alt);
        
        if(Math.abs(xpressed - xreleased) < 5 && Math.abs(ypressed - yreleased) < 5)
        {
            //now do the mouse clicked code i.e. Select a star if within range
            double min_c = 2;
            double x = InvertX(xreleased);
            double y = InvertY(yreleased);
            System.out.println("(" + x + ", " + y + ")");
            for(Star item : smap)
            {
                if(item.visible && item.vmag < VMagCutoff)
                {
                    double a, b, c;
                    a = Math.abs(x - item.x);
                    b = Math.abs(y - item.y);
                    c = Math.sqrt(a*a + b*b);

                    if(c < min_c)
                    {
                        //this star should probably be selected
                        min_c = c;
                        SelectedStar = item;
                    }
                }
            }
        }
        
    }

    private double FixRadians(double rad) 
    {
        int n = (int)(rad / (2*Math.PI));   //n will be negative if rad is neg
        
        return (rad - (2*Math.PI*n));
    }

    private void DrawGridLines(Graphics2D g2d) 
    {
        g2d.setColor(Color.GREEN);
        //HORIZONTAL LINES
        //draw line along the alt = 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 lines
        Point p1 = new Point(); 
        Point p2;
        for(int i = 0; i <= 90; i += 10)
        {
            for(int j = 0; j <= 360; j++)
            {
                p2 = p1;
                p1 = GetXY(i, j);
                if(j != 0 && p1 != null && p2 != null)
                {
                    if(j % 20 == 5)
                    {
                        g2d.drawChars((Integer.toString(i) + " Deg").toCharArray(), 0, 
                                       (Integer.toString(i) + " Deg").length(), p1.x, p1.y-3);
                    }
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
        
        //VERTICAL LINES
        //draw line along the azi = 0, 10, 20, ... 250 lines each line has azi with alt from 0 to 90
        for(int i = 0; i <= 360; i += 10)
        {
            for(int j = 0; j <= 90; j++)
            {
                p2 = p1;
                p1 = GetXY(j, i);
                if(j != 0 && p1 != null && p2 != null)
                {
                    if(j % 20 == 5 && i != 360 && j != 85)
                        g2d.drawChars((Integer.toString(i) + " Deg").toCharArray(), 0, 
                                      (Integer.toString(i) + " Deg").length(), p1.x+3, p1.y);
                    
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
                    
            }   
        }
        
        //Finaly Draw the Cardinal directions
        g2d.setColor(Color.RED);
        p1 = GetXY(1, 0);
        if(p1 != null)
            g2d.drawChars("NORTH".toCharArray(), 0, 5, p1.x+3, p1.y-3);
        p1 = GetXY(1, 180);
        if(p1 != null)
            g2d.drawChars("SOUTH".toCharArray(), 0, 5, p1.x+3, p1.y-3);
        p1 = GetXY(1, 90);
        if(p1 != null)
            g2d.drawChars("EAST".toCharArray(), 0, 4, p1.x+3, p1.y-3);
        p1 = GetXY(1, 270);
        if(p1 != null)
            g2d.drawChars("WEST".toCharArray(), 0, 4, p1.x+3, p1.y-3);
    }
    
    public Point GetXY(double alt, double azi)
    {
        Point retpoint = new Point();
        alt = Math.toRadians(alt);
        azi = Math.toRadians(azi);
        
        // project star's (alt,azi) position on sphere to (x,y) coordinate on viewing window
        double R = 1.0;		// distance to star: assume all stars are located on sphere of radius 1
        double x = R * Math.cos( alt ) * Math.sin( azi - gaze_azi );
        double y = R * ( Math.cos( gaze_alt ) * Math.sin( alt ) - Math.sin( gaze_alt ) * Math.cos( alt ) * Math.cos( azi - gaze_azi ) );
        double clip = Math.sin( gaze_alt ) * Math.sin ( alt ) + Math.cos( gaze_alt ) * Math.cos( alt ) * Math.cos( azi - gaze_azi );
        
        boolean visible = (clip >= 0.0) && (alt >= Math.toRadians(0) && alt <= Math.toRadians(90));
        if(!visible)
            return null;
        retpoint.x = ConvertX(x);
        retpoint.y = ConvertY(y);
        return retpoint;
    }

    public void LookUp(int n)
    {
        gaze_alt += (n/500.0 / zoom_factor);
        if(gaze_alt >= Math.PI/2)
            gaze_alt = Math.PI/2;
    }
    
    public void LookDown(int n)
    {
        gaze_alt -= (n/500.0 / zoom_factor);
        if(gaze_alt <= 0)
            gaze_alt = 0;
    }
    
    public void LookLeft(int n)
    {
        gaze_azi -= ((n/500.0) / zoom_factor);  
    }
    
    public void LookRight(int n)
    {
        gaze_azi += ((n/500.0) / zoom_factor);
    }



}
