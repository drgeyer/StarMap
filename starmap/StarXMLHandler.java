/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starmap;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author 7051665
 */
public class StarXMLHandler
{
    String XmlFilename;
    Star starTmp;
    Constellation constTmp;
    String DataString;
    int FileType = -1;  //-1 = unknown, 1 = star, 2 = constellation
    List<Star> StarList = new ArrayList();
    HashMap<String, Star> NamedStarMap = new HashMap();
    List<Constellation> ConstList = new ArrayList();
    boolean ConstellationRead = false;
    boolean StarRead = false;
    
    public StarXMLHandler()
    {
        
    }
    public StarXMLHandler(String XmlFile)
    {
        XmlFilename = XmlFile;
        FileType = -1;
        ParseDocument();
    }
    
    public void ReadXMLFile(String XmlFile)
    {
        XmlFilename = XmlFile;
        FileType = -1;
        ParseDocument();
    }
    
    private void ParseDocument()
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            Document doc = builder.build(XmlFilename);
            Element root = doc.getRootElement();
            ReadChildren(root);
        }
        catch(JDOMException e)
        {
            System.out.println(XmlFilename + " is not well-formed.");
            System.out.println(e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
    
    public void ReadChildren(Element current)
    {
        List items = current.getChildren();
        Element type = (Element) items.get(0);
        if(type.getName().equalsIgnoreCase("constellation"))
        {
                        // get children of current node
            List constellations = items;
            Iterator iterator = constellations.iterator();

            //loop through all stars
            for (Object constellation : constellations) 
            {
                //get star i and read its attributes
                Element node = (Element) constellation;
                List attributes = node.getChildren();
                constTmp = new Constellation();
                constTmp.ConstellationColor = randColor();
                for (Object attribute : attributes) 
                {
                    Element attr = (Element) attribute;
                    String value = attr.getValue().substring(1, attr.getValue().length()-1);
                    constTmp.SetProperty(attr.getName(), value);
                }
                ConstList.add(constTmp);
            }
            ConstellationRead = true;
        }
        else if(type.getName().equalsIgnoreCase("star"))
        {
            // get children of current node
            List stars = items;

            //loop through all stars
            for (Object star : stars) 
            {
                //get star i and read its attributes
                Element node = (Element) star;
                List attributes = node.getChildren();
                starTmp = new Star();
                for (Object attribute : attributes) 
                {
                    Element attr = (Element) attribute;
                    String value = attr.getValue().substring(1, attr.getValue().length()-1);
                    starTmp.SetProperty(attr.getName(), value);
                }
                StarList.add(starTmp);
                if(starTmp.common_name.length() > 0)
                {
                    NamedStarMap.put(starTmp.common_name, starTmp );
                }
                if(starTmp.name.equalsIgnoreCase("???") == false)
                {
                    NamedStarMap.put(starTmp.name, starTmp);
                }
            }
            StarRead = true;
        }
    }
    
    private Color randColor()
    {
        int max = 16777215;
        int min = 0;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        
        Color color = new Color(randomNum);
        
        return color;
    }
    
    public List<Star> GetStarList()
    {
        return StarList;
    }
    
    public HashMap<String, Star> GetNamedStars()
    {
        return NamedStarMap;
    }
    
    public List<Constellation> GetConstellationList()
    {
        return ConstList;
    }
    
    public void ClearStarList()
    {
        StarList = new ArrayList();
        StarRead = false;
    }
    
    public void ClearConstMap()
    {
        ConstList = new ArrayList();
        ConstellationRead = false;
    }
    
    public void ClearNamed()
    {
        NamedStarMap = new HashMap();
    }
}

