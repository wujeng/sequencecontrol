package tw.com.justiot.sequencecontrol.config;

import java.awt.*;
import java.io.File;

import tw.com.justiot.sequencecontrol.*;

public class EDeviceParameter
 {public String modelName,modelType,toolTip,abbr;
  public static Dimension realImageDim;
  public Image realImage;
  public static Dimension imageDim;
  public int actionType;
  public Image ino,inohc,inc,incho,isol1,isol1on,isol2,isol2on;
  public EDeviceParameter(String mtype,String mname,String tip,String ab,int type,String freal,String fno,String fnohc,String fnc,String fncho,
                             String fsol1,String fsol1on,String fsol2,String fsol2on)
   {modelType=mtype;
    modelName=mname;
    toolTip=tip;
    abbr=ab;
    actionType=type;
//    realImageDim=new Dimension(riwidth,riheight);
//    imageDim=new Dimension(iwidth,iheight);
    realImage=util.loadImage(mtype,mname,"realImage",File.separator+"resources"+File.separator+freal);
    loadImages(mtype,mname,fno,fnohc,fnc,fncho,fsol1,fsol1on,fsol2,fsol2on);
   }

  private void loadImages(String mtype,String mname,String fno,String fnohc,String fnc,String fncho,String fsol1,String fsol1on,String fsol2,String fsol2on) 
   {
      if(!fno.toLowerCase().equals("null"))
       {ino=util.loadImage(mtype,mname,"ino",File.separator+"resources"+File.separator+fno);
       }
      if(!fnohc.toLowerCase().equals("null"))
       {inohc=util.loadImage(mtype,mname,"inohc",File.separator+"resources"+File.separator+fnohc);
       }
      if(!fnc.toLowerCase().equals("null"))
       {inc=util.loadImage(mtype,mname,"inc",File.separator+"resources"+File.separator+fnc);
       }
      if(!fncho.toLowerCase().equals("null"))
       {incho=util.loadImage(mtype,mname,"incho",File.separator+"resources"+File.separator+fncho);
       }
      if(!fsol1.toLowerCase().equals("null"))
       {isol1=util.loadImage(mtype,mname,"isol1",File.separator+"resources"+File.separator+fsol1);
       }
      if(!fsol1on.toLowerCase().equals("null"))
       {isol1on=util.loadImage(mtype,mname,"isol1on",File.separator+"resources"+File.separator+fsol1on);
       }
      if(!fsol2.toLowerCase().equals("null"))
       {isol2=util.loadImage(mtype,mname,"isol2",File.separator+"resources"+File.separator+fsol2);
       }
      if(!fsol2on.toLowerCase().equals("null"))
       {isol2on=util.loadImage(mtype,mname,"isol2on",File.separator+"resources"+File.separator+fsol2on);
       }
   }
 }
