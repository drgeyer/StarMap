/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starmap;

import java.util.List;

/**
 *
 * @author 7051665
 */
public class ConstellationLine 
{
    String start;
    String end;
    Star start_star;
    Star end_star;
    
    public ConstellationLine()
    {
    }
    
    public boolean setProperties(String n)
    {
        int begin = 0;
        int last = n.length()-1;
        while(n.charAt(begin) == ' ')
            begin++;
        
        while(n.charAt(last) == ' ')
            last--;
        
        int i = begin;
        while(n.charAt(i) != ' ')
            i++;
        
        start = n.substring(begin, i);
        
        
        i = last;
        
        while(n.charAt(i) != ' ')
            i--;
        
        end = n.substring(i+1, last+1);
        return true;
    }
    
}
