package tw.com.justiot.sequencecontrol.config;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import java.net.*;
import java.util.*;

public class PLCParameter
 {public String modelName,modelType,toolTip;
  public Dimension realImageDim;
  public Image realImage;
  public String[] commands;
  public int lineLimit;

  public String[] prefix;
  public int[] base;
  public ArrayList[]  rangeArray;
  public String timerForm;
  public String counterForm;
  public PLCParameter(String mname,String tip,int riwidth,int riheight,String rifile,String[] com,int limit,
                                   String[] prefix,int[] base,ArrayList[] rarray, String tform,String cform)
   {
    modelType="PLC";
    modelName=mname;
    toolTip=tip;
    realImageDim=new Dimension(riwidth,riheight);
    realImage=util.loadImage(modelType,mname,"realImage",File.separator+"resources"+File.separator+rifile);
    commands=com;
    lineLimit=limit;
    this.prefix=prefix;
    this.base=base;
    this.rangeArray=rarray;
    this.timerForm=tform;
    this.counterForm=cform;
//System.err.println(modelType+modelName+toolTip+realImageDim.toString()+rifile+com+lineLimit);
   }

 }
