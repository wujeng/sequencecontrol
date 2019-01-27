package tw.com.justiot.sequencecontrol.config;

import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class PneumaticConfig
 {
	private static final String file=File.separator+"resources"+File.separator+"config";
//  public static Hashtable parameter=new Hashtable();
//  public static Hashtable circuit=new Hashtable();
  public static LinkedHashMap parameter=new LinkedHashMap();
  public static LinkedHashMap circuit=new LinkedHashMap();
  private String mtype;
  public PneumaticConfig(String lang)
   {
//System.out.println("PneumaticConfig");	   
	String line=null;
    InputStream is;
    InputStreamReader isr;
    URL url;

    try
     {String fstr=file+"-"+lang+".txt";
      BufferedReader breader=new BufferedReader(new FileReader(Paths.get("").toAbsolutePath().toString()+fstr));
      while((line=breader.readLine()) != null)
       {if(line.length()<=0) continue;
        if(line.substring(0,1).equals("%")) continue;
//System.out.println(line);
        if(line.substring(0,1).equals("["))
         {mtype=line.substring(1,line.indexOf("]"));
//System.err.println(mtype);
//Tools.trace("modeltype:"+mtype);
          if(mtype.equals("EDevice"))
           {line=line.substring(line.indexOf("]")+1,line.length()); 
             StringTokenizer token=new StringTokenizer(line);
             int realw=Integer.parseInt(token.nextToken());
             int realh=Integer.parseInt(token.nextToken());
             int iw=Integer.parseInt(token.nextToken());
             int ih=Integer.parseInt(token.nextToken());
             EDeviceParameter.realImageDim=new Dimension(realw,realh);
             EDeviceParameter.imageDim=new Dimension(iw,ih);
           }
          if(mtype.equals("PLC")) PLC(breader);
          continue;
         }
        if(mtype.equals("Actuator")) Actuator(line);
        if(mtype.equals("Valve")) Valve(line);
        if(mtype.equals("Connector")) Connector(line);
        if(mtype.equals("Logic")) Logic(line);
        if(mtype.equals("Delay")) Delay(line);
        if(mtype.equals("FlowValve")) FlowValve(line);
        if(mtype.equals("PressureValve")) PressureValve(line);
        if(mtype.equals("Gauge")) Gauge(line);

        if(mtype.equals("EValve")) EValve(line);
        if(mtype.equals("Cascade Method")) Circuit(line,"Cascade Method");
        if(mtype.equals("Demos")) Circuit(line,"Demos");
        if(mtype.equals("EDemos")) Circuit(line,"EDemos");
        
        if(mtype.equals("EDevice")) EDevice(mtype,line);
        if(mtype.equals("ESystem")) ESystem(line);
       }
     }
    catch(FileNotFoundException e)
     {java.lang.System.err.println("FileNotFound!"+e.getMessage());}
    catch(Exception ee)
     {java.lang.System.err.println(ee.getMessage());}
   }

   private void Actuator(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int imageNumber=Integer.parseInt(token.nextToken());
     int x=Integer.parseInt(token.nextToken());
     int y=Integer.parseInt(token.nextToken());
     Position fpos=null;
     if(x > 0 || y > 0) fpos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position bpos=null;
     if(x > 0 || y > 0) bpos=new Position(x,y);

     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position ls1=null;
     if(x > 0 || y > 0) ls1=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position ls2=null;
     if(x > 0 || y > 0) ls2=new Position(x,y);
     parameter.put(modelName,new ActuatorParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,imageNumber,fpos,bpos,ls1,ls2));
     }
     catch(Exception e)
      {java.lang.System.err.println("Actuator configuration error!"+e.getMessage());}
    }

   private void Valve(String line)
    {try{
//Tools.trace(line);
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int imageNumber=Integer.parseInt(token.nextToken());
//Tools.trace("valve"+imageFile);
     int squareNumber=Integer.parseInt(token.nextToken());
     int forceNumber=Integer.parseInt(token.nextToken());
     int forceType=0;
     String ftypestr=token.nextToken();
     if(ftypestr.equals("FLUID")) forceType=ValveParameter.FORCE_FLUID;
     if(ftypestr.equals("MAN")) forceType=ValveParameter.FORCE_MAN;
     if(ftypestr.equals("MECHANIC")) forceType=ValveParameter.FORCE_MECHANIC;
     if(ftypestr.equals("ELECTRIC")) forceType=ValveParameter.FORCE_ELECTRIC;

     boolean exclusiveForce=false;
     if(token.nextToken().equals("1")) exclusiveForce=true;

     int x=Integer.parseInt(token.nextToken());
     int y=Integer.parseInt(token.nextToken());
     Position ppos=null;
     if(x > 0 || y > 0) ppos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position rpos=null;
     if(x > 0 || y > 0) rpos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position spos=null;
     if(x > 0 || y > 0) spos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position apos=null;
     if(x > 0 || y > 0) apos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position bpos=null;
     if(x > 0 || y >0) bpos=new Position(x,y);
     Position[] pos5=new Position[]{ppos,rpos,spos,apos,bpos};

     String[] connections=new String[squareNumber];
     for(int i=0;i<squareNumber;i++)
      connections[i]=token.nextToken();

     Position[] forcePos=new Position[forceNumber];
     for(int i=0;i<forceNumber;i++)
      {forcePos[i]=null;
       x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
//Tools.trace("x"+x+":y"+y);
       if(x > 0 || y > 0) forcePos[i]=new Position(x,y);
      }

     boolean oneWay=false;
     if(token.nextToken().equals("yes")) oneWay=true;
     parameter.put(modelName,new ValveParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,imageNumber,
         squareNumber,forceNumber,forceType,exclusiveForce, pos5, connections, forcePos, oneWay));
     }
     catch(Exception e)
      {java.lang.System.err.println("Valve configuration error!"+e.getMessage());}
    }

   private void Connector(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int portNumber=Integer.parseInt(token.nextToken());
     Position[] pos=new Position[portNumber];
     int[] dir=new int[portNumber];
     int x=0,y=0;
     String dirstr=null;
     for(int i=0;i<portNumber;i++)
      {x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
       dirstr=token.nextToken().toLowerCase();
       pos[i]=null;   
       if(x > 0 || y > 0) 
        {pos[i]=new Position(x,y);
         if(dirstr.equals("up")) dir[i]=Port.DIR_UP;
         else if(dirstr.equals("down")) dir[i]=Port.DIR_DOWN;
         else if(dirstr.equals("right")) dir[i]=Port.DIR_RIGHT;
         else if(dirstr.equals("left")) dir[i]=Port.DIR_LEFT;
         else dir[i]=Port.DIR_DOWN;
        }
      }
     parameter.put(modelName,new ConnectorParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,portNumber,pos,dir));
     }
     catch(Exception e)
      {java.lang.System.err.println("Connector configuration error!"+e.getMessage());}
    }

   private void Logic(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int x=Integer.parseInt(token.nextToken());
     int y=Integer.parseInt(token.nextToken());
     Position apos=null;
     if(x > 0 || y > 0) apos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position xpos=null;
     if(x > 0 || y > 0) xpos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position ypos=null;
     if(x > 0 || y > 0) ypos=new Position(x,y);
     parameter.put(modelName,new LogicParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,apos,xpos,ypos));
     }
     catch(Exception e)
      {java.lang.System.err.println("Logic configuration error!"+e.getMessage());}
    }

  private void Delay(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int portNumber=3;
     Position[] pos=new Position[portNumber];
     int[] dir=new int[portNumber];
     int x=0,y=0;
     String dirstr=null;
     for(int i=0;i<portNumber;i++)
      {x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
       dirstr=token.nextToken().toLowerCase();
       pos[i]=null;   
       if(x > 0 || y > 0) 
        {pos[i]=new Position(x,y);
         if(dirstr.equals("up")) dir[i]=Port.DIR_UP;
         else if(dirstr.equals("down")) dir[i]=Port.DIR_DOWN;
         else if(dirstr.equals("right")) dir[i]=Port.DIR_RIGHT;
         else if(dirstr.equals("left")) dir[i]=Port.DIR_LEFT;
         else dir[i]=Port.DIR_DOWN;
        }
      }
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position tpos=null;
     if(x > 0 || y > 0) tpos=new Position(x,y);
     boolean upEnd=true;
     if(token.nextToken().equals("0")) upEnd=false;
     boolean open=true;
     if(token.nextToken().equals("0")) open=false;     
     parameter.put(modelName,new DelayParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,pos,dir,tpos,upEnd,open));
     }
     catch(Exception e)
      {java.lang.System.err.println("Logic configuration error!"+e.getMessage());}
    }

  private void FlowValve(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();

     Position[] pos=new Position[2];
     int[] dir=new int[2];
     int x=0,y=0;
     String dirstr=null;
     for(int i=0;i<2;i++)
      {x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
       dirstr=token.nextToken().toLowerCase();
//System.err.println(x+":"+y+":"+dirstr);
       pos[i]=null;   
       if(x > 0 || y > 0) 
        {pos[i]=new Position(x,y);
         if(dirstr.equals("up")) dir[i]=Port.DIR_UP;
         else if(dirstr.equals("down")) dir[i]=Port.DIR_DOWN;
         else if(dirstr.equals("right")) dir[i]=Port.DIR_RIGHT;
         else if(dirstr.equals("left")) dir[i]=Port.DIR_LEFT;
         else dir[i]=Port.DIR_DOWN;
        }
      }

/*
     int x=Integer.parseInt(token.nextToken());
     int y=Integer.parseInt(token.nextToken());
     Pos pos1=null;
     if(x > 0 || y > 0) pos1=new Pos(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Pos pos2=null;
     if(x > 0 || y > 0) pos2=new Pos(x,y);
*/

     String para=token.nextToken();
     boolean oneWay=false;
     if(para.equals("1")) oneWay=true;
     para=token.nextToken();
     boolean adjust=false;
     if(para.equals("1")) adjust=true;
     parameter.put(modelName,new FlowValveParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,pos,dir,oneWay,adjust));
     }
     catch(Exception e)
      {java.lang.System.err.println("FlowValve configuration error!"+e.getMessage());}
    }

  private void PressureValve(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int portNumber=Integer.parseInt(token.nextToken());
     Position[] pos=new Position[portNumber];
     int[] dir=new int[portNumber];
     int x=0,y=0;
     String dirstr=null;
     for(int i=0;i<portNumber;i++)
      {x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
       dirstr=token.nextToken().toLowerCase();
       pos[i]=null;   
       if(x > 0 || y > 0) 
        {pos[i]=new Position(x,y);
         if(dirstr.equals("up")) dir[i]=Port.DIR_UP;
         else if(dirstr.equals("down")) dir[i]=Port.DIR_DOWN;
         else if(dirstr.equals("right")) dir[i]=Port.DIR_RIGHT;
         else if(dirstr.equals("left")) dir[i]=Port.DIR_LEFT;
         else dir[i]=Port.DIR_DOWN;
        }
      }
     parameter.put(modelName,new PressureValveParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,portNumber,pos,dir));
     }
     catch(Exception e)
      {java.lang.System.err.println("Connector configuration error!"+e.getMessage());}
    }

  private void Gauge(String line)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int portNumber=Integer.parseInt(token.nextToken());
     Position[] pos=new Position[portNumber];
     int[] dir=new int[portNumber];
     int x=0,y=0;
     String dirstr=null;
     for(int i=0;i<portNumber;i++)
      {x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
       dirstr=token.nextToken().toLowerCase();
       pos[i]=null;   
       if(x > 0 || y > 0) 
        {pos[i]=new Position(x,y);
         if(dirstr.equals("up")) dir[i]=Port.DIR_UP;
         else if(dirstr.equals("down")) dir[i]=Port.DIR_DOWN;
         else if(dirstr.equals("right")) dir[i]=Port.DIR_RIGHT;
         else if(dirstr.equals("left")) dir[i]=Port.DIR_LEFT;
         else dir[i]=Port.DIR_DOWN;
        }
      }
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position tpos= new Position(x,y);
     parameter.put(modelName,new GaugeParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,portNumber,pos,dir,tpos));
     }
     catch(Exception e)
      {java.lang.System.err.println("Gauge configuration error!"+e.getMessage());}
    }

  private void Circuit(String line,String modelType)
    {try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(circuit.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     String fstr=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     circuit.put(modelName,new CircuitParameter(modelType,modelName,toolTip,fstr,realImageWidth,realImageHeight,realImageFile));
     }
     catch(Exception e)
      {java.lang.System.err.println("Circuit configuration error!"+e.getMessage());}
    }

  private void EValve(String line)
    {try{
//Tools.trace(line);
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int imageNumber=Integer.parseInt(token.nextToken());
//Tools.trace("valve"+imageFile);
     int squareNumber=Integer.parseInt(token.nextToken());
     int forceNumber=Integer.parseInt(token.nextToken());
     int forceType=0;
     String ftypestr=token.nextToken();
     if(ftypestr.equals("FLUID")) forceType=ValveParameter.FORCE_FLUID;
     if(ftypestr.equals("MAN")) forceType=ValveParameter.FORCE_MAN;
     if(ftypestr.equals("MECHANIC")) forceType=ValveParameter.FORCE_MECHANIC;
     if(ftypestr.equals("ELECTRIC")) forceType=ValveParameter.FORCE_ELECTRIC;

     boolean exclusiveForce=false;
     if(token.nextToken().equals("1")) exclusiveForce=true;

     int x=Integer.parseInt(token.nextToken());
     int y=Integer.parseInt(token.nextToken());
     Position ppos=null;
     if(x > 0 || y > 0) ppos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position rpos=null;
     if(x > 0 || y > 0) rpos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position spos=null;
     if(x > 0 || y > 0) spos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position apos=null;
     if(x > 0 || y > 0) apos=new Position(x,y);
     x=Integer.parseInt(token.nextToken());
     y=Integer.parseInt(token.nextToken());
     Position bpos=null;
     if(x > 0 || y >0) bpos=new Position(x,y);
     Position[] pos5=new Position[]{ppos,rpos,spos,apos,bpos};

     String[] connections=new String[squareNumber];
     for(int i=0;i<squareNumber;i++)
      connections[i]=token.nextToken();

     Position[] forcePos=new Position[forceNumber];
     for(int i=0;i<forceNumber;i++)
      {forcePos[i]=null;
       x=Integer.parseInt(token.nextToken());
       y=Integer.parseInt(token.nextToken());
//Tools.trace("x"+x+":y"+y);
       if(x > 0 || y > 0) forcePos[i]=new Position(x,y);
      }

     boolean oneWay=false;
     if(token.nextToken().equals("yes")) oneWay=true;

     String esol1=token.nextToken();
     String esol1on=token.nextToken();
     String esol2=token.nextToken();
     String esol2on=token.nextToken();
     if(esol1.toLowerCase().equals("null")) esol1=null;
     if(esol1on.toLowerCase().equals("null")) esol1on=null;
     if(esol2.toLowerCase().equals("null")) esol2=null;
     if(esol2on.toLowerCase().equals("null")) esol2on=null;

     int x1=Integer.parseInt(token.nextToken());
     int y1=Integer.parseInt(token.nextToken());
     int x2=Integer.parseInt(token.nextToken());
     int y2=Integer.parseInt(token.nextToken());
     Rect rect1=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      rect1=null;
     else
      rect1=new Rect(x1,y1,x2,y2);

     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect rect2=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      rect2=null;
     else
      rect2=new Rect(x1,y1,x2,y2);
 
     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect irect1=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      irect1=null;
     else
      irect1=new Rect(x1,y1,x2,y2);

     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect irect2=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      irect2=null;
     else
      irect2=new Rect(x1,y1,x2,y2);

     parameter.put(modelName,new EValveParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,imageNumber,
         squareNumber,forceNumber,forceType,exclusiveForce, pos5, connections, forcePos, oneWay,
         esol1,esol1on,esol2,esol2on,rect1,rect2,irect1,irect2));
     }
     catch(Exception e)
      {java.lang.System.err.println("Valve configuration error!"+e.getMessage());}
    }

  private void EDevice(String modelType,String line)
    {try{
//Tools.trace(line);
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     String abbr=token.nextToken();
     String type=token.nextToken().toLowerCase();
     int ntype=CEDevice.TYPE_MANUAL_AUTO;
     if(type.equals("manual_toggle")) ntype=CEDevice.TYPE_MANUAL_TOGGLE;
     else if(type.equals("mechanic")) ntype=CEDevice.TYPE_MECHANIC;
     else if(type.equals("electric")) ntype=CEDevice.TYPE_ELECTRIC;
     else if(type.equals("timer")) ntype=CEDevice.TYPE_TIMER;
     else if(type.equals("counter")) ntype=CEDevice.TYPE_COUNTER;
     else if(type.equals("sensor")) ntype=CEDevice.TYPE_SENSOR;
     String freal=token.nextToken();
     String fno=token.nextToken();
     String fnohc=token.nextToken();
     String fnc=token.nextToken();
     String fncho=token.nextToken();
     String fsol1=token.nextToken();
     String fsol1on=token.nextToken();
     String fsol2=token.nextToken();
     String fsol2on=token.nextToken(); 
     parameter.put(modelName,new EDeviceParameter(modelType,modelName,toolTip,abbr,ntype,freal,fno,fnohc,fnc,fncho,fsol1,fsol1on,fsol2,fsol2on));
     }
     catch(Exception e)
      {java.lang.System.err.println("Valve configuration error!"+e.getMessage());}
    }

  private void ESystem(String line)
    {
//System.err.println(line);
    try{
     StringTokenizer token=new StringTokenizer(line);
     String modelName=token.nextToken();
     if(parameter.containsKey(modelName)) return;
     String toolTip=token.nextToken();
     int realImageWidth=Integer.parseInt(token.nextToken());
     int realImageHeight=Integer.parseInt(token.nextToken());
     String realImageFile=token.nextToken();
     int imageWidth=Integer.parseInt(token.nextToken());
     int imageHeight=Integer.parseInt(token.nextToken());
     String imageFile=token.nextToken();
     int imageNumber=Integer.parseInt(token.nextToken());

     String esol1=token.nextToken();
     String esol1on=token.nextToken();
     String esol2=token.nextToken();
     String esol2on=token.nextToken();
     if(esol1.toLowerCase().equals("null")) esol1=null;
     if(esol1on.toLowerCase().equals("null")) esol1on=null;
     if(esol2.toLowerCase().equals("null")) esol2=null;
     if(esol2on.toLowerCase().equals("null")) esol2on=null;

     int x1=Integer.parseInt(token.nextToken());
     int y1=Integer.parseInt(token.nextToken());
     int x2=Integer.parseInt(token.nextToken());
     int y2=Integer.parseInt(token.nextToken());
     Rect rect1=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      rect1=null;
     else
      rect1=new Rect(x1,y1,x2,y2);

     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect rect2=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      rect2=null;
     else
      rect2=new Rect(x1,y1,x2,y2);
 
     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect irect1=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      irect1=null;
     else
      irect1=new Rect(x1,y1,x2,y2);

     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect irect2=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      irect2=null;
     else
      irect2=new Rect(x1,y1,x2,y2);
/*
     x1=Integer.parseInt(token.nextToken());
     y1=Integer.parseInt(token.nextToken());
     x2=Integer.parseInt(token.nextToken());
     y2=Integer.parseInt(token.nextToken());
     Rect rectls=null;
     if(x1==0 && y1==0 && x2==0 && y2==0)
      rectls=null;
     else
      rectls=new Rect(x1,y1,x2,y2);
*/
     
     int lsx=Integer.parseInt(token.nextToken());
     int lsy=Integer.parseInt(token.nextToken());
     Position ls1pos=null;
     if(lsx>0 && lsy>0) ls1pos=new Position(lsx, lsy);
     
     lsx=Integer.parseInt(token.nextToken());
     lsy=Integer.parseInt(token.nextToken());
     Position ls2pos=null;
     if(lsx>0 && lsy>0) ls2pos=new Position(lsx, lsy);
     
     String sound=token.nextToken();
     if(sound.toLowerCase().equals("null")) sound=null;

     parameter.put(modelName,new SystemParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
         imageWidth,imageHeight,imageFile,imageNumber,esol1,esol1on,esol2,esol2on,rect1,rect2,irect1,irect2,ls1pos, ls2pos, sound));
//         no,nohc,nc,ncho,no2,nohc2,nc2,ncho2));
     }
     catch(Exception e)
      {java.lang.System.err.println("System configuration error!"+e.getMessage());}
    }


  private void PLC(BufferedReader breader)
    {String line;
      while(true)
        {try
          {
           while((line=breader.readLine()) != null)
             {if(line.length()<=0) continue;
               if(line.substring(0,1).equals("%")) continue;
               if(line.substring(0,1).equals("["))
                {mtype=line.substring(1,line.indexOf("]"));
                  if(mtype.equals("EDevice"))
                   {line=line.substring(line.indexOf("]")+1,line.length()); 
                     StringTokenizer token=new StringTokenizer(line);
                     int realw=Integer.parseInt(token.nextToken());
                     int realh=Integer.parseInt(token.nextToken());
                     int iw=Integer.parseInt(token.nextToken());
                     int ih=Integer.parseInt(token.nextToken());
                     EDeviceParameter.realImageDim=new Dimension(realw,realh);
                     EDeviceParameter.imageDim=new Dimension(iw,ih);
                   }
                  return;
                }
//System.err.println(line);
              StringTokenizer token=new StringTokenizer(line);
              String modelName=(token.nextToken()).replace('_',' ');
              if(parameter.containsKey(modelName)) return;
              String toolTip=token.nextToken();
              int realImageWidth=Integer.parseInt(token.nextToken());
              int realImageHeight=Integer.parseInt(token.nextToken());
              String realImageFile=token.nextToken();

              while((line=breader.readLine())!=null)
               {if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              String[] com=new String[15];
              for(int i=0;i<com.length;i++)
               com[i]=(token.nextToken()).replace('_',' ');
/*
String[] com=new String[16];
              com[0]="NOP";
              for(int i=0;i<com.length-1;i++)
               com[i+1]=(token.nextToken()).replace('_',' ');
*/
              int limit=Integer.parseInt(token.nextToken());

              String[] prefix=new String[5];
              int[] base=new int[5];
              ArrayList[] rangeArray=new ArrayList[5];
              for(int i=0;i<rangeArray.length;i++)
               rangeArray[i]=new ArrayList(); 
              String temptoken=null;
              int ind=-1;
              Range rang=null;
              int tcount=0;

              while((line=breader.readLine())!=null)
               {if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              prefix[0]=token.nextToken();
              if(prefix[0].toLowerCase().equals("null")) prefix[0]="";
              base[0]=Integer.parseInt(token.nextToken());
              tcount=token.countTokens();
              for(int i=0;i<tcount;i++)
               {temptoken=token.nextToken();
                  ind=temptoken.indexOf("-");
                  rangeArray[0].add(new Range(Integer.parseInt(temptoken.substring(0,ind),base[0]),
                                                                Integer.parseInt(temptoken.substring(ind+1,temptoken.length()),base[0])));
                }

              while((line=breader.readLine())!=null)
               {if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              prefix[1]=token.nextToken();
              if(prefix[1].toLowerCase().equals("null")) prefix[1]="";
              base[1]=Integer.parseInt(token.nextToken());
              tcount=token.countTokens();
              for(int i=0;i<tcount;i++)
               {temptoken=token.nextToken();
                ind=temptoken.indexOf("-");
                  rangeArray[1].add(new Range(Integer.parseInt(temptoken.substring(0,ind),base[1]),
                                                                Integer.parseInt(temptoken.substring(ind+1,temptoken.length()),base[1])));
                }

              while((line=breader.readLine())!=null)
               {if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              prefix[2]=token.nextToken();
              if(prefix[2].toLowerCase().equals("null")) prefix[2]="";
              base[2]=Integer.parseInt(token.nextToken());
              tcount=token.countTokens();
              for(int i=0;i<tcount;i++)
               {temptoken=token.nextToken();
                 ind=temptoken.indexOf("-");
                  rangeArray[2].add(new Range(Integer.parseInt(temptoken.substring(0,ind),base[2]),
                                                                Integer.parseInt(temptoken.substring(ind+1,temptoken.length()),base[2])));
                }

              while((line=breader.readLine())!=null)
               {
//System.err.println(line);
                 if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              prefix[3]=token.nextToken();
              if(prefix[3].toLowerCase().equals("null")) prefix[3]="";
              String tform=token.nextToken();
              base[3]=Integer.parseInt(token.nextToken());
              tcount=token.countTokens();
              for(int i=0;i<tcount;i++)
               {temptoken=token.nextToken();
                 ind=temptoken.indexOf("-");
                  rangeArray[3].add(new Range(Integer.parseInt(temptoken.substring(0,ind),base[3]),
                                                                Integer.parseInt(temptoken.substring(ind+1,temptoken.length()),base[3])));
                }

              while((line=breader.readLine())!=null)
               {if(line.length()<=0) continue;
                 if(line.substring(0,1).equals("%")) continue;
                 else break;
               }
              if(line==null) return;
//System.err.println(line);
              token=new StringTokenizer(line);
              prefix[4]=token.nextToken();
              if(prefix[4].toLowerCase().equals("null")) prefix[4]="";
              String cform=token.nextToken();
              base[4]=Integer.parseInt(token.nextToken());
              tcount=token.countTokens();
              for(int i=0;i<tcount;i++)
               {temptoken=token.nextToken();
                 ind=temptoken.indexOf("-");
                  rangeArray[4].add(new Range(Integer.parseInt(temptoken.substring(0,ind),base[4]),
                                                                Integer.parseInt(temptoken.substring(ind+1,temptoken.length()),base[4])));
                }
              parameter.put(modelName,new PLCParameter(modelName,toolTip,realImageWidth,realImageHeight,realImageFile,
                   com,limit,prefix,base,rangeArray,tform,cform));
             }

           }
          catch(Exception e)
           {System.err.println("PLC.set error!: "+e.getMessage());
             return;
           }
       }
     
     }
/*
  public Image loadImage(String pa)
    {Image image=null;
      String path=pa;
      try
      {if(applet==null)
        {if(!path.substring(0,1).equals("/")) path="/"+path;
//System.err.println(path);
          image=Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
//         image=Toolkit.getDefaultToolkit().getImage(Creater.getResourceURL(path));
        }
       else
        {if(path.substring(0,1).equals("/")) path=path.substring(1,path.length());
          image=applet.getImage(applet.getCodeBase(), path);
       }
     }
    catch (Exception e)
     {System.err.println("Please verify your imageURL.:"+path);}
    return image;
  }
*/
 }     
