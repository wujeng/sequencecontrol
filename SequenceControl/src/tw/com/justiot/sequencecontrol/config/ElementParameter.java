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

public class ElementParameter
 {public String modelName,modelType,toolTip;
  public Dimension realImageDim;
  public Image realImage;
  public Dimension imageDim;
  public Image[] images;
  public ElementParameter(String mtype,String mname,String tip,int riwidth,int riheight,String rifile,
                             int iwidth,int iheight,String ifile, int imgnumber)
   {modelType=mtype;
    modelName=mname;
    toolTip=tip;
    realImageDim=new Dimension(riwidth,riheight);
    imageDim=new Dimension(iwidth,iheight);
    realImage=util.loadImage(mtype,mname,"realImage",File.separator+"resources"+File.separator+rifile);
    images=util.loadImages(mtype,mname,ifile,imgnumber);
   }
 }
