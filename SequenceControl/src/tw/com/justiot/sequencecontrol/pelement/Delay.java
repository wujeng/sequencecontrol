package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.swing.JMenuItem;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.DelayParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;

// port[0] : A port
// port[1] : Z port
// port[2] : P port

public class Delay extends PneumaticElement
 {private boolean timeron;
  private static final int TimerPeriod=1000;
  private static final int defaultpreset=3;
  private int preset;
  private int count;
  private Position textPos;
  private boolean isUpEnd;
  private boolean isNO;
  private Delay seft;
  private boolean firstPopup=true;

 private boolean activated=false;
  public static int allcount=0;
  public Delay(String mname,PneumaticListener pneumaticlistener)
   {super(mname,true, pneumaticlistener);

    if(PneumaticConfig.parameter.containsKey(mname))
     {DelayParameter ep=(DelayParameter) PneumaticConfig.parameter.get(mname);
      this.ports=new Port[ep.ports.length];
      portOrg=new Port[this.ports.length];
      for(int i=0;i<ep.ports.length;i++)
       {
        if(ep.ports[i]!=null)
         {this.ports[i]=new Port(ep.ports[i]);
          this.ports[i].setOwner(this);
          portOrg[i]=new Port(ports[i]);
         }
        else
         {this.ports[i]=null;
          portOrg[i]=null;
         }
       }
      textPos=ep.textPos;
      isNO=ep.isNO;
      isUpEnd=ep.isUpEnd;
      preset=defaultpreset;
      count=3;
      timeron=false;

      seft=this;
     }
   }

  public void paintComponent(Graphics g)
   {
    super.paintComponent(g);
    Graphics2D g2=(Graphics2D) g;
    g2.drawString(Integer.toString(count),textPos.x,textPos.y);
   }

  public boolean isTimeron() {return timeron;}
  public void setTimeron(boolean ton) {timeron=ton;}
  public void startTimer(java.util.Timer timer)
   {timer.scheduleAtFixedRate(new TimeThread(), 0, TimerPeriod);
   }

   class TimeThread extends TimerTask
    {
     public void run()
      {if(!timeron) return;
       if(count>0) count--;
       if(count<=0)
        {if(isUpEnd) curImage=1;
         else curImage=0;

         activated=true;
         count=preset;
         repaint();
         setTimeron(false);
        }
      }
    }

  public void reset()
   {setTimeron(false);
    curImage=0;
    count=preset;
//    paintComponent(getGraphics());
   }

   public Port[] nextPorts(Port pt)
    {if(pt==ports[1]) return null;
     if(pt==ports[0])
      {if((isNO && curImage==0) || (!isNO && curImage==1))
        {Port[] pts= {ports[2]};
         return pts;
        }
      }
     if(pt==ports[2])
      {if((isNO && curImage==0) || (!isNO && curImage==1))
        {Port[] pts= {ports[0]};
         return pts;
        }
      }
     return null;
    }

   public void mark() {

 	  if((isNO && curImage==0) || (!isNO && curImage==1))
 	      Line.connect(ports[0], ports[2]);
 	else
 	 {ports[0].setIsDrain(true);
       ports[2].setIsStop(true);
 	 }
 	  check();
   }
   public void check()
    {
 	// port[0] : A port
 	// port[1] : Z port
 	// port[2] : P port

 	if(activated) {
       if(isUpEnd) {
         curImage=1;
         ports[1].setIsStop(true);
       } else {
     	curImage=0;
       }
     } else {
     	if(isUpEnd) {
             curImage=0;
           } else {
         	curImage=1;
           }
     }


 	if(ports[1].getPressure()>0.0f)
       {if(!activated && !timeron && isUpEnd && curImage==0) setTimeron(true);

 		if(!isUpEnd) {activated=false; curImage=1;}
       }
      else
       {if(isUpEnd) {activated=false; curImage=0;}
        if(!activated && !timeron && !isUpEnd && curImage==1) setTimeron(true);
       }

 	if((isNO && curImage==0) || (!isNO && curImage==1))
 	      Line.connect(ports[0], ports[2]);
 	else
 	 {ports[0].setIsDrain(true);
       ports[2].setIsStop(true);
 	 }


 	/*



      if(ports[2].getPressure()>0.0f)
       {if(ports[1].getPressure()>0.0f)
          {if(isUpEnd)
             {if(!timeron && curImage==0) setTimeron(true);}
            else
             {if(curImage==0)
                {curImage=1;
 //                 paintComponent(getGraphics());
                }
             }
          }
         else
          {
            if(curImage==1)
             {if(!isUpEnd)
                {if(!timeron) setTimeron(true);}
               else
                {if(curImage==1)
                   {curImage=0;
 //                    paintComponent(getGraphics());
                     count=preset;
 //                    ports[0].setFlowrate(3.0f);
 //                    ports[0].setPressure(0.0f);
                     ports[0].setIsDrain(true);
                   }
                }
             }

          }
       }
     else
      {
 //System.err.println(ports[0].getPressure()+":"+ports[1].getPressure()+":"+ports[2].getPressure());
        if(ports[1].getPressure()>Port.sourcePressure/2.0f)
          {if(!isUpEnd)
             {if(!timeron && curImage==1) setTimeron(true);}
            else
             {
 //System.err.println("Z none"+curImage);
               if(curImage==1)
                {curImage=0;
 //                 paintComponent(getGraphics());
                  count=preset;
                }
             }
          }
         else
          {if(!isUpEnd)
             {if(!timeron && curImage==1) setTimeron(true);}
            else
             {if(curImage==1)
                {curImage=0;
 //                 paintComponent(getGraphics());
                  count=preset;

 //                 ports[0].setFlowrate(3.0f);
 //                    ports[0].setPressure(0.0f);
                     ports[0].setIsDrain(true);
                }
             }
          }
      }
     if((isNO && curImage==0) || (!isNO && curImage==1))
 //     ports[0].connect(ports[2],0.0f);
       Line.connect(ports[0], ports[2]);
     else
      {ports[0].open();
  //      ports[0].setFlowrate(3.0f);
  //                   ports[0].setPressure(0.0f);
                     ports[0].setIsDrain(true);
       ports[2].setFlowrate(0.0f);
   //    ports[2].setQCapacity(0.0f);
      }
     */
    }
/*
  public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop) {
        if(firstPopup)
         {
          m = new JMenuItem(Config.getString("Delay.preset"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          firstPopup=false;
         }
        popup.show(this, ex, ey);
      }
    }
*/
    public void ActionPerformed(JMenuItem mi,String op,String input){
      super.ActionPerformed(mi,op,input);
        String option=mi.getText();
  //System.err.println("option:"+option);
       if(option.equals(Config.getString("Delay.preset")))
        {CustomDialog customDialog = new CustomDialog(pneumaticlistener.getFrame(),"Enter Delay Load","Enter timer preset value?",CustomDialog.VALUE_INT);
            customDialog.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            customDialog.setLocation(
  		screenSize.width/2 - customDialog.getSize().width/2,
  		screenSize.height/2 - customDialog.getSize().height/2);
            customDialog.setTextField(Integer.toString(preset));
            customDialog.setVisible(true);
            preset=customDialog.getInt();

       }
  }
 class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  public String extraWrite()
   {return Integer.toString(preset);}

  public void extraRead(String str)
   {StringTokenizer token=new StringTokenizer(str);
     if(token.hasMoreTokens()) preset=Integer.parseInt(token.nextToken());
   }

 }
