/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starmap;

import org.xml.sax.SAXException;
/**
 *
 * @author 7051665
 */
public class Star 
{
    public int HRnumber;
    public String name = new String();
    public String constellation;
    public double[] ra;
    public double[] dec;
    public double vmag;
    public String starClass = new String();
    public String common_name = new String();
    public double alt, azi;
    public double x, y;
    public boolean visible = false;
    
    public Star()
    {
    }
    
    public boolean SetProperty(String prop, String n)
    {
        switch(prop)
        {
            case "HRnumber":
                HRnumber = Integer.parseInt(n.replaceAll(" ", ""));
                break;
            case "name":
                name = n;
                break;
            case "constellation":
                constellation = n;
                break;
            case "ra":
                ra = ToDoubleArray(n);
                break;
            case "dec":
                dec = ToDoubleArray(n);
                break;
            case "vmag":
                vmag = Double.parseDouble(n);
                break;
            case "class":
                starClass = n;
                break;
            case "common_name":
                common_name = n;
                break;
            case "xml":
                break;
            default:
                return false;
        }
        return true;
    }
    
    private double[] ToDoubleArray(String str)
    {
        double[] arr = new double[3];
        int i = 0;
        int j = 0;
        int pos = 0;
        String substr;
        //while there are garbage characters at start move iterators forward
        while((str.charAt(i) >= 0x3A || str.charAt(i) <= 0x2F) && str.charAt(i) != '-' && str.charAt(i) != '.')
            i++;
        j = i;
        while(j < str.length())
        {
            while(j < str.length() && (str.charAt(j) == '-' || str.charAt(j)  == '.' || (str.charAt(j) >= 0x30 && str.charAt(j) <= 0x39)))
            {
                j += 1;
            }
            substr = str.substring(i, j);
            arr[pos++] = Double.parseDouble(substr);
            i = j+1;
            j = i;
        }
        return arr;
    }   
}
