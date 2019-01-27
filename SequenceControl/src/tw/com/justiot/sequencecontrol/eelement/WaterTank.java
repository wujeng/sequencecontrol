package tw.com.justiot.sequencecontrol.eelement;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.Creater;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.util;
import tw.com.justiot.sequencecontrol.dialog.DBDialog;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase.menuItemMouseAdapter;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.pelement.TRect;

public class WaterTank extends ESystemBase implements Compound
 {private ArrayList cdoutputs=new ArrayList();
  public String name;
  String modelType="Water";
  String modelName="WaterTank";
  public PumpMonitor pumpa;
  public PumpMonitor pumpb;
  private Image levelvhigh;
  private Image levelhigh;
  private Image levelnormal;
  private Image levellow;
  private Image levelvlow;
  private int Height,Width;
  private Dimension dimlevel=new Dimension(25,118);
  private final int thickness0=12;
  private final int LVHigh0=15;
  private final int LHigh0=29;
  private final int LNormal0=53;
  private final int LLow0=89;
  private final int LVLow0=101;
  private int level=53;
  private int htitle0=12;
  private int hlevel0=24;
  private int levelpx,imagepx;
  private boolean firstPopup=true;
  public CEDevice cedvh,cedh,cedl,cedvl;
//  private DIOModule diomodule;
  private Image lightg,lightr,lightgoff,lightroff;
  private Dimension dimlight=new Dimension(14,14);
//  private String NAPKey1,NAPKey2,NAPKey3,NAPKey4;
//  private int    NAPno1,NAPno2,NAPno3,NAPno4;
//  private DIOModule diom1,diom2,diom3,diom4;
  private int pumpwidth0,pumpheight0,thickness;
  private int LVHigh,LHigh,LNormal,LLow,LVLow,htitle,hlevel;
  private int levelwidth,levelheight,lightwidth,lightheight;

  public ArrayList getCDOutputs()
   {return cdoutputs;
   }
  public WaterTank(String vhNAPKey, int vhNAPno, // very high level sensor
		           String hNAPKey, int hNAPno,   // high level sensor
		           String lNAPKey, int lNAPno,   // low level sensor
		           String vlNAPKey, int vlNAPno, // very low level sensor
		           String ma1NAPKey, int ma1NAPno,	// motorA start
		           String ma2NAPKey, int ma2NAPno,  // motorA stop
		           String mb1NAPKey, int mb1NAPno,  // motorB start
		           String mb2NAPKey, int mb2NAPno, ElectricListener electriclistener)  // motorB stop
   {this(electriclistener);
    pumpa.cdo.NAPKey1=ma1NAPKey;
    pumpa.cdo.NAPno1=ma1NAPno;
    pumpa.cdo.NAPKey2=ma2NAPKey;
    pumpa.cdo.NAPno2=ma2NAPno;
    pumpb.cdo.NAPKey1=mb1NAPKey;
    pumpb.cdo.NAPno1=mb1NAPno;
    pumpb.cdo.NAPKey2=mb2NAPKey;
    pumpb.cdo.NAPno2=mb2NAPno;
    cedvh=new CEDevice("Sensor","S",CEDevice.TYPE_SENSOR, electriclistener);
    cedvh.NAPKey=vhNAPKey;
    cedvh.NAPno=vhNAPno;
    cedh=new CEDevice("Sensor","S",CEDevice.TYPE_SENSOR, electriclistener);
    cedh.NAPKey=hNAPKey;
    cedh.NAPno=hNAPno;
    cedl=new CEDevice("Sensor","S",CEDevice.TYPE_SENSOR, electriclistener);
    cedl.NAPKey=lNAPKey;
    cedl.NAPno=lNAPno;
    cedvl=new CEDevice("Sensor","S",CEDevice.TYPE_SENSOR, electriclistener);
    cedvl.NAPKey=vlNAPKey;
    cedvl.NAPno=vlNAPno;
   }

  public WaterTank(ElectricListener electriclistener)
   {super(electriclistener);

	levelvhigh=util.loadImage(modelType,modelName,"levelvhigh","/resources/images/SymSP/levelvhigh.gif");
	levelhigh=util.loadImage(modelType,modelName,"levelhigh","/resources/images/SymSP/levelhigh.gif");
	levelnormal=util.loadImage(modelType,modelName,"levelnormal","/resources/images/SymSP/levelnormal.gif");
	levellow=util.loadImage(modelType,modelName,"levellow","/resources/images/SymSP/levellow.gif");
	levelvlow=util.loadImage(modelType,modelName,"levelvlow","/resources/images/SymSP/levelvlow.gif");

	lightg=util.loadImage(modelType,modelName,"lightg","/resources/images/SymSP/light_green.gif");
	lightgoff=util.loadImage(modelType,modelName,"lightgoff","/resources/images/SymSP/light_green_off.gif");
	lightr=util.loadImage(modelType,modelName,"lightr","/resources/images/SymSP/light_red.gif");
	lightroff=util.loadImage(modelType,modelName,"lightroff","/resources/images/SymSP/light_red_off.gif");
	audioClip=util.loadSound("sound/alarm.wav");
	pumpa=new PumpMonitor(false,false,false, electriclistener);
 	pumpb=new PumpMonitor(false,false,false, electriclistener);
 	setLayout(null);
 	add(pumpa);
 	add(pumpb);
 	pumpwidth0=pumpa.getWidth();
	pumpheight0=pumpa.getHeight();

 	rescale();

// 	pumpa.setBounds(1+thickness, Height-thickness-1-pumpa.getHeight(), pumpa.getWidth(), pumpa.getHeight());
//    pumpb.setBounds(Width-1-thickness-pumpb.getWidth(),Height-thickness-1-pumpb.getHeight() , pumpb.getWidth(), pumpb.getHeight());

    name=defaultName();
    cdoutputs.add(pumpa.cdo);
    cdoutputs.add(pumpb.cdo);
/*
    Border blackline, raisedetched, loweredetched,
    raisedbevel, loweredbevel, empty;
blackline = BorderFactory.createLineBorder(Color.black);
raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
raisedbevel = BorderFactory.createRaisedBevelBorder();
loweredbevel = BorderFactory.createLoweredBevelBorder();
empty = BorderFactory.createEmptyBorder();
    setBorder(loweredbevel);
*/

   }
  public String getActuatorName() {return name;}

 public String defaultName()
  {String newname=null;
	String npref=modelName;
	if(modelName.length()>0) npref=modelName.substring(0,1);
	String nameno=(String) nametable.get(modelType+"*"+modelName);
   if(nameno!=null && nameno.length()>0)
    {nameno=new Integer(Integer.parseInt(nameno)+1).toString();
	  newname=npref+nameno;
	  nametable.put(modelType+"*"+modelName, nameno);
    }
   else
    {
	  newname=npref+"1";
	  nametable.put(modelType+"*"+modelName, "1");
    }
   return newname;
  }

public void setRatio(double ratio)
 {
	this.ratio=ratio;
	pumpa.setRatio(ratio);
	pumpb.setRatio(ratio);
	repaint();
 }

public void Logic()
 {
  if(cedvh!=null && cedvh.status)
   {if(pumpa.cdo!=null)
	 {pumpa.cdo.setOutputFStatus(true);
	  pumpa.cdo.setSolFStatus(true);
	 }
    if(pumpb.cdo!=null)
	 {pumpb.cdo.setOutputFStatus(true);
	  pumpb.cdo.setSolFStatus(true);
	 }
   }
  else if(cedvl!=null && cedvl.status)
   {if(pumpa.cdo!=null)
	 {pumpa.cdo.setOutputBStatus(true);
	  pumpa.cdo.setSolBStatus(true);
	 }
    if(pumpb.cdo!=null)
	 {pumpb.cdo.setOutputBStatus(true);
	  pumpb.cdo.setSolBStatus(true);
	 }
   }
  else if((cedvh==null || !cedvh.status) &&
		  (cedh==null || !cedh.status) &&
		  (cedl==null || !cedl.status) &&
		  (cedvl==null || !cedvl.status))
   {if(pumpa.cdo!=null)
	 {pumpa.cdo.setOutputFStatus(false);
	  pumpa.cdo.setSolFStatus(false);
	  pumpa.cdo.setOutputBStatus(false);
	  pumpa.cdo.setSolBStatus(false);
	 }
    if(pumpb.cdo!=null)
	 {pumpb.cdo.setOutputFStatus(false);
	  pumpb.cdo.setSolFStatus(false);
      pumpb.cdo.setOutputBStatus(false);
	  pumpb.cdo.setSolBStatus(false);
	 }

   }
 }

  private boolean paabnormal,pbabnormal;
public void check()
 {
//System.out.println("WaterTank check()");
	level=LNormal;
	if(cedvh!=null && cedvh.status) level=LVHigh;
	if(cedh!=null && cedh.status) level=LHigh;
	if(cedl!=null && cedl.status) level=LLow;
	if(cedvl!=null && cedvl.status) level=LVLow;
	paabnormal=false;
	if(pumpa.ced2!=null) paabnormal=pumpa.ced2.status;
	pbabnormal=false;
	if(pumpb.ced2!=null) pbabnormal=pumpb.ced2.status;

	if(audioClip!=null)
	 {if((level==LVHigh || level==LVLow || paabnormal  || pbabnormal)
			  && !oneplaying)
	   {audioClip.loop();
	    playing=true;
	    oneplaying=true;
//System.out.println("playing ...");
	   }

	  if(playing && level!=LVHigh && level!=LVLow &&
			!paabnormal && !pbabnormal)
      {
	    audioClip.stop();
	    playing=false;
	    ESystemBase.updateOneplaying();
	   }
	 }

	Logic();
 }

TRect DRect;
private void rescale()
{level=LNormal;
if(cedvh!=null && cedvh.status) level=LVHigh;
if(cedh!=null && cedh.status) level=LHigh;
if(cedl!=null && cedl.status) level=LLow;
if(cedvl!=null && cedvl.status) level=LVLow;

 Width=(int) (pumpwidth0*3*ratio);
 Height=(int) (pumpheight0*2*ratio);
 thickness=(int) (thickness0*ratio);
 setSize(Width,Height);
 LVHigh=(int)(LVHigh0*ratio);
 LHigh=(int) (LHigh0*ratio);
 LNormal=(int) (LNormal0*ratio);
 LLow=(int) (LLow0*ratio);
 LVLow=(int) (LVLow0*ratio);
 htitle=(int) (htitle0*ratio);
 hlevel=(int) (hlevel0*ratio);
 levelwidth=(int) (dimlevel.width*ratio);
 levelheight=(int) (dimlevel.height*ratio);
 lightwidth=(int) (dimlight.width*ratio);
 lightheight=(int) (dimlight.height*ratio);

 pumpa.setBounds(1+thickness, Height-thickness-1-pumpa.getHeight(), pumpa.getWidth(), pumpa.getHeight());
 pumpb.setBounds(Width-1-thickness-pumpb.getWidth(),Height-thickness-1-pumpb.getHeight() , pumpb.getWidth(), pumpb.getHeight());
// levelpx=2+thickness+pumpa.getWidth()+2;
 levelpx=(Width-levelwidth-lightwidth)/2;
 imagepx=levelpx+levelwidth+2;
}

private void DrawFrame(Graphics g)
 {g.setColor(Color.BLUE);
	g.drawLine(0,Height-1,0,0);
	g.drawLine(0,0,Width-1,0);
	g.drawLine(Width-1,0,Width-1,Height-1);
	g.drawLine(Width-1,Height-1,0,Height-1);

	g.setColor(Color.BLACK);
	g.fillRect(1,1,thickness,Height-2);
	g.fillRect(Width-thickness-1,1,thickness,Height-2);
	g.fillRect(1,Height-thickness-1,Height-2,thickness);

//	g.setColor(Color.BLUE);


	g.setColor(new Color(149,168,52));
	g.fillRect(1+thickness,level,Width-2*thickness-2,Height-2-thickness-level);

	g.setColor(Color.BLUE);
	g.setFont(new Font("�ө���", Font.BOLD, 10));
	if(name==null || name.length()==0)
	 g.drawString("�ä���", 5+thickness, htitle);
	else
	 g.drawString(name, 5+thickness, htitle);
//	g.drawString("�ä���", 5+thickness, htitle);
//	g.drawString("�ä���", 5+thickness+5, htitle);
}

public void paintComponent(Graphics g)
{
 super.paintComponent(g);
 rescale();
 DrawFrame(g);
 g.drawImage(lightroff,imagepx,LVHigh-lightheight/2,lightwidth,lightheight,this);
	 g.drawImage(lightgoff,imagepx,LHigh-lightheight/2,lightwidth,lightheight,this);
	 g.drawImage(lightgoff,imagepx,LNormal-lightheight/2,lightwidth,lightheight,this);
	 g.drawImage(lightgoff,imagepx,LLow-lightheight/2,lightwidth,lightheight,this);
	 g.drawImage(lightroff,imagepx,LVLow-lightheight/2,lightwidth,lightheight,this);

 if(level==LVHigh)
  {g.drawImage(levelvhigh,levelpx,3,levelwidth,levelheight,this);
	 g.drawImage(lightr,imagepx,level-lightheight/2,lightwidth,lightheight,this);
	 g.setColor(Color.RED);
	 g.drawString("�W������", 5+thickness, hlevel);
  }
 else if(level==LHigh)
  {g.drawImage(levelhigh,levelpx,3,levelwidth,levelheight,this);
	 g.drawImage(lightg,imagepx,level-lightheight/2,lightwidth,lightheight,this);
	 g.setColor(Color.BLACK);
	 g.drawString("������", 5+thickness, hlevel);
  }
 else if(level==LLow)
  {g.drawImage(levellow,levelpx,3,levelwidth,levelheight,this);
	 g.drawImage(lightg,imagepx,level-lightheight/2,lightwidth,lightheight,this);
	 g.setColor(Color.BLACK);
	 g.drawString("�C����", 5+thickness, hlevel);
  }
 else if(level==LVLow)
  {g.drawImage(levelvlow,levelpx,3,levelwidth,levelheight,this);
	 g.drawImage(lightr,imagepx,level-lightheight/2,lightwidth,lightheight,this);
	 g.setColor(Color.RED);
	 g.drawString("�W�C����", 5+thickness, hlevel);
  }
 else
	{g.drawImage(levelnormal,levelpx,3,levelwidth,levelheight,this);
	 g.drawImage(lightg,imagepx,LNormal-lightheight/2,lightwidth,lightheight,this);
	 g.setColor(Color.BLACK);
	 g.drawString("���`����", 5+thickness, hlevel);
	}
 }
/*
  public void paintComponent(Graphics g)
  {
   super.paintComponent(g);
   DrawFrame(g);
   if(level==LVHigh)
    {
	 g.drawImage(levelvhigh,(Width-dimlevel.width)/2,3,dimlevel.width,dimlevel.height,this);
	 g.drawImage(lightr,(Width+dimlevel.width)/2+2,level-dimlight.height/2,dimlight.width,dimlight.height,this);
    }
   else if(level==LHigh)
    {
	 g.drawImage(levelhigh,(Width-dimlevel.width)/2,3,dimlevel.width,dimlevel.height,this);
	 g.drawImage(lightg,(Width+dimlevel.width)/2+2,level-dimlight.height/2,dimlight.width,dimlight.height,this);
    }
   else if(level==LLow)
    {
	 g.drawImage(levellow,(Width-dimlevel.width)/2,3,dimlevel.width,dimlevel.height,this);
	 g.drawImage(lightg,(Width+dimlevel.width)/2+2,level-dimlight.height/2,dimlight.width,dimlight.height,this);
    }
   else if(level==LVLow)
    {
	 g.drawImage(levelvlow,(Width-dimlevel.width)/2,3,dimlevel.width,dimlevel.height,this);
	 g.drawImage(lightr,(Width+dimlevel.width)/2+2,level-dimlight.height/2,dimlight.width,dimlight.height,this);
    }
   else
	{g.drawImage(levelnormal,(Width-dimlevel.width)/2,3,dimlevel.width,dimlevel.height,this);
	 g.drawImage(lightgon,(Width+dimlevel.width)/2+2,level-dimlight.height/2,dimlight.width,dimlight.height,this);
	}
  }
*/
   public static tw.com.justiot.sequencecontrol.eelement.ESystemBase read(String str, ElectricListener electriclistener)
   {
 try {
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

    WaterTank wt=new WaterTank(electriclistener);
    wt.name=name;
    wt.setLocation(x,y);
    wt.curImage=cimage;
//if(WebLadderCAD.debug)  System.out.println("WaterTank read");
    String cedvhname=token.nextToken();
    String cedhname=token.nextToken();
    String cedlname=token.nextToken();
    String cedvlname=token.nextToken();
    
    wt.cedvh=electriclistener.getEArrays().findCEDeviceByName(cedvhname);
    if(wt.cedvh!=null) wt.cedvh.esb=wt;
    wt.cedh=electriclistener.getEArrays().findCEDeviceByName(cedhname);
    if(wt.cedh!=null) wt.cedh.esb=wt;
    wt.cedl=electriclistener.getEArrays().findCEDeviceByName(cedlname);
    if(wt.cedl!=null) wt.cedl.esb=wt;
    wt.cedvl=electriclistener.getEArrays().findCEDeviceByName(cedvlname);
    if(wt.cedvl!=null) wt.cedvl.esb=wt;

    
    
 String ceda1name=token.nextToken();
 String ceda2name=token.nextToken();
 wt.pumpa.ced1=electriclistener.getEArrays().findCEDeviceByName(ceda1name);
 if(wt.pumpa.ced1!=null) wt.pumpa.ced1.esb=wt.pumpa;
 wt.pumpa.ced2=electriclistener.getEArrays().findCEDeviceByName(ceda2name);
 if(wt.pumpa.ced2!=null) wt.pumpa.ced2.esb=wt.pumpa;

 
    wt.pumpa.cdo.FPLCAddress=token.nextToken();
      if(wt.pumpa.cdo.FPLCAddress.equals("null")) wt.pumpa.cdo.FPLCAddress=null;
      wt.pumpa.cdo.BPLCAddress=token.nextToken();
      if(wt.pumpa.cdo.BPLCAddress.equals("null")) wt.pumpa.cdo.BPLCAddress=null;
      wt.pumpa.cdo.NAPKey1=token.nextToken();
      wt.pumpa.cdo.NAPno1=Integer.parseInt(token.nextToken());
      wt.pumpa.cdo.NAPKey2=token.nextToken();
      wt.pumpa.cdo.NAPno2=Integer.parseInt(token.nextToken());
      if(wt.pumpa.cdo.NAPKey1.equals("null")) wt.pumpa.cdo.NAPKey1=null;
      if(wt.pumpa.cdo.NAPKey2.equals("null")) wt.pumpa.cdo.NAPKey2=null;
//if(WebLadderCAD.debug) System.out.println("WaterTank read");

      String cedb1name=token.nextToken();
      String cedb2name=token.nextToken();
      wt.pumpb.ced1=electriclistener.getEArrays().findCEDeviceByName(cedb1name);
      if(wt.pumpb.ced1!=null) wt.pumpb.ced1.esb=wt.pumpb;
      wt.pumpb.ced2=electriclistener.getEArrays().findCEDeviceByName(cedb2name);
      if(wt.pumpb.ced2!=null) wt.pumpb.ced2.esb=wt.pumpb;

      wt.pumpb.cdo.FPLCAddress=token.nextToken();
      if(wt.pumpb.cdo.FPLCAddress.equals("null")) wt.pumpb.cdo.FPLCAddress=null;
      wt.pumpb.cdo.BPLCAddress=token.nextToken();
      if(wt.pumpb.cdo.BPLCAddress.equals("null")) wt.pumpb.cdo.BPLCAddress=null;
      wt.pumpb.cdo.NAPKey1=token.nextToken();
      wt.pumpb.cdo.NAPno1=Integer.parseInt(token.nextToken());
      wt.pumpb.cdo.NAPKey2=token.nextToken();
      wt.pumpb.cdo.NAPno2=Integer.parseInt(token.nextToken());
      if(wt.pumpb.cdo.NAPKey1.equals("null")) wt.pumpb.cdo.NAPKey1=null;
      if(wt.pumpb.cdo.NAPKey2.equals("null")) wt.pumpb.cdo.NAPKey2=null;

    return wt;
 } catch(Exception e) {
	 e.printStackTrace();
	 return null;
 }
   }

   public String write()
   {
	   
    StringBuffer sb=new StringBuffer();
    sb.append(Integer.toString(SCCAD.Data_Water)+" ");
    sb.append(modelType+" ");
    sb.append(modelName+" ");
    if(name==null || name.length()==0) sb.append("null ");
    else sb.append(name+" ");
    sb.append(getX()+" "+getY()+" ");
    sb.append(curImage+" ");

    String cedvhname="null";
    String cedhname="null";
    String cedlname="null";
    String cedvlname="null";
    if(cedvh.name!=null) cedvhname=cedvh.name;
    if(cedh.name!=null) cedhname=cedh.name;
    if(cedl.name!=null) cedlname=cedl.name;
    if(cedvl.name!=null) cedvlname=cedvl.name;
    sb.append(cedvhname+" ");
    sb.append(cedhname+" ");
    sb.append(cedlname+" ");
    sb.append(cedvlname+" ");
    
    String paced1name="null";
    String paced2name="null";
    if(pumpa.ced1.name!=null) paced1name=pumpa.ced1.name;
    if(pumpa.ced2.name!=null) paced2name=pumpa.ced2.name;
    sb.append(paced1name+" ");
    sb.append(paced2name+" ");
    
    
    if(pumpa.cdo.FPLCAddress==null) sb.append("null ");
    else sb.append(pumpa.cdo.FPLCAddress+" ");
    if(pumpa.cdo.BPLCAddress==null) sb.append("null ");
    else sb.append(pumpa.cdo.BPLCAddress+" ");
    if(pumpa.cdo.NAPKey1==null) sb.append("null ");
    else sb.append(pumpa.cdo.NAPKey1+" ");
    sb.append(pumpa.cdo.NAPno1+" ");
    if(pumpa.cdo.NAPKey2==null) sb.append("null ");
    else sb.append(pumpa.cdo.NAPKey2+" ");
    sb.append(pumpa.cdo.NAPno2+" ");

    
    String pbced1name="null";
    String pbced2name="null";
    if(pumpb.ced1.name!=null) pbced1name=pumpb.ced1.name;
    if(pumpb.ced2.name!=null) pbced2name=pumpb.ced2.name;
    sb.append(pbced1name+" ");
    sb.append(pbced2name+" ");
     
    if(pumpb.cdo.FPLCAddress==null) sb.append("null ");
    else sb.append(pumpb.cdo.FPLCAddress+" ");
    if(pumpb.cdo.BPLCAddress==null) sb.append("null ");
    else sb.append(pumpb.cdo.BPLCAddress+" ");
    if(pumpb.cdo.NAPKey1==null) sb.append("null ");
    else sb.append(pumpb.cdo.NAPKey1+" ");
    sb.append(pumpb.cdo.NAPno1+" ");
    if(pumpb.cdo.NAPKey2==null) sb.append("null ");
    else sb.append(pumpb.cdo.NAPKey2+" ");
    sb.append(pumpb.cdo.NAPno2+" ");
//if(WebLadderCAD.debug) System.out.println(sb.toString());
    return sb.toString();
   }

public void extraRead(String str) {
	// TODO Auto-generated method stub

}

public String extraWrite() {
	// TODO Auto-generated method stub
	return null;
}

public void solBStatusChanged(CDOutput ces) {
	// TODO Auto-generated method stub

}

public void solFStatusChanged(CDOutput ces) {
	// TODO Auto-generated method stub

}

public void maybeShowPopup(boolean pop, int ex, int ey) {
    if(pop) {
      if(firstPopup)
       {
    	m = new JMenuItem(Config.getString("WaterTank.vhsensor"));
        m.addActionListener(this);
        m.addMouseListener(new menuItemMouseAdapter());
        popup.add(m);
        m = new JMenuItem(Config.getString("WaterTank.hsensor"));
        m.addActionListener(this);
        m.addMouseListener(new menuItemMouseAdapter());
        popup.add(m);
        m = new JMenuItem(Config.getString("WaterTank.lsensor"));
        m.addActionListener(this);
        m.addMouseListener(new menuItemMouseAdapter());
        popup.add(m);
        m = new JMenuItem(Config.getString("WaterTank.vlsensor"));
        m.addActionListener(this);
        m.addMouseListener(new menuItemMouseAdapter());
        popup.add(m);
//
        firstPopup=false;
       }
//      popup.show(e.getComponent(), e.getX(), e.getY());
     popup.show(this, ex, ey);
    }
  }

public void ActionPerformed(JMenuItem mi,String op,String input)
  { super.ActionPerformed(mi,op,input);
    String option=mi.getText();

    if(option.equals(Config.getString("WaterTank.vhsensor")))
     {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
  	if(al==null || al.size()==0)
  	 {
  	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
  	  return;
  	 }
     DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("WaterTank.vhsensor"),false);
     dbDialog.pack();
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
     dbDialog.setVisible(true);
     EDevice ed=(EDevice) dbDialog.getObject();
     cedvh=ed.ced;
     cedvh.esb=this;
 //    electriclistener.repaint();
    }
    else if(option.equals(Config.getString("WaterTank.hsensor")))
     {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
  	if(al==null || al.size()==0)
  	 {
  	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
  	  return;
  	 }
  	DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("WaterTank.hsensor"),false);
     dbDialog.pack();
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
     dbDialog.setVisible(true);
     EDevice ed=(EDevice) dbDialog.getObject();
     cedh=ed.ced;
     cedh.esb=this;
 //    electriclistener.repaint();
   }
    else if(option.equals(Config.getString("WaterTank.lsensor")))
    {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
 	if(al==null || al.size()==0)
 	 {
 	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
 	  return;
 	 }
 	DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("WaterTank.lsensor"),false);
    dbDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
    dbDialog.setVisible(true);
    EDevice ed=(EDevice) dbDialog.getObject();
    cedl=ed.ced;
    cedl.esb=this;
 //   electriclistener.repaint();
  }
    else if(option.equals(Config.getString("WaterTank.vlsensor")))
    {ArrayList al=electriclistener.getEArrays().getEDeviceArray(CEDevice.TYPE_SENSOR);
 	if(al==null || al.size()==0)
 	 {
 	  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("PumpMonitor.nosensor"));
 	  return;
 	 }
 	DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),al,Config.getString("WaterTank.vlsensor"),false);
    dbDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
    dbDialog.setVisible(true);
    EDevice ed=(EDevice) dbDialog.getObject();
    cedvl=ed.ced;
    cedvl.esb=this;
  //  electriclistener.repaint();
  }
}


 }
