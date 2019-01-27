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

public class ActuatorParameter extends ElementParameter
 {public Port fport,bport;
  public Position LSpos1,LSpos2;
  public ActuatorParameter(String mname,String tooltip,int riwidth,int riheight,String rifile,
                             int iwidth,int iheight,String ifile, int imgnumber, Position fpos, Position bpos, Position ls1, Position ls2)
   {super("Actuator",mname,tooltip,riwidth,riheight,rifile,iwidth,iheight,ifile,imgnumber);
    if(fpos!=null) fport=new Port(fpos);
    else fport=null;
    if(bpos!=null) bport=new Port(bpos);
    else bport=null;
    LSpos1=ls1;
    LSpos2=ls2;
   }
 }
