package tw.com.justiot.sequencecontrol.config;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;

public class PressureValveParameter extends ElementParameter
 {public Port[] ports;
  public PressureValveParameter(String mname,String tip,int riwidth,int riheight,String rifile,
                             int iwidth,int iheight,String ifile, int portnumber, Position[] pos, int[] dir)
   {super("PressureValve",mname,tip,riwidth,riheight,rifile,iwidth,iheight,ifile,1);
    ports=new Port[portnumber];
    for(int i=0;i<portnumber;i++)
     {ports[i]=new Port(pos[i]);
      ports[i].setDir(dir[i]);
     }
   }
 }
