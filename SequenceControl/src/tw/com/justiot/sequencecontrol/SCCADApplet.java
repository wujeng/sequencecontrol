package tw.com.justiot.sequencecontrol;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;
import tw.com.justiot.sequencecontrol.theme.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * 
 *
 * @version 1.3 07/22/99
 * @author Jeff Dinkins
 */

public class SCCADApplet extends JApplet {
    SCCAD laddercad=null;

    public void init() {        
        getContentPane().setLayout(new BorderLayout());
        laddercad=new SCCAD(null,this,null);
        getContentPane().add(BorderLayout.CENTER, laddercad);
    }


    public void destroy() 
    {
//        laddercad.pneumaticPanel.stop();
        laddercad.stopTimer();
        System.runFinalization();
        System.gc();
    }

    public URL getURL(String filename) {
        URL codeBase = this.getCodeBase();
        URL url = null;
	
        try {
            url = new URL(codeBase, filename);
//if(WebLadderCAD.debug)  System.out.println(url);
        } catch (java.net.MalformedURLException e) {
//if(WebLadderCAD.debug)  System.out.println("Error: badly specified URL");
            return null;
        }

        return url;
    }


}
