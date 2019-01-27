package tw.com.justiot.sequencecontrol.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import tw.com.justiot.sequencecontrol.EArrays;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Position;
import tw.com.justiot.sequencecontrol.pelement.Actuator;
import tw.com.justiot.sequencecontrol.pelement.Delay;
import tw.com.justiot.sequencecontrol.pelement.EValve;
import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class ESystemPanel extends JPanel
//MouseListener,MouseMotionListener,KeyListener,ActionListener,ESystemBaseListener
 {private Component[] comps;
  public boolean started=false;
  private ElectricListener electriclistener;
  private Position curPos,initPos;
  public ESystemPanel(ElectricListener electriclistener)
   {super();
   this.electriclistener=electriclistener;
	setLayout(null);
  //  setLayout(new FlowLayout());
	setBackground(Color.white);

//	addMouseListener(this);
//	addMouseMotionListener(this);
//	addKeyListener(this);
   }

  public void open(String line) throws Exception   // for SCCAD.Data_ElectricFace
  { StringTokenizer token=new StringTokenizer(line);
    int dtype=Integer.parseInt(token.nextToken());
//    int dtype=Integer.parseInt(line.substring(0,1));
    String newline=line.substring(2);
    StringTokenizer token2=new StringTokenizer(newline);
    dtype=Integer.parseInt(token2.nextToken());
    String mType,mName,name;
    switch(dtype)
     {case SCCAD.Data_Element:
    	mType=token2.nextToken();
     	mName=token2.nextToken();
     	name=token2.nextToken();
     	EValve ev=null;
    	break;
      case SCCAD.Data_ESystemBase:
    	ESystemBase esb=ESystemBase.read(newline, electriclistener);
    	if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION && electriclistener.getTimer()!=null) esb.startTimer(electriclistener.getTimer());
    	add(esb);
    	electriclistener.getEArrays().ElectricFaceArray.add((ESystemBase) esb);
    	repaint();
        break;
      case SCCAD.Data_Water:
    	mType=token2.nextToken();
    	mName=token2.nextToken();
//if(WebLadderCAD.debug) System.out.println("mName="+mName);
    	if(!mType.equals("Water")) return;
    	if(mName.equals("PumpMonitor"))
    	 {PumpMonitor pm=(PumpMonitor) PumpMonitor.read(newline, electriclistener);
    	  electriclistener.getEArrays().ElectricFaceArray.add(pm);
  	      add(pm);
  	      repaint();
    	 }
    	else if(mName.equals("WaterTank"))
   	     {WaterTank wt=(WaterTank) WaterTank.read(newline, electriclistener);
//if(WebLadderCAD.debug) System.out.println("ESystemPanel open");
   	      electriclistener.getEArrays().ElectricFaceArray.add(wt);
//if(WebLadderCAD.debug) System.out.println("ESystemPanel open");
   	      add(wt);
//if(WebLadderCAD.debug) System.out.println("ESystemPanel open");
   	      repaint();
	     }
        break;
     }
  }

  public String writeWater()
  {Component[] comps=getComponents();
   if(comps.length<=0) return "";
   StringBuffer sb=new StringBuffer();
   ESystemBase esb=null;
   for(int i=0;i<comps.length;i++)
    {
     esb=(ESystemBase) comps[i];
     if(esb.cdo!=null && esb.cdo.modelType.equals("ESystem")) continue;
     sb.append(esb.write()+"\n");
    }
   return sb.toString();
  }
  public String writeElectricFace()
  {StringBuffer sb=new StringBuffer();
   Object obj;
   
   for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
    {obj=electriclistener.getEArrays().ElectricFaceArray.get(i);
     if(obj instanceof WaterTank)
	   sb.append(SCCAD.Data_ElectricFace+" "+((WaterTank) obj).write()+"\n");
     else if(obj instanceof PumpMonitor)
	   sb.append(SCCAD.Data_ElectricFace+" "+((PumpMonitor) obj).write()+"\n");
     else if(obj instanceof EValve)
  	   continue;
     else if(obj instanceof ESystem)
    	   sb.append(SCCAD.Data_ElectricFace+" "+((ESystem) obj).write()+"\n");
     else
      sb.append(SCCAD.Data_ElectricFace+" "+((ESystemBase) obj).write()+"\n");
    }
   return sb.toString();
  }
  public String writeESystem()
   {Component[] comps=getComponents();
    if(comps.length<=0) return "";
    StringBuffer sb=new StringBuffer();
    ESystemBase esb=null;
    for(int i=0;i<comps.length;i++)
     {if(!(comps[i] instanceof ESystemBase)) continue;
      esb=(ESystemBase) comps[i];
      if(esb.cdo==null || !esb.cdo.modelType.equals("ESystem")) continue;
      sb.append(esb.write()+"\n");
     }
    return sb.toString();
   }

  public void startTimer(java.util.Timer timer)
   {comps=getComponents();
    for(int i=0;i<comps.length;i++)
     {if(comps[i] instanceof ESystem) ((ESystem) comps[i]).startTimer(timer);
      else if(comps[i] instanceof ESystemBase) ((ESystemBase) comps[i]).startTimer(timer);
     }
   }

  int minx,miny,maxx,maxy;
  private Dimension area=new Dimension(0,0);
  private void rescale()
   {Component[] comps=getComponents();
    int x,y,w,h;
	for(int i=0;i<comps.length;i++)
	 {x=comps[i].getX();
	  y=comps[i].getY();
	  w=comps[i].getSize().width;
	  h=comps[i].getSize().height;
	  if(minx>x) minx=x;
	  if(miny>y) miny=y;
	  if(maxx<x+w) maxx=x+w;
	  if(maxy<y+h) maxy=y+h;
	 }
	scrollRectToVisible(new Rectangle(minx,miny,maxx,maxy));
	area.width=maxx;
	area.height=maxy;
	setPreferredSize(area);
    revalidate();
   }
  public void paintComponent(Graphics g)
   {super.paintComponent(g);
    rescale();
   }



//	-------------------------------------------------------------------
//	 elementListener
	//
/*
	   public Component[] getPneumaticPanelComponents()  {return circuitListener.getPneumaticPanelComponents();}
	   public void AddCell(int type,int state,CEDevice ed,CDOutput cdo,boolean ShiftEnabled)
	    {circuitListener.AddCell(type,state,ed,cdo,ShiftEnabled);}
	   public void setModified(boolean b)
	    {circuitListener.setModified(b);}
	   public void AddCell(int type,ESystemBase sys, EDevice ed,int direct, boolean ShiftEnabled)
	    {circuitListener.AddCell(type,sys,ed,direct,ShiftEnabled);}
*/
	/*
	   public ArrayList getTempElectricFaceArray()
	    {return circuitListener.getTempElectricFaceArray();}
	*/
 }
