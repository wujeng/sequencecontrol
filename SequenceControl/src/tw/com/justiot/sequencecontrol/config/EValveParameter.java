package tw.com.justiot.sequencecontrol.config;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class EValveParameter extends ValveParameter
 {public Image EFsol1;
    public Image EFsol1on;
    public Image EFsol2;
    public Image EFsol2on;
    public Rect actionRect1;
    public Rect actionRect2;
    public Rect inputRect1;
    public Rect inputRect2;
  public EValveParameter(String mname,String tip,int riwidth,int riheight,String rifile,
                             int iwidth,int iheight,String ifile, int imgnumber,
int squarenum, int forcenum, int ftype, boolean exforce, Position[] pos5, String[] connect, Position[] fpos, boolean oway,
String esol1,String esol1on,String esol2,String esol2on,Rect aRect1,Rect aRect2,Rect iRect1,Rect iRect2)
   {super(mname,tip,riwidth,riheight,rifile,iwidth,iheight,ifile,imgnumber,
                                                  squarenum,forcenum,ftype,exforce,pos5,connect,fpos,oway);
    modelType="EValve";
    if(esol1!=null && esol1.length()>0) EFsol1=util.loadImage(modelType,mname,"EFsol1",File.separator+"resources"+File.separator+esol1);
     if(esol1on!=null && esol1on.length()>0) EFsol1on=util.loadImage(modelType,mname,"EFsol1on",File.separator+"resources"+File.separator+esol1on);
     if(esol2!=null && esol2.length()>0) EFsol2=util.loadImage(modelType,mname,"EFsol2",File.separator+"resources"+File.separator+esol2);
     if(esol2on!=null && esol2on.length()>0) EFsol2on=util.loadImage(modelType,mname,"EFsol2on",File.separator+"resources"+File.separator+esol2on);
     actionRect1=aRect1;
     actionRect2=aRect2;
     inputRect1=iRect1;
     inputRect2=iRect2;
   }
 }
