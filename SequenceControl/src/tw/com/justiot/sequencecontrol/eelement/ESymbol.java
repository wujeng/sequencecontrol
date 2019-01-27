package tw.com.justiot.sequencecontrol.eelement;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.panel.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ESymbol extends JPanel implements MouseListener,ActionListener
 { public CDOutput cdo;
   private Image EFsol1;
   private Image EFsol1on;
   private Image EFsol2;
   private Image EFsol2on;

   public Image EDno;
   public Image EDnohc;
   public Image EDnc;
   public Image EDncho;
   public Image EDno2;
   public Image EDnohc2;
   public Image EDnc2;
   public Image EDncho2;

   public static double ratio=1.0;
   private static Dimension cellDim;
   public static int DeviceWidth0,DeviceHeight0;
   private int imagex1,imagex2,imagex3,imagey1,imagey2,imagey3;
   private int Width,Height;
   private int cellwid,cellhgt;

  public JPopupMenu popup;
  protected JMenuItem cMenuItem1,cMenuItem2;
  protected ElectricListener electriclistener;
   public ESymbol(CDOutput cdo, ElectricListener electriclistener)
   {super(true);
     this.electriclistener=electriclistener;
     this.cdo=cdo;
     EFsol1=ImageMap.getImage(cdo.modelType,cdo.modelName,"EFsol1");
     EFsol1on=ImageMap.getImage(cdo.modelType,cdo.modelName,"EFsol1on");
     EFsol2=ImageMap.getImage(cdo.modelType,cdo.modelName,"EFsol2");
     EFsol2on=ImageMap.getImage(cdo.modelType,cdo.modelName,"EFsol2on");
     EDno=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDno");
     EDnohc=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDnohc");
     EDnc=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDnc");
     EDncho=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDncho");
     EDno2=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDno2");
     EDnohc2=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDnohc2");
     EDnc2=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDnc2");
     EDncho2=ImageMap.getImage(cdo.modelType,cdo.modelName,"EDncho2");
//System.out.println("ESymbol entry");
     if(cellDim==null)
      {if(EDevice.cellDim!=null)
         {cellDim=EDevice.cellDim;
           DeviceWidth0=cellDim.width*2+3+5;
           DeviceHeight0=cellDim.height+5+4+12*2;
/*
          DeviceWidth0=cellDim.width*2+3+5;
          if(EDno2==null)
           DeviceHeight0=cellDim.height*2+5+4+12*2;
          else
           DeviceHeight0=cellDim.height*3+5+4+12*2;
*/
         }
        else
         {ImageIcon ii=new ImageIcon(EFsol1);
           int wid=ii.getIconWidth();
           int hgt=ii.getIconHeight();
           if(wid==-1) {wid=25;  System.err.println("error in sys.getEFsol1()");}
           if(hgt==-1) {hgt=32;  System.err.println("error in sys.getEFsol1()");}
           cellDim=new Dimension(wid,hgt);
           DeviceWidth0=cellDim.width*2+3+5;
           DeviceHeight0=cellDim.height+5+4+12*2;
/*
           DeviceWidth0=cellDim.width*2+3+5;
           if(EDno2==null)
            DeviceHeight0=cellDim.height*2+5+4+12*2;
           else
        	DeviceHeight0=cellDim.height*3+5+4+12*2;
*/
         }
      }
/*

*/
//System.out.println("ESymbol rescale");
     rescale();
    addMouseListener(this);
    /*
      popup = new JPopupMenu();
      if(EFsol2==null)
       {cMenuItem1 = new JMenuItem(Config.getString("ESymbol.Connection"));
         cMenuItem1.addActionListener(this);
         cMenuItem1.addMouseListener(new menuItemMouseAdapter());
         popup.add(cMenuItem1);
       }
      else
       {cMenuItem1 = new JMenuItem(Config.getString("ESymbol.Connection1"));
         cMenuItem1.addActionListener(this);
         cMenuItem1.addMouseListener(new menuItemMouseAdapter());
         popup.add(cMenuItem1);
         cMenuItem2 = new JMenuItem(Config.getString("ESymbol.Connection2"));
         cMenuItem2.addActionListener(this);
         cMenuItem2.addMouseListener(new menuItemMouseAdapter());
         popup.add(cMenuItem2);
       }
       */
   }

  public void rescale()
    {
//System.err.println(cellDim.width+":"+cellDim.height);
     Width=(int)(((double)DeviceWidth0)*ratio);
     Height=(int)(((double)DeviceHeight0)*ratio);
//System.err.println(Width+":"+Height);
//     setPreferredSize(new Dimension(Width,Height));
     cellwid=(int)(((double)cellDim.width)*ratio);
     cellhgt=(int)(((double)cellDim.height)*ratio);
     imagex1=(int) (4*ratio);
     imagey1=(int) (16*ratio);

     imagex2=imagex1+cellwid+1;
     imagey2=imagey1+cellhgt+1;

     imagex3=imagex2+cellwid+1;
     imagey3=imagey2+cellhgt+1;

     setSize(Width,Height);
    }

  private void drawFrame(Graphics g)
   {g.setColor(Color.white);
    g.drawLine(0,Height-2,0,0);
    g.drawLine(0,0,Width-2,0);
    g.drawLine(Width-2,0,Width-2,Height-2);
    g.drawLine(Width-2,Height-2,0,Height-2);

    g.setColor(Color.gray);
    g.drawLine(1,Height-3,1,1);
    g.drawLine(1,1,Width-3,1);
    g.drawLine(Width-1,1,Width-1,Height-1);
    g.drawLine(Width-1,Height-1,1,Height-1);

  }

  /*
  private Image offScreenBuffer=null;
  public void update(Graphics g)
   {Graphics gr=null;
    if(offScreenBuffer==null || (!(offScreenBuffer.getWidth(this) == this.size().width
                                && offScreenBuffer.getHeight(this) == this.size().height)))
     offScreenBuffer=this.createImage(size().width, size().height);
    gr=offScreenBuffer.getGraphics();
    paint(gr);
    g.drawImage(offScreenBuffer,0,0,this);
   }
*/

  public void paintComponent(Graphics g)
   {super.paintComponent(g);
    rescale();
    drawFrame(g);
/*
    if(cdo.SolFStatus)
     {g.drawImage(EDnohc,imagex1,imagey1,cellwid,cellhgt,this);
      g.drawImage(EDncho,imagex2,imagey1,cellwid,cellhgt,this);
     }
    else
     {g.drawImage(EDno,imagex1,imagey1,cellwid,cellhgt,this);
      g.drawImage(EDnc,imagex2,imagey1,cellwid,cellhgt,this);
     }
    if(EDno2!=null)
     {if(cdo.SolBStatus)
       {g.drawImage(EDnohc2,imagex1,imagey2,cellwid,cellhgt,this);
        g.drawImage(EDncho2,imagex2,imagey2,cellwid,cellhgt,this);
       }
      else
       {g.drawImage(EDno2,imagex1,imagey2,cellwid,cellhgt,this);
        g.drawImage(EDnc2,imagex2,imagey2,cellwid,cellhgt,this);
       }
     }
 */
    String fname=cdo.getName();
//  System.err.println(fname);
    g.setColor(Color.black);
    g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*ratio)));
    if(fname.toLowerCase().indexOf("null")<0) g.drawString(fname,imagex1,imagey1-4);

//    int y1=imagey2;
//    if(EDno2!=null) y1=imagey3;
    if(EFsol1!=null)
     {if(cdo.getSolFStatus())
       g.drawImage(EFsol1on,imagex1,imagey1,cellwid,cellhgt,this);
      else
       g.drawImage(EFsol1,imagex1,imagey1,cellwid,cellhgt,this);
    if(!cdo.modelType.equals("Water"))
    {
      if(EFsol2!=null) fname=fname+"+";
      g.setColor(Color.blue);
      g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*ratio)));
      if(fname.toLowerCase().indexOf("null")<0)
       g.drawString(fname,imagex1,imagey1+cellhgt-4);
    }
      String str=cdo.FPLCAddress;
//      if(NAPKey1!=null && NAPKey1.length()>0 && !NAPKey1.toLowerCase().equals("null")) str=NAPKey1+" "+Integer.toString(NAPno1);
      if(cdo.NAPKey1!=null && cdo.NAPKey1.length()>0 && !cdo.NAPKey1.toLowerCase().equals("null"))
       str=cutNAPKey(cdo.NAPKey1,2)+" "+Integer.toString(cdo.NAPno1);
      if(str!=null && str.length()>0)
       {g.setColor(Color.blue);
//System.err.println(str);
         g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*ratio)));
         if(str.toLowerCase().indexOf("null")<0)
           g.drawString(str,imagex1,Height-4);
       }
    }

    if(EFsol2!=null)
     {if(cdo.getSolBStatus())
        g.drawImage(EFsol2on,imagex2,imagey1,cellwid,cellhgt,this);
       else
        g.drawImage(EFsol2,imagex2,imagey1,cellwid,cellhgt,this);

     if(!cdo.modelType.equals("Water"))
     {
       String bname=cdo.getName()+"-";
       g.setColor(Color.blue);
       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*ratio)));
       if(bname.toLowerCase().indexOf("null")<0)
        g.drawString(bname,imagex2,imagey1+cellhgt-4);
     }
       String str=cdo.BPLCAddress;
//       if(NAPKey2!=null && NAPKey2.length()>0 && !NAPKey2.toLowerCase().equals("null")) str=NAPKey2+" "+Integer.toString(NAPno2);
       if(cdo.NAPKey2!=null && cdo.NAPKey2.length()>0 && !cdo.NAPKey2.toLowerCase().equals("null"))
         str=cutNAPKey(cdo.NAPKey2,2)+" "+Integer.toString(cdo.NAPno2);
       if(str!=null && str.length()>0)
        {g.setColor(Color.blue);
          g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*ratio)));
//System.err.println(str);
          if(str.toLowerCase().indexOf("null")<0)
           g.drawString(str,imagex2,Height-4);
        }
     }

   }


 private String cutNAPKey(String napkey, int n)
  {if(napkey==null || napkey.length()==0) return "";
    int ind=napkey.lastIndexOf('.');
    String str=napkey.substring(ind+1,napkey.length());
    if(str.length()>n) str=str.substring(0,n);
    return str;
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
//System.out.println("mousepressed");
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {
//System.out.println("mousepressed left");
	    int wid=imagex2-imagex1;
         int hgt=cellhgt;
/*
         boolean inAreano=(ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey1+hgt);
         boolean inAreanc=(ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey1+hgt);
         boolean inAreano2=(EDno2!=null && ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey1+hgt);
         boolean inAreanc2=(EDno2!=null && ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey1+hgt);
         if(inAreano)
          {
           switch(WebLadderCAD.opMode)
            {case WebLadderCAD.OP_SIMULATION:
                 break;
              case WebLadderCAD.OP_EDIT:
            	 WebLadderCAD.electrics.electricPanel.AddCell(LadderCell.T_System,LadderCell.G_SFNO,null,cdo,true);
                 WebLadderCAD.self.modified=true;
                 WebLadderCAD.electrics.electricPanel.repaint();
                 break;
              case WebLadderCAD.OP_INPUT:
                 break;
            }
          }
         else if(inAreanc)
          {
           switch(WebLadderCAD.opMode)
            {case WebLadderCAD.OP_SIMULATION:
                 break;
             case WebLadderCAD.OP_EDIT:
           	    WebLadderCAD.electrics.electricPanel.AddCell(LadderCell.T_System,LadderCell.G_SFNC,null,cdo,true);
                WebLadderCAD.self.modified=true;
                WebLadderCAD.electrics.electricPanel.repaint();
                break;
             case WebLadderCAD.OP_INPUT:
                break;
            }
          }
         else if(inAreano2)
          {
           switch(WebLadderCAD.opMode)
            {case WebLadderCAD.OP_SIMULATION:
                break;
             case WebLadderCAD.OP_EDIT:
          	    WebLadderCAD.electrics.electricPanel.AddCell(LadderCell.T_System,LadderCell.G_SBNO,null,cdo,true);
               WebLadderCAD.self.modified=true;
               WebLadderCAD.electrics.electricPanel.repaint();
               break;
             case WebLadderCAD.OP_INPUT:
               break;
            }
          }
         else if(inAreanc2)
          {
           switch(WebLadderCAD.opMode)
            {case WebLadderCAD.OP_SIMULATION:
               break;
             case WebLadderCAD.OP_EDIT:
         	    WebLadderCAD.electrics.electricPanel.AddCell(LadderCell.T_System,LadderCell.G_SBNC,null,cdo,true);
              WebLadderCAD.self.modified=true;
              WebLadderCAD.electrics.electricPanel.repaint();
              break;
             case WebLadderCAD.OP_INPUT:
              break;
            }
          }
*/
//         int soly=imagey1;
//         if(EDno!=null) soly=imagey2;
         boolean inArea1=(ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey1+hgt);
         boolean inArea2=(ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey1+hgt);
         if(inArea1)
         {switch(electriclistener.getOpMode())
            {case SCCAD.OP_SIMULATION:
                 cdo.setSolFStatus(true);
                 cdo.setOutputFStatus(true);
                 break;
              case SCCAD.OP_EDIT:
//if(WebLadderCAD.debug) System.out.println("inArea1 Edit");
            	 electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,cdo,true);
                 electriclistener.setModified(true);
                 electriclistener.getElectricPanel().repaint();
                 break;
              case SCCAD.OP_INPUT:
/*
                 if(Sequence.self!=null)
                  {Sequence.self.sequencePanel.AddCell(SequenceCell.IT_System,sys,sys.getLastLimitswitch(),SequenceCell.ID_Forward,true);
                    Sequence.self.sequencePanel.modified=true;
                    Sequence.self.sequencePanel.repaint();
                  }
*/
                 break;
            }
         }
         if(EFsol2!=null && inArea2)
         {switch(electriclistener.getOpMode())
            {case SCCAD.OP_SIMULATION:
                 cdo.setSolBStatus(true);
                 cdo.setOutputBStatus(true);
                 break;
              case SCCAD.OP_EDIT:
            	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL2,null,cdo,true);
            	  electriclistener.setModified(true);
                 electriclistener.getElectricPanel().repaint();
                 break;
              case SCCAD.OP_INPUT:
/*
                 if(Sequence.self!=null)
                  {Sequence.self.sequencePanel.AddCell(SequenceCell.IT_System,sys,sys.getFirstLimitswitch(),SequenceCell.ID_Backward,true);
                    Sequence.self.sequencePanel.modified=true;
                    Sequence.self.sequencePanel.repaint();
                  }
*/
                 break;
            }
         }
//         electricPanel.repaint();
//         repaint();
  //      electriclistener.repaint();
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
      if(left)
       {int wid=imagex2-imagex1;
         int hgt=cellhgt;
      //   int soly=imagey2;
      //   if(EDno!=null) soly=imagey3;
         boolean inArea1=(ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey1+hgt);
         boolean inArea2=(ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey1+hgt);
         
         if(inArea1)
         {switch(electriclistener.getOpMode())
            {case SCCAD.OP_SIMULATION:
                 cdo.setSolFStatus(false);
                 cdo.setOutputFStatus(false);
                 break;
              case SCCAD.OP_EDIT:
                 break;
              case SCCAD.OP_INPUT:
                 break;
            }
         }
         if(inArea2)
         {switch(electriclistener.getOpMode())
            {case SCCAD.OP_SIMULATION:
                 cdo.setSolBStatus(false);
                 cdo.setOutputBStatus(false);
                 break;
              case SCCAD.OP_EDIT:
                 break;
              case SCCAD.OP_INPUT:
                 break;
            }
         }
//        electricPanel.repaint();
//        repaint();
   //     electriclistener.repaint();
       }
      else
       maybeShowPopup(pop,ex,ey);
    }

   public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop)
       {
        // if(Modules.modules==null || Modules.modules.size()==0)
             {cMenuItem1.setEnabled(false);
               if(EFsol2!=null) cMenuItem2.setEnabled(false);
             }
         //   else
         //    {cMenuItem1.setEnabled(true);
         //      if(EFsol2!=null) cMenuItem2.setEnabled(true);
         //    }
        popup.show(this, ex, ey);
//        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }

   public void actionPerformed(ActionEvent e){

         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }

  public void ActionPerformed(JMenuItem mi,String op,String input)
    {/*
      String option=mi.getText();
//System.err.println("option:"+option);
     if(option.equals(Config.getString("ESymbol.Connection")) || option.equals(Config.getString("ESymbol.Connection1")) || option.equals(Config.getString("ESymbol.Connection2")))
      {
       String solname=cdo.getName();
          if(option.equals(Config.getString("ESymbol.Connection1"))) solname=solname+"+";
          if(option.equals(Config.getString("ESymbol.Connection2"))) solname=solname+"-";
          String NAPKey=cdo.NAPKey1;
          int NAPno=cdo.NAPno1;
          if(option.equals(Config.getString("ESymbol.Connection2"))) {NAPKey=cdo.NAPKey2;NAPno=cdo.NAPno2;}
         ConnectionDialog cDialog = new ConnectionDialog(electriclistener.getFrame(),solname,false,NAPKey,NAPno);
         cDialog.pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         cDialog.setLocation(
		screenSize.width/2 - cDialog.getSize().width/2,
		screenSize.height/2 - cDialog.getSize().height/2);
         cDialog.setVisible(true);
         String rstr=cDialog.getText();
         if(option.equals(Config.getString("ESymbol.Connection2"))) setNAP(rstr,false);
         else setNAP(rstr,true);
//         repaint();

         electriclistener.repaint();
     }
     */
  }
/*
  public void setNAP(String str, boolean isFirst)
    {if(str==null || str.length()==0) return;
      String NAPKey=cdo.NAPKey1;
      int NAPno=cdo.NAPno1;
      if(!isFirst) {NAPKey=cdo.NAPKey2;NAPno=cdo.NAPno2;}

      ModuleBase mb=null;
      if(NAPKey!=null)
       {mb=(ModuleBase) Modules.modules.get(NAPKey);
//         m40.outObject[NAPno]=null;
         mb.outObject[NAPno]=(Object)null;
//         m40.repaint();
       }
      if(str.equals("None"))
       {if(isFirst) cdo.NAPKey1=null;
         else cdo.NAPKey2=null;
       }
      else
       {int ind=str.indexOf('_');
         if(ind==-1)
          {electriclistener.setStatus(Config.getString("EDevice.connection.returnstrerror"));
            return;
          }
         NAPKey=str.substring(0,ind);
         NAPno=Integer.parseInt(str.substring(ind+1,str.length()));
         mb=(ModuleBase) Modules.modules.get(NAPKey);
//         m40.outObject[NAPno]=this;
         mb.outObject[NAPno]=(Object)this.cdo;
//         m40.repaint();
         if(isFirst) {cdo.NAPKey1=NAPKey;cdo.NAPno1=NAPno;}
         else {cdo.NAPKey2=NAPKey;cdo.NAPno2=NAPno;}
       }
      repaint();
    }
    */
  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());
       }
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
 }
