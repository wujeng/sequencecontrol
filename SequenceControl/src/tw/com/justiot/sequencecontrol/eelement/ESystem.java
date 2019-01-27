package tw.com.justiot.sequencecontrol.eelement;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.TRect;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class ESystem extends ESystemBase
 {public static int ConveyerCount=0;
  public static int count=0;
  public static final int MOTION_FORWARD=0;
  public static final int MOTION_BACKWARD=1;
  public static final int MOTION_NONE=2;
  public int motiondir;
  protected ESystem seft;

  private Position lspos1, lspos2;
  private EDevice ls1,ls2;
   public void check() {}
//   protected electriclistener electriclistener;
   private int width0,height0;
   public ESystem(String mname, ElectricListener electriclistener)
   {super(electriclistener);
 //  this.electriclistener=electriclistener;
    if(PneumaticConfig.parameter.containsKey(mname))
     {SystemParameter ep=(SystemParameter) PneumaticConfig.parameter.get(mname);
       EFsol1=ep.EFsol1;
       EFsol1on=ep.EFsol1on;
       EFsol2=ep.EFsol2;
       EFsol2on=ep.EFsol2on;
       actionRect1=ep.actionRect1;
       actionRect2=ep.actionRect2;
       inputRect1=ep.inputRect1;
       inputRect2=ep.inputRect2;
       audioClip=ep.audioClip;
       realImage=ep.realImage;
       realImageDim=ep.realImageDim;
       images=ep.images;
       imageDim=ep.imageDim;
       setSize(imageDim.width,imageDim.height);
       width0=imageDim.width;
       height0=imageDim.height;
       this.lspos1=ep.lspos1;
       this.lspos2=ep.lspos2;
       if(lspos1!=null) {
      	 ls1=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
      	 electriclistener.getEArrays().addEDevice(ls1);
       }
  	   if(lspos2!=null) {
  		   ls2=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
  		   electriclistener.getEArrays().addEDevice(ls2);
  	   }
  	   if(lspos1!=null || lspos2!=null) {
  	     if(LSpressed==null)
          LSpressed=util.loadImage("Actuator","LimitSwitch","LSpressed",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSpressed.GIF");
         if(LSrelease==null)
          LSrelease=util.loadImage("Actuator","LimitSwitch","LSrelease",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSrelease.GIF");
  	   }
      timeron=false;
      motiondir=MOTION_NONE;
      seft=this;

     int systemType=0;
      if(images.length==2) systemType=CDOutput.TYPE_ONOFF;
      else if(EFsol2==null)
       {if(!withLS()) systemType=CDOutput.TYPE_LOOP;
         else systemType=CDOutput.TYPE_ONEWAY;
       }
      else systemType=CDOutput.TYPE_TWOWAY;
      boolean tway=false;
      if(EFsol2!=null) tway=true;

      cdo=new CDOutput("ESystem",mname,systemType,tway);
      cdo.addListener(this);
      esymbol=new ESymbol(cdo, electriclistener);
      cdo.modelType=ep.modelType;
      cdo.modelName=ep.modelName;
//      setName(defaultName());
      cdo.name=defaultName();

      if(cdo.modelName.equals("Conveyer")) ConveyerCount++;
      else count++;
     }
   }

   public ESystem(CDOutput cdo, ElectricListener electriclistener)
   {super(electriclistener);
    if(PneumaticConfig.parameter.containsKey(cdo.modelName))
     {SystemParameter ep=(SystemParameter) PneumaticConfig.parameter.get(cdo.modelName);
     EFsol1=ep.EFsol1;
     EFsol1on=ep.EFsol1on;
     EFsol2=ep.EFsol2;
     EFsol2on=ep.EFsol2on;
     actionRect1=ep.actionRect1;
     actionRect2=ep.actionRect2;
     inputRect1=ep.inputRect1;
     inputRect2=ep.inputRect2;
     audioClip=ep.audioClip;
     realImage=ep.realImage;
     realImageDim=ep.realImageDim;
     images=ep.images;
     imageDim=ep.imageDim;
     setSize(imageDim.width,imageDim.height);
     width0=imageDim.width;
     height0=imageDim.height;
     
     this.lspos1=ep.lspos1;
     this.lspos2=ep.lspos2;
     if(lspos1!=null) {
    	 ls1=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
    	 electriclistener.getEArrays().addEDevice(ls1);
     }
	   if(lspos2!=null) {
		   ls2=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
		   electriclistener.getEArrays().addEDevice(ls2);
	   }
	   if(lspos1!=null || lspos2!=null) {
	     if(LSpressed==null)
        LSpressed=util.loadImage("Actuator","LimitSwitch","LSpressed",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSpressed.GIF");
       if(LSrelease==null)
        LSrelease=util.loadImage("Actuator","LimitSwitch","LSrelease",File.separator+"resources"+File.separator+"images"+File.separator+"SymSys"+File.separator+"LSrelease.GIF");
	   }
     
      timeron=false;
      motiondir=MOTION_NONE;
      seft=this;

     int systemType=0;
      if(images.length==2) systemType=CDOutput.TYPE_ONOFF;
      else if(EFsol2==null)
       {if(!withLS()) systemType=CDOutput.TYPE_LOOP;
         else systemType=CDOutput.TYPE_ONEWAY;
       }
      else systemType=CDOutput.TYPE_TWOWAY;
      boolean tway=false;
      if(EFsol2!=null) tway=true;

      this.cdo=cdo;
      this.cdo.addListener(this);
      esymbol=new ESymbol(this.cdo, electriclistener);
      cdo.modelType=ep.modelType;
      cdo.modelName=ep.modelName;
 //     setName(defaultName());
      cdo.name=defaultName();

      if(cdo.modelName.equals("Conveyer")) ConveyerCount++;
      else count++;
     }
   }


   public void solFStatusChanged(CDOutput cdo)
    {boolean b=cdo.getSolFStatus();
     switch(cdo.systemType)
      {case CDOutput.TYPE_ONOFF:
//System.err.println(b);
         if(b)
          {curImage=1;
            if(audioClip!=null) {
            	System.out.println("play audioClip :"+ cdo.name);
            	audioClip.play();
  //          	audioClip.loop();
            }
          }
         else
          {curImage=0;
            if(audioClip!=null) audioClip.stop();
          }
         repaint();
         break;
      case CDOutput.TYPE_LOOP:
         if(b)
          {setTimeron(true);
           if(audioClip!=null) audioClip.play();
          }
         else
          {setTimeron(false);
            if(audioClip!=null) audioClip.stop();
          }
         break;
      case CDOutput.TYPE_TWOWAY:
      case CDOutput.TYPE_ONEWAY:
         if(b)
          {motiondir=MOTION_FORWARD;
            setTimeron(true);
            cdo.setSolBStatus(false);
            if(audioClip!=null) audioClip.play();
          }
         else
          {motiondir=MOTION_NONE;
            setTimeron(false);
            if(audioClip!=null) audioClip.stop();
          }
         break;
     }
 //   electriclistener.repaint();
   }

   public void solBStatusChanged(CDOutput cdo)
    {if(EFsol2==null) return;
     boolean b=cdo.getSolBStatus();
     switch(cdo.systemType)
      {case CDOutput.TYPE_ONOFF:
         break;
       case CDOutput.TYPE_LOOP:
         break;
       case CDOutput.TYPE_TWOWAY:
         if(b)
          {motiondir=MOTION_BACKWARD;
           setTimeron(true);
           cdo.setSolFStatus(false);
           if(audioClip!=null) audioClip.play();
          }
         else
          {motiondir=MOTION_NONE;
           setTimeron(false);
           if(audioClip!=null) audioClip.stop();
          }
         break;
      }
  //   electriclistener.repaint();
    }

  public void nameChanged(CDOutput cdo)
   {//name=cdo.getName();
    //if(WebLadderCAD.electrics!=null) WebLadderCAD.electrics.repaint();
   }

  private static Image LSpressed, LSrelease; //,LSleft,LSlefty,LSleftn,LSright,LSrighty,LSrightn;
//  public ArrayList valveArray;
//  public ArrayList valvePos;
  private Position LSpos1,LSpos2;
//  private boolean inLS;
 // private int pressedLS;

  private boolean firstPopup=true;

  private int FSystemWidth,FSystemHeight,Tline,Width,Height,imagewid,imagehgt;
  TRect SRect,DRect;
  private void ReScale()
{ FSystemWidth=imageDim.width+8;
  FSystemHeight=imageDim.height+8+12;
  Tline=FSystemHeight;
//  FSystemHeight=FSystemHeight+22;
  Width=(int)(((double)FSystemWidth)*ratio);
  Height=(int)(((double)FSystemHeight)*ratio);
  imagewid=(int)(imageDim.width*ratio);
  imagehgt=(int)(imageDim.height*ratio);
  SRect=new TRect(0,0,imageDim.width-1,imageDim.height-1);
  DRect=new TRect((int)(4*ratio),(int)(4*ratio),
         (int)(4*ratio)+(int)(imageDim.width*ratio)-1,(int)(4*ratio)+(int)(imageDim.height*ratio)-1);
//  setPreferredSize(new Dimension(Width,Height));
  setSize(Width,Height);
}

  private void DrawFrame(Graphics g)
{g.setColor(Color.white);
//	  g.setColor(Color.red);
  g.drawLine(0,Height-2,0,0);
  g.drawLine(0,0,Width-2,0);
  g.drawLine(Width-2,0,Width-2,Height-2);
  g.drawLine(Width-2,Height-2,0,Height-2);
/*
  g.drawLine(Width-8,Height-2,Width-8,Height-8);
  g.drawLine(Width-8,Height-8,Width-2,Height-8);
*/
  g.setColor(Color.gray);
//  g.setColor(Color.red);
  g.drawLine(1,Height-3,1,1);
  g.drawLine(1,1,Width-3,1);
  g.drawLine(Width-1,1,Width-1,Height-1);
  g.drawLine(Width-1,Height-1,1,Height-1);

  g.drawLine(Width-9,Height-3,Width-9,Height-9);
  g.drawLine(Width-9,Height-9,Width-3,Height-9);
/*
  g.setColor(Color.black);
  g.drawLine(Width-7,Height-7,Width-3,Height-3);
  g.drawLine(Width-7,Height-3,Width-3,Height-7);
*/
}
public void paintComponent(Graphics g)
{ super.paintComponent(g);
   ReScale();

   g.drawImage(images[curImage],DRect.Left,DRect.Top,imagewid,imagehgt,this);
   if(cdo.name!=null)
    {g.setFont(new Font("Small Fonts",Font.PLAIN,(int)(10*ratio)));
      g.setColor(Color.blue);
      g.drawString(cdo.name,6,(int)((imageDim.height+16)*ratio));
    }
   if(ls2!=null) {
		 if(curImage==0)
	       g.drawImage(LSpressed,lspos2.x,lspos2.y,this);
	     else
	       g.drawImage(LSrelease,lspos2.x,lspos2.y,this);
	   }
	   
	   if(ls1!=null) {
			 if(curImage==images.length-1)
		       g.drawImage(LSpressed,lspos1.x,lspos1.y,this);
		     else
		       g.drawImage(LSrelease,lspos1.x,lspos1.y,this);
		   }
/*
   if(cdo.FPLCAddress!=null)
    {g.setFont(new Font("Small Fonts",Font.PLAIN,(int)(8*ratio)));
      g.setColor(Color.green);
      g.drawString(cdo.FPLCAddress,2,Height-(int)(2*ratio));
    }
   if(cdo.BPLCAddress!=null)
    {g.setFont(new Font("Small Fonts",Font.PLAIN,(int)(8*ratio)));
      g.setColor(Color.green);
      g.drawString(cdo.BPLCAddress,Width-(int)(14*ratio),Height-(int)(2*ratio));
    }
*/
   
/*
  int index=0;int x=0,y=0;
   String valveName=null;
   EDevice ed=null;
    for(int i=0;i<valveArray.size();i++)
     {
      index=((Integer) valvePos.get(i)).intValue();
      x=DRect.Left+LSpos1.x+(LSpos2.x-LSpos1.x)*index/images.length;
      y=DRect.Top+LSpos1.y;
//System.err.println(LSpos1.x+":"+LSpos1.y+" "+LSpos2.x+":"+LSpos2.y);
      Object obj=valveArray.get(i);
      
    if(obj instanceof EDevice)
     {ed=(EDevice) obj;
       try
        {if(curImage==index)
           g.drawImage(LSpressed,x,y,this);
          else
           g.drawImage(LSrelease,x,y-3,this);
          valveName=ed.getName();
          if(valveName!=null && valveName.length()>0)
           {g.setColor(Color.blue);
             g.setFont(new Font("Small Fonts",Font.PLAIN,(int)(6*ratio)));
             if(index==images.length-1)
             g.drawString(valveName,x-9,y+8);
            else
             g.drawString(valveName,x+7,y+8);
           }
         }
        catch(Exception all) {System.err.println(all.getMessage());}
      }
     }
*/
}

/*
  public void DrawSolCell(Graphics g)
   {WebLadderCAD.electrics.electricPanel.PaintCell(g,cdo.SP1.y,cdo.SP1.x);
    WebLadderCAD.electrics.electricPanel.PaintCell(g,cdo.SP2.y,cdo.SP2.x);
   }
*/

/*
public int getLSPosition(Object obj)
   {for(int i=0;i<valveArray.size();i++)
      if(obj==valveArray.get(i)) return ((Integer) valvePos.get(i)).intValue();
     return -1;
   }

private boolean findValvePos(int n)
   {for(int i=0;i<valvePos.size();i++)
     if(((Integer) valvePos.get(i)).intValue()==n) return true;
    return false;
   }


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
*/
  public boolean isTimeron() {return timeron;}
  public void setTimeron(boolean ton) {timeron=ton;}

  public void startTimer(java.util.Timer timer)
   {if(images.length>2)
	 timer.scheduleAtFixedRate(new TimeThread(), 0, TimerPeriod);
   }

  public void timeStep()
  {if(!timeron) return;
   if(electriclistener.getOpMode()==SCCAD.OP_CONTROL) return;
	  switch(cdo.systemType)
    {case CDOutput.TYPE_ONOFF:
        break;
     case CDOutput.TYPE_LOOP:
        if(curImage < images.length) curImage++;
        if(curImage==images.length) curImage=0;
//System.err.println("curImage:"+curImage);
//        repaint();
        break;
     case CDOutput.TYPE_TWOWAY:
        if(motiondir==MOTION_FORWARD) nextStep();
        if(motiondir==MOTION_BACKWARD) preStep();
        break;
     case CDOutput.TYPE_ONEWAY:
        if(motiondir==MOTION_FORWARD) nextStep();
        break;
   }
// checkLimitSwitch();
//  repaint();
 }

  class TimeThread extends TimerTask
   {
    public void run()
     {if(!timeron) return;
      if(electriclistener.getOpMode()==SCCAD.OP_CONTROL) return;
      if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION && electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) return;
	  switch(cdo.systemType)
       {case CDOutput.TYPE_ONOFF:
           break;
        case CDOutput.TYPE_LOOP:
           if(curImage < images.length) {curImage++; setLimitSwitch();}
           if(curImage==images.length) {curImage=0;setLimitSwitch();}
//System.err.println("curImage:"+curImage);
//           repaint();
           break;
        case CDOutput.TYPE_TWOWAY:
           if(motiondir==MOTION_FORWARD) nextStep();
           if(motiondir==MOTION_BACKWARD) preStep();
           break;
        case CDOutput.TYPE_ONEWAY:
           if(motiondir==MOTION_FORWARD) nextStep();
           break;
      }
//    checkLimitSwitch();
     repaint();
    }
  }

  protected void setLimitSwitch() {
	if(ls1!=null) {
	  if(curImage==images.length-1) ls1.ced.setStatus(true);
	  else ls1.ced.setStatus(false);
	}
	if(ls2!=null) {
	  if(curImage==0) ls2.ced.setStatus(true);
	  else ls2.ced.setStatus(false);
    }
  }

  protected void nextStep()
   {switch(cdo.systemType)
      {
        case CDOutput.TYPE_TWOWAY:
           if(curImage<(images.length-1)) {curImage++;setLimitSwitch();}
           else
            {setTimeron(false);
              motiondir=MOTION_NONE;
              if(audioClip!=null) audioClip.stop();
            }
           break;
        case CDOutput.TYPE_ONEWAY:
           if(curImage<(images.length-1)) {curImage++;setLimitSwitch();}
           else
            {setTimeron(false);
              motiondir=MOTION_NONE;
              if(audioClip!=null) audioClip.stop();
              curImage=0;setLimitSwitch();
            }
           break;
     }
   }

  protected void preStep()
   {if(curImage>0) {curImage--;setLimitSwitch();}
    else
     {setTimeron(false);
      motiondir=MOTION_NONE;
      if(audioClip!=null) audioClip.stop();
     }
   }
/*
  public void onTime(java.awt.event.ActionEvent evt)
   {
//System.err.println("onTime");
     switch(cdo.systemType)
      {case CDOutput.TYPE_ONOFF:
           break;
        case CDOutput.TYPE_LOOP:
           if(curImage < images.length) curImage++;
           if(curImage==images.length) curImage=0;
//System.err.println("curImage:"+curImage);
//           repaint();
           break;
        case CDOutput.TYPE_TWOWAY:
           if(motiondir==MOTION_FORWARD) nextStep();
           if(motiondir==MOTION_BACKWARD) preStep();
           break;
        case CDOutput.TYPE_ONEWAY:
           if(motiondir==MOTION_FORWARD) nextStep();
           break;
     }
//    checkLimitSwitch();
    repaint();
   }
*/
/*
  public Port[] nextPorts(Port pt) {return null;}
  public Port[] nextDrainPorts(Port pt) {return null;}
  public Port[] nextPowerPorts(Port pt) {return null;}
  public Port[] nextStopPorts(Port pt) {return null;}

  private void checkLimitSwitch()
   {
   }

  public void check() {checkLimitSwitch();}
*/
  public void mousePressed(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      super.MousePressed(e,left,ex,ey,pop);
      if(left)
       {//savedCursor = pneumaticPanel.getCursor( );
//        pneumaticPanel.setCursor(Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        pressedx=ex;
        pressedy=ey;
        pressedPosx=getLocation().x;
        pressedPosy=getLocation().y;
        switch(electriclistener.getOpMode())
          {case SCCAD.OP_SIMULATION:
              if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
               cdo.setSolFStatus(true);
              if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
               cdo.setSolBStatus(true);
              break;
/*
            case WebLadderCAD.OP_EDIT:
              if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
               {esystemBaseListener.AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,this.cdo,true);
               }
              if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
               {esystemBaseListener.AddCell(LadderCell.T_System,LadderCell.G_SSOL2,null,this.cdo,true);
               }
              break;
            case WebLadderCAD.OP_INPUT:
              if(inputRect1!=null && ex>inputRect1.left && ex<inputRect1.right && ey>inputRect1.top && ey<inputRect1.bottom)
               {if(esystemBaseListener!=null) esystemBaseListener.AddCell(SequenceCell.IT_System,this,getLastLimitswitch(),SequenceCell.ID_Forward,true);
               }
              if(inputRect2!=null && ex>inputRect2.left && ex<inputRect2.right && ey>inputRect2.top && ey<inputRect2.bottom)
               {if(esystemBaseListener!=null) esystemBaseListener.AddCell(SequenceCell.IT_System,this,getFirstLimitswitch(),SequenceCell.ID_Backward,true);
               }
               break;
*/
          }
/*
        if(!withLS()) return;
        if(pressedx<LSpos2.x+7 && pressedx>LSpos1.x-7 &&
           pressedy<LSpos2.y && pressedy>LSpos1.y)
         {inLS=true;
//          pneumaticPanel.setCursor(Cursor.getPredefinedCursor( Cursor.TEXT_CURSOR ) );
          pressedLS=-1;
          int pk=(pressedx-LSpos1.x)*images.length/(LSpos2.x-LSpos1.x);
          int pkleft=(pressedx-LSpos1.x-5)*images.length/(LSpos2.x-LSpos1.x);
          int pkright=(pressedx-LSpos1.x+5)*images.length/(LSpos2.x-LSpos1.x);
          int lsp=0;

         }
        */
       }
      else
       maybeShowPopup(pop,ex,ey);
   }

  public void mouseReleased(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      super.MouseReleased(e,left,ex,ey,pop);
      if(left && !drag)
       {int x=ex,y=ey;
         if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
          {if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION)
             cdo.setSolFStatus(false);
          }
        if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
          {if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION)
             cdo.setSolBStatus(false);
          }
       }
      /*
      if(left && drag)
       {if(inLS)
         {if(pressedLS >=0)
           {
//System.err.println("ls");
            int xx=ex,yy=ey;
// System.err.println("size"+getSize().width+" "+getSize().height+" "+xx+" "+yy);
//System.err.println(LSpos1.x+":"+LSpos2.x+":"+LSpos1.y+":"+LSpos2.y+"::"+ex+":"+ey);
            if(!withLS()) return;
            if(xx<LSpos2.x && xx>LSpos1.x && yy<LSpos2.y && yy>LSpos1.y)
             {
              int pk=(xx-LSpos1.x)*(images.length-1)/(LSpos2.x-LSpos1.x);
              int pkleft=(xx-LSpos1.x-5)*(images.length-1)/(LSpos2.x-LSpos1.x);
              int pkright=(xx-LSpos1.x+5)*(images.length-1)/(LSpos2.x-LSpos1.x);
             }
            else
             {if(xx > getSize().width || yy > getSize().height)
               {valveArray.remove(pressedLS);
                valvePos.remove(pressedLS);
               }
              else
               {if(xx<LSpos1.x)
                 {valvePos.set(pressedLS,new Integer(0));  //((Integ) valvePos.get(pressedLS)).x=0;
                 }
                else if(xx>LSpos2.x)
                 {valvePos.set(pressedLS,new Integer(images.length-1));  //((Integ) valvePos.get(pressedLS)).x=images.length-1;
                 }
               }
             }
           }

//          pneumaticPanel.setCursor(savedCursor);
          drag=false;
          inLS=false;
          pressedLS=-1;
          return;
         }
       }
*/
   }


/*
  public void addValvePos(int x)
    {valvePos.add(new Integer(x));}
  public int getValvePos(int j)
    {return ((Integer) valvePos.get(j)).intValue();}
*/
  public String extraWrite()
   {return "";
   }

  public void extraRead(String str)
   {
   }

 }
