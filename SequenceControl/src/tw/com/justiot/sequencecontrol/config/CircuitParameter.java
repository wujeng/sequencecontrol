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

public class CircuitParameter
 {public String modelType;
  public String modelName;
  public String toolTip;
  public String fileString;
  public Image image;
  public Dimension imageDim;
  public CircuitParameter(String mtype,String mname,String tip,String fstr,int iwidth,int iheight,String ifile)
   {modelType=mtype;
    modelName=mname;
    toolTip=tip;
    fileString=fstr;
    imageDim=new Dimension(iwidth,iheight);
    image=util.loadImage(mtype,mname,"image",File.separator+"resources"+File.separator+ifile);
   }
 }
