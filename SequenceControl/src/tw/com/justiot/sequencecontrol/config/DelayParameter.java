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

public class DelayParameter extends ElementParameter
 {public Port[] ports;
  public Position textPos;
  public boolean isUpEnd;
  public boolean isNO;
  public DelayParameter(String mname,String tip,int riwidth,int riheight,String rifile,
       int iwidth,int iheight,String ifile, Position[] pos, int[] dir,Position tpos,boolean upEnd,boolean no)
   {super("Delay",mname,tip,riwidth,riheight,rifile,iwidth,iheight,ifile,2);
    int portnumber=3;
    ports=new Port[portnumber];
    for(int i=0;i<portnumber;i++)
     {ports[i]=new Port(pos[i]);
      ports[i].setDir(dir[i]);
     }
    textPos=tpos;
    isUpEnd=upEnd;
    isNO=no;
   }
 }
