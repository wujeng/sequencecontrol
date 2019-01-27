package tw.com.justiot.sequencecontrol.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.Creater;
import tw.com.justiot.sequencecontrol.EArrays;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESystem;
import tw.com.justiot.sequencecontrol.eelement.ElectricFace;
import tw.com.justiot.sequencecontrol.part.BoardElement;
import tw.com.justiot.sequencecontrol.part.BoardLine;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;
import tw.com.justiot.sequencecontrol.pelement.Actuator;
import tw.com.justiot.sequencecontrol.pelement.Connector;
import tw.com.justiot.sequencecontrol.pelement.Delay;
import tw.com.justiot.sequencecontrol.pelement.EValve;
import tw.com.justiot.sequencecontrol.pelement.FlowValve;
import tw.com.justiot.sequencecontrol.pelement.Gauge;
import tw.com.justiot.sequencecontrol.pelement.Logic;
import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;
import tw.com.justiot.sequencecontrol.pelement.PressureValve;
import tw.com.justiot.sequencecontrol.pelement.Valve;

public class PneumaticPanel extends JPanel //implements MouseListener,MouseMotionListener,KeyListener,ActionListener
 {private boolean mouseoff=true;
  
  public static final int Command_selectLine=1;
  public static final int Command_moveGroup=2;
  public static final int Command_group=3;
  public static final int Command_pasteBoardTo=4;
  public static final int Command_copyToBoard=5;
  public static final int Command_deleteLine=6;
  public static final int Command_changeFlowInc=7;
  public static final int Command_deleteGroup=8;
  public static final int Command_delete=9;
  public static final int Command_addLine=10;
  public static final int Command_portClicked=11;
  public static final int Command_moveElement=12;

  private static final int TimerPeriod=200;
  private static final int iterationNo=10; //  // iteration must be large enough to correctly simulate.
  public java.util.Timer timer=new java.util.Timer();

  private PneumaticListener pneumaticlistener;
  private ElectricListener electriclistener;
  public ArrayList tempElementArray=new ArrayList();
  public ArrayList lineArray;
  public ArrayList groupArray;
  public ArrayList boardArray;
  public ArrayList boardLineArray;
  public File file;

  public Line line;
  public Line selectedLine;
  public boolean started=false;
  private boolean groupping=false;
  public boolean groupMove=false;
  private Position gmovePos;
  private Position gmovePosf1=new Position(0,0),gmovePosf2=new Position(0,0);
  private Position curPos,initPos;
  private JPopupMenu popup;
  private JMenuItem m;

  private ArrayList<Port> markportarray=new ArrayList<Port>();
  
  public void setActuatorPosAccording2LS()
   {Component[] compls=getComponents();
    for(int i=0;i<compls.length;i++)
     {if(compls[i] instanceof Actuator)
      ((Actuator) compls[i]).setPosAccording2LS();
     }
   }
  public ArrayList getLimitSwitchArray()
  {ArrayList al=new ArrayList();
   Component[] comps=getComponents();
   Valve v=null;
   for(int i=0;i<comps.length;i++)
    {
	 if(comps[i] instanceof Valve) {
       v=(Valve) comps[i];
       if(v.forceType == Valve.FORCE_MECHANIC) al.add(v);
     }
    }
	return al;
  }

  public PneumaticPanel(PneumaticListener pneumaticlistener, ElectricListener electriclistener)
   {super();
    this.pneumaticlistener=pneumaticlistener;
    this.electriclistener=electriclistener;
    setLayout(null);
    setBackground(Color.white);
    lineArray=new ArrayList();
    groupArray=new ArrayList();
    boardArray=new ArrayList();
    boardLineArray=new ArrayList();
    started=false;

 //   addMouseListener(this);
  //  addMouseMotionListener(this);
//    addKeyListener(this);

    popup = new JPopupMenu();
    curPos=new Position();
    initPos=new Position();
    gmovePos=new Position();

    startTimer();
   }

   public void forceClear()
    {lineArray.clear();
     removeAll();
    }
   public boolean empty() {
 	Component[] comps=getComponents();
 	if(comps.length>0) return false;
 	else return true;
   }

   public void reset()
    {Component[] comps=getComponents();
     for(int i=0;i<comps.length;i++)
      {//if(comps[i] instanceof ESystem) continue;
       ((PneumaticElement) comps[i]).reset();
      }
     for(int i=0;i<lineArray.size();i++) ((Line) lineArray.get(i)).reset();
    }

   public void stopTimer()
   {
    if(timer==null) return;
    timer.cancel();
    timer=null;
   }
   
   public void startTimer()
    {
 	if(timer==null) timer=new java.util.Timer();
 	timer.scheduleAtFixedRate(new CheckThread(), 0, TimerPeriod);
    } 
  
    class CheckThread extends TimerTask
     {public void run()
       {
        try
         {if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION && electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) return;
      	mark();
      	for(int k=0;k<PneumaticPanel.iterationNo;k++) {
            Component[] checkcomponents=getComponents();
            for(int i=0;i<checkcomponents.length;i++) ((PneumaticElement) checkcomponents[i]).check();
            Line.flowing=false;
            for(int i=0;i<lineArray.size();i++) ((Line) lineArray.get(i)).check();
            if(Line.flowing) {
          	  Component[] cps=getComponents();
          	  PneumaticElement pe=null;
                for(int i=0;i<cps.length;i++)
                 {pe=(PneumaticElement) cps[i];
                  if(pe.getModelName().equals("Source")) {
                  	pe.ports[0].setFlowrate(Port.sourceFlowrate);
                  }
                 }
            }
      	}
      	repaint();
         }
        catch (Exception e)
         { System.out.println("CheckThread error:"+e.getMessage());
  	   }
      }
    }

    public void mark()
     {
  	clearMarks();
      markportarray.clear();
      markDrains(getSinks());
  //    markportarray.clear();
  //	markPowers(getSources());
  	markportarray.clear();
      markStops(getStops());
     }

    private void clearMarks() {
  	Component[] cs=getComponents();
  	PneumaticElement pe=null;
  	Port p=null;
  	for(int i=0;i<cs.length;i++) {
  	  pe=(PneumaticElement) cs[i];
  	  for(int j=0;j<pe.ports.length;j++) {
  		p=pe.ports[j];
  		if(p==null) continue;
  		p.setIsDrain(false);
  		p.setIsPower(false);
  		p.setIsStop(false);
  //		p.setPressure(Port.unknownPressure);
  //		p.setFlowrate(Port.unknownFlowrate);
  	  }
  	}
  	for(int i=0;i<cs.length;i++) {
  		  pe=(PneumaticElement) cs[i];
  		  if(pe!=null) pe.mark();
  		}
  	for(int i=0;i<lineArray.size();i++) ((Line) lineArray.get(i)).mark();
    }


    private Port[] getSources() {
    	ArrayList<Port> al=new ArrayList<Port>();
    	Component[] cs=getComponents();
    	PneumaticElement pe=null;
    	Port p=null;
    	for(int i=0;i<cs.length;i++) {
          pe=(PneumaticElement) cs[i];
          if(!pe.modelName.equals("Source")) continue;
    	  for(int j=0;j<pe.ports.length;j++) {
    		p=pe.ports[j];
    		if(p==null) continue;
    		p.setIsPower(true);
    		al.add(p);
    	  }
    	}
    	return al.toArray(new Port[0]);
      }

      private Port[] getSinks() {
        ArrayList<Port> al=new ArrayList<Port>();
    	Component[] cs=getComponents();
    	PneumaticElement pe=null;
    	Port p=null;
    	for(int i=0;i<cs.length;i++) {
    	  pe=(PneumaticElement) cs[i];
    	  if(!pe.modelName.equals("Sink")) continue;
          for(int j=0;j<pe.ports.length;j++) {
    	    p=pe.ports[j];
    	    if(p==null) continue;
    	    p.setIsDrain(true);
    		al.add(p);
    	  }
    	}
    	for(int i=0;i<cs.length;i++) {
    	  pe=(PneumaticElement) cs[i];
    	  for(int j=0;j<pe.ports.length;j++) {
    	    p=pe.ports[j];
    	    if(p==null) continue;
    		if(!p.getConnected()) {
    		  p.setIsDrain(true);
    		  al.add(p);
    		}
    	  }
    	}
    	return al.toArray(new Port[0]);
      }

      private Port[] getStops() {
    	    ArrayList<Port> al=new ArrayList<Port>();
    		Component[] cs=getComponents();
    		if(cs==null || cs.length==0) return al.toArray(new Port[0]);
    		PneumaticElement pe=null;
    		Port p=null;
    		for(int i=0;i<cs.length;i++) {
    		  pe=(PneumaticElement) cs[i];
    		  if(!pe.modelName.equals("Stop")) continue;
    	      for(int j=0;j<pe.ports.length;j++) {
    		    p=pe.ports[j];
    		    if(p==null) continue;
    		    p.setIsStop(true);
    	//	    p.setQCapacity(0.0f);
    			al.add(p);
    		  }
    		}
    		for(int i=0;i<cs.length;i++) {
    		  pe=(PneumaticElement) cs[i];
    		  for(int j=0;j<pe.ports.length;j++) {
    		    p=pe.ports[j];
    		    if(p==null) continue;
    		    if(!p.getIsStop()) continue;
    //		    p.setIsStop(true);
    			//	    p.setQCapacity(0.0f);
    					al.add(p);
    	//		if(p.getQCapacity()==0.0f) {
    	//		  p.setIsStop(true);
    	//		  p.setQCapacity(0.0f);
    	//		  al.add(p);
    	//		}
    		  }
    		}
    		return al.toArray(new Port[0]);
    	  }


      private void markDrains(Port[] pts) {
       if(pts==null) return;
       for(int i=0;i<pts.length;i++)
        {if(pts[i]==null) continue;
         pts[i].setIsDrain(true);
         pts[i].setIsPower(false);
     //    pts[i].setIsStop(false);
         markportarray.add(pts[i]);
         markDrains(nextDrainPorts(pts[i]));
        }
      }
      private boolean inMarkPortArray(Port p) {
    	for(int i=0;i<markportarray.size();i++) {
    	  if(markportarray.get(i)==p) return true;
    	}
    	return false;
      }

      private Line getLine(Port p) {
    	  Line line=null;
    	  for(int i=0;i<lineArray.size();i++)
    	    {line=(Line) lineArray.get(i);
    	     if(line.port1==p) return line;
    	     if(line.port2==p) return line;
    	    }
    	  return null;
      }

      private Port[] nextDrainPorts(Port pt)
      {ArrayList<Port> al=new ArrayList<Port>();
       if(pt.getConnected()) {
    	 Line line=getLine(pt);
    	 Port ap=line.anotherPort(pt);
    	 if(!inMarkPortArray(ap)) {
    	   if(ap.getOwner().modelName.equals("Actuator")) {
    		 ap.setIsDrain(true);
    	     ap.setIsPower(false);
    		 return null;
    	   }
    	   al.add(ap);
    	   return al.toArray(new Port[0]);
    	 }
       }
       PneumaticElement pe=pt.getOwner();
       if(pe!=null) {
    	   if(pe.modelName.equals("Actuator")) {
    			 pt.setIsDrain(true);
    			 return null;
    		   }
    	 Port[] ps=pe.nextDrainPorts(pt);
    	 if(ps==null || ps.length==0) return null;
    	 for(int i=0;i<ps.length;i++) {
    	   if(!inMarkPortArray(ps[i])) {
    		 al.add(ps[i]);
    	   }
    	 }
    	 return al.toArray(new Port[0]);
       }
       else return null;
      }

      private void markPowers(Port[] pts) {
    	   if(pts==null) return;
    	   for(int i=0;i<pts.length;i++)
    	    {if(pts[i]==null) continue;
    	     if(pts[i].getIsDrain()) continue;
    	     pts[i].setIsPower(true);
    	     markportarray.add(pts[i]);
    	     markPowers(nextPowerPorts(pts[i]));
    	    }
    	  }
    	  private Port[] nextPowerPorts(Port pt)
    	  {ArrayList<Port> al=new ArrayList<Port>();
    	   if(pt.getConnected()) {
    		 Line line=getLine(pt);
    		 Port ap=line.anotherPort(pt);
    		 if(!inMarkPortArray(ap)) {
    		   al.add(ap);
    		   return al.toArray(new Port[0]);
    		 }
    	   }
    	   PneumaticElement pe=pt.getOwner();
    	   if(pe!=null) {
    			 Port[] ps=pe.nextPowerPorts(pt);
    			 if(ps==null || ps.length==0) return null;
    			 for(int i=0;i<ps.length;i++) {
    			   if(!inMarkPortArray(ps[i])) {
    				 al.add(ps[i]);
    			   }
    			 }
    			 return al.toArray(new Port[0]);
    		   }
    		   else return null;
    	  }
    	  private void markStops(Port[] pts) {

    		   if(pts==null || pts.length==0) return;
    		   for(int i=0;i<pts.length;i++)
    		    {if(pts[i]==null) continue;
    		 //    pts[i].setIsStop(false);
    		//     if(pts[i].getIsDrain()) continue;
    		//     if(pts[i].getIsPower()) continue;
    		     pts[i].setIsStop(true);
    //		     pts[i].setQCapacity(0.0f);
    		//     pts[i].setQCapacity(0.0f);  // clear
    		     markportarray.add(pts[i]);
    		     markStops(nextStopPorts(pts[i]));
    		    }
    		  }
    		  private Port[] nextStopPorts(Port pt)
    		  {
    			  ArrayList<Port> al=new ArrayList<Port>();
    		   if(pt.getConnected() && getLine(pt)!=null) {
    			 Line line=getLine(pt);
    			 Port ap=line.anotherPort(pt);
    			 if(!inMarkPortArray(ap)) {
    			   al.add(ap);
    			   return al.toArray(new Port[0]);
    			 }
    		   }
    		   PneumaticElement pe=pt.getOwner();
    		   if(pe!=null) {
    				 Port[] ps=pe.nextStopPorts(pt);
    				 if(ps==null || ps.length==0) return null;
    				 for(int i=0;i<ps.length;i++) {
    				   if(ps[i]==null) continue;
    				   if(!inMarkPortArray(ps[i])) {
    					 al.add(ps[i]);
    				   }
    				 }
    				 return al.toArray(new Port[0]);
    			   }
    			   else return null;

    		  }

      /*
      public void markDrains()
       {markcomponents=getComponents();
        drainList.clear();
        for(int i=0;i<markcomponents.length;i++)
         {//if(markcomponents[i] instanceof ESystem) continue;
          elementmark=(PneumaticElement) markcomponents[i];
          for(int j=0;j<elementmark.ports.length;j++)
           {if(elementmark.ports[j]!=null)
             {elementmark.ports[j].setIsDrain(false);
     //         if(elementmark.ports[j].getIsDrain())
               {if(elementmark.ports[j].getConnected())
                 {drainList.add(elementmark.ports[j]);
                  elementmark.ports[j].setIsDrain(true);
                 }
               }
              else if(!elementmark.ports[j].getConnected() && !elementmark.ports[j].getIsSource())
               {elementmark.ports[j].setIsDrain(true);
                drainPorts=elementmark.nextDrainPorts(elementmark.ports[j]);
                if(drainPorts!=null)
                 {for(int k=0;k<drainPorts.length;k++)
                   {drainList.add(drainPorts[k]);
                    drainPorts[k].setIsDrain(true);
                   }
                 }
                drainPorts=null;
               }
             }
           }
         }
        if(drainList.size()>0)
         {drainPorts=new Port[drainList.size()];
          for(int i=0;i<drainList.size();i++)
           drainPorts[i]=(Port) drainList.get(i);
          markDrain(drainPorts);
         }
       }

      public void markDrain(Port[] pts)
       {if(pts==null) return;
        for(int i=0;i<pts.length;i++)
         {if(pts[i]==null) continue;
          markDrain(nextDrainPorts(pts[i]));
         }
       }

      private Port[] nextDrainPorts(Port pt)
       {Port ptemp=null;
        for(int i=0;i<lineArray.size();i++)
         {ptemp=((Line) lineArray.get(i)).anotherPort(pt);
          if(ptemp!=null)
           {ptemp.setIsDrain(true);
            return ptemp.getOwner().nextDrainPorts(ptemp);
           }
         }
        return null;
       }
    */
      /*
      public void markPowers()
       {markcomponents=getComponents();
        powerList.clear();
        for(int i=0;i<markcomponents.length;i++)
         {//if(markcomponents[i] instanceof ESystem) continue;
          elementmark=(PneumaticElement) markcomponents[i];
          for(int j=0;j<elementmark.ports.length;j++)
           {if(elementmark.ports[j]!=null)
             {elementmark.ports[j].setIsPower(false);
              //elementmark.ports[j].setPCapacity(0.0f);
              if(!(elementmark instanceof Actuator)) elementmark.ports[j].setQCapacity(Float.MAX_VALUE);
              if(elementmark.ports[j].getIsSource() && elementmark.ports[j].getConnected())
               {//elementmark.ports[j].setPCapacity(Port.sourcePressure);
                elementmark.ports[j].setQCapacity(Port.sourceFlowrate);
                elementmark.ports[j].setIsPower(true);
                powerList.add(elementmark.ports[j]);
               }
             }
           }
         }
        if(powerList.size()>0)
         {powerPorts=new Port[powerList.size()];
          for(int i=0;i<powerList.size();i++)
           powerPorts[i]=(Port) powerList.get(i);
          markPower(powerPorts);
         }
       }

      public void markPower(Port[] pts)
       {if(pts==null) return;
        for(int i=0;i<pts.length;i++)
         {if(pts[i]==null) continue;
          markPower(nextPowerPorts(pts[i]));
         }
       }

      private Port[] nextPowerPorts(Port pt)
       {Port ptemp=null;
        Line lin=null;
        for(int i=0;i<lineArray.size();i++)
         {lin=(Line) lineArray.get(i);
          ptemp=lin.anotherPort(pt);
          if(ptemp!=null)
           {ptemp.setIsPower(true);
            //ptemp.setPCapacity(pt.getPCapacity());
            if(!(ptemp.getOwner() instanceof Actuator)) ptemp.setQCapacity(pt.getQCapacity());
            return ptemp.getOwner().nextPowerPorts(ptemp);
           }
         }
        return null;
       }
       */

  public Line removeLineAt(Port port)
   {Line line=null;
    for(int i=0;i<lineArray.size();i++)
     {line= (Line)lineArray.get(i);
      if(line.hasPort(port))
       {lineArray.remove(line);
        line.delete();
        return line;
       }
     }
    return null;
   }

  public int getLSPosition(Object obj)
   {Component[] comps=getComponents();
    ESystem esys=null;
    Actuator act=null;
    for(int i=0;i<comps.length;i++)
     {if(comps[i] instanceof Actuator)
        {act=(Actuator) comps[i];
         if(act.hasValve(obj)) return act.getLSPosition(obj);
        }
  //     if(comps[i] instanceof ESystem)
  //      {esys=(ESystem) comps[i];
  //        if(esys.hasValve(obj)) return esys.getLSPosition(obj);
  //      }
     }
    return -1;
  }

 public int getActuatorCount()
   {int count=0;
     Component[] comps=getComponents();
     for(int i=0;i<comps.length;i++)
      {if(comps[i] instanceof Actuator) count++;}
     return count;
   }

 public int getESystemCount()
   {int count=0;
     Component[] comps=getComponents();
     for(int i=0;i<comps.length;i++)
      {if(comps[i] instanceof ESystem) count++;}
     return count;
   }
  public int getConveyerCount()
   {int count=0;
     Component[] comps=getComponents();
     for(int i=0;i<comps.length;i++)
      {if(comps[i] instanceof ESystem)
        {if(((ESystem)comps[i]).cdo.modelName.equals("Conveyer"))count++;
        }
      }
     return count;
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

    Graphics2D g2=(Graphics2D) g;
    Font oldFont=g2.getFont();
    Font newFont=new Font(oldFont.getName(),Font.PLAIN,9);
    Color oldColor=g2.getColor();

    for(int i=0;i<lineArray.size();i++)
     ((Line)lineArray.get(i)).draw(g,false);
    if(selectedLine!=null) selectedLine.draw(g,true);
    if(started)
     {g2.setColor(Color.blue);
      g2.drawLine(initPos.x,initPos.y,curPos.x,curPos.y);
      g2.setColor(oldColor);
     }
    if(groupping)
     {g2.setColor(Color.gray);
      g2.drawLine(initPos.x,initPos.y,initPos.x,curPos.y);
      g2.drawLine(initPos.x,curPos.y,curPos.x,curPos.y);
      g2.drawLine(curPos.x,curPos.y,curPos.x,initPos.y);
      g2.drawLine(curPos.x,initPos.y,initPos.x,initPos.y);
      g2.setColor(oldColor);
     }

    g2.setFont(newFont);
    g2.setColor(Color.blue);
    Component[] paintcomponents=getComponents();
    Position pos=null;
    Valve val=null;
    int elex=0,eley=0;
    String eleName=null;
    PneumaticElement elementpaint=null;
    for(int i=0;i<paintcomponents.length;i++)
     {//if(paintcomponents[i] instanceof ESystem) continue;
      elementpaint=(PneumaticElement) paintcomponents[i];
      elex=elementpaint.getX();
      eley=elementpaint.getY();

      eleName=elementpaint.getName();
      if(eleName!=null && eleName.length()>0)
       {//g2.setFont(new Font(oldFont.getFontName(),Font.PLAIN,9));
        // g2.drawString(eleName,elex+elementpaint.getSize().width/2,eley+elementpaint.getSize().height+10);
         g2.drawString(eleName,elex,eley-2);
         //g2.setFont(newFont);
       }

      if(elementpaint instanceof Valve)
       {val=(Valve) elementpaint;
        if(val.getForceType()==Valve.FORCE_MAN && val.getActivateKey() > 0)
         {g2.setFont(new Font(null,Font.PLAIN,9));
          g2.setColor(Color.blue);
          g2.drawString(val.getKeyString(),elex-10,eley+10);
         }
       }
      for(int j=0;j<elementpaint.ports.length;j++)
       {if(elementpaint.ports[j]!=null)
         {pos=new Position(elementpaint.ports[j].getPosition().x+elex,elementpaint.ports[j].getPosition().y+eley);
          switch(elementpaint.ports[j].getDir())
           {case Port.DIR_UP: pos.y=pos.y-20; break;
            case Port.DIR_DOWN: pos.y=pos.y+20; break;
            case Port.DIR_RIGHT: pos.x=pos.x+20; break;
            case Port.DIR_LEFT: pos.x=pos.x-30; break;
           }
          /*
          if(elementpaint.ports[j].getIsPower() && !elementpaint.ports[j].getIsDrain())
           drawPressure(g2,elementpaint.ports[j].getPressure(),elementpaint.ports[j].getFlowrate(),pos.x,pos.y,true,
             elementpaint.ports[j].getQCapacity(),
             elementpaint.ports[j].getIsPower(),elementpaint.ports[j].getIsDrain(),
             elementpaint.ports[j].getIsStop());
          else
           drawPressure(g2,elementpaint.ports[j].getPressure(),elementpaint.ports[j].getFlowrate(),pos.x,pos.y,false,
             elementpaint.ports[j].getQCapacity(),
             elementpaint.ports[j].getIsPower(),elementpaint.ports[j].getIsDrain(),
             elementpaint.ports[j].getIsStop());
             */
         }
       }
     }
    rescale();
   }

  private void drawPressure(Graphics2D g2,float p, float f,int x,int y,boolean star,float PC,float QC,boolean isp, boolean isd,boolean iss)
   {
/*
    g2.setFont(newFont);
    NumberFormat nf=NumberFormat.getNumberInstance();
    nf.setMaximumFractionDigits(5);
    if(star)
     g2.drawString(nf.format(p)+"*",x,y);
    else
     g2.drawString(nf.format(p),x,y);
    g2.drawString(nf.format(f),x,y+10);
    g2.drawString(nf.format(PC),x,y+20);
    String qcstr=nf.format(QC);
    if(qcstr.length()<5) g2.drawString(qcstr,x,y+30);
    else g2.drawString(qcstr.substring(0,5),x,y+30);
    if(isp) g2.drawString("p:y",x,y+40);
    else g2.drawString("p:n",x,y+40);
    if(isd) g2.drawString("d:y",x,y+50);
    else g2.drawString("d:n",x,y+50);
    if(iss) g2.drawString("s:y",x,y+60);
    else g2.drawString("s:n",x,y+60);
*/
   }

//-----------
  private String saveLines(Object[] objs)
  {StringBuffer sb=new StringBuffer();
   Port pt=null;
   Line lin=null;
   boolean in1=false,in2=false;
   int bi1=0,bi2=0,ei1=0,ei2=0;
   PneumaticElement elementsave=null;
   for(int i=0;i<lineArray.size();i++)
    {lin=(Line) lineArray.get(i);
     in1=false;in2=false;
     pt=lin.getPort1();
     for(int j=0;j<objs.length;j++)
      {elementsave=(PneumaticElement) objs[j];
       ei1=elementsave.portInElement(pt);
       if(ei1>=0)
        {in1=true;bi1=j;break;}
      }
     pt=lin.getPort2();
     for(int k=0;k<objs.length;k++)
      {elementsave=(PneumaticElement) objs[k];
       ei2=elementsave.portInElement(pt);
       if(ei2>=0)
        {in2=true;bi2=k;break;}
      }
     if(in1 && in2)
      {sb.append(Integer.toString(SCCAD.Data_Line)+" "+Integer.toString(bi1)+" "+Integer.toString(ei1)+" "+
         Integer.toString(bi2)+" "+Integer.toString(ei2)+"\n");}
    }
    return sb.toString();
  }

 private String saveLimitswitch(Object[] objs)
  {StringBuffer sb=new StringBuffer();
   int elei=-1;
   ArrayList valveArray=null;
   ArrayList valvePos=null;
   PneumaticElement elementsave=null;
   for(int i=0;i<objs.length;i++)
    {elementsave=(PneumaticElement) objs[i];
     if(!(elementsave instanceof Actuator)) continue;
      if(elementsave instanceof Actuator) 
       {valveArray=((Actuator) objs[i]).valveArray;
        valvePos=((Actuator) objs[i]).valvePos;
       }
      if(valveArray!=null)
       {PneumaticElement valve=null;
    	for(int j=0;j<valveArray.size();j++)
         {if(valveArray.get(j) instanceof PneumaticElement)
            {valve=(PneumaticElement) valveArray.get(j);
             elei=inElement((PneumaticElement) valveArray.get(j),objs);
             if(elei>=0) sb.append(Integer.toString(SCCAD.Data_Limitswitch)+" "+Integer.toString(i)+" "+Integer.toString(elei)+" "+
                                 Integer.toString(((Integer)valvePos.get(j)).intValue())+"\n");
             elei=-1;
            }
         }
       }
    }
    return sb.toString();
  }

 private int inElement(PneumaticElement ele, Object[] objs)
 {
  for(int i=0;i<objs.length;i++)
   if(ele==(PneumaticElement) objs[i]) return i;
  return -1;
 }

 public void getLimitswitchLocation(EDevice ed)
  {Object[] objs=getComponents();
   ArrayList valveArray=null;
   ArrayList valvePos=null;
   EDevice edt=null;
   PneumaticElement ele=null;
    for(int i=0;i<objs.length;i++)
     {ele=(PneumaticElement) objs[i];
      if(!(ele instanceof Actuator) && !(ele instanceof EValve)) continue;
      if(ele instanceof EValve)
       {valveArray=((EValve) objs[i]).getValveArray();
        valvePos=((EValve) objs[i]).getValvePos();
       }
      else if(ele instanceof Actuator)
       {valveArray=((Actuator) objs[i]).valveArray;
        valvePos=((Actuator) objs[i]).valvePos;
       }
      if(valveArray!=null)
       {for(int j=0;j<valveArray.size();j++)
        {
   	     if(valveArray.get(j) instanceof EDevice)
          {edt=(EDevice) valveArray.get(j);
           if(edt==ed)
        	{ed.ced.element=ele;
        	 ed.ced.LSpos=((Integer)valvePos.get(j)).intValue();
        	 return;
        	}
          }
        }
       }
     }
  }
/*
 public String saveELimitswitch(Object[] objs)
  {StringBuffer sb=new StringBuffer();
//System.out.println("PneumaticPanel.saveElimitswitch()");
    int elei=-1;
   ArrayList valveArray=null;
   ArrayList valvePos=null;
   PneumaticElement elementsave=null;
   for(int i=0;i<objs.length;i++)
    {elementsave=(PneumaticElement) objs[i];

      if(!(elementsave instanceof Actuator) && !(elementsave instanceof EValve)) continue;
      if(elementsave instanceof EValve)
       {valveArray=((EValve) objs[i]).getValveArray();
         valvePos=((EValve) objs[i]).getValvePos();
       }
      else if(elementsave instanceof Actuator)
       {valveArray=((Actuator) objs[i]).valveArray;
         valvePos=((Actuator) objs[i]).valvePos;
       }
      if(valveArray!=null)
       {for(int j=0;j<valveArray.size();j++)
         {
//System.out.println("saveELimit"+i+":"+j);

    	   if(valveArray.get(j) instanceof EDevice)
            {elei=electriclistener.getEArrays().getEDeviceNo(((EDevice) valveArray.get(j)).ced);
             if(elei>=0) sb.append(Integer.toString(SCCAD.Data_ELimitswitch)+" "+Integer.toString(i)+" "+Integer.toString(elei)+" "+
                                 Integer.toString(((Integer)valvePos.get(j)).intValue())+"\n");
              elei=-1;
            }
         }
       }
    }
//System.out.println(sb.toString());
    return sb.toString();
  }
*/
  public String save(Object[] objs)
   {StringBuffer sb=new StringBuffer();
    for(int i=0;i<objs.length;i++)
     {sb.append(((PneumaticElement) objs[i]).write()+"\n");
   /* 	
      if(!(objs[i] instanceof ElectricFace))
        sb.append(((PneumaticElement) objs[i]).write()+"\n");
      else
        {if(pneumaticlistener.hasElectrics())
           sb.append(((ElectricFace) objs[i]).write()+"\n");
        }
   */     
     }
    sb.append(saveLines(objs));
    sb.append(saveLimitswitch(objs));
//    sb.append(saveELimitswitch(objs));
    return sb.toString();
   }

  public String save()
   {return save(getComponents());}
  public String saveGroup()
   {return save(groupArray.toArray());}
//-------------
  public void openLine(String str)
  {StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_Line) return;
    Line lin=null;
    int e1=0,p1=0,e2=0,p2=0;
    try
     {e1=Integer.parseInt(token.nextToken());
       p1=Integer.parseInt(token.nextToken());
       e2=Integer.parseInt(token.nextToken());
       p2=Integer.parseInt(token.nextToken());
//System.err.println(e1+p1+e2+p2);
      }
     catch(Exception ie)
      {pneumaticlistener.setStatus(Config.getString("PneumaticPanel.lineformaterror"));return;}
     lin=new Line();
     PneumaticElement element=(PneumaticElement)  tempElementArray.get(e1);
     lin.addPort(element.ports[p1]);
     element=(PneumaticElement)  tempElementArray.get(e2);
     lin.addPort(element.ports[p2]);
     lineArray.add(lin);
  }
public void openLimitswitch(String str)
 {
//System.out.println("openLimitswitch");
	StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_Limitswitch) return;
    int e1=0,p1=0,e2=0;
    try
     {e1=Integer.parseInt(token.nextToken());
       p1=Integer.parseInt(token.nextToken());
       e2=Integer.parseInt(token.nextToken());
      }
     catch(Exception ie)
       {pneumaticlistener.setStatus(Config.getString("PneumaticPanel.limitswitchformaterror"));return;}
     Object obj= tempElementArray.get(e1);
     ArrayList valveArray=null;
     ArrayList valvePos=null;
     if(obj instanceof Actuator)
      {Actuator act=(Actuator) obj;
        valveArray=act.valveArray;
        valvePos=act.valvePos;
      }
     else if(obj instanceof ElectricFace)
      {ElectricFace sys=(ElectricFace) obj;
       valveArray=sys.getValveArray();
       valvePos=sys.getValvePos();
      }
     valveArray.add((Valve) tempElementArray.get(p1));
     valvePos.add(new Integer(e2));
/*
     if(valveArray!=null)
     {boolean find=false;
      Object lsobj=electriclistener.getEArrays().tempEDeviceArray.get(p1);
      for(int i=0;i<valveArray.size();i++)
       {
   	    if(lsobj==valveArray.get(i))
   	     {
   	      find=true;
   	      break;
   	     }
       }
     if(!find)
      {valveArray.add(lsobj);
       valvePos.add(new Integer(e2));
     System.out.println("openELimitswitch:"+valveArray.size());
      }
     }
*/
  }
/*
public void openELimitswitch(String str)
 {

    StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_ELimitswitch) return;
    int e1=0,p1=0,e2=0;
    try
     {e1=Integer.parseInt(token.nextToken());
       p1=Integer.parseInt(token.nextToken());
       e2=Integer.parseInt(token.nextToken());
      }
     catch(Exception ie)
       {pneumaticlistener.setStatus(Config.getString("PneumaticPanel.limitswitchformaterror"));return;}
     Object obj= tempElementArray.get(e1);
     ArrayList valveArray=null;
     ArrayList valvePos=null;

     if(obj instanceof Actuator)
      {Actuator act=(Actuator) obj;
        valveArray=act.valveArray;
        valvePos=act.valvePos;
//System.out.println(act.getName()+":"+valveArray.size());
     }
     else if(obj instanceof EValve)
     {EValve sys=(EValve) obj;
       valveArray=sys.getValveArray();
       valvePos=sys.getValvePos();
//System.out.println(sys.getCDOutput().getName()+":"+valveArray.size());
     }

     valveArray.add((EDevice) electriclistener.getEArrays().tempEDeviceArray.get(p1));
     valvePos.add(new Integer(e2));
  }
*/
  public static void clearCount()
   {Actuator.count=0;
     Connector.count=0;
     Delay.allcount=0;
//     Element.count=0;
     EValve.count=0;
     FlowValve.count=0;
     Gauge.count=0;
     Logic.count=0;
     PressureValve.count=0;
     Valve.count=0;
   }

  public void open(String line) throws Exception
   {
     int dtype=Integer.parseInt(line.substring(0,1));
     switch(dtype)
      {case SCCAD.Data_Element:
    	 PneumaticElement ele=PneumaticElement.read(line, pneumaticlistener, electriclistener);
         if(!SCCAD.checkLimit(ele.getModelType(),ele.getModelName())) return;
          tempElementArray.add(ele);
         add(ele);
         groupArray.add(ele);
         ele.setGrouped(true);
         break;
       case SCCAD.Data_Line: openLine(line);break;
//       case SCCAD.Data_Limitswitch: openLimitswitch(line);break;
//       case SCCAD.Data_ELimitswitch: openELimitswitch(line);break;
      }

   }

//------------------------------------------


  public void deGroup()
  {ArrayList oldgroup=groupArray;
   PneumaticElement elementgroup=null;
	for(int i=0;i<groupArray.size();i++)
    {elementgroup=(PneumaticElement)groupArray.get(i);
//System.err.println(element.getModelName()+":degroup");
     elementgroup.setGrouped(false);
    }
   groupArray.clear();
   repaint();
  }

  public int componentNo(PneumaticElement e)
    {Component[] comps=getComponents();
      for(int i=0;i<comps.length;i++)
       if(((PneumaticElement) comps[i])==e) return i;
      return -99;
    }

  public void mouseClicked(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseClicked(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseClicked(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
     if(left)
      {
       if(!started && !groupping && !groupMove)
        {
         Line inlin=onLines(ex,ey);
         if(inlin!=null)
          {Line oline=selectedLine;
	       selectedLine=inlin;
	       repaint();
          }
        }
      }
     }

   public void mouseEntered(MouseEvent e) {}
   public void mouseExited(MouseEvent e) {}
   public void mousePressed(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }

   private int groupmovestartx,groupmovestarty;
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
	 //System.err.println("mousepressed");
	      requestFocus();
	      if(left)
	       {if(groupMove)
	         {gmovePos.x=ex;
	          gmovePos.y=ey;
	          groupmovestartx=ex;
	          groupmovestarty=ey;
	          int maxx=0,minx=Integer.MAX_VALUE,maxy=0,miny=Integer.MAX_VALUE;
	          Point pt=null;
	          Dimension dim=null;
	          PneumaticElement ge=null;
	          for(int i=0;i<groupArray.size();i++)
	           {ge=(PneumaticElement) groupArray.get(i);
	            pt=ge.getLocation();
	            dim=ge.getSize();
	            if(pt.x<minx) minx=pt.x;
	            if(pt.y<miny) miny=pt.y;
	            if(pt.x+dim.width>maxx) maxx=pt.x+dim.width;
	            if(pt.y+dim.height>maxy) maxy=pt.y+dim.height;
	           }
	          gmovePosf1.x=minx;gmovePosf1.y=miny;
	          gmovePosf2.x=maxx;gmovePosf2.y=maxy;
	         }
	        else if(!started)
	         {deGroup();
	          if(!groupping)
	           {
	            initPos.x=ex;
	            initPos.y=ey;         
	           }
	         }
	       }
	      else
	       {if(line!=null) line.delete();
	        started=false;
//	        maybeShowPopup(pop,ex,ey);
	       }
	    }

   public void mouseReleased(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {if(groupMove)
         {ArrayList garray=new ArrayList(groupArray);
          int delx=ex-groupmovestartx;
          int dely=ey-groupmovestarty;
          boolean modified=pneumaticlistener.getModified();
          pneumaticlistener.setModified(true);
          pneumaticlistener.addCommand(new moveGroupCommand(this,modified,garray,delx,dely));

	      groupMove=false;
          deGroup();
         }
        else if(groupping)
         {ArrayList oldgroup=groupArray;
	      int x=0,y=0,w=0,h=0;
          int minx=0,miny=0,maxx=0,maxy=0;
          if(initPos.x > curPos.x)
           {minx=curPos.x;maxx=initPos.x;}
          else
           {minx=initPos.x;maxx=curPos.x;}
          if(initPos.y > curPos.y)
           {miny=curPos.y;maxy=initPos.y;}
          else
           {miny=initPos.y;maxy=curPos.y;}
          Component[] comps=getComponents();
          Component comp=null;
          for(int i=0;i<comps.length;i++)
           {comp=(Component) comps[i];
            x=comp.getX(); y=comp.getY();
            w=x+comp.getSize().width;
            h=y+comp.getSize().height;
            if((x>minx && x<maxx && y>miny && y<maxy) ||
               (w>minx && w<maxx && h>miny && h<maxy) ||
               (w>minx && w<maxx && y>miny && y<maxy) ||
               (x>minx && x<maxx && h>miny && h<maxy))
             {PneumaticElement elementpop=(PneumaticElement) comp;
              elementpop.setGrouped(true);
              groupArray.add(elementpop);
             }
           }
//          if(commandListener!=null) commandListener.add(new groupCommand(this,oldgroup,groupArray));
          groupping=false;
          repaint();
         }
       }
      else
       {//maybeShowPopup(pop,ex,ey);
       }
   }

   public void selectAll()
   {deGroup();
     Component[] comps=getComponents();
     PneumaticElement element=null;
     for(int i=0;i<comps.length;i++)
      {element=(PneumaticElement) comps[i];
        element.setGrouped(true);
        groupArray.add(element);
      }
    repaint();
   }

  private boolean overLay(Component com, int x1, int y1, int x2, int y2)
   {int cx1=com.getX(),cy1=com.getY();
    int cx2=cx1+com.getSize().width,cy2=cy1+com.getSize().height;
    if(x1>cx1 && x1<cx2 && y1>cy1 && y1<cy2) return true;
    if(x2>cx1 && x2<cx2 && y1>cy1 && y1<cy2) return true;
    if(x2>cx1 && x2<cx2 && y2>cy1 && y2<cy2) return true;
    if(x1>cx1 && x1<cx2 && y2>cy1 && y2<cy2) return true;

    if(cx1>x1 && cx1<x2 && cy1>y1 && cy1<y2) return true;
    if(cx2>x1 && cx2<x2 && cy1>y1 && cy1<y2) return true;
    if(cx2>x1 && cx2<x2 && cy2>y1 && cy2<y2) return true;
    if(cx1>x1 && cx1<x2 && cy2>y1 && cy2<y2) return true;

    return false;
   }

  public Component inComponent(int x1, int y1, int x2, int y2)
   {Component[] coms=null;
    coms=getComponents();
    Component com=null;
    for(int i=0;i<coms.length;i++)
     {com=(Component) coms[i];
      if(overLay(com, x1,y1,x2,y2)) return com;
     }
    return null;
   }

  private Line onLines(int x, int y)
   {Line lin=null;
    Line inlin=null;
    for(int i=0;i<lineArray.size();i++)
     {lin=(Line) lineArray.get(i);
      if(lin.inLine(x,y))
       {inlin=lin;
        break;
       }
     }
    return inlin;
   }
/*
  public Point popupPoint=null;
  private void maybeShowPopup(boolean pop, int ex, int ey)
   {
      if(pop)
       {popup.removeAll();
       m = new JMenuItem(Config.getString("PneumaticPanel.reset"));
       m.addActionListener(this);
       m.addMouseListener(new menuItemMouseAdapter());
       popup.add(m);
        if(lineArray.size()>0)
         {m = new JMenuItem(Config.getString("PneumaticPanel.flowvelocity"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
        }
        if(selectedLine!=null)
         {m = new JMenuItem(Config.getString("PneumaticPanel.deleteline"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
         }
        if(groupArray.size()>0)
         {if(popup.getComponentCount()>0) popup.addSeparator();
          m = new JMenuItem(Config.getString("PneumaticPanel.groupmove"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          m = new JMenuItem(Config.getString("PneumaticPanel.groupdelete"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          m = new JMenuItem(Config.getString("PneumaticPanel.groupcut"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          m = new JMenuItem(Config.getString("PneumaticPanel.groupcopy"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
         }
        if(boardArray.size()>0)
         {
          m = new JMenuItem(Config.getString("PneumaticPanel.paste"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
         }
        if(popup.getComponentCount()==0) return;
        groupping=false;
        popupPoint=new Point(ex,ey);
//        popup.show(e.getComponent(), e.getX(), e.getY());
        popup.show(this,ex,ey);
       }
   }
*/
   public void mouseDragged(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseDragged(e,left,e.getX(),e.getY(),pop);
   }

   private class moveGroupCommand extends Command
    {boolean modified;
	 ArrayList group;
     int dx;
     int dy;
	public moveGroupCommand(Object ele,boolean modified,ArrayList group,int dx,int dy)
     {super("PneumaticPanel",ele,Command_moveGroup);
      this.modified=modified;
      this.group=group;
      this.dx=dx;
      this.dy=dy;
     }
	public void undo()
    {PneumaticElement element=null;
     for(int i=0;i<group.size();i++)
      {element=(PneumaticElement) group.get(i);
       element.setLocation(new Point(element.getX()-dx,element.getY()-dy));
      }
     pneumaticlistener.setModified(modified);
     repaint();
    }
   public void redo()
    {PneumaticElement element=null;
     for(int i=0;i<group.size();i++)
      {element=(PneumaticElement) group.get(i);
       element.setLocation(new Point(element.getX()+dx,element.getY()+dy));
      }
     pneumaticlistener.setModified(true);
     repaint();
    }
   }

   public void MouseDragged(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
	      if(left)
	       {if(groupMove)
	         {
	          int delx=ex-gmovePos.x,dely=ey-gmovePos.y;
	//System.err.println(delx+" "+dely);
	          if(delx+gmovePosf1.x <0 || dely+gmovePosf1.y < 0)
	           {//gmovePos.x=e.getX();
	            //gmovePos.y=e.getY();
	            return;
	           }
	          PneumaticElement elementdrag=null;
	          for(int i=0;i<groupArray.size();i++)
	           {elementdrag=(PneumaticElement) groupArray.get(i);
	            elementdrag.setLocation(new Point(elementdrag.getX()+delx,elementdrag.getY()+dely));
	           }
	          gmovePos.x=ex;
	          gmovePos.y=ey;
	          gmovePosf1.x+=delx;
	          gmovePosf1.y+=dely;
	          gmovePosf2.x+=delx;
	          gmovePosf2.y+=dely;
//	          mark();
//	          if(gmovePosf2.x > getSize().width || gmovePosf2.y > getSize().height)
	           {
//		        setPreferredSize(new Dimension(gmovePosf2.x, gmovePosf2.y));
//	            revalidate();
//	            if(net && pneumatics.allClient.connected) pneumatics.writeEvent(new PneumaticsEvent(PneumaticsEvent.S_pneumaticPanel,"mouseDragged",e,null));
	           }
	         }
	        else
	         {curPos.x=ex;
	          curPos.y=ey;
	          groupping=true;
//	           if(net && pneumatics.allClient.connected) pneumatics.writeEvent(new PneumaticsEvent(PneumaticsEvent.S_pneumaticPanel,"mouseDragged",e,null));
	         }
	        repaint();
	       }    
	   }

   public void mouseMoved(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseMoved(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseMoved(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {if(started)
         {curPos.x=ex;
          curPos.y=ey;
          repaint();
         }
       }
    }
  public void keyTyped(KeyEvent e){}
  public void keyPressed(KeyEvent e)
    {int kcode=e.getKeyCode();
     KeyPressed(e,kcode);
    }
  public void KeyPressed(KeyEvent e,int kcode)
  {Component[] coms=getComponents();
   PneumaticElement ele=null;
   for(int i=0;i<coms.length;i++)
    {ele=(PneumaticElement) coms[i];
     if((ele instanceof Valve))
      {Valve val=(Valve) ele;
       if(val.getForceType()==Valve.FORCE_MAN && val.getActivateKey()==kcode)
        {val.setForce(0,true);    
         break;
        }
//System.err.println("keypressed");                     
      }
    }
//System.err.println(kcode);
  }
  public void keyReleased(KeyEvent e)
   {int kcode=e.getKeyCode();
    KeyReleased(e,kcode);
   }
  public void KeyReleased(KeyEvent e,int kcode)
  {
   Component[] coms=getComponents();
   PneumaticElement ele=null;
   for(int i=0;i<coms.length;i++)
    {ele=(PneumaticElement) coms[i];
     if((ele instanceof Valve))
      {Valve val=(Valve) ele;
       if(val.getForceType()==Valve.FORCE_MAN && val.getActivateKey()==kcode)
        {val.setForce(0,false);                         
         break;
        }
      }
    }
//System.err.println(kcode);
  }

   private class pasteBoardToCommand extends Command
    {boolean modified;
     ArrayList oldBoardArray;
     ArrayList oldBoardLineArray;
	 ArrayList tArray;
     ArrayList tLineArray;
	public pasteBoardToCommand(Object ele,boolean modified,ArrayList oldBoardArray,ArrayList oldBoardLineArray,ArrayList tArray,ArrayList tLineArray)
     {super("PneumaticPanel",ele,Command_pasteBoardTo);
      this.modified=modified;
      this.oldBoardArray=oldBoardArray;
      this.oldBoardLineArray=oldBoardLineArray;
      this.tArray=tArray;
      this.tLineArray=tLineArray;
     }
	public void undo()
    {for(int i=0;i<tLineArray.size();i++)
      {
       lineArray.remove(tLineArray.get(i));
      }
     PneumaticElement element=null;
     for(int i=0;i<tArray.size();i++)
      {element=(PneumaticElement) tArray.get(i);
       if(element!=null)
        {
         remove(element);
        }
      }            
     pneumaticlistener.setModified(modified);
     boardArray=oldBoardArray;
     boardLineArray=oldBoardLineArray;
     repaint();
    }
   public void redo()
    {
     PneumaticElement element=null;
     for(int i=0;i<tArray.size();i++)
      {element=(PneumaticElement) tArray.get(i);
       if(element!=null)
        {add(element);
        }
      }
     for(int i=0;i<tLineArray.size();i++)
      {
       lineArray.add(tLineArray.get(i));
      }
     pneumaticlistener.setModified(true);
     boardArray=oldBoardArray;
     boardLineArray=oldBoardLineArray;
     repaint();
    }
   }

   public void pasteBoardTo()
   {boolean modified=pneumaticlistener.getModified();
	BoardElement be=null;
    ArrayList tempArray=new ArrayList();
    ArrayList oldBoardArray=new ArrayList(boardArray);
    ArrayList oldBoardLineArray=new ArrayList(boardLineArray);
    PneumaticElement elementpaste=null;
    Object obj=null;
    for(int i=0;i<boardArray.size();i++)
     {be=(BoardElement) boardArray.get(i);
      try
       {obj=Creater.instanceElement(be.modelType,be.modelName,pneumaticlistener, null);
       
       }
      catch(Exception e)
      {pneumaticlistener.MessageBox("error", "create element error!");
       return;	  
      }
      if(obj!=null)
       {elementpaste=(PneumaticElement) obj;
//	    elementpaste.setLocation(be.location.x+popupPoint.x,be.location.y+popupPoint.y);
        tempArray.add(elementpaste);      
        add(elementpaste);
       }
     }
    BoardLine bl=null;
    Line lin=null;
    ArrayList tempLineArray=new ArrayList();
    for(int i=0;i<boardLineArray.size();i++)
     {bl=(BoardLine) boardLineArray.get(i);
     lin=new Line();
      elementpaste=(PneumaticElement) tempArray.get(bl.elementi1);
      lin.addPort(elementpaste.ports[bl.porti1]);
      elementpaste=(PneumaticElement) tempArray.get(bl.elementi2);
      lin.addPort(elementpaste.ports[bl.porti2]);
      tempLineArray.add(lin);
      lineArray.add(lin);
     }
    pneumaticlistener.setModified(true);
    pneumaticlistener.addCommand(new pasteBoardToCommand(this,modified,oldBoardArray,oldBoardLineArray,tempArray,tempLineArray));
    repaint();    
   }

   private class copyToBoardCommand extends Command
    {boolean modified;
     ArrayList bArray;
     ArrayList bLineArray;
     ArrayList obArray;
     ArrayList obLineArray;
	public copyToBoardCommand(Object ele,boolean modified,ArrayList bArray,ArrayList bLineArray,ArrayList obArray,ArrayList obLineArray)
     {super("PneumaticPanel",ele,Command_copyToBoard);
      this.modified=modified;
      this.bArray=bArray;
      this.bLineArray=bLineArray;
      this.obArray=obArray;
      this.obLineArray=obLineArray;
     }
    public void undo()
     {boardArray=new ArrayList(obArray);
      boardLineArray=new ArrayList(obLineArray);
      pneumaticlistener.setModified(modified);
     }
    public void redo()
     {boardArray=new ArrayList(bArray);
      boardLineArray=new ArrayList(bLineArray);
      pneumaticlistener.setModified(true);
     }
   }

   public void copyToBoard()
   {boolean modified=pneumaticlistener.getModified();
	ArrayList oldbArray=new ArrayList(boardArray);
    ArrayList oldbLineArray=new ArrayList(boardLineArray);
	boardArray.clear();
    boardLineArray.clear();
    BoardElement be=null;
    PneumaticElement elementboard=null;
    for(int i=0;i<groupArray.size();i++)
     {elementboard=(PneumaticElement) groupArray.get(i);
      be=elementboard.getBoardElement();
      be.location.x=be.location.x-initPos.x;
      be.location.y=be.location.y-initPos.y;
      boardArray.add(be);
     }
    Port pt=null;
    Line lin=null;
    boolean in1=false,in2=false;
    int bi1=0,bi2=0,ei1=0,ei2=0;
    BoardLine bl=null;
    for(int i=0;i<lineArray.size();i++)
     {lin=(Line) lineArray.get(i);
      in1=false;in2=false;
      pt=lin.getPort1();
      for(int j=0;j<groupArray.size();j++)
       {elementboard=(PneumaticElement) groupArray.get(j);
        ei1=elementboard.portInElement(pt);
        if(ei1>=0)
         {in1=true;bi1=j;break;}
       }
      pt=lin.getPort2();
      for(int k=0;k<groupArray.size();k++)
       {elementboard=(PneumaticElement) groupArray.get(k);
        ei2=elementboard.portInElement(pt);
        if(ei2>=0)
         {in2=true;bi2=k;break;}
       }
      if(in1 && in2)
       {boardLineArray.add(new BoardLine(bi1,ei1,bi2,ei2));}
     }
    pneumaticlistener.setModified(true);
    pneumaticlistener.addCommand(new copyToBoardCommand(this,modified,new ArrayList(boardArray),new ArrayList(boardLineArray),oldbArray,oldbLineArray));    
   }

  public void switchItem(int n)
    {JMenuItem mi=(JMenuItem) popup.getComponent(n);
      Component[] coms=popup.getComponents();
//System.err.println("switchitem"+coms.length);
       for(int i=0;i<coms.length;i++)
         {if(coms[i]==mi) mi.setArmed(true);
          else
            {if(coms[i] instanceof JMenuItem)
                ((JMenuItem) coms[i]).setArmed(false);
            }
         }
    }
/*
  private void removeESymbol(Port[] port)
   {Port tempport=null;
     Object obj=null;
     Port[] pts=null;
     if(port==null) return;
     if(port.length==0) return;
     for(int i=0;i<port.length;i++)
      {if(port[i]==null) continue;
        tempport=port[i];
        obj=tempport.getOwner();
        while(true)
         {if(obj instanceof ESystemBase)
            {pneumatics.electrics.edevicePanel.remove(esymbol);break;}
           pts=((Element) obj).nextPorts(tempport);
           removeESymbol(pts);
         }
      }
   }
*/
  public void actionPerformed(ActionEvent e){
         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }

  private class deleteLineCommand extends Command
    {boolean modified;
	 Line sline;
     Line line;
	public deleteLineCommand(Object ele,boolean modified,Line sline,Line line)
     {super("PneumaticPanel",ele,Command_deleteLine);
      this.modified=modified;
      this.sline=sline;
      this.line=line;
     }
    public void undo()
     {sline.undelete();
      lineArray.add(sline);
      selectedLine=sline;
      if(line!=null) line.undelete();
      pneumaticlistener.setModified(modified);
      repaint();
     }
    public void redo()
     {sline.delete();
      lineArray.remove(sline);
      selectedLine=null;
      if(line!=null) line.delete();
      started=false;
      pneumaticlistener.setModified(true);
      repaint();
     }
   }

  private class changeFlowIncCommand extends Command
  {boolean modified0;
	 int oldinc;
   int newinc;
	public changeFlowIncCommand(Object ele,boolean modified0,int oldinc,int newinc)
   {super("PneumaticPanel",ele,Command_changeFlowInc);
    this.modified0=modified0;
    this.oldinc=oldinc;
    this.newinc=newinc;
   }
  public void undo()
   {Line.defaultlineinc=oldinc;
    pneumaticlistener.setModified(modified0);
   }
  public void redo()
   {Line.defaultlineinc=newinc;
    pneumaticlistener.setModified(true);
   }
 }   

  private class deleteGroupCommand extends Command
    {boolean modified;
	 ArrayList group;
     ArrayList lines;
	public deleteGroupCommand(Object ele,boolean modified,ArrayList group,ArrayList lines)
     {super("PneumaticPanel",ele,Command_deleteGroup);
      this.modified=modified;
      this.group=group;
      this.lines=lines;
     }
	public void undo()
    {PneumaticElement element=null;
     for(int i=0;i<group.size();i++)
      {element=(PneumaticElement) group.get(i);
       add(element);
      }
     Line line=null;
     for(int i=0;i<lines.size();i++)
      {line=(Line) lines.get(i);
       line.undelete();
       lineArray.add(line);
      }
     pneumaticlistener.setModified(modified);
     repaint();
    }
   public void redo()
    {Line line=null;
     for(int i=0;i<lines.size();i++)
      {line=(Line) lines.get(i);
       line.delete();
       lineArray.remove(line);
      }
	  PneumaticElement element=null;
     for(int i=0;i<group.size();i++)
      {element=(PneumaticElement) group.get(i);
       remove(element);
      }
     pneumaticlistener.setModified(true);
     repaint();
    }
   }

  public void ActionPerformed(JMenuItem mi,String op,String input)
    {
        String option=mi.getText();
        if(option.equals(Config.getString("PneumaticPanel.deleteline")))
         {if(selectedLine!=null)
           {boolean modified=pneumaticlistener.getModified();
	        Line sline=selectedLine;
            Line templine=line;
            selectedLine.delete();
            lineArray.remove(selectedLine);
            selectedLine=null;
            if(line!=null) line.delete();
            started=false;
            repaint();
            pneumaticlistener.setModified(true);
            pneumaticlistener.addCommand(new deleteLineCommand(this,modified,sline,line));
            mark();
           }
         }
        else if(option.equals(Config.getString("PneumaticPanel.flowvelocity")))
         {    boolean modified0=pneumaticlistener.getModified();
	          int lineinc=Line.defaultlineinc;
	          CustomDialog customDialog = new CustomDialog(new JFrame(),Config.getString("PneumaticPanel.flowvelocity"),Config.getString("PneumaticPanel.changeflowvelocity"),CustomDialog.VALUE_INT);
              customDialog.pack();
              Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
              customDialog.setTextField(Integer.toString(Line.defaultlineinc));
              customDialog.setVisible(true);
              Line.defaultlineinc=customDialog.getInt();
              pneumaticlistener.setModified(true);
              pneumaticlistener.addCommand(new changeFlowIncCommand(this,modified0,lineinc,Line.defaultlineinc));
          }
        else if(option.equals(Config.getString("PneumaticPanel.groupdelete")))
         {groupDelete();
         }
        else if(option.equals(Config.getString("PneumaticPanel.groupmove")))
         {
//System.err.println("group move");
          groupMove=true;
         }
        else if(option.equals(Config.getString("PneumaticPanel.groupcut")))
         {groupCut();
         }
        else if(option.equals(Config.getString("PneumaticPanel.groupcopy")))
         {copyToBoard();
         }
        else if(option.equals(Config.getString("PneumaticPanel.paste")))
         {pasteBoardTo();
         }
     }

  public void groupCut()
   {copyToBoard();
    groupDelete();
   }

  public void groupDelete()
  {boolean modified0=pneumaticlistener.getModified();
   ArrayList garray=new ArrayList(groupArray);
	PneumaticElement elementdel=null;
   ArrayList lines=new ArrayList();
   ArrayList sublines=null;
   for(int i=0;i<groupArray.size();i++)
    {elementdel=(PneumaticElement) groupArray.get(i);
     sublines=delete(elementdel);
     for(int j=0;j<sublines.size();j++) lines.add(sublines.get(j));
     remove(elementdel);
    }
   pneumaticlistener.setModified(true);
   pneumaticlistener.addCommand(new deleteGroupCommand(this,modified0,garray,lines));             
   deGroup();
//   mark();
  }   

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  //--------------- ElementContainer ----------
 private class deleteCommand extends Command
  {boolean modified;
	 ArrayList lines;
	 public deleteCommand(Object ele,boolean modified, ArrayList lines)
   {super("PneumaticPanel",ele,PneumaticPanel.Command_delete);
    this.lines=lines;
   }
  public void undo()
   {Line line=null;
    for(int i=0;i<lines.size();i++)
     {line=(Line) lines.get(i);
	    line.undelete();
      lineArray.add(line);
     }
    add((PneumaticElement) source);
    pneumaticlistener.setModified(modified);
    repaint();
   }
  public void redo()
   {delete((PneumaticElement) source);
   pneumaticlistener.setModified(true);
    repaint();
   }
 }

   public ArrayList delete(PneumaticElement element)
    {ArrayList lines=new ArrayList();
     Line line=null;
     if(element.ports!=null)
      {
       for(int i=0;i<element.ports.length;i++)
        {if(element.ports[i]!=null && element.ports[i].getConnected())
          {line=removeLineAt(element.ports[i]);
           if(line!=null) lines.add(line);
          }
        }
      }
     remove(element);
     /*
     if(element instanceof EValve)
      {pneumaticlistener.removeEValve((EValve) element);
      }
      */
     repaint();
     return lines;
    }

   public void deleteElement(PneumaticElement element)
    {boolean modified=pneumaticlistener.getModified();
	 ArrayList lines=delete(element);
	 pneumaticlistener.setModified(true);
     pneumaticlistener.addCommand(new deleteCommand(element,modified,lines));
    }
/*
   public void delete(ElementEvent e)
    {
	 Object obj=e.getSource();
     if(!(obj instanceof Element)) return;
     Element element=(Element) obj;
     deleteElement(element);
    }
*/
/*
   private class portClickedCommand extends Command
    {boolean modified;
     Line line0;
     Pos initPos0;
     Pos curPos0;
	 Port port;
     int x,y;
	 public portClickedCommand(Object ele,boolean modified,Line line0,Pos initPos0,Pos curPos0,Port port,int x,int y)
     {super(ele,"Pneumatic Element Port Clicked");
      this.modified=modified;
      this.line0=line0;
      this.initPos0=initPos0;
      this.curPos0=curPos0;
      this.port=port;
      this.x=x;
      this.y=y;
     }
    public void undo()
     {if(started)
       {line=null;
        started=false;
        initPos.x=initPos0.x;
        initPos.y=initPos0.y;
        curPos.x=curPos0.x;
        curPos.y=curPos0.y;
        started=false;
       }
      else
       {line=line0;
	    line.deletePort(port);
	    lineArray.remove(line);
        started=true;
        if(circuitListener!=null) circuitListener.refreshSystemCombo();
       }
      circuitListener.setModified(modified);
     }
    public void redo()
     {if(!started)
      {line=new Line();
       line.addPort(port);
       initPos.x=x;
       initPos.y=y;
       curPos.x=x;
       curPos.y=y;
       started=true;
      }
     else
      {line=line0;
       line.addPort(port);
       lineArray.add(line);
//if(pneumatics.electrics!=null && pneumatics.electrics.sequence!=null) pneumatics.electrics.sequence.refreshSystemCombo();
       if(circuitListener!=null) circuitListener.refreshSystemCombo();
       line=null;
       started=false;
       repaint();
      }
     circuitListener.setModified(true);
     }
   }
*/

  private class addLineCommand extends Command
    {boolean modified;
	 Line line0;
	public addLineCommand(Object ele,boolean modified,Line line0)
     {super("PneumaticPanel",ele,Command_addLine);
      this.modified=modified;
      this.line0=line0;
     }
    public void undo()
     {line=line0;
	  line.delete();
      lineArray.remove(line);
      line=null;
      started=false;
      pneumaticlistener.setModified(modified);
      repaint();
     }
    public void redo()
     {line=line0;
      line.undelete();
      lineArray.add(line);
      line=null;
      started=false;
      pneumaticlistener.setModified(true);
      repaint();
     }
   }

   public void portClicked(ConnectEvent ce)   // PneumaticPanel
    {
      if(!started)
      {line=new Line();
       line.addPort(ce.port);
       initPos.x=ce.x;
       initPos.y=ce.y;
       curPos.x=ce.x;
       curPos.y=ce.y;
       started=true;
      }
     else
      {boolean modified=pneumaticlistener.getModified();
       line.addPort(ce.port);
       lineArray.add(line);
       pneumaticlistener.setModified(true);
       pneumaticlistener.addCommand(new addLineCommand(ce.getSource(),modified,line));
//if(pneumatics.electrics!=null && pneumatics.electrics.sequence!=null) pneumatics.electrics.sequence.refreshSystemCombo();
/*
       if(circuitListener!=null)
        {pneumaticlistener.refreshSystemCombo();
         pneumaticlistener.setModified(true);
        }
*/
       line=null;
       started=false;
      }
      pneumaticlistener.repaint();
//     circuitListener.setModified(true);
//     if(commandListener!=null) commandListener.add(new portClickedCommand(ce.getSource(),modified,line0,initPos0,curPos0,ce.port,ce.x,ce.y));
    }

   private class moveElementCommand extends Command
    {boolean modified;
	 int x0,y0;
     int x,y;
	 public moveElementCommand(Object ele,boolean modified,int x,int y,int x0,int y0)
     {super("PneumaticPanel",ele,Command_moveElement);
      this.modified=modified;
      this.x0=x0;
      this.y0=y0;
      this.x=x;
      this.y=y;
     }
    public void undo()
     {moveElementTo((PneumaticElement) source,x0,y0);
      pneumaticlistener.setModified(modified);
      repaint();
     }
    public void redo()
     {moveElementTo((PneumaticElement) source,x,y);
      pneumaticlistener.setModified(true);
      repaint();
     }
   }
/*
   public void move(MoveEvent me)            // PneumaticPanel
    {//setCursor(savedCursor);
     boolean modified=circuitListener.getModified();
     Object obj=me.getSource();
     if(!(obj instanceof Element)) return;
     Element element=(Element) obj;
     int x0=element.getX();
     int y0=element.getY();
     moveElementTo(element,me.x,me.y);
     circuitListener.setModified(true);
     if(commandListener!=null) commandListener.add(new moveElementCommand(element,modified,me.x,me.y,x0,y0));
    }
*/
   private void moveElementTo(PneumaticElement element,int x,int y)
    {Component com=inComponent(x,y,x+element.getSize().width,y+element.getSize().height);
     if(com!=null && com!=element)
      {
       if(((PneumaticElement)com).getModelType().equals("Actuator") &&
          (element instanceof Valve) && ((Valve) element).forceType==Valve.FORCE_MECHANIC)
//         modelName.equals("LimitSwitch"))
        {Actuator act=(Actuator) com;
         act.addValve((Valve)element);
         pneumaticlistener.setStatus(Config.getString("Status.install_LS"));
        }
       else
    	 pneumaticlistener.setStatus(Config.getString("Status.overlay"));
//      return;
      }
     element.setLocation(x,y);
     repaint();
    }

  public Port anotherPort(Port pt)
   {Port ptemp=null;
    Line lin=null;
    for(int i=0;i<lineArray.size();i++)
     {lin=(Line) lineArray.get(i);
      ptemp=lin.anotherPort(pt);
      if(ptemp!=null) break;
     }
    return ptemp;
   }
//-------------------------------------------------------------------
// elementListener
//
/*
   public Component[] getPneumaticPanelComponents()  {return pneumaticlistener.getPneumaticPanelComponents();}
   public void AddCell(int type,int state,CEDevice ed,CDOutput cdo,boolean ShiftEnabled)
    {WebLadderCAD.electrics.electricPanel.AddCell(type,state,ed,cdo,ShiftEnabled);
    pneumaticlistener.setModified(true);
    }
   public void setModified(boolean b)
    {pneumaticlistener.setModified(b);}
   public void AddCell(int type,ESystemBase sys, EDevice ed,int direct, boolean ShiftEnabled)
    {WebLadderCAD.electrics.electricPanel.AddCell(type,sys,ed,direct,ShiftEnabled);
    pneumaticlistener.setModified(true);
    }
*/
/*
   public ArrayList getTempElectricFaceArray()
    {return circuitListener.getTempElectricFaceArray();}
*/
 }
