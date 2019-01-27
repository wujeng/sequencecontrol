package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.eelement.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EDevicePanel extends JPanel implements MouseListener,ActionListener,EArraysListener
 {
   private boolean showManual;
   private boolean showMechanic;
   private boolean  showElectric;
   private boolean showTimer;
   private boolean showCounter;
   private boolean showSensor;
   public JPopupMenu popup;
   private JMenuItem m;
   private JCheckBoxMenuItem cbm;
   private double ratio=1.0;
   private ElectricListener electriclistener;
  public EDevicePanel(ElectricListener electriclistener)
   {super();
    this.electriclistener=electriclistener;
     setLayout(new FlowLayout());
     showManual=true;
     showMechanic=true;
     showElectric=true;
     showTimer=true;
     showCounter=true;
     showSensor=true;
     popup = new JPopupMenu();
      cbm=new JCheckBoxMenuItem(Config.getString("EDevicePanel.Button"));
      cbm.setState(showManual);
      cbm.addActionListener(this);
      cbm.addMouseListener(new menuItemMouseAdapter());
      popup.add(cbm);
      cbm=new JCheckBoxMenuItem(Config.getString("EDevicePanel.LimitSwitch"));
      cbm.setState(showMechanic);
      cbm.addActionListener(this);
      cbm.addMouseListener(new menuItemMouseAdapter());
      popup.add(cbm);
      cbm=new JCheckBoxMenuItem(Config.getString("EDevicePanel.Relay"));
      cbm.setState(showElectric);
      cbm.addActionListener(this);
      cbm.addMouseListener(new menuItemMouseAdapter());
      popup.add(cbm);
      cbm=new JCheckBoxMenuItem(Config.getString("EDevicePanel.Timer"));
      cbm.setState(showTimer);
      cbm.addActionListener(this);
      cbm.addMouseListener(new menuItemMouseAdapter());
      popup.add(cbm);
      cbm=new JCheckBoxMenuItem(Config.getString("EDevicePanel.Counter"));
      cbm.setState(showCounter);
      cbm.addActionListener(this);
      cbm.addMouseListener(new menuItemMouseAdapter());
      popup.add(cbm);
      popup.addSeparator();
      m = new JMenuItem(Config.getString("EDevicePanel.zoom"));
      m.addActionListener(this);
      m.addMouseListener(new menuItemMouseAdapter());
      popup.add(m);
      addMouseListener(this);
      electriclistener.getEArrays().addListener(this);
   }

  private void add2Panel(EDevice ed)
   {
     if(showManual &&
              (ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO || ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE)) add(ed);
     if(showMechanic && ed.ced.actionType==CEDevice.TYPE_MECHANIC) add(ed);
     if(showElectric && ed.ced.actionType==CEDevice.TYPE_ELECTRIC) add(ed);
     if(showTimer && ed.ced.actionType==CEDevice.TYPE_TIMER) add(ed);
     if(showCounter && ed.ced.actionType==CEDevice.TYPE_COUNTER) add(ed);
     if(showSensor && ed.ced.actionType==CEDevice.TYPE_SENSOR) add(ed);
   }

  public void EArraysChanged(String type,String op,Object obj)
  {if(type.equals("EDevice") && op.equals("addEDevice"))
    {EDevice ed=(EDevice) obj;
     add2Panel(ed);
     repaint();
     return;
    }
   if(type.equals("EDevice") && op.equals("deleteEDevice"))
    {EDevice ed=(EDevice) obj;
      remove(ed);
      repaint();
     return;
    }
   if(type.equals("EDevice") && op.equals("removeAllEDevice"))
    {removeAll();
     repaint();
     return;
    }
  }


  private void reshow()
   {removeAll();
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
      add2Panel((EDevice) electriclistener.getEArrays().EDeviceArray.get(i));
     repaint();
   }

  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
//System.out.println("paintComponent");
    int wid=1,hgt=0,winwid=1,colmax=1,cols=0,rows=0,y0=0;
    winwid=getSize().width;
 //   if(electriclistener.hasElectrics()) winwid=WebLadderCAD.electrics.scrollPane1.getViewport().getViewRect().width;
    if(electriclistener.getEArrays().ElectricFaceArray.size()>0)
     {
       wid=(int)(((double)ESymbol.DeviceWidth0)*ratio);
       if(wid==0) return;
       hgt=(int)(((double)ESymbol.DeviceHeight0)*ratio);
//System.err.println(wid+":"+hgt);

//       if(electrics.scrollPane1!=null)
//        winwid=electrics.scrollPane1.getViewport().getViewRect().width;
//       winwid=getParent().getViewport().getViewRect().width;
       colmax=winwid/wid;
       cols=0;
       rows=0;
       ESymbol esym=null;
       Component[] comps=getComponents();
       String aname=null;
       for(int i=0;i<comps.length;i++)
        {if(!(comps[i] instanceof ESymbol)) continue;
          esym=(ESymbol) comps[i];
//System.err.println("esym");
//          aname=sys.getActuatorName();
//aname="";
//System.err.println(aname);
          aname="";
          if(esym.cdo!=null) aname=esym.cdo.name;
          if(aname==null || aname.length()==0)
            esym.setVisible(false);
          else
            {
              esym.setLocation(cols*wid+2,rows*hgt+2);
              esym.setPreferredSize(new Dimension(wid, hgt));
              esym.setVisible(true); //show();
              cols++;if(cols==colmax) {cols=0;rows++;}
            }
        }
       y0=(rows+1)*hgt;
     }
    if(electriclistener.getEArrays().EDeviceArray.size()==0) return;
    wid=(int)(((double)EDevice.DeviceWidth0)*ratio);
    if(wid==0) return;
    hgt=(int)(((double)EDevice.DeviceHeight0)*ratio);
//System.err.println(wid+":"+hgt);

    colmax=winwid/wid;
//System.out.println("winwid:"+winwid);
//System.out.println("colmax:"+colmax);
    cols=0;
    rows=0;
    EDevice ed=null;
    if(showManual)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
      for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showMechanic)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MECHANIC)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showSensor)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_SENSOR)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showTimer)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_TIMER)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showCounter)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_COUNTER)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showElectric)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_ELECTRIC)
          {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
//            ed.repaint();
            cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }

//    setPreferredSize(new Dimension(colmax*wid, y0+rows*hgt));
//    revalidate();
  }

 public void resize()
  {
    int wid=1,hgt=0,winwid=1,colmax=1,cols=0,rows=0,y0=0;
    winwid=getSize().width;
 //   if(WebLadderCAD.electrics.scrollPane1!=null) winwid=WebLadderCAD.electrics.scrollPane1.getViewport().getViewRect().width;
    if(electriclistener.getEArrays().ElectricFaceArray.size()>0)
     {
       wid=(int)(((double)ESymbol.DeviceWidth0)*ratio);
       if(wid==0) return;
       hgt=(int)(((double)ESymbol.DeviceHeight0)*ratio);
       colmax=winwid/wid;
       cols=0;
       rows=0;
       ESymbol esym=null;
       ElectricFace sys=null;
       String aname=null;
       for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
        {sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
          esym=sys.getESymbol();
//System.err.println("esym");
          aname=sys.getActuatorName();
//aname="";
//System.err.println(aname);
          if(aname==null || aname.length()==0)
            {}
          else
            {
              cols++;if(cols==colmax) {cols=0;rows++;}
            }
        }
       y0=(rows+1)*hgt;
     }
    if(electriclistener.getEArrays().EDeviceArray.size()==0)
     {setPreferredSize(new Dimension(colmax*wid, y0));
//    revalidate();
	   return;
     }
    wid=(int)(((double)EDevice.DeviceWidth0)*ratio);
    if(wid==0) return;
    hgt=(int)(((double)EDevice.DeviceHeight0)*ratio);
    colmax=winwid/wid;
    cols=0;
    rows=0;
    EDevice ed=null;
    if(showManual)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO)
          {cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
      for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE)
          { cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showMechanic)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_MECHANIC)
          {cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showTimer)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_TIMER)
          {cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showCounter)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_COUNTER)
          {cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }
    if(showElectric)
     {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
       {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
         if(ed.ced.actionType==CEDevice.TYPE_ELECTRIC)
          {cols++;if(cols==colmax) {cols=0;rows++;}
          }
       }
     }

    setPreferredSize(new Dimension(colmax*wid, y0+rows*hgt));
    revalidate();
  }

/*
  public void paintComponent(Graphics g)
   {
     super.paintComponent(g);
//System.out.println("paintComponent");
     int wid=1,hgt=0,winwid=1,colmax=1,cols=0,rows=0,y0=0;
     if(EArrays.ElectricFaceArray.size()>0)
      {
        wid=(int)(((double)ESymbol.DeviceWidth0)*ratio);
        if(wid==0) return;
        hgt=(int)(((double)ESymbol.DeviceHeight0)*ratio);
//System.err.println(wid+":"+hgt);
        winwid=getSize().width;
//        if(electrics.scrollPane1!=null)
//         winwid=electrics.scrollPane1.getViewport().getViewRect().width;
//        winwid=getParent().getViewport().getViewRect().width;
        colmax=winwid/wid;
        cols=0;
        rows=0;
        ESymbol esym=null;
        ESystemBase sys=null;
        String aname=null;
        for(int i=0;i<EArrays.ElectricFaceArray.size();i++)
         {sys=(ESystemBase) EArrays.ElectricFaceArray.get(i);
           esym=sys.getESymbol();
//System.err.println("esym");
           aname=sys.getActuatorName();
//aname="";
//System.err.println(aname);
//           if(aname==null || aname.length()==0)
//             esym.setVisible(false);
//           else
             {
               esym.setLocation(cols*wid+2,rows*hgt+2);
               esym.setVisible(true); //show();
               cols++;if(cols==colmax) {cols=0;rows++;}
             }
         }
        y0=(rows+1)*hgt;
      }
     if(electriclistener.getEArrays().EDeviceArray.size()==0) return;
     wid=(int)(((double)EDevice.DeviceWidth0)*ratio);
     if(wid==0) return;
     hgt=(int)(((double)EDevice.DeviceHeight0)*ratio);
//System.err.println(wid+":"+hgt);
     winwid=getSize().width;
//     if(electrics.scrollPane1!=null)
//      winwid=electrics.scrollPane1.getViewport().getViewRect().width;
//     winwid=getParent().getViewport().getViewRect().width;
     colmax=winwid/wid;
     cols=0;
     rows=0;
     EDevice ed=null;
     if(showManual)
      {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
       for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
      }
     if(showMechanic)
      {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_MECHANIC)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
      }
     if(showTimer)
      {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_TIMER)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
      }
     if(showCounter)
      {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_COUNTER)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
      }
     if(showElectric)
      {for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_ELECTRIC)
           {ed.setLocation(cols*wid+2,y0+rows*hgt+2);
             ed.repaint();
             cols++;if(cols==colmax) {cols=0;rows++;}
           }
        }
      }

//     setPreferredSize(new Dimension((colmax+1)*wid, y0+(rows+1)*hgt));
   }
*/

  public String writeEDevice()
   {
//System.out.println("edevicepanel.writeedevice()");
	if(electriclistener.getEArrays().EDeviceArray==null) return "";
     StringBuffer sb=new StringBuffer();
     EDevice ed=null;
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++) {
       ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
       if(ed.ced.modelName.equals("ELimitSwitch")) continue;
       sb.append(ed.write()+"\n");
     }
      
     return sb.toString();
   }

  public void rescale()
   {CEDevice.ratio=ratio;
     EDevice ed=null;
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
      {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
        ed.rescale();
      }
//     update();
//     this.invalidate();
//     repaint();
   }

 public void mouseClicked(MouseEvent e) {}
   public void mouseEntered(MouseEvent e) {}
   public void mouseExited(MouseEvent e) {}
   public void mousePressed(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
  public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop)
   {
     if(!left) maybeShowPopup(pop,ex,ey);
   }

   public void mouseReleased(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop)
    {if(!left) maybeShowPopup(pop,ex,ey);
    }


  public Point popupPoint=null;
  private void maybeShowPopup(boolean pop, int ex, int ey)
   {
      if(pop)
       {
         popupPoint=new Point(ex,ey);
//        popup.show(e.getComponent(), e.getX(), e.getY());
        popup.show(this,ex,ey);
       }
   }

  public void actionPerformed(ActionEvent e){
         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }
  public void ActionPerformed(JMenuItem mi,String op,String input)
    {
      String option=mi.getText();
      if(option.equals(Config.getString("EDevicePanel.Button")))
         {showManual=!showManual;
           ((JCheckBoxMenuItem) mi).setState(showManual);

         }
        else if(option.equals(Config.getString("EDevicePanel.LimitSwitch")))
         {showMechanic=!showMechanic;
           ((JCheckBoxMenuItem) mi).setState(showMechanic);

         }
        else if(option.equals(Config.getString("EDevicePanel.Relay")))
         {showElectric=!showElectric;
           ((JCheckBoxMenuItem) mi).setState(showElectric);

         }
        else if(option.equals(Config.getString("EDevicePanel.Timer")))
         {showTimer=!showTimer;
           ((JCheckBoxMenuItem) mi).setState(showTimer);

         }
        else if(option.equals(Config.getString("EDevicePanel.Counter")))
         {showCounter=!showCounter;
           ((JCheckBoxMenuItem) mi).setState(showCounter);

         }
        else if(option.equals(Config.getString("EDevicePanel.zoom")))
         {double oldratio=ratio;
	      CustomDialog customDialog = new CustomDialog(new JFrame(),Config.getString("EDevicePanel.zoom"),Config.getString("EDevicePanel.zoomfactor"),CustomDialog.VALUE_FLOAT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Double.toString(ratio));
          customDialog.setVisible(true);
          ratio=(double) customDialog.getFloat();
          rescale();
         }
        reshow();
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

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

  public void timeStep()
   {EDevice ed=null;
    for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
     {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
      if(ed.ced.actionType==CEDevice.TYPE_TIMER) ed.ced.timeStep();
     }
   }

  public void startTimer(java.util.Timer timer)
   {EDevice ed=null;
    for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
     {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
      if(ed.ced.actionType==CEDevice.TYPE_TIMER) ed.ced.startTimer(timer);
     }
   }

  public int getESwitchCount()
   {int count=0;
    EDevice ed=null;
    for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
     {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
      if(ed.ced.actionType==CEDevice.TYPE_MECHANIC) count++;
     }
     return count;
   }

}
