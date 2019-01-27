package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import tw.com.justiot.sequencecontrol.config.GaugeParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;

public class Gauge extends PneumaticElement
 {private float preset;
  private Position textPos;

  public static int count=0;
  public Gauge(String mname,PneumaticListener ownerlistener)
   {super(mname,false, ownerlistener);
    if(PneumaticConfig.parameter.containsKey(mname))
     {GaugeParameter ep=(GaugeParameter) PneumaticConfig.parameter.get(mname);
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
      this.textPos=ep.textPos;
     }
   }

   public Port[] nextPorts(Port pt)
    {if(ports.length<2) return null;
     if(ports[0]==pt) {
      Port[] ps= {ports[1]};
      return ps;
     }
     if(ports[1]==pt) {
     	Port[] ps= {ports[0]};
         return ps;
     }
     return null;
    }

    private String text;
    private Font oldFont;
    private Color oldColor;
    private Font newFont=new Font(null,Font.BOLD,9);
    private float showValue;
    public void paintComponent(Graphics g)
     {
      super.paintComponent(g);
      Graphics2D g2=(Graphics2D) g;
      oldFont=g2.getFont();
      g2.setFont(newFont);
      oldColor=g2.getColor();
      g2.setColor(Color.blue);
      showValue=0.0f;
      if(modelName.equals("FlowGauge"))
       {//for(int i=0;i<ports.length;i++)
        // showValue=showValue+ports[i].getFlowrate();
        //text=Float.toString(showValue/ports.length);
        float f=ports[0].getFlowrate();
        if(f<0) f=0.0f;
        text=String.format("%.2f", f);
       }
      if(modelName.equals("PressureGauge"))
       {//for(int i=0;i<ports.length;i++)
        // showValue=showValue+ports[i].getPressure();
      	//text=Float.toString(showValue/ports.length);
        float f=ports[0].getPressure();
        if(f<0) f=0.0f;
        text=String.format("%.2f", f);
       }
      if(text.length()>4) text=text.substring(0,4);
      g2.drawString(text,textPos.x,textPos.y);
      g2.setColor(oldColor);
      g2.setFont(oldFont);
     }

    public void check()
     {//if(ports.length==2) //ports[0].connect(ports[1],0.0f);
  	   Line.connect(ports[0], ports[1]);
  //    paintComponent(getGraphics());
     }

  public String extraWrite()
   {return "";}

  public void extraRead(String str)
   {}
 }
