package tw.com.justiot.sequencecontrol.eelement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.dialog.DBDialog;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.pelement.TRect;

public class PumpMonitor extends ESystemBase implements Compound
 {private ArrayList cdoutputs=new ArrayList();
  private Image startpump;
  private Image stoppump;
  private Image startpumpon;
  private Image stoppumpon;
  private Image lightg,lightg_off;
  private Image lightr,lightr_off;
//  private Dimension dimlight=new Dimension(28,28);
  private Dimension dimlight=new Dimension(21,21);
  private int lightwidth;
  private int lightheight;
//  private TRect glightRect=new TRect(0,0,0,0),rlightRect=new TRect(0,0,0,0);
  //public DIOModule dimodule1; // light green    pump operating
  //public DIOModule dimodule2; // light red      pump abnormal
  public CEDevice ced1,ced2; // input sensor2
                             //light green    pump operating
                             // light red      pump abnormal
  private boolean firstPopup=true;

  public ArrayList getCDOutputs()
   {return cdoutputs;
   }


  public PumpMonitor(boolean movable, boolean soundon, boolean popupon, ElectricListener electriclistener)
   {super(movable,soundon,popupon, electriclistener);
   setLayout(null);
   setBackground(Color.white);
   String modelType="Water";
   String modelName="PumpMonitor";

	realImage=util.loadImage(modelType,modelName,"realImage","/resources/images/SymSP/pumpc.gif");
	realImageDim=new Dimension(32,32);
//System.out.println("images.length="+images.length);
	images=util.loadImages(modelType,modelName,"images/SymSP/pump.gif",4);
//System.out.println("images.length="+images.length);
	animatedImage=util.loadImage(modelType,modelName,"pumpm","/resources/images/SymSP/pumpm.gif");

	imageDim=new Dimension(62,56);
	ReScale();
//	width0=imageDim.width;
//	height0=imageDim.height+40;
	EFsol1=util.loadImage(modelType,modelName,"EFsol1","/resources/images/SymSP/EDPumpstart.GIF");
   EFsol1on=util.loadImage(modelType,modelName,"EFsol1on","/resources/images/SymSP/EDPumpstartON.GIF");
   EFsol2=util.loadImage(modelType,modelName,"EFsol2","/resources/images/SymSP/EDPumpstop.GIF");
   EFsol2on=util.loadImage(modelType,modelName,"EFsol2on","/resources/images/SymSP/EDPumpstopON.GIF");

   startpump=util.loadImage(modelType,modelName,"startpump","/resources/images/SymSP/startpump.gif");
   stoppump=util.loadImage(modelType,modelName,"stoppump","/resources/images/SymSP/stoppump.gif");
   startpumpon=util.loadImage(modelType,modelName,"startpumpon","/resources/images/SymSP/startpumpon.gif");
   stoppumpon=util.loadImage(modelType,modelName,"stoppumpon","/resources/images/SymSP/stoppumpon.gif");
   lightg=util.loadImage(modelType,modelName,"lightg","/resources/images/SymSP/light_green.gif");
   lightr=util.loadImage(modelType,modelName,"lightr","/resources/images/SymSP/light_red.gif");
   lightg_off=util.loadImage(modelType,modelName,"lightg_on","/resources/images/SymSP/light_green_off.gif");
   lightr_off=util.loadImage(modelType,modelName,"lightr_on","/resources/images/SymSP/light_red_off.gif");


	audioClip=util.loadSound("sound/alarm.wav");
	/*      if(images.length>2 && timer==null)
	   {timer=new com.wujeng.data.timer.Timer();
	    timer.setDelay(200);
	   }
	*/
	timeron=false;
	cdo=new CDOutput(modelType,modelName,CDOutput.TYPE_TWOWAY,true);
   cdo.addListener(this);
   esymbol=new ESymbol(cdo, electriclistener);
   cdo.modelType=modelType;
   cdo.modelName=modelName;
//   setName(defaultName());
   cdo.name=defaultName();
   cdoutputs.add(cdo);
   }

  public PumpMonitor(ElectricListener electriclistener)
   {super(electriclistener);
    setLayout(null);
    setBackground(Color.white);
    String modelType="Water";
    String modelName="PumpMonitor";

	realImage=util.loadImage(modelType,modelName,"realImage","/resources/images/SymSP/pumpc.gif");
	realImageDim=new Dimension(32,32);
//System.out.println("images.length="+images.length);
	images=util.loadImages(modelType,modelName,"images/SymSP/pump.gif",4);
//System.out.println("images.length="+images.length);
	animatedImage=util.loadImage(modelType,modelName,"pumpm","/resources/images/SymSP/pumpm.gif");

	imageDim=new Dimension(62,56);
	ReScale();
//	width0=imageDim.width;
//	height0=imageDim.height+40;
	EFsol1=util.loadImage(modelType,modelName,"EFsol1","/resources/images/SymSP/EDPumpstart.GIF");
    EFsol1on=util.loadImage(modelType,modelName,"EFsol1on","/resources/images/SymSP/EDPumpstartON.GIF");
    EFsol2=util.loadImage(modelType,modelName,"EFsol2","/resources/images/SymSP/EDPumpstop.GIF");
    EFsol2on=util.loadImage(modelType,modelName,"EFsol2on","/resources/images/SymSP/EDPumpstopON.GIF");

    startpump=util.loadImage(modelType,modelName,"startpump","/resources/images/SymSP/startpump.gif");
    stoppump=util.loadImage(modelType,modelName,"stoppump","/resources/images/SymSP/stoppump.gif");
    startpumpon=util.loadImage(modelType,modelName,"startpumpon","/resources/images/SymSP/startpumpon.gif");
    stoppumpon=util.loadImage(modelType,modelName,"stoppumpon","/resources/images/SymSP/stoppumpon.gif");
    lightg=util.loadImage(modelType,modelName,"lightg","/resources/images/SymSP/light_green.gif");
    lightr=util.loadImage(modelType,modelName,"lightr","/resources/images/SymSP/light_red.gif");
    lightg_off=util.loadImage(modelType,modelName,"lightg_on","/resources/images/SymSP/light_green_off.gif");
    lightr_off=util.loadImage(modelType,modelName,"lightr_on","/resources/images/SymSP/light_red_off.gif");


	audioClip=util.loadSound("sound/alarm.wav");
	/*      if(images.length>2 && timer==null)
	   {timer=new com.wujeng.data.timer.Timer();
	    timer.setDelay(200);
	   }
	*/
	timeron=false;
	cdo=new CDOutput(modelType,modelName,CDOutput.TYPE_TWOWAY,true);
    cdo.addListener(this);
    esymbol=new ESymbol(cdo, electriclistener);
    cdo.modelType=modelType;
    cdo.modelName=modelName;
//    setName(defaultName());
    cdo.name=defaultName();
    cdoutputs.add(cdo);
   }

  public PumpMonitor(CDOutput cdo, ElectricListener electriclistener)
   {super(electriclistener);
    this.cdo=cdo;
    cdo.addListener(this);
    esymbol=new ESymbol(cdo, electriclistener);
    setLayout(null);

    setBackground(Color.white);
    String modelType=cdo.modelType;
    String modelName=cdo.modelName;

    realImage=util.loadImage(modelType,modelName,"realImage","/resources/images/SymSP/pumpc.gif");
	realImageDim=new Dimension(32,32);
//System.out.println("images.length="+images.length);
	images=util.loadImages(modelType,modelName,"images/SymSP/pump.gif",4);
//System.out.println("images.length="+images.length);
	imageDim=new Dimension(62,56);
	ReScale();

	EFsol1=util.loadImage(modelType,modelName,"EFsol1","/resources/images/SymSP/EDPumpstart.GIF");
    EFsol1on=util.loadImage(modelType,modelName,"EFsol1on","/resources/images/SymSP/EDPumpstartON.GIF");
    EFsol2=util.loadImage(modelType,modelName,"EFsol2","/resources/images/SymSP/EDPumpstop.GIF");
    EFsol2on=util.loadImage(modelType,modelName,"EFsol2on","/resources/images/SymSP/EDPumpstopON.GIF");

    startpump=util.loadImage(modelType,modelName,"startpump","/resources/images/SymSP/startpump.gif");
    stoppump=util.loadImage(modelType,modelName,"stoppump","/resources/images/SymSP/stoppump.gif");
    startpumpon=util.loadImage(modelType,modelName,"startpumpon","/resources/images/SymSP/startpumpon.gif");
    stoppumpon=util.loadImage(modelType,modelName,"stoppumpon","/resources/images/SymSP/stoppumpon.gif");
    lightg=util.loadImage(modelType,modelName,"lightg","/resources/images/SymSP/light_green.gif");
    lightr=util.loadImage(modelType,modelName,"lightr","/resources/images/SymSP/light_red.gif");
    lightg_off=util.loadImage(modelType,modelName,"lightg_on","/resources/images/SymSP/light_green_off.gif");
    lightr_off=util.loadImage(modelType,modelName,"lightr_on","/resources/images/SymSP/light_red_off.gif");


	audioClip=util.loadSound("sound/alarm.wav");
	/*      if(images.length>2 && timer==null)
	   {timer=new com.wujeng.data.timer.Timer();
	    timer.setDelay(200);
	   }
	*/
	timeron=false;
	cdo.modelType=modelType;
	cdo.modelName=modelName;
//    setName(defaultName());
	cdo.name=defaultName();
//    setModules();
	cdoutputs.add(cdo);
   }

  public void solFStatusChanged(CDOutput cdo)
   {/*
	boolean b=cdo.getSolFStatus();
    if(b)
     {setTimeron(true);
     }
    else
     {setTimeron(false);
     }
	if(WebLadderCAD.electrics!=null) WebLadderCAD.electrics.repaint();
	*/
   }

  public void solBStatusChanged(CDOutput cdo)
   {/*
    boolean b=cdo.getSolBStatus();
    if(WebLadderCAD.electrics!=null) WebLadderCAD.electrics.repaint();
    */
   }

  public void check()
  {if(audioClip!=null && ced2!=null && soundon)
     {
	  if(ced2.status && !oneplaying)
		{audioClip.loop();
		 oneplaying=true;
		 playing=true;
		}
	  if(!ced2.status && playing)
	   {
	    audioClip.stop();
	    playing=false;
	    ESystemBase.updateOneplaying();
	   }
     }
  }


 TRect DRect=new TRect(0,0,0,0);
 private void ReScale()
  {imagewid=(int)(imageDim.width*ratio);
	imagehgt=(int)(imageDim.height*ratio);
	lightheight=(int)(dimlight.height*ratio);
	lightwidth=(int)(dimlight.width*ratio);
	Width=imagewid+8;
   Height=imagehgt+lightheight*2+8;

//	DRect=new TRect(2,2+lightheight,2+imagewid,2+lightheight+imagehgt);
    DRect.Left=2;
    DRect.Top=2+lightheight;
    DRect.Right=2+imagewid;
    DRect.Bottom=2+lightheight+imagehgt;
	setSize(Width,Height);

	glight=false;
	if(ced1!=null)
	 {if(ced1.status) glight=true;
	 }
//	glightRect.Left=4;
//	glightRect.Top=4;
//	glightRect.Right=glightRect.Left+lightwidth;
//	glightRect.Bottom=glightRect.Top+lightheight;

	rlight=false;
	if(ced2!=null)
	 {if(ced2.status) rlight=true;
	 }

//	rlightRect.Left=4;
//	rlightRect.Top=4;
//	rlightRect.Right=glightRect.Left+lightwidth;
//	rlightRect.Bottom=glightRect.Top+lightheight;
	pstart=false;
	pstop=false;
	if(cdo!=null)
	 {if(cdo.getSolFStatus()) pstart=true;
	  if(cdo.getSolBStatus()) pstop=true;
	 }
  }

 private void DrawFrame(Graphics g)
  {g.setColor(Color.white);
	g.drawLine(0,Height-2,0,0);
	g.drawLine(0,0,Width-2,0);
	g.drawLine(Width-2,0,Width-2,Height-2);
	g.drawLine(Width-2,Height-2,0,Height-2);
		/*
		  g.drawLine(Width-8,Height-2,Width-8,Height-8);
		  g.drawLine(Width-8,Height-8,Width-2,Height-8);
		*/
	g.setColor(Color.blue);
	g.drawLine(1,Height-3,1,1);
	g.drawLine(1,1,Width-3,1);
	g.drawLine(Width-1,1,Width-1,Height-1);
	g.drawLine(Width-1,Height-1,1,Height-1);

//	g.drawLine(Width-9,Height-3,Width-9,Height-9);
//	g.drawLine(Width-9,Height-9,Width-3,Height-9);
  }
/*
  private void setModules()
   {if(cdo.NAPKey1!=null && cdo.NAPKey1.length()>0)
	 dimodule1=(DIOModule) Modules.modules.get(cdo.NAPKey1);
    if(cdo.NAPKey2!=null && cdo.NAPKey2.length()>0)
	 dimodule2=(DIOModule) Modules.modules.get(cdo.NAPKey2);
   }
*/
  private boolean glight,rlight,pstart,pstop;
  public void paintComponent(Graphics g)
  { super.paintComponent(g);
	ReScale();
//	g.setColor(Color.lightGray);
//	g.fillRect(0,0,Width,Height);

	DrawFrame(g);
	if(glight)
	 g.drawImage(lightg,4,4,lightwidth,lightheight,this);
	else
	 g.drawImage(lightg_off,4,4,lightwidth,lightheight,this);

	if(rlight)
	 g.drawImage(lightr,4+lightwidth,4,lightwidth,lightheight,this);
	else
     g.drawImage(lightr_off,4+lightwidth,4,lightwidth,lightheight,this);

	if(glight)
	 g.drawImage(animatedImage,DRect.Left,DRect.Top,imagewid,imagehgt,this);
	else
 	 g.drawImage(images[curImage],DRect.Left,DRect.Top,imagewid,imagehgt,this);

	if(pstart)
	 g.drawImage(startpumpon,DRect.Right-lightwidth*2,DRect.Bottom+1,lightwidth,lightheight,this);
	else
	 g.drawImage(startpump,DRect.Right-lightwidth*2,DRect.Bottom+1,lightwidth,lightheight,this);
	if(pstop)
	 g.drawImage(stoppumpon,DRect.Right-lightwidth,DRect.Bottom+1,lightwidth,lightheight,this);
	else
	 g.drawImage(stoppump,DRect.Right-lightwidth,DRect.Bottom+1,lightwidth,lightheight,this);


	if(cdo.name!=null)
	 {g.setFont(new Font("Small Fonts",Font.PLAIN,(int)(10*ratio)));
	  g.setColor(Color.blue);
	  g.drawString(cdo.name,6,DRect.Bottom+dimlight.height);
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
 }

/*
		  public void DrawSolCell(Graphics g)
		   {esystemListener.PaintCell(g,cdo.SP1.y,cdo.SP1.x);
		    esystemListener.PaintCell(g,cdo.SP2.y,cdo.SP2.x);
		   }
*/
/*
		  public boolean isTimeron() {return timeron;}
		  public void setTimeron(boolean ton) {timeron=ton;}

		  public void startTimer(java.util.Timer timer)
		   {if(images.length>2)
			 timer.scheduleAtFixedRate(new TimeThread(), 0, TimerPeriod);
		   }

		  class TimeThread extends TimerTask
		   {
		    public void run()
		     {if(!timeron) return;
		      if(WebLadderCAD.opMode==WebLadderCAD.OP_CONTROL) return;
			  switch(cdo.systemType)
		       {case CDOutput.TYPE_ONOFF:
		           break;
		        case CDOutput.TYPE_LOOP:
		           if(curImage < images.length) curImage++;
		           if(curImage==images.length) curImage=0;
//		System.err.println("curImage:"+curImage);
//		           repaint();
		           break;
		        case CDOutput.TYPE_TWOWAY:
//		           if(motiondir==MOTION_FORWARD) nextStep();
//		           if(motiondir==MOTION_BACKWARD) preStep();
		           break;
		        case CDOutput.TYPE_ONEWAY:
//		           if(motiondir==MOTION_FORWARD) nextStep();
		           break;
		      }
//		    checkLimitSwitch();
		     repaint();
		    }
		  }


		  protected void nextStep()
		   {switch(cdo.systemType)
		      {
		        case CDOutput.TYPE_TWOWAY:
		           if(curImage<(images.length-1)) curImage++;
		           else
		            {setTimeron(false);
//		              motiondir=MOTION_NONE;
		              if(audioClip!=null) audioClip.stop();
		            }
		           break;
		        case CDOutput.TYPE_ONEWAY:
		           if(curImage<(images.length-1)) curImage++;
		           else
		            {setTimeron(false);
//		              motiondir=MOTION_NONE;
		              if(audioClip!=null) audioClip.stop();
		              curImage=0;
		            }
		           break;
		     }
		   }

		  protected void preStep()
		   {if(curImage>0) curImage--;
		    else
		     {setTimeron(false);
//		      motiondir=MOTION_NONE;
		      if(audioClip!=null) audioClip.stop();
		     }
		   }

		  public void onTime(java.awt.event.ActionEvent evt)
		   {
//		System.err.println("onTime");
		     switch(cdo.systemType)
		      {case CDOutput.TYPE_ONOFF:
		           break;
		        case CDOutput.TYPE_LOOP:
		           if(curImage < images.length) curImage++;
		           if(curImage==images.length) curImage=0;
//		System.err.println("curImage:"+curImage);
//		           repaint();
		           break;
		        case CDOutput.TYPE_TWOWAY:
//		           if(motiondir==MOTION_FORWARD) nextStep();
//		           if(motiondir==MOTION_BACKWARD) preStep();
		           break;
		        case CDOutput.TYPE_ONEWAY:
//		           if(motiondir==MOTION_FORWARD) nextStep();
		           break;
		     }
//		    checkLimitSwitch();
		    repaint();
		   }
		   */
/*
		  public void mousePressed(MouseEvent e) {
		     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
		     boolean pop=e.isPopupTrigger();
		     MousePressed(e,left,e.getX(),e.getY(),pop);
		   }
		   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
		      super.MousePressed(e,left,ex,ey,pop);
		      if(left)
		       {//savedCursor = pneumaticPanel.getCursor( );
//		        pneumaticPanel.setCursor(Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		        pressedx=ex;
		        pressedy=ey;
		        pressedPosx=getLocation().x;
		        pressedPosy=getLocation().y;
		        switch(WebLadderCAD.opMode)
		          {case WebLadderCAD.OP_SIMULATION:
		              if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
		               cdo.setSolFStatus(true);
		              if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
		               cdo.setSolBStatus(true);
		              break;

		          }

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
		          {if(WebLadderCAD.opMode==WebLadderCAD.OP_SIMULATION)
		             cdo.setSolFStatus(false);
		          }
		        if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
		          {if(WebLadderCAD.opMode==WebLadderCAD.OP_SIMULATION)
		             cdo.setSolBStatus(false);
		          }
		       }
		   }

		  public void maybeShowPopup(boolean pop, int ex, int ey) {
		      if(pop) {
//		       rotm.hide();
//		         rotm.setVisible(false);
//		        popup.show(e.getComponent(), e.getX(), e.getY());
		       popup.show(this, ex, ey);

		      }
		    }

		  public void ActionPerformed(JMenuItem mi,String op,String input)
		    {
		      String option=mi.getText();
//		System.err.println("option:"+option);
		     if(option.equals(Config.getString("ESystem.delete")))
		     {delete();
		     }
		     super.ActionPerformed(mi,op,input);
		  }



		  class menuItemMouseAdapter extends MouseAdapter
		   {
		     public void mouseEntered(MouseEvent e)
		       {
		        int ind=popup.getComponentIndex((Component)e.getSource());

		       }
		   }
*/
/*
		  public void addValvePos(int x)
		    {valvePos.add(new Integer(x));}
		  public int getValvePos(int j)
		    {return ((Integer) valvePos.get(j)).intValue();}
*/
  public static tw.com.justiot.sequencecontrol.eelement.ESystemBase read(String str, ElectricListener electriclistener)
  {try {
   StringTokenizer token=new StringTokenizer(str);
   int dtype=Integer.parseInt(token.nextToken());
   if(dtype!=SCCAD.Data_Water) return null;
   String mType=token.nextToken();
   String mName=token.nextToken();
   String name=token.nextToken();
   if(name.equals("null")) name="";
   int x=Integer.parseInt(token.nextToken());
   int y=Integer.parseInt(token.nextToken());
   int cimage=Integer.parseInt(token.nextToken());
//System.out.println("Element instanceElement");
   PumpMonitor pm=new PumpMonitor(electriclistener);

   if(pm!=null)
    {if(name!=null) pm.cdo.name=name;
     pm.setLocation(x,y);
     pm.curImage=cimage;

     String ced1name=token.nextToken();
     String ced2name=token.nextToken();
     pm.ced1=electriclistener.getEArrays().findCEDeviceByName(ced1name);
     if(pm.ced1!=null) pm.ced1.esb=pm;
     pm.ced2=electriclistener.getEArrays().findCEDeviceByName(ced2name);
     if(pm.ced2!=null) pm.ced2.esb=pm;

     pm.cdo.FPLCAddress=token.nextToken();
     if(pm.cdo.FPLCAddress.toLowerCase().indexOf("null")>=0) pm.cdo.FPLCAddress=null;
     pm.cdo.BPLCAddress=token.nextToken();
     if(pm.cdo.BPLCAddress.toLowerCase().indexOf("null")>=0) pm.cdo.BPLCAddress=null;
     pm.cdo.NAPKey1=token.nextToken();
     pm.cdo.NAPno1=Integer.parseInt(token.nextToken());
     pm.cdo.NAPKey2=token.nextToken();
     pm.cdo.NAPno2=Integer.parseInt(token.nextToken());
     if(pm.cdo.NAPKey1.toLowerCase().indexOf("null")>=0) pm.cdo.NAPKey1=null;
     if(pm.cdo.NAPKey2.toLowerCase().indexOf("null")>=0) pm.cdo.NAPKey2=null;


//System.err.println("enter extra");
     if(token.hasMoreTokens())
      {
        String extra=token.nextToken("\n");
//System.err.println("element extra"+extra);
        if(extra!=null && extra.length()>1 && extra.substring(0,1).equals(" ")) extra=extra.substring(1,extra.length());
        if(extra!=null && extra.length()>0)
         {
//System.out.println(extra);
        	pm.extraRead(extra);
         }
      }
    }
   return pm;
  } catch(Exception e) {
	  e.printStackTrace();
	  return null;
  }
  }

		  public String write()
		   {
		    StringBuffer sb=new StringBuffer();
		    
		    sb.append(Integer.toString(SCCAD.Data_Water)+" ");
		    sb.append(cdo.modelType+" ");
		    sb.append(cdo.modelName+" ");
		    if(cdo.name==null || cdo.name.length()==0) sb.append("null ");
		    else sb.append(cdo.name+" ");
		    sb.append(getX()+" "+getY()+" ");
		    sb.append(curImage+" ");

		    String ced1name="null";
		    String ced2name="null";
		    if(ced1!=null && ced1.name!=null) ced1name=ced1.name;
		    if(ced2!=null && ced2.name!=null) ced2name=ced2.name;
            sb.append(ced1name+" ");
            sb.append(ced2name+" ");

		    if(cdo.FPLCAddress==null) sb.append("null ");
		    else sb.append(cdo.FPLCAddress+" ");
		    if(cdo.BPLCAddress==null) sb.append("null ");
		    else sb.append(cdo.BPLCAddress+" ");
		    if(cdo.NAPKey1==null) sb.append("null ");
		    else sb.append(cdo.NAPKey1+" ");
		    sb.append(cdo.NAPno1+" ");
		    if(cdo.NAPKey2==null) sb.append("null ");
		    else sb.append(cdo.NAPKey2+" ");
		    sb.append(cdo.NAPno2+" ");
		    
		    return sb.toString()+extraWrite();
		   }

		  public String extraWrite()
		   {return "";
		   }

		  public void extraRead(String str)
		   {
		   }
		  public void delete()
		    {//esystemListener.delete(this);
		    }

		  public void maybeShowPopup(boolean pop, int ex, int ey) {
		      if(pop) {
		        if(firstPopup)
		         {m = new JMenuItem(Config.getString("PumpMonitor.gsensor"));
		          m.addActionListener(this);
		          m.addMouseListener(new menuItemMouseAdapter());
		          popup.add(m);
		          m = new JMenuItem(Config.getString("PumpMonitor.rsensor"));
		          m.addActionListener(this);
		          m.addMouseListener(new menuItemMouseAdapter());
		          popup.add(m);
//		          popup.addSeparator();
		          firstPopup=false;
		         }
//		        popup.show(e.getComponent(), e.getX(), e.getY());
		       popup.show(this, ex, ey);
		      }
		    }

		  public void ActionPerformed(JMenuItem mi,String op,String input)
		    { super.ActionPerformed(mi,op,input);
		      String option=mi.getText();

		      if(option.equals(Config.getString("PumpMonitor.gsensor")))
		       {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
		    	if(al==null || al.size()==0)
		    	 {
		    	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
		    	  return;
		    	 }
		       DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("PumpMonitor.gsensor"),false);
		       dbDialog.pack();
		       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		       dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
		       dbDialog.setVisible(true);
		       EDevice ed=(EDevice) dbDialog.getObject();
		       ced1=ed.ced;
		       ced1.esb=this;
//		       electriclistener.repaint();
		      }
		      else if(option.equals(Config.getString("PumpMonitor.rsensor")))
		       {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
		    	if(al==null || al.size()==0)
		    	 {
		    	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
		    	  return;
		    	 }
		    	DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("PumpMonitor.rsensor"),false);
		       dbDialog.pack();
		       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		       dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
		       dbDialog.setVisible(true);
		       EDevice ed=(EDevice) dbDialog.getObject();
		       ced2=ed.ced;
		       ced2.esb=this;
//		       electriclistener.repaint();
		     }
		  }
		 }
