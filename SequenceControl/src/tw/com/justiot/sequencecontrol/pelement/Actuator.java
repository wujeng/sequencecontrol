package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.swing.JMenuItem;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.util;
import tw.com.justiot.sequencecontrol.config.ActuatorParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.dialog.DBDialog;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;


/*
commandListener.add(new setValvePosCommand(this,valvePos,n,i,oldi));
commandListener.add(new removeValveCommand(this,obj));
commandListener.add(new changeLoadCommand(this,oldload,load));
commandListener.add(new changeAreaFCommand(this,oldareaf,areaf));
commandListener.add(new changeAreaBCommand(this,oldareab,areab));
commandListener.add(new changeFTimesetCommand(this,oldftimeset,ftimeset));
commandListener.add(new changeBTimesetCommand(this,oldbtimeset,btimeset));
*/
public class Actuator extends PneumaticElement
 {public static final int Command_setValvePos=1;
  public static final int Command_removeValve=2;
  public static final int Command_changeLoad=3;
  public static final int Command_changeAreaF=4;
  public static final int Command_changeAreaB=5;
  public static final int Command_changeFTimeset=6;
  public static final int Command_changeBTimeset=7;

  private static final int TimerPeriod=100;

  public static final int MOTION_FORWARD=0;
  public static final int MOTION_BACKWARD=1;
  public static final int MOTION_NONE=2;

  public static final float DEFAULT_AREAF=100.0f;
  public static final float DEFAULT_AREAB=100.0f;
  public static final float DEFAULT_LOAD=0.0f;

  public static final float dfriction=0.00f;
  public static final float sfriction=0.00f;

  private int ftimeset=1;
  private int ftimecount=1;
  private int btimeset=1;
  private int btimecount=1;
  public int fflowvalve=100;
  public int bflowvalve=100;

  private boolean timeron;
  public int motiondir;

  protected float load,areaf,areab;
  public Port fport,bport;

  private static Image LSpressed, LSrelease,LSleft,LSlefty,LSleftn,LSright,LSrighty,LSrightn;
  public ArrayList valveArray;
  public ArrayList valvePos;
  private Position LSpos1,LSpos2;
  private boolean inLS;
  private int pressedLS;

//  private Actuator seft;
  private boolean firstPopup=true;
  public static int count=0;
  public static void clearCount() {count=0;}

//  private PneumaticPanel pneumaticPanel=null;

  public Actuator(String mname,PneumaticListener pneumaticlistener)
   {super(mname,true, pneumaticlistener);
    count++;
//    if(Config.getBoolean("debug")) System.out.println("Creater instanceElement "+mname);
    if(PneumaticConfig.parameter.containsKey(mname))
     {	
      ActuatorParameter ep=(ActuatorParameter) PneumaticConfig.parameter.get(mname);
       if(ep.fport!=null)
        {this.fport=new Port(ep.fport);
         this.fport.setDir(Port.DIR_DOWN);
         this.fport.setIsDrain(false);
         this.fport.setIsPower(false);
         this.fport.setIsStop(false);
        }
       else this.fport=null;
       if(ep.bport!=null)
        {this.bport=new Port(ep.bport);
         this.bport.setDir(Port.DIR_DOWN);
         this.bport.setIsDrain(false);
         this.bport.setIsPower(false);
         this.bport.setIsStop(false);
        }
       else this.bport=null;
      ports=new Port[]{this.fport,this.bport};
      portOrg=new Port[ports.length];
      for(int i=0;i<ports.length;i++)
       {if(ports[i]!=null)
         {ports[i].setOwner(this);
          ports[i].open();
          portOrg[i]=new Port(ports[i]);
         }
        else
         portOrg[i]=null;
       }
      timeron=false;
      motiondir=MOTION_NONE;
      load=DEFAULT_LOAD;
      areaf=DEFAULT_AREAF;
      areab=DEFAULT_AREAB;
      try
       {if(LSpressed==null)
         LSpressed=util.loadImage("Actuator","LimitSwitch","LSpressed",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSpressed.GIF");
        if(LSrelease==null)
         LSrelease=util.loadImage("Actuator","LimitSwitch","LSrelease",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSrelease.GIF");
        if(LSleft==null)
         LSleft=util.loadImage("Actuator","LimitSwitch","LSleft",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSleft.GIF");
        if(LSlefty==null)
         LSlefty=util.loadImage("Actuator","LimitSwitch","LSlefty",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSlefty.GIF");
        if(LSleftn==null)
         LSleftn=util.loadImage("Actuator","LimitSwitch","LSleftn",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSleftn.GIF");
        if(LSright==null)
         LSright=util.loadImage("Actuator","LimitSwitch","LSright",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSright.GIF");
        if(LSrighty==null)
         LSrighty=util.loadImage("Actuator","LimitSwitch","LSrighty",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSrighty.GIF");
        if(LSrightn==null)
         LSrightn=util.loadImage("Actuator","LimitSwitch","LSrightn",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSrightn.GIF");
       }
      catch (Exception e)
       {java.lang.System.err.println("Please verify LSpressed.gif or LSrelease.gif !!");}
      valveArray=new ArrayList();
      valvePos=new ArrayList();
      if(ep.LSpos1!=null)
       LSpos1=ep.LSpos1;
      else
       LSpos1=new Position();
      if(ep.LSpos2!=null)
       LSpos2=ep.LSpos2;
      else
       LSpos2=new Position(getSize().width-1,getSize().height-1);
     }
  //  setName(defaultName());
   }

  private boolean findValvePos(int n)
   {for(int i=0;i<valvePos.size();i++)
     if(((Integer) valvePos.get(i)).intValue()==n) return true;
    return false;
   }

  public boolean withLS()
     {if(LSpos1.x==0 && LSpos1.y==0 && LSpos2.x==0 && LSpos2.y==0)
        return false;
       else
        return true;
     }

  public ArrayList getValveArray()
   {return valveArray;}
  public ArrayList getValvePos()
   {return valvePos;}
  public void deleteValve(Object val)
   {int n=-1;
     for(int i=0;i<valveArray.size();i++)
      if(val==valveArray.get(i))
       {n=i;break;}
     if(n>=0)
      {valveArray.remove(n);
        valvePos.remove(n);
      }
//     paintComponent(getGraphics());
   }

  public boolean hasValve(Object val)
    {if(valveArray.contains(val)) return true;
      else return false;
    }

  public int getLSPosition(Object obj)
   {for(int i=0;i<valveArray.size();i++)
      if(obj==valveArray.get(i)) return ((Integer) valvePos.get(i)).intValue();
     return -1;
   }

  public void addValve(Object val, int n)
  {if(LSpos1.x==0 && LSpos1.y==0 && LSpos2.x==0 && LSpos2.y==0) return;
   valveArray.add(val);
   valvePos.add(new Integer(n));
  }

  public void addValve(Object val)
   {if(LSpos1.x==0 && LSpos1.y==0 && LSpos2.x==0 && LSpos2.y==0) return;
    if(valveArray.contains(val)) return;
    int max=images.length-1;
    valveArray.add(val);
    if(!findValvePos(max)) valvePos.add(new Integer(max));
    else
     {if(!findValvePos(0)) valvePos.add(new Integer(0));
      else valvePos.add(new Integer(max/2));
     }
//    paintComponent(getGraphics());
   }

 private int getLSPosX(int index)
  {int x=LSpos1.x+(LSpos2.x-LSpos1.x)*index/images.length;
    int y=LSpos1.y;
    if(angle==0.0) return x;
    if(angle==Math.PI/2.0) return getSize().width-y;
    if(angle==Math.PI) return getSize().width-x;
    if(angle==(Math.PI*3.0)/2.0) return y;
    return 0;
  }

 private int getLSPosY(int index)
  {int x=LSpos1.x+(LSpos2.x-LSpos1.x)*index/images.length;
    int y=LSpos1.y;
    if(angle==0.0) return y;
    if(angle==Math.PI/2.0) return x;
    if(angle==Math.PI) return getSize().height-y;
    if(angle==(Math.PI*3.0)/2.0) return getSize().height-x;
    return 0;
  }

 private int getX4Angle(int x,int y)
  {
    if(angle==0.0) return x;
    if(angle==Math.PI/2.0) return getSize().width-y;
    if(angle==Math.PI) return getSize().width-x;
    if(angle==(Math.PI*3.0)/2.0) return y;
    return 0;
  }

 private int getY4Angle(int x,int y)
  {
    if(angle==0.0) return y;
    if(angle==Math.PI/2.0) return x;
    if(angle==Math.PI) return getSize().height-y;
    if(angle==(Math.PI*3.0)/2.0) return getSize().height-x;
    return 0;
  }


  private String valveName=null;
  private Valve valcom=null;
  private EDevice ed=null;
  private AffineTransform translate=null;
  public void paintComponent(Graphics g)
   {
    super.paintComponent(g);
    Graphics2D g2=(Graphics2D) g;
    Font oldFont=g2.getFont();
    Font newFont=new Font(oldFont.getName(),Font.PLAIN,9);
    Color oldColor=g2.getColor();
    g2.setFont(newFont);
    g2.setColor(Color.blue);
    int index=0;int x=0,y=0,x0=0,y0=0;

    Image image=null;
    for(int i=0;i<valveArray.size();i++)
     {
      index=((Integer) valvePos.get(i)).intValue();
      x=getLSPosX(index);
      y=getLSPosY(index);
      image=null;
      Object obj=valveArray.get(i);
      if(obj instanceof Valve)
       {
	     valcom=(Valve) obj;
         try
          {if(curImage==index)
            {if(valcom.getForce(0))
              {if(valcom.oneWay)
                {if(valcom.oneWayLeft)
                   {image=LSlefty; x0=getX4Angle(0,0);y0=getY4Angle(0,0);}
                  else
                   {image=LSrighty; x0=getX4Angle(-6,0);y0=getY4Angle(-6,0);}
                }
               else
                {image=LSpressed; x0=getX4Angle(0,0);y0=getY4Angle(0,0);}
             }
            else
             {if(valcom.oneWay)
               {if(valcom.oneWayLeft)
                  {image=LSleftn;    x0=getX4Angle(0,-1);y0=getY4Angle(0,-1);}
                 else
                  {image=LSrightn;  x0=getX4Angle(-6,-1);y0=getY4Angle(-6,-1);}
               }
            }
          }
         else
           {if(valcom.oneWay)
             {if(valcom.oneWayLeft)
                {image=LSleft;    x0=getX4Angle(0,-3);y0=getY4Angle(0,-3);}
               else
                {image=LSright;  x0=getX4Angle(-6,-3);y0=getY4Angle(-6,-3);}
             }
            else
             {image=LSrelease;x0=getX4Angle(0,-3);y0=getY4Angle(0,-3);}
          }
if(image!=null)
 {translate= AffineTransform.getTranslateInstance(x+x0,y+y0);
    translate.concatenate(AffineTransform.getRotateInstance(angle));
    g2.drawImage(image,translate,this);
 }
        }
        catch(Exception all) {System.err.println(all.getMessage());}
        valveName=valcom.getName();
//System.out.println("Valve:"+valveName+" in Actuator");
        if(valveName!=null && valveName.length()>0)
         {if(index==images.length-1)
           g2.drawString(valveName,x-9,y+8);
          else
           g2.drawString(valveName,x+7,y+8);
         }
      }
    if(obj instanceof EDevice)
     {ed=(EDevice) obj;
       try
        {if(curImage==index)
           {image=LSpressed;    x0=getX4Angle(0,0);y0=getY4Angle(0,0);}
          else
           {image=LSrelease;x0=getX4Angle(0,-3);y0=getY4Angle(0,-3);}
if(image!=null)
 {translate= AffineTransform.getTranslateInstance(x+x0,y+y0);
    translate.concatenate(AffineTransform.getRotateInstance(angle));
    g2.drawImage(image,translate,this);
 }

          valveName=ed.ced.getName();
//System.out.println("EValve:"+valveName+" in Actuator");
          if(valveName!=null && valveName.length()>0)
           {
             g2.setFont(new Font(oldFont.getName(),Font.PLAIN,7));
             g2.setColor(Color.blue);
            if(index==images.length-1)
             g2.drawString(valveName,x-9,y-4); // g2.drawString(valveName,x-9,y+8);
            else
             g2.drawString(valveName,x+7,y+8);
           }
         }
        catch(Exception all) {System.err.println(all.getMessage());}
      }
     }
    g2.setFont(oldFont);
    g2.setColor(oldColor);
   }

  public float getLoad() {return load;}
  public void setLoad(float l) {load=l;}
  public float getAreaf() {return areaf;}
  public void setAreaf(float af) {areaf=af;}
  public float getAreab() {return areab;}
  public void setAreab(float ab) {areab=ab;}


  public boolean isTimeron() {return timeron;}
  public void setTimeron(boolean ton) {timeron=ton;}

  protected void nextStep()
   {if(curImage<(images.length-1))
     {curImage++;
 //     if(ports[1]!=null) ports[1].setFlowrate(areab*ports[0].getFlowrate()/areaf);
 //     ports[0].setFlowrate(Port.sourceFlowrate);
 //     if(ports[1]!=null) {
 //   	  ports[1].setPressure(Port.unknownPressure);
 //   	  ports[1].setFlowrate(Port.sourceFlowrate);
  //    }
     }
    else
     {
    	if(ports[1]!=null)
       {//ports[1].setFlowrate(0.0f);
    	ports[1].setIsStop(true);
 //       ports[1].setQCapacity(0.0f);
 //       ports[1].setPressure(Port.unknownPressure);
       }
 //     ports[0].setFlowrate(0.0f);
    	ports[0].setIsStop(true);
 //     ports[0].setQCapacity(0.0f);
  //    ports[0].setPressure(Port.unknownPressure);
      motiondir=MOTION_NONE;
     }
   }

   protected void preStep()
    {if(curImage>0)
      {curImage--;
  //     if(ports[1]!=null) ports[0].setFlowrate(areaf*ports[1].getFlowrate()/areab);
  //     setmotion();
 //      if(ports[0]!=null) {
  //  	    ports[0].setPressure(Port.unknownPressure);
   // 	    ports[0].setFlowrate(Port.sourceFlowrate);
   //    }
      }
     else
      {if(ports[1]!=null)
        {ports[1].setIsStop(true);
  //       ports[1].setQCapacity(0.0f);
 //        ports[1].setPressure(Port.unknownPressure);
        }
      ports[0].setIsStop(true);
 //      ports[0].setQCapacity(0.0f);
 //      ports[0].setPressure(Port.unknownPressure);
       motiondir=MOTION_NONE;
      }
    }

  public void startTimer(java.util.Timer timer)
   {timer.scheduleAtFixedRate(new TimeThread(), 0, TimerPeriod);
   }
/*
  public void timeStep()
  {if(!timeron) return;
   if(WebLadderCAD.opMode==WebLadderCAD.OP_CONTROL) return;
   if(motiondir==MOTION_FORWARD)
    {ftimecount--;
//System.err.println(ftimecount+":"+fflowvalve+":"+ftimeset);
     if(ftimecount>0) return;
     nextStep();
     ftimecount=(ftimeset*100)/fflowvalve;
     repaint();
    }
   if(motiondir==MOTION_BACKWARD)
    {btimecount--;
//System.err.println(btimecount+":"+bflowvalve+":"+btimeset);
     if(btimecount>0) return;
     preStep();
     btimecount=(btimeset*100)/bflowvalve;
// checkLimitSwitch();
    repaint();
   }
 }
  */
  class TimeThread extends TimerTask
   {
    public void run()
     {if(!timeron) return;
      if(pneumaticlistener.isPause()) return;
      if(motiondir==MOTION_FORWARD)
       {ftimecount--;
//System.err.println(ftimecount+":"+fflowvalve+":"+ftimeset);
        if(ftimecount>0) return;
        nextStep();
        ftimecount=(ftimeset*100)/fflowvalve;
        repaint();
       }
      if(motiondir==MOTION_BACKWARD)
       {btimecount--;
//System.err.println(btimecount+":"+bflowvalve+":"+btimeset);
        if(btimecount>0) return;
        preStep();
        btimecount=(btimeset*100)/bflowvalve;
//    checkLimitSwitch();
       repaint();
      }
    }
  }

  public Port[] nextPorts(Port pt)
   {if(ports[1]==null) return null;
    ArrayList<Port> al=new ArrayList<Port>();
    if(pt==ports[0]) al.add(ports[1]);
    if(pt==ports[1]) al.add(ports[0]);
    return al.toArray(new Port[0]);
   }
   public Port[] nextDrainPorts(Port pt) {return null;}
   public Port[] nextStopPorts(Port pt) {return null;}

  public void setPosAccording2LS()
   { EDevice ed=null;
     Object obj=null;
     for(int i=0;i<valvePos.size();i++)
     {obj=valveArray.get(i);
       if(obj instanceof Valve) continue;
       
       if(obj instanceof EDevice)
        {//if(pneumaticlistener.getOpMode()==WebLadderCAD.OP_CONTROL)
           {ed=(EDevice) obj;
            if(ed.ced.status)
             {curImage=((Integer) valvePos.get(i)).intValue();
              repaint();
              return;
             }
           }
        }
        
     }
    curImage=images.length/2;
//    repaint();
   }

  private void checkLimitSwitch()  // check if LS is activated by this Actuator
   { Valve val=null;
     EDevice ed=null;
     Object obj=null;
     for(int i=0;i<valvePos.size();i++)
     {obj=valveArray.get(i);
       if(obj instanceof Valve)
        {val=(Valve) obj;
          if(((Integer) valvePos.get(i)).intValue()==curImage)
           {if(!val.oneWay) val.setForce(0,true);
             else
              {if(motiondir==MOTION_NONE && ((curImage==(images.length-1) && val.oneWayLeft) ||
                     (curImage==0 && !val.oneWayLeft)))
                   val.setForce(0,true);
                else if((val.oneWayLeft && motiondir==MOTION_FORWARD) ||
                                 (!val.oneWayLeft && motiondir==MOTION_BACKWARD))
                   val.setForce(0,true);
                else
                   val.setForce(0,false);
              }
           }
          else
           val.setForce(0,false);
        }
       
       if(obj instanceof EDevice)
        {//if(pneumaticlistener.getOpMode()==WebLadderCAD.OP_SIMULATION)
           {ed=(EDevice) obj;
             if(((Integer) valvePos.get(i)).intValue()==curImage)
              ed.ced.setStatus(true);
             else
              ed.ced.setStatus(false);
           }
        }
        
     }
   }

   public void check()
    {
 	checkLimitSwitch();
 	Port aport=null;
     if(ports[1]!=null)
      {float f0=ports[0].getPressure();
       float f1=ports[1].getPressure();
       if(f0<=0.0f && f1<=0.0f) {motiondir=MOTION_NONE; setTimeron(false); return;}
       if(ports[0].getIsStop() || ports[1].getIsStop()) {motiondir=MOTION_NONE; setTimeron(false); return;}
       if(ports[0].getIsDrain() && ports[1].getIsDrain()) {motiondir=MOTION_NONE;setTimeron(false); return;}
   //    if(ports[0].getFlowrate()==0.0f && ports[1].getFlowrate()==0.0f) motiondir=MOTION_NONE;
       if(ports[0].getPressure()==ports[1].getPressure()) {
     	  motiondir=MOTION_NONE;
     	  ports[0].setIsStop(true);
     	  ports[1].setIsStop(true);
     	  setTimeron(false);
       }
       if(motiondir==MOTION_FORWARD && f0<=0.0f) {motiondir=MOTION_NONE; setTimeron(false); return;}
       if(motiondir==MOTION_BACKWARD && f1<=0.0f) {motiondir=MOTION_NONE; setTimeron(false); return;}
       if(f0>0.0f && f0>f1+load+sfriction && curImage<(images.length-1)) {
     	 motiondir=MOTION_FORWARD;
    // 	 ports[1].setFlowrate(areab*ports[0].getFlowrate()/areaf);
    //      ports[1].setQCapacity(areab*ports[0].getQCapacity()/areaf);
     	 ports[1].setFlowrate(ports[0].getFlowrate());
     	 setTimeron(true);
       }
       if(f1>0.0f && f0+sfriction+load<f1 && curImage>0) {
     	 motiondir=MOTION_BACKWARD;
     	 ports[0].setFlowrate(ports[1].getFlowrate());
     	 setTimeron(true);
       }
      }
     else
      {
       float f0=ports[0].getPressure();
       if(ports[0].getIsStop()) {motiondir=MOTION_NONE;setTimeron(false); return;}

       if(ports[0].getPressure()>load+sfriction && curImage<(images.length-1)) {
     	 motiondir=MOTION_FORWARD;
   //  	 ports[0].setFlowrate(Port.sourceFlowrate);
     	 setTimeron(true);
       }
       if(ports[0].getPressure()<=0.0f && curImage>0) {
     	  motiondir=MOTION_BACKWARD;
   //  	  ports[0].setFlowrate(Port.sourceFlowrate);
     	  setTimeron(true);
       }

      }

   }

  public void mousePressed(MouseEvent e) {
	  if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {
        pressedx=ex;
        pressedy=ey;
        pressedPosx=getLocation().x;
        pressedPosy=getLocation().y;
        if(pressedx<LSpos2.x+7 && pressedx>LSpos1.x-7 &&
           pressedy<LSpos2.y && pressedy>LSpos1.y)
         {inLS=true;
          pressedLS=-1;
          int pk=(pressedx-LSpos1.x)*images.length/(LSpos2.x-LSpos1.x);
          int pkleft=(pressedx-LSpos1.x-5)*images.length/(LSpos2.x-LSpos1.x);
          int pkright=(pressedx-LSpos1.x+5)*images.length/(LSpos2.x-LSpos1.x);
          int lsp=0;
          Valve val=null;
          EDevice ed=null;
          Object obj=null;
          for(int i=0;i<valveArray.size();i++)
           {obj=valveArray.get(i);
             if(obj instanceof Valve)
              {val=(Valve) obj;
                lsp=((Integer) valvePos.get(i)).intValue();
                if(!val.oneWay)
                 {if(lsp==pk)
                    {pressedLS=i;break;}
                 }
                else
                 {if(val.oneWayLeft)
                    {if(lsp==pkleft)
                      {pressedLS=i;break;}
                    }
                   else
                    {if(lsp==pkright)
                      {pressedLS=i;break;}
                    }
                 }
               }
              if(obj instanceof EDevice)
               {ed=(EDevice) obj;
                lsp=((Integer) valvePos.get(i)).intValue();
                if(lsp==pk)
                 {pressedLS=i;break;}
               }
           }
//System.err.println(pressedLS);
         }
       }
      else
       maybeShowPopup(pop,ex,ey);
   }

  private class setValvePosCommand extends Command
    {ArrayList vpos;
     int index;
     Object newint;
     Object oldint;
	 public setValvePosCommand(Object ele, ArrayList vpos,int index, Object ni, Object oi)
     {super("Actuator",ele,Actuator.Command_setValvePos);
      this.vpos=vpos;
      this.index=index;
      newint=ni;
      oldint=oi;
     }
    public void undo()
     {vpos.set(index,oldint);
     }
    public void redo()
     {vpos.set(index,newint);
     }
   }
  public void setValvePos(int n,Integer i)
   {Object oldi=valvePos.get(n);
//    if(obj instanceof Integer) return;
	valvePos.set(n,i);
	pneumaticlistener.addCommand(new setValvePosCommand(this,valvePos,n,i,oldi));
   }


  private class removeValveCommand extends Command
    {int index;
     Object obj;
	 public removeValveCommand(Object ele,Object obj)
     {super("Actuator",ele,Command_removeValve);
      this.obj=obj;
     }
    public void undo()
     {
      valveArray.add(obj);
      valvePos.add(obj);
     }
    public void redo()
     {Valve val=null;
      EDevice ed=null;
      Object o;
      for(int i=0;i<valveArray.size();i++)
       {o=valveArray.get(i);
        if(o==obj)
         {if(o instanceof Valve)
           {val=(Valve) obj;
            val.setForce(0,false);
           }
          if(o instanceof EDevice)
           {ed=(EDevice) obj;
            ed.ced.setStatus(false);
           }

          valveArray.remove(i);
          valvePos.remove(i);
          break;
         }
       }
     }
   }

  public void removeValve(int i)
   {Valve val=null;
    EDevice ed=null;

    Object obj=valveArray.get(i);
    if(obj instanceof Valve)
     {val=(Valve) obj;
      val.setForce(0,false);
     }
    if(obj instanceof EDevice)
     {ed=(EDevice) obj;
      ed.ced.setStatus(false);
     }

    valveArray.remove(i);
    valvePos.remove(i);
    pneumaticlistener.addCommand(new removeValveCommand(this,obj));
   }

  public void mouseReleased(MouseEvent e) {
	  if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left && drag)
       {if(inLS)
         {if(pressedLS >=0)
           {
            int xx=ex,yy=ey;
            if(xx<LSpos2.x && xx>LSpos1.x && yy<LSpos2.y && yy>LSpos1.y)
             {
              int pk=(xx-LSpos1.x)*(images.length-1)/(LSpos2.x-LSpos1.x);
              int pkleft=(xx-LSpos1.x-5)*(images.length-1)/(LSpos2.x-LSpos1.x);
              int pkright=(xx-LSpos1.x+5)*(images.length-1)/(LSpos2.x-LSpos1.x);
              Valve val=null;
              EDevice ed=null;
              Object obj=valveArray.get(pressedLS);
              if(obj instanceof Valve)
               {val=(Valve) obj;
                if(!val.oneWay) setValvePos(pressedLS,new Integer(pk));
                else
                 {if(val.oneWayLeft) setValvePos(pressedLS,new Integer(pkleft));
                  else setValvePos(pressedLS,new Integer(pkright));
                 }
               }
              if(obj instanceof EDevice) setValvePos(pressedLS,new Integer(pk));
//              paintComponent(getGraphics());
             }
            else
             {if(xx > getSize().width || yy > getSize().height)
               {removeValve(pressedLS);
//                paintComponent(getGraphics());
               }
              else
               {if(xx<LSpos1.x)
                 {setValvePos(pressedLS,new Integer(0));
//                  paintComponent(getGraphics());
                 }
                else if(xx>LSpos2.x)
                 {setValvePos(pressedLS,new Integer(images.length-1));
//                  paintComponent(getGraphics());
                 }
               }
             }
           }

          drag=false;
          inLS=false;
          pressedLS=-1;
          return;
         }
       }
      super.MouseReleased(e,left,ex,ey,pop);
   }
/*
  public void maybeShowPopup(boolean pop, int ex, int ey) {
      if(pop) {
        if(firstPopup)
         {m = new JMenuItem(Config.getString("Actuator.LS1"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          m = new JMenuItem(Config.getString("Actuator.LS2"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          popup.addSeparator();
        
      //    m = new JMenuItem(Config.getString("Actuator.load"));
      //    m.addActionListener(this);
      //    m.addMouseListener(new menuItemMouseAdapter());
      //    popup.add(m);
      //    m = new JMenuItem(Config.getString("Actuator.A1"));
      //    m.addActionListener(this);
      //    m.addMouseListener(new menuItemMouseAdapter());
      //    popup.add(m);
      //    m = new JMenuItem(Config.getString("Actuator.A2"));
      //    m.addActionListener(this);
      //    m.addMouseListener(new menuItemMouseAdapter());
      //    popup.add(m);
      //    popup.addSeparator();
        

          m = new JMenuItem(Config.getString("Actuator.forwardtimestep"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          m = new JMenuItem(Config.getString("Actuator.backwardtimestep"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          popup.addSeparator();
          m = new JMenuItem(Config.getString("Actuator.reset"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          firstPopup=false;
         }
//        popup.show(e.getComponent(), e.getX(), e.getY());
       popup.show(this, ex, ey);
      }
    }
*/
  private class changeLoadCommand extends Command
    {float oldload;
     float newload;
	public changeLoadCommand(Object ele,float ol,float nl)
     {super("Actuator",ele,Command_changeLoad);
      oldload=ol;
      newload=nl;
     }
    public void undo()
     {load=oldload;
     }
    public void redo()
     {load=newload;
     }
   }
  private class changeAreaFCommand extends Command
    {float oldareaf;
     float newareaf;
	public changeAreaFCommand(Object ele,float oa,float na)
     {super("Actuator",ele,Command_changeAreaF);
      oldareaf=oa;
      newareaf=na;
     }
    public void undo()
     {areaf=oldareaf;
     }
    public void redo()
     {areaf=newareaf;
     }
   }
  private class changeAreaBCommand extends Command
    {float oldareab;
     float newareab;
	public changeAreaBCommand(Object ele,float oa,float na)
     {super("Actuator",ele,Command_changeAreaB);
      oldareab=oa;
      newareab=na;
     }
    public void undo()
     {areab=oldareab;
     }
    public void redo()
     {areab=newareab;
     }
   }
  private class changeFTimesetCommand extends Command
    {int oldtimeset;
     int newtimeset;
	public changeFTimesetCommand(Object ele,int ot,int nt)
     {super("Actuator",ele,Command_changeFTimeset);
      oldtimeset=ot;
      newtimeset=nt;
     }
    public void undo()
     {ftimeset=oldtimeset;
     }
    public void redo()
     {ftimeset=newtimeset;
     }
   }
  private class changeBTimesetCommand extends Command
    {int oldtimeset;
     int newtimeset;
	public changeBTimesetCommand(Object ele,int ot,int nt)
     {super("Actuator",ele,Command_changeBTimeset);
      oldtimeset=ot;
      newtimeset=nt;
     }
    public void undo()
     {btimeset=oldtimeset;
     }
    public void redo()
     {btimeset=newtimeset;
     }
   }
   public void ActionPerformed(JMenuItem mi,String op,String input)
     { super.ActionPerformed(mi,op,input);
       String option=mi.getText();
       if(option.equals(Config.getString("Actuator.load")))
        { float oldload=load;
          customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("Actuator.load"),Config.getString("Actuator.modifyload"),CustomDialog.VALUE_FLOAT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(
 		screenSize.width/2 - customDialog.getSize().width/2,
 		screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Float.toString(load));
          customDialog.setVisible(true);
          load=customDialog.getFloat();
          pneumaticlistener.addCommand(new changeLoadCommand(this,oldload,load));

       }
       else if(option.equals(Config.getString("Actuator.LS1")))
       {
        ArrayList al=pneumaticPanel.getLimitSwitchArray();
        if(al.size()<=0) {
     	   pneumaticlistener.MessageBox(Config.getString("Actuator.noLS"), "Error");
     	   return;
        }
        DBDialog dbDialog = new DBDialog(pneumaticlistener.getFrame(),al,Config.getString("Actuator.SelectLS1"),false);
        dbDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
        dbDialog.setVisible(true);
        Valve ed=(Valve) dbDialog.getObject();
        addValve(ed,images.length-1);
        pneumaticPanel.repaint();

       }
       else if(option.equals(Config.getString("Actuator.LS2")))
       {ArrayList al=pneumaticPanel.getLimitSwitchArray();
        if(al.size()<=0) {
    	     pneumaticlistener.MessageBox(Config.getString("Actuator.noLS"), "Error");
    	     return;
        }
        DBDialog dbDialog = new DBDialog(pneumaticlistener.getFrame(),al,Config.getString("Actuator.SelectLS2"),false);
        dbDialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
        dbDialog.setVisible(true);
        Valve ed=(Valve) dbDialog.getObject();
        addValve(ed,0);
        pneumaticPanel.repaint();

      }
      else if(option.equals(Config.getString("Actuator.A1")))
       {  float oldareaf=areaf;
          customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("Actuator.A1"),Config.getString("Actuator.modifyA1"),CustomDialog.VALUE_FLOAT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(
 		screenSize.width/2 - customDialog.getSize().width/2,
 		screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Float.toString(areaf));
          customDialog.setVisible(true);
          areaf=customDialog.getFloat();
          pneumaticlistener.addCommand(new changeAreaFCommand(this,oldareaf,areaf));

      }
     else if(option.equals(Config.getString("Actuator.A2")))
      {   float oldareab=areab;
          customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("Actuator.A2"),Config.getString("Actuator.modifyA2"),CustomDialog.VALUE_FLOAT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(
 		screenSize.width/2 - customDialog.getSize().width/2,
 		screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Float.toString(areab));
          customDialog.setVisible(true);
          areab=customDialog.getFloat();
          pneumaticlistener.addCommand(new changeAreaBCommand(this,oldareab,areab));

      }
     else if(option.equals(Config.getString("Actuator.forwardtimestep")))
      {   int oldftimeset=ftimeset;
 	     customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("Actuator.forwardtimestep"),Config.getString("Actuator.modifyforwardtimestep"),CustomDialog.VALUE_INT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(
 		screenSize.width/2 - customDialog.getSize().width/2,
 		screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Integer.toString(ftimeset));
          customDialog.setVisible(true);
          ftimeset=customDialog.getInt();
          pneumaticlistener.addCommand(new changeFTimesetCommand(this,oldftimeset,ftimeset));

      }
     else if(option.equals(Config.getString("Actuator.backwardtimestep")))
      {   int oldbtimeset=btimeset;

 	     customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("Actuator.backwardtimestep"),Config.getString("Actuator.modifybackwardtimestep"),CustomDialog.VALUE_INT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(
 		screenSize.width/2 - customDialog.getSize().width/2,
 		screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Integer.toString(btimeset));
          customDialog.setVisible(true);
          btimeset=customDialog.getInt();
          pneumaticlistener.addCommand(new changeBTimesetCommand(this,oldbtimeset,btimeset));
      }
     else if(option.equals(Config.getString("Actuator.reset")))
      {curImage=0;
       motiondir=MOTION_NONE;

 //      paintComponent(getGraphics());
      }
   }


public void reset()
 {setTimeron(false);
  curImage=0;
  motiondir=MOTION_NONE;
//  paintComponent(getGraphics());
 }

class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  public void addValvePos(int x)
    {valvePos.add(new Integer(x));}
  public int getValvePos(int j)
    {return ((Integer) valvePos.get(j)).intValue();}

  public String extraWrite()
   {return Integer.toString(ftimeset)+" "+Integer.toString(btimeset);}

  public void extraRead(String str)
   {if(str==null) return;
//System.err.println("actuator extra "+str);
     StringTokenizer token=new StringTokenizer(str);
     if(token.hasMoreTokens())
       {ftimeset=Integer.parseInt(token.nextToken());
       }
     if(token.hasMoreTokens())
       {btimeset=Integer.parseInt(token.nextToken());
       }
   }
 }
