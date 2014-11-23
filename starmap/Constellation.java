/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starmap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 7051665
 */
public class Constellation 
{
    String name;
    String abbreviation;
    ArrayList<ConstellationLine> Lines = new ArrayList();
    boolean Displayed = true;
    Color ConstellationColor;
    
    public Constellation()
    {
    }
    
    public boolean SetProperty(String prop, String n)
    {
        switch(prop)
        {
            case "name":
                name = n;
                break;
            case "abbr":
                abbreviation = n;
                break;
            case "line":
                ConstellationLine tmpLine = new ConstellationLine();
                tmpLine.setProperties(n);
                Lines.add(tmpLine);
                break;
            case "xml":
                break;
            default:
                return false;
        }
        return true;
    }
}
