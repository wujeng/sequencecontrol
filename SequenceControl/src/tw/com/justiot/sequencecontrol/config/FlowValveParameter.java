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

public class FlowValveParameter extends ElementParameter
 {public Port port1;
  public Port port2;
  public boolean oneWay;
  public boolean adjustable; 
  public FlowValveParameter(String mname,String tip,int riwidth,int riheight,String rifile,
       int iwidth,int iheight,String ifile, Position[] pos, int[] dir,boolean ow,boolean adj)
   {super("FlowValve",mname,tip,riwidth,riheight,rifile,iwidth,iheight,ifile,1);    
     if(pos[0]!=null)
      {port1=new Port(pos[0]);
        port1.setDir(dir[0]);
      }
     if(pos[1]!=null)
      {
        port2=new Port(pos[1]);
        port2.setDir(dir[1]);
      }
/*
    if(p1!=null) 
     {port1=new Port(p1);
      port1.setDir(Port.DIR_LEFT);
     }
    else port1=null;
    if(p2!=null) 
     {port2=new Port(p2);
      port2.setDir(Port.DIR_RIGHT);
     }
    else port2=null;
*/
    oneWay=ow;
    adjustable=adj;
   }
 }
