package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.config.PressureValveParameter;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;


public class PressureValve extends PneumaticElement
 {private float preset;
  private PressureValve seft;

  public static int count=0;
  public PressureValve(String mname,PneumaticListener ownerlistener)
   {super(mname,false, ownerlistener);
    if(PneumaticConfig.parameter.containsKey(mname))
     {PressureValveParameter ep=(PressureValveParameter) PneumaticConfig.parameter.get(mname);
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
      preset=3.0f;

      seft=this;
     }
   }

  public float getPreset() {return preset;}
  public void setPreset(float f) {preset=f;}

  public Port[] nextPorts(Port pt)
   {if(modelName.equals("Reduce"))
     {if(ports[1].getPressure() > preset) return null;
      Port[] rps=new Port[1];
      if(ports[0]==pt) rps[0]=ports[1];
      if(ports[1]==pt) rps[0]=ports[0];
      return rps;
     }
    if(modelName.equals("Sequence2"))
     {if(ports[0].getPressure() < preset) return null;
      Port[] rps=new Port[1];
      if(ports[0]==pt) rps[0]=ports[1];
      if(ports[1]==pt) rps[0]=ports[0];
      return rps;
     }
    if(modelName.equals("Sequence1"))
     {if(ports[2].getPressure() < preset) return null;
      if(ports[2]==pt) return null;
      Port[] rps=new Port[1];
      if(ports[0]==pt) rps[0]=ports[1];
      if(ports[1]==pt) rps[0]=ports[0];
      return rps;
     }
    if(modelName.equals("Relief")) return null;
    if(modelName.equals("Unload")) return null;
    return null;
   }

  public void check()
   {if(modelName.equals("Reduce"))
     {if(ports[0].getPressure() >= preset)
       {
        ports[1].setPressure(preset);;
       }
      else
       {//ports[0].connect(ports[1],0.0f);
        Line.connect(ports[0], ports[1]);
       }
     }
    if(modelName.equals("Sequence2"))
     {if(ports[0].getPressure() < preset)
       {ports[0].setIsStop(true);
        ports[1].setIsStop(true);
       }
      else
       {//ports[0].connect(ports[1],0.0f);
    	  Line.connect(ports[0], ports[1]);
       }
     }
    if(modelName.equals("Sequence1"))
     {
//System.err.println(ports[2].getPressure()+":"+preset);
       if(ports[2].getPressure() < preset)
       {ports[0].setIsStop(true);
        ports[1].setIsStop(true);
       }
      else
       {//ports[0].connect(ports[1],0.0f);
        Line.connect(ports[0], ports[1]);
       }
     }
    if(modelName.equals("Relief"))
     {if(ports[0].getConnected()) {
    	if(ports[0].getPressure()>preset) {
    	  ports[0].setPressure(preset);
    	}
      }
     }
    if(modelName.equals("Unload"))
     {if(ports[0].getPressure() < preset)
       {ports[0].setIsStop(true);
        ports[1].setIsStop(true);
       }
      else
       {ports[0].setIsDrain(true);
        ports[1].setIsDrain(true);
       }
     }
   }
/*
  private boolean firstPopup=true;
  public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop) {
        if(firstPopup)
         {popup.addSeparator();
          m = new JMenuItem(Config.getString("Pressure.Preset"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
          firstPopup=false;
         }
        popup.show(this,ex,ey);
      }
    }
*/
    public void ActionPerformed(JMenuItem mi,String op,String input)
       {
        super.ActionPerformed(mi,op,input);
        String option=mi.getText();
  //System.err.println("option:"+option);
       if(option.equals(Config.getString("Pressure.Preset")))
        {CustomDialog customDialog = new CustomDialog(new JFrame(),Config.getString("Pressure.Preset"),Config.getString("Pressure.adjustPreset"),CustomDialog.VALUE_FLOAT);
           customDialog.pack();
           Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
           customDialog.setLocation(
  		screenSize.width/2 - customDialog.getSize().width/2,
  		screenSize.height/2 - customDialog.getSize().height/2);
           customDialog.setTextField(Float.toString(preset));
           customDialog.setVisible(true);
           preset=customDialog.getFloat();

       }
    }
     class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  public String extraWrite()
   {return "";}

  public void extraRead(String str)
   {}

 }
