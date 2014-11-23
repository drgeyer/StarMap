/*
	**** StarPos.java ****

Convert star position in ra/dec
      + viewer position in lat/lon
      + current date/time
   into star position in alt/azi and x/y (orthographic projection).

Ref: Don Teets, College Math J 38(3), pp.170-173, May 2007.

Author: John M. Weiss, Ph.D.
Written for CSC421/521 GUI/OOP class, SDSM&T Fall 2014.

Modifications:
*/
package starmap;

import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class StarPos
{
    public double alt, azi;

    // compute number of days elapsed since June 10, 2005 6:45:14 GMT
    public static double elapsed_days(long now_msec)
    {
        //This is our base date we will do all calculations from
        GregorianCalendar then_cal = new GregorianCalendar();
        then_cal.set( 2005, 5, 10, 6, 45, 14 );

        // need current time in GMT (MST + 6 hours, or MST 7 hours if not daylight savings time)
        long then_msec = then_cal.getTimeInMillis();
        double diff_days = ( now_msec - then_msec ) / 1000.0 / ( 24.0 * 3600.0 );
        // System.out.println( "Diff in days = " + diff_days );
        return diff_days;
    }

    // given observer position (lat,lon), convert star position in (ra,dec) to (azi, alt)
    public void alt_azi( double[] right, double[] decline, double lat, double lon, long now_msec )
    {
        double ra, dec;
        ra = Math.toRadians( ( right[0] + (right[1] / 60) + (right[2] / 3600 )) * 15 );
        dec = Math.toRadians( Math.abs( decline[0] ) + decline[1] / 60 + decline[2] / 3600 );
        if(decline[0] < 0)
            dec = -dec;

        double t = elapsed_days(now_msec);
        // System.out.printf( "t = %.3f days elapsed\n", t );
        double tG = Math.IEEEremainder( 360.0 * 1.0027379093 * t, 360.0 );
        double psi = tG + Math.toDegrees( lon ) + 90;
        // System.out.printf( "psi = %.3f = %.3f\n", psi, Math.toRadians( psi ) );

        // rename ala formulas in Don's paper
        double alpha = ra;
        double beta  = lat;
        double delta = dec;
        psi = Math.toRadians( psi );

        double X =  Math.cos( psi ) * Math.cos( delta ) * Math.cos( alpha )
                  + Math.sin( psi ) * Math.cos( delta ) * Math.sin( alpha );
        double Y = -Math.sin( beta ) * Math.sin( psi ) * Math.cos( delta ) * Math.cos( alpha )
                  + Math.sin( beta ) * Math.cos( psi ) * Math.cos( delta ) * Math.sin( alpha )
                  + Math.cos( beta ) * Math.sin( delta );
        double Z =  Math.cos( beta ) * Math.sin( psi ) * Math.cos( delta ) * Math.cos( alpha )
                  - Math.cos( beta ) * Math.cos( psi ) * Math.cos( delta ) * Math.sin( alpha )
                  + Math.sin( beta ) * Math.sin( delta );
        // System.out.printf( "(X,Y,Z) = (%.3f,%.3f,%3f)\n\n", X, Y, Z );

        // finally compute alt/azi values
        alt = Math.atan( Z / Math.sqrt( X * X + Y * Y ) );
        azi = Math.acos( Y / Math.sqrt( X * X + Y * Y ) );
        if ( X < 0.0 ) azi = 2.0 * Math.PI - azi;
    }
    
    public static void CalculateCoords(List<Star> smap, double gaze_alt, double gaze_azi, double lat, double lon, long now_msec)
    {
        double rad_lat = Math.toRadians(lat);
        double rad_lon = Math.toRadians(lon);
        
        for(Star item: smap) 
        {
            StarPos position = new StarPos();
            
            //convert lattitude and longitude to radians
            
            
            // compute alt/azi of star
            position.alt_azi( item.ra, item.dec, rad_lat, rad_lon, now_msec );
            
            // get direction of viewer's gaze, also in (azi,alt)
            // azi: N=0, E=90, S=180, W=270
            // alt: 0=horizon,90=straight up)
            item.alt = position.alt;
            item.azi = position.azi;
            // project star's (alt,azi) position on sphere to (x,y) coordinate on viewing window
            double R = 1.0;		// distance to star: assume all stars are located on sphere of radius 1
            double x = R * Math.cos( position.alt ) * Math.sin( position.azi - gaze_azi );
            double y = R * ( Math.cos( gaze_alt ) * Math.sin( position.alt ) - Math.sin( gaze_alt ) * Math.cos( position.alt ) * Math.cos( position.azi - gaze_azi ) );
            double clip = Math.sin( gaze_alt ) * Math.sin ( position.alt ) + Math.cos( gaze_alt ) * Math.cos( position.alt ) * Math.cos( position.azi - gaze_azi );
            item.visible = (clip >= 0.0) && (position.alt >= Math.toRadians(0) && position.alt <= Math.toRadians(90));
            item.x = x;
            item.y = y;
        }
    }
}