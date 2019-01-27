package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.FlowValveParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;

public class FlowValve extends PneumaticElement
 {
   public static float blankflowrate=-1.0f;
   private Port port1,port2;
   private boolean oneWay;
   private boolean adjustable;
   private int percent=50;
   public float enterflowrate=blankflowrate;
   private FlowValve seft;
 //  2Flow   Non-adjustable_Two_Way_Flow_Valve 34 28 images/SymFLow/2Flow1.GIF    34 28 images/SymFlow/2Flow.GIF  2 14 left 30 14 right 0 0
 //  2FlowA  Adjustable_Two_Way_Flow_Valve 34 28 images/SymFLow/2FlowA1.GIF   34 28 images/SymFlow/2FlowA.GIF 2 14 left 30 14 right 0 1
 //  1Flow   Non-adjustable_One_Way_Flow_Valve 34 28 images/SymFLow/1Flow1.GIF    34 28 images/SymFlow/1Flow.GIF  2 21 left 30 21 right 1 0
 //  1FlowA  Adjustable_One_Way_Flow_Valve 34 28 images/SymFLow/1FlowA1.GIF   34 28 images/SymFlow/1FlowA.GIF 2 21 left 30 21 right 1 1
 //  Exhaust  Fast_Exhaust_Line

  public static int count=0;
  public FlowValve(String mname,PneumaticListener pneumaticlistener)
   {super(mname,false, pneumaticlistener);
    if(PneumaticConfig.parameter.containsKey(mname))
     {FlowValveParameter ep=(FlowValveParameter) PneumaticConfig.parameter.get(mname);
      if(ep.port1!=null)
       {
        this.port1=new Port(ep.port1);
        this.port1.setDir(ep.port1.getDir());
       }
      else this.port1=null;
      if(ep.port2!=null)
       {this.port2=new Port(ep.port2);
        this.port2.setDir(ep.port2.getDir());
       }
      else this.port2=null;
      ports=new Port[]{this.port1,this.port2};
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
      this.oneWay=ep.oneWay;
      this.adjustable=ep.adjustable;
//      percent=50;
      if(modelName.equals("Exhaust")) percent=500;
      seft=this;
     }
   }

   public Port[] nextPorts(Port pt)
    {//if(!port1.getConnected() || !port2.getConnected()) return null;
 	    Port[] pts=new Port[1];
 	    if(pt==port1) {
 	      Port[] ps= {port2};
 	      return ps;
 	    }
 	    if(pt==port2)
 	     {if(modelName.equals("Exhaust")) return null;
 	       else {
 	    	   Port[] ps= {port1};
 	    	   return ps;
 	       }
 	     }
 	    return null;
    }
   public void mark() {
 	  if(modelName.equals("Exhaust")) return;
 	enterflowrate=blankflowrate;
   }
   public void check()
    {if(modelName.equals("Exhaust")) {
 	 if(!port1.getConnected()) {
 	  if(port2.getConnected()) {
 	    port2.setIsDrain(true);
 //	    port2.setFlowrate(Port.sourceFlowrate*2.0f);
 	    port2.setPressure(0.0f);
 	  }
 	 } else {
 	  if(port2.getConnected()) {
 		if(port1.getPressure()>0.0f) {
 		  Line.connect(port1, port2);
 		} else {
 			port2.setIsDrain(true);
 		    port2.setFlowrate(Port.sourceFlowrate*2.0f);
 		    port2.setPressure(0.0f);
 		}
 	   } else {
 		   port1.setIsDrain(true);
 		    port1.setPressure(0.0f);
 	  }
 	 }
 	  return;
     }

 	if(port1.getConnected() && port2.getConnected())
      {float f1=port1.getFlowrate();
       float f2=port2.getFlowrate();
   //    if(f1<0.0f && f2<0.0f) {
   //  	  Line.connect(port1, port2, false);
   //  	  return;
   //    }
 	  if(!oneWay) {

        if(f1>0.0f && f1 > f2) {
     	   Line.connect(port1, port2, false);
     	   if(enterflowrate<0.0f) enterflowrate=f1;
   //  	   port2.setQCapacity(enterflowrate*percent/100.0f);
     	   port2.setFlowrate(enterflowrate*percent/100.0f);
  //   	   port1.setQCapacity(enterflowrate*percent/100.0f);
     	   port1.setFlowrate(enterflowrate*percent/100.0f);
    // 	   System.out.println("enterflowrate="+enterflowrate);
        } else if(f2>0.0f && f2 > f1) {
     	   Line.connect(port1, port2, false);
     	   if(enterflowrate<0.0f) enterflowrate=f2;
    // 	   port1.setQCapacity(enterflowrate*percent/100.0f);
     	   port1.setFlowrate(enterflowrate*percent/100.0f);
   //  	   port2.setQCapacity(enterflowrate*percent/100.0f);
     	   port2.setFlowrate(enterflowrate*percent/100.0f);
  //   	   System.out.println("enterflowrate="+enterflowrate);
        } else {
     	   Line.connect(port1, port2);
        }
       } else {
     	  if(f2>0.0f && f2 > f1) {
        	   Line.connect(port1, port2, false);
        	   if(enterflowrate<0.0f) enterflowrate=f2;
       // 	   port1.setQCapacity(enterflowrate*percent/100.0f);
        	   port1.setFlowrate(enterflowrate*percent/100.0f);
      //  	   port2.setQCapacity(enterflowrate*percent/100.0f);
        	   port2.setFlowrate(enterflowrate*percent/100.0f);
     //   	   System.out.println("enterflowrate="+enterflowrate);
     	  } else {
     		  Line.connect(port1, port2);
     	  }
       }
 /*
 	   setActuatorFlow();
 //      port1.connect(port2,0.0f);
       Line.connect(port1, port2);
       if(port1.getFlowrate()!=0.0f && port2.getFlowrate()!=0.0f)
        {if(port1.getPressure() > port2.getPressure())
          {if(oneWay) port2.setFlowrate(port1.getFlowrate());
           else port2.setFlowrate(port1.getFlowrate()*percent/100.0f);
   //        port1.setPressure(port1.getPCapacity());
          }
         else if(port1.getPressure() < port2.getPressure())
          {port1.setFlowrate(port2.getFlowrate()*percent/100.0f);
     //      port2.setPressure(port2.getPCapacity());
          }
 //      else
 //       {port1.setFlowrate(0.0f);
 //        port2.setFlowrate(0.0f);
 //       }
        }
       */
      }
    }
/*
  private boolean firstPopup=true;
  public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop) {
        if(firstPopup)
         {if(adjustable)
            {popup.addSeparator();
              m = new JMenuItem(Config.getString("FlowValve.FlowRate"));
              m.addActionListener(this);
              m.addMouseListener(new menuItemMouseAdapter());
              popup.add(m);
            }
          firstPopup=false;
         }
        popup.show(this, ex, ey);
      }
    }
*/
    public void ActionPerformed(JMenuItem mi,String op,String input)
       {
        super.ActionPerformed(mi,op,input);
        String option=mi.getText();
  //System.err.println("option:"+option);
       if(option.equals(Config.getString("FlowValve.FlowRate")))
        {CustomDialog customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("FlowValve.FlowRate"),Config.getString("FlowValve.AdjustFlowRate"),CustomDialog.VALUE_INT);
         customDialog.pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
         customDialog.setTextField(Integer.toString(percent));
         customDialog.setVisible(true);
         percent=customDialog.getInt();
  //System.err.println(percent);
   //        setActuatorFlow();

       }
    }
/*
  private void setActuatorFlow()
   {
//System.err.println("setactuator");
     if(modelName.equals("Exhaust"))
      {setActuatorFlow(port2,FlowValve_IN);
//System.err.println("exhaust setAct");
      }
     else
      {
       if(oneWay)
        {setActuatorFlow(port1,FlowValve_OUT);
          setActuatorFlow(port2,FlowValve_IN);
        }
       else
        {setActuatorFlow(port1,FlowValve_BOTH);
          setActuatorFlow(port2,FlowValve_BOTH);
        }
     }
  }
 private void setActuatorFlow(Port port,int type)
  {Port pt=WebLadderCAD.pneumatics.pneumaticPanel.anotherPort(port);
//System.err.println("setActuator2");
    if(pt==null) return;
    if(pt.getOwner() instanceof Actuator)
     {Actuator act=(Actuator) pt.getOwner();
       if(pt==act.fport)
        {switch(type)
           {case FlowValve_BOTH: act.fflowvalve=percent;act.bflowvalve=percent;break;
             case FlowValve_OUT:act.fflowvalve=percent;break;
             case FlowValve_IN:act.bflowvalve=percent;break;
           }
        }
       if(pt==act.bport)
        {switch(type)
           {case FlowValve_BOTH: act.fflowvalve=percent;act.bflowvalve=percent;break;
             case FlowValve_OUT:act.bflowvalve=percent;break;
             case FlowValve_IN:act.fflowvalve=percent;break;
           }
        }
     }
     Port[] pts=pt.getOwner().nextPorts(pt);
     if(pts!=null)
      {for(int i=0;i<pts.length;i++)
         setActuatorFlow(pts[i],type);
      }
   }
   */
  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  public String extraWrite()
   {return Integer.toString(percent);}

  public void extraRead(String str)
   {StringTokenizer token=new StringTokenizer(str);
     if(token.hasMoreTokens()) percent=Integer.parseInt(token.nextToken());
   }

 }
