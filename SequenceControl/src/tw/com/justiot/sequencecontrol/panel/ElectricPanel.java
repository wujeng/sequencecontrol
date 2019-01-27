package tw.com.justiot.sequencecontrol.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.ImageMap;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.util;
import tw.com.justiot.sequencecontrol.config.EDeviceParameter;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.dialog.DBDialog;
import tw.com.justiot.sequencecontrol.eelement.CDOutput;
import tw.com.justiot.sequencecontrol.eelement.CEDevice;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESystem;
import tw.com.justiot.sequencecontrol.eelement.ElectricFace;
import tw.com.justiot.sequencecontrol.eelement.PLC;
import tw.com.justiot.sequencecontrol.pelement.Actuator;

public class ElectricPanel extends JPanel implements MouseListener,MouseMotionListener,
        ActionListener
 {
  public static final int Command_addCell=1;
  public static final int Command_clear=2;
  public static final int Command_setCursor=3;
  public static final int Command_block=4;
  public static final int Command_pasteBoardTo=5;
  public static final int Command_copyToBoard=6;
  public static final int Command_deleteBlock=7;
  public static final int Command_changeEditMode=8;
  public static final int Command_changeRatio=9;

   private static final int MaxRows=40;
   private static final int MaxColumns=16;

  private static Image[] wireImage;
  public static Image[] iwireImage;
  public static Image cursorImage;
  public LadderCell[][] matrix;
  private int LadderRangeCol,LadderRangeRow,StartRow,StartCol;

  public double ratio=1.0;
  private boolean checked;
  public boolean overWrite;
  public Point dragPoint1=new Point(),dragPoint2=new Point();
  private int cellwid,cellhgt;

  public PLC FPLC;
  private boolean running=false;
  private boolean dragging=false;
  private boolean hasCommand=false;
  public ArrayList cellGroup;
  public int groupWidth;

//  private int clipRows,clipColumns;
  private int LadderWinRow1,LadderWinRow2,LadderWinCol1,LadderWinCol2;
  private int GroundCol;
  private int Temp;

  public JPopupMenu popup;
  JMenuItem m,mp,mm;
  private ElectricListener electriclistener;
  public ElectricPanel(ElectricListener electriclistener)
   {super();
    this.electriclistener=electriclistener;
    setLayout(null);
    setBackground(Color.white);
    init();
    addMouseListener(this);
    addMouseMotionListener(this);
 //   addKeyListener(this);
    menuItemMouseAdapter allmenuItemMouseAdapter=new menuItemMouseAdapter();
    popup = new JPopupMenu();
    m = new JMenuItem(Config.getString("ElectricPanel.delete"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);
    m = new JMenuItem(Config.getString("ElectricPanel.cut"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);
    mp = new JMenuItem(Config.getString("ElectricPanel.paste"));
    mp.addActionListener(this);
    mp.addMouseListener(allmenuItemMouseAdapter);
    popup.add(mp);
    m = new JMenuItem(Config.getString("ElectricPanel.copy"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);
    popup.addSeparator();
    overWrite=false;
    mm = new JMenuItem(Config.getString("Status.overwrite"));
    mm.addActionListener(this);
    mm.addMouseListener(allmenuItemMouseAdapter);
    popup.add(mm);
    popup.addSeparator();
    m = new JMenuItem(Config.getString("ElectricPanel.selectall"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);
    m = new JMenuItem(Config.getString("ElectricPanel.clearall"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);
    popup.addSeparator();
    m = new JMenuItem(Config.getString("ElectricPanel.zoom"));
    m.addActionListener(this);
    m.addMouseListener(allmenuItemMouseAdapter);
    popup.add(m);

    FPLC=new PLC("OMRON C20", electriclistener);
    FPLC.codeReady=false;
    cellGroup=new ArrayList();
//    rescale();
//    repaint();
   }

  public boolean isEmpty()
    {rescale();
      if(LadderRangeCol>StartCol+1 || LadderRangeRow>StartRow)
       return false;
      else
       return true;
   }

  private void readWire()
   {if(wireImage!=null) return;
    wireImage=new Image[9];
    iwireImage=new Image[9];
    String path=null;
    for(int i=0;i<9;i++)
     {path="/resources/images/Wire/w"+Integer.toString(i+1)+".gif";
       wireImage[i]=util.loadImage("electricPanel","","wireImage"+i,path);
       path="/resources/images/Wire/w"+Integer.toString(i+1)+"c.gif";
       iwireImage[i]=util.loadImage("electricPanel","","iwireImage"+i,path);
     }
    path="/resources/images/Wire/cursor.gif";
    cursorImage=util.loadImage("electricPanel","","cursorImage",path);
   }

  private void init()
   {readWire();
    ratio=1.0;
    checked=false;
    overWrite=false;
    matrix=new LadderCell[MaxRows][MaxColumns];
    for(int i=0;i<MaxRows;i++)
     for(int j=0;j<MaxColumns;j++)
      matrix[i][j]=new LadderCell();
//    matrix[0][0].type=LadderCell.T_Wire;            // �w�]���u���m
//    matrix[0][0].state=LadderCell.G_Power;
    dragPoint1.x=0;
    dragPoint1.y=0;
    dragPoint2.x=dragPoint1.x;
    dragPoint2.y=dragPoint2.y;
  }


  int minx=0,miny=0,maxx,maxy;
  private Dimension area=new Dimension(0,0);
  private void rescroll()
   {maxx=(LadderRangeCol+1)*cellwid;
    maxy=(LadderRangeRow+1)*cellhgt;
  	scrollRectToVisible(new Rectangle(minx,miny,maxx,maxy));
  	area.width=maxx;
  	area.height=maxy;
  	setPreferredSize(area);
    revalidate();
   }

  private void rescale()
   {
    LadderRangeCol=MaxColumns-1;
    LadderRangeRow=0;
    for(int j=LadderRangeCol;j>=0;j--)
     for(int i=MaxRows-1;i>=0;i--)
      if (matrix[i][j].type >= LadderCell.T_Wire)
       {if(LadderRangeCol==(MaxColumns-1)) LadderRangeCol=j;
         if(LadderRangeRow<i) LadderRangeRow=i;
       }
    StartRow=LadderRangeRow;
    StartCol=LadderRangeCol;
    for(int j=0;j<=LadderRangeCol;j++)
     for(int i=0;i<=LadderRangeRow;i++)
      if(matrix[i][j].type >= LadderCell.T_Wire)
       {if(StartCol==LadderRangeCol) StartCol=j;
         if(StartRow>i) StartRow=i;
       }

//       StartRow=0;
//       StartCol=0;
//System.err.println(LadderRangeRow+":"+LadderRangeCol);
 //   if(dragPoint2.x>LadderRangeCol) LadderRangeCol=dragPoint2.x;
 //   if(dragPoint2.y>LadderRangeRow) LadderRangeRow=dragPoint2.y;
  // �]�w�����ϥi�����d��(�G�����󪺽d��)
      // ���o�����Ϥ�,��ø�X���d��
    cellwid=(int) (EDeviceParameter.imageDim.width*ratio);
    cellhgt=(int) (EDeviceParameter.imageDim.height*ratio);

    if(electriclistener.getElectricsElectricScrollPane()!=null)
     {Rectangle rect=electriclistener.getElectricsElectricScrollPane().getViewport().getViewRect();
      LadderWinCol1=rect.x/cellwid;
      LadderWinCol2=LadderWinCol1+rect.width/cellwid;
      LadderWinRow1=rect.y/cellhgt;
      LadderWinRow2=LadderWinRow1+rect.height/cellhgt;
     }
    if(LadderWinCol1<0) LadderWinCol1=0;
    if(LadderWinRow1<0) LadderWinRow1=0;
    if(LadderWinCol2>=MaxColumns-1) LadderWinCol2=MaxColumns-1;
    if(LadderWinRow2>=MaxRows-1) LadderWinRow2=MaxRows-1;

    rescroll();
//    setPreferredSize(new Dimension((LadderRangeCol+1)*cellwid, (LadderRangeRow+1)*cellhgt));
  }

  private Image CEDeviceImage(int i,int j,String var)
   {CEDevice ced=matrix[i][j].ced;
    Image img=(Image) ImageMap.getImage(ced.modelType, ced.modelName, var);
// System.out.println("img="+img+" for key="+key);
	return img;
   }
  private Image CDOutputImage(int i,int j,String var)
   {CDOutput cdo=matrix[i][j].cdo;
	return (Image) ImageMap.getImage(cdo.modelType, cdo.modelName, var);
   }
  public void PaintCell(Graphics g,int i,int j)
   {if(i<LadderWinRow1 || i>LadderWinRow2 || j<LadderWinCol1 || j>LadderWinCol2) return;
     int x0=j*cellwid;
     int y0=i*cellhgt;
//System.out.println("i:"+i+":J:"+j);
  if(matrix[i][j].type >= LadderCell.T_Wire && i < MaxRows && j < MaxColumns)
   {switch(matrix[i][j].type)
     {case LadderCell.T_Wire:
        g.drawImage(wireImage[matrix[i][j].state],x0,y0,cellwid,cellhgt,this);
        break;
      case LadderCell.T_EDevice:
        if(matrix[i][j].ced!=null)
         {switch(matrix[i][j].state)
           {case LadderCell.G_NO:
             if(matrix[i][j].ced.status)
              g.drawImage(CEDeviceImage(i,j,"inohc"),x0,y0,cellwid,cellhgt,this);
             else
              g.drawImage(CEDeviceImage(i,j,"ino"),x0,y0,cellwid,cellhgt,this);
             break;
            case LadderCell.G_NC:
             if(matrix[i][j].ced.status)
              g.drawImage(CEDeviceImage(i,j,"incho"),x0,y0,cellwid,cellhgt,this);
             else
              g.drawImage(CEDeviceImage(i,j,"inc"),x0,y0,cellwid,cellhgt,this);
             break;
            case LadderCell.G_ESOL1:
             if(matrix[i][j].ced.sol1Status)
              g.drawImage(CEDeviceImage(i,j,"isol1on"),x0,y0,cellwid,cellhgt,this);
             else
              g.drawImage(CEDeviceImage(i,j,"isol1"),x0,y0,cellwid,cellhgt,this);
             break;
            case LadderCell.G_ESOL2:
             if(matrix[i][j].ced.sol1Status)
              g.drawImage(CEDeviceImage(i,j,"isol2on"),x0,y0,cellwid,cellhgt,this);
             else
              g.drawImage(CEDeviceImage(i,j,"isol2"),x0,y0,cellwid,cellhgt,this);
             break;
           }
          if(matrix[i][j].ced.name!=null)
           {g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (9*ratio)));
             g.setColor(Color.blue);
             g.drawString(matrix[i][j].ced.name,x0+2,y0+cellhgt-2);
           }
         }
        else
         System.err.println("error: without ed in matrix["+i+"]["+j+"]");
        break;
      case LadderCell.T_System:
        if(matrix[i][j].cdo!=null)
         {boolean st;
             String sname=null;
             switch(matrix[i][j].state)
              {
               case LadderCell.G_SSOL1:
                  st=matrix[i][j].cdo.getSolFStatus();
   //       System.out.println(matrix[i][j].cdo.name+" "+st);
                  
                  if(st)
                   g.drawImage(CDOutputImage(i,j,"EFsol1on"),x0,y0,cellwid,cellhgt,this);
                  else
                   g.drawImage(CDOutputImage(i,j,"EFsol1"),x0,y0,cellwid,cellhgt,this);
//                  if(matrix[i][j].sys.withLS())
                  if(matrix[i][j].cdo.twoWay)
                   sname=matrix[i][j].cdo.getName()+"+";
                  else
                   sname=matrix[i][j].cdo.getName();
                  break;
               case LadderCell.G_SSOL2:
                  st=matrix[i][j].cdo.getSolBStatus();
                  if(st)
                   g.drawImage(CDOutputImage(i,j,"EFsol2on"),x0,y0,cellwid,cellhgt,this);
                  else
                   g.drawImage(CDOutputImage(i,j,"EFsol2"),x0,y0,cellwid,cellhgt,this);
                  sname=matrix[i][j].cdo.getName()+"-";
                  break;
              }
             if(sname!=null)
              {g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (9*ratio)));
                g.setColor(Color.blue);
                g.drawString(sname,x0+2,y0+cellhgt-2);
              }
            }
           else
             System.err.println("error: without sys in matrix["+i+"]["+j+"]");
           break;
      }
/*
g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (9*ratio)));
g.setColor(Color.red);
g.drawString(Integer.toString(matrix[i][j].Tshapepos),x0+cellwid-8,y0+cellhgt-2);
*/
   }
  else                 // �S�����󪺦a��,�e����
   {g.setColor(Color.white);
    g.fillRect(x0,y0,cellwid,cellhgt);
   }
}

private void MarkArea(Graphics g)
{ int i,j;         // ���ܴ���(�϶�)
  for(i=dragPoint1.x;i<=dragPoint2.x;i++)
   for(j=dragPoint1.y;j<=dragPoint2.y;j++)
    {g.setXORMode(Color.yellow);
     g.drawImage(cursorImage,i*cellwid,j*cellhgt,cellwid,cellhgt,this);
    }
}

private void copyMatrix(int row1,int col1,int row2,int col2)
  {matrix[row2][col2].type=matrix[row1][col1].type;
    matrix[row2][col2].state=matrix[row1][col1].state;
    matrix[row2][col2].ced=matrix[row1][col1].ced;
    matrix[row2][col2].cdo=matrix[row1][col1].cdo;
    matrix[row2][col2].Tshapepos=matrix[row1][col1].Tshapepos;
    matrix[row2][col2].dir=matrix[row1][col1].dir;
  }

   private class addCellCommand extends Command
    {boolean modified0;
	 Point p0;
	 int type0;
     int state0;
     CEDevice ced0;
     CDOutput cdo0;
     int type1;
     int state1;
     CEDevice ced1;
     CDOutput cdo1;
     boolean ShiftEnabled;
  	 public addCellCommand(Object ele,boolean ShiftEnabled,boolean modified0,Point p0,
  	   int type0,int state0,CEDevice ced0,CDOutput cdo0,
  	   int type1,int state1,CEDevice ced1,CDOutput cdo1)
      {super("ElectricPanel",ele,Command_addCell);
       this.ShiftEnabled=ShiftEnabled;
       this.modified0=modified0;
       this.p0=p0;
       this.type0=type0;
       this.state0=state0;
       this.ced0=ced0;
       this.cdo0=cdo0;
       this.type1=type1;
       this.state1=state1;
       this.ced1=ced1;
       this.cdo1=cdo1;
      }
    public void undo()
     {if(!overWrite && ShiftEnabled)
       {for(int j=p0.x;j<MaxColumns-2;j++)
        copyMatrix(p0.y,j+1,p0.y,j);
       }
      matrix[p0.y][p0.x].type=type0;
      matrix[p0.y][p0.x].state=state0;
      matrix[p0.y][p0.x].ced=ced0;
      matrix[p0.y][p0.x].cdo=cdo0;
      dragPoint1=new Point(p0);
//      rescale();
      repaint();
      electriclistener.setModified(modified0);
     }
    public void redo()
     {dragPoint1=new Point(p0);
	  if(!overWrite && ShiftEnabled)
       {for(int j=MaxColumns-2;j>=p0.x;j--)
        copyMatrix(p0.y,j,p0.y,j+1);
       }
      matrix[p0.y][p0.x].type=type1;
      matrix[p0.y][p0.x].state=state1;
      matrix[p0.y][p0.x].ced=ced1;
      matrix[p0.y][p0.x].cdo=cdo1;
      ShiftCursor();
//      rescale();
      repaint();
      electriclistener.setModified(true);
     }
   }

public void AddCell(int type,int state,CEDevice ced,CDOutput cdo,boolean ShiftEnabled)
{Point p0=new Point(dragPoint1);
 int type0=matrix[dragPoint1.y][dragPoint1.x].type;
 int state0=matrix[dragPoint1.y][dragPoint1.x].state;
 CEDevice ced0=matrix[dragPoint1.y][dragPoint1.x].ced;
 CDOutput cdo0=matrix[dragPoint1.y][dragPoint1.x].cdo;
 boolean modified0=electriclistener.getModified();
 if(!overWrite && ShiftEnabled)
   {for(int j=MaxColumns-2;j>=dragPoint1.x;j--)
       copyMatrix(dragPoint1.y,j,dragPoint1.y,j+1);
   }
  matrix[dragPoint1.y][dragPoint1.x].type=type;
  matrix[dragPoint1.y][dragPoint1.x].state=state;
  matrix[dragPoint1.y][dragPoint1.x].ced=ced;
  matrix[dragPoint1.y][dragPoint1.x].cdo=cdo;
  ShiftCursor();
//  rescale();
  repaint();
  electriclistener.setModified(true);
  electriclistener.addCommand(new addCellCommand(this,ShiftEnabled,modified0,p0,type0,state0,ced0,cdo0,
                                      type,state,ced,cdo));
}

private void ShiftCursor() // �]�w�q�𤸥󪺤��e
{
  if(dragPoint1.x < MaxColumns -1)   // ���Хk���@��
   {dragPoint1.x++;dragPoint2.x=dragPoint1.x;}
  else if (dragPoint1.y < MaxRows -1 )
   {dragPoint1.y++;dragPoint1.x=0;
    dragPoint2.x=dragPoint1.x;      // �p�G���F����,���в����U�@�C���}�Y
    dragPoint2.y=dragPoint1.y;
   }
  else
   {electriclistener.setStatus("exceed");
   }
}

/*
  private Image offScreenBuffer=null;
  public void update(Graphics g)
   {Graphics gr=null;
    if(offScreenBuffer==null || (!(offScreenBuffer.getWidth(this) == this.getSize().width
                                && offScreenBuffer.getHeight(this) == this.getSize().height)))
     offScreenBuffer=this.createImage(getSize().width, getSize().height);
    gr=offScreenBuffer.getGraphics();
    paint(gr);
    g.drawImage(offScreenBuffer,0,0,this);
   }
*/
public void paintComponent(Graphics g)
 {super.paintComponent(g);
        // �e�X�����Ϧb LadderPaint �ϭ�    !InputMode
         // �e�X�ʧ@���Ǧb LadderPaint �ϭ�   InputMode
  rescale();

//System.err.println(LadderWinRow1+":"+LadderWinRow2+":"+LadderWinCol1+":"+LadderWinCol2);
  for (int i=LadderWinRow1;i<=LadderWinRow2;i++)
   for (int j=LadderWinCol1;j<=LadderWinCol2;j++)
    PaintCell(g,i,j);
// if(!PLCRunFlag) MarkArea(g);
  MarkArea(g);
 }

//---------------------------------------------------------------------------
public boolean LadderCheck()
{//if(electrics.pneumatics.exampleURL!=null)
 //  {checked=true;return true;}
                           // �j�����R,�ˬd�����ϤW�����u���~
 // ���w�����ϤW���󪺤��G�d��
  if(LadderRangeCol <=0 && LadderRangeRow <= 0)
    {JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s1"));
     return false;
    }
//  InitState();       // ���m�t�u�������Хܭ�

  if(!CheckPowerLine()) return false;  // �ˬd�����ϤW���u�t�m�����~
  if(!CheckGround()) return false;     // �ˬd�a�u�O�_����?
  ConnectGround();
  deleteBrokenVline();
  if(!CheckBrokenLine()) return false; // �ˬd�u���t���O�_���_�u ?
  if(!CheckTshape()) return false;     // �ˬd���p�϶��O�_�����걵?
  if(!CheckOutput()) return false;     // �ˬd�t���O�_����?
  if(!CheckGround()) return false;     // �ˬd�a�u�O�_����?
  checked=true;
  electriclistener.setStatus(Config.getString("electricPanel.s3"));
  SetPos();
  return true;
}

private void ConnectGround()
{ int i,j,k;
  boolean find=false;
  j=LadderRangeCol;
  for(i=StartRow;i<=LadderRangeRow;i++)
   {for(j=LadderRangeCol;j>=StartCol;j--)
     if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Ground)
      {find=true;break;}
     if(find) break;
   }
  for(k=i+1;k<=LadderRangeRow;k++)
   {if(matrix[k][j].type==LadderCell.T_None &&
       (matrix[k-1][j].type==LadderCell.T_Wire &&
        (matrix[k-1][j].state==LadderCell.G_Ground || matrix[k-1][j].state==LadderCell.G_Vline)
       ))
     {matrix[k][j].type=LadderCell.T_Wire;
      matrix[k][j].state=LadderCell.G_Vline;
     }
   }
}

private boolean CheckGround()    // �ˬd�a�u�O�_����?
{ int i,j,k,gcount,gcol,ocount;

  for(i=StartRow;i<=LadderRangeRow;i++)  // �Y�� -| �~���a�u,�󥿤�
   {for(k=LadderRangeCol;k<=StartCol;k--) if(matrix[i][k].type >= LadderCell.T_Wire) break;
    if(matrix[i][k].type==LadderCell.T_Wire && matrix[i][k].state==LadderCell.G_RTshape)
     {
//System.err.println("ik"+i+":"+k);
      SetCursor(i,k);
      AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,false);
      break;
     }
   }

  gcol=StartCol;
  for(i=StartRow;i<=LadderRangeRow;i++)   // �ˬd�C�@�C�W�a�u���Ӽ�
   {gcount=0;ocount=0;
    for(j=StartCol;j<=LadderRangeCol;j++)
     {if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Ground)
       {gcount++;gcol=j;}
      if(matrix[i][j].type > LadderCell.T_Wire && IsLoad(i,j)) ocount++;
     }
    if(gcount >1)   // �P�@�C��,�Y���G�ӥH�W�t��,���~!
     { SetCursor(i,gcol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s4"));

       return false;
     }
    if(ocount > 0 && gcount == 0)  // �P�@�C��,�Y���t���S�a�u,���~!
     {SetCursor(i,LadderRangeCol);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s6"));

      return false;
     }
    if(gcount==1)                // �����a�u
     {SetCursor(i,LadderRangeCol);
//System.err.println("iLadd"+i+":"+LadderRangeCol);
      AddCell(LadderCell.T_Wire, LadderCell.G_Ground, null, null,false);
      for(k=gcol;k<LadderRangeCol;k++)
       {SetCursor(i,k);
//System.err.println("ik2"+i+":"+k);
        AddCell(LadderCell.T_Wire, LadderCell.G_Hline, null, null,false);
       }
     }
    else
     {if(i>0)
       {if(matrix[i-1][LadderRangeCol].type==LadderCell.T_Wire &&
          matrix[i-1][LadderRangeCol].state==LadderCell.G_Ground)
         {
          if(matrix[i][GroundCol].type==LadderCell.T_Wire && matrix[i][GroundCol].state==LadderCell.G_Vline)
            matrix[i][GroundCol].type=LadderCell.T_None;
          SetCursor(i,LadderRangeCol);
//System.err.println("iLadd2"+i+":"+LadderRangeCol);
          AddCell(LadderCell.T_Wire, LadderCell.G_Vline, null, null,false);
          GroundCol=LadderRangeCol;  // ??? Sure ?
         }
       }
     }
    }
   boolean breakflag=false;      // ���i���Y�u�j��
   for(j=LadderRangeCol-1;j>=StartCol;j--)
    {
     for(i=StartRow;i<=LadderRangeRow;i++)
      {
       if(!(matrix[i][j].type==LadderCell.T_None || (matrix[i][j].type==LadderCell.T_Wire &&
                                       matrix[i][j].state==LadderCell.G_Hline)))
        {breakflag=true;break;}
      }
     if(breakflag) break;
    }
   if(j+1 != LadderRangeCol)
    {for(i=StartRow;i<=LadderRangeRow;i++)
       copyMatrix(i,LadderRangeCol,i,j+1);
     for(k=j+2;k<=LadderRangeCol;k++)
      for(i=StartRow;i<=LadderRangeRow;i++) matrix[i][k].type = LadderCell.T_None;
     LadderRangeCol=j+1;
     GroundCol=LadderRangeCol;  // ??? Sure ?
    // SetLadderRange();
//     repaint();
    }
   GroundCol=LadderRangeCol;  // ??? Sure ?
//  SetLadderRange();
  return true;
}

private boolean CheckTshape() // �ˬd���p�϶��O�_�����걵?
{ int i,j;                              // �Y���p�϶��L�����걵,�h
                                        // ���p�϶������@���u���|�W�X�϶��d��
                                        // (�b���w���t�u���󪺭����U)
  for(i=StartRow;i<=LadderRangeRow;i++) // (�T�w���@T�Τ����U���t�u���󳣦b�P�@���V)
   for(j=StartCol;j<=LadderRangeCol;j++)
    if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Tshape)
      {if(!SetTshape(i,j)) return false;} // ���XT�ΰt�u���t�@��,�s�bmatrix[][].switchtype��,
                                          //���V�s�� matrix[][].label ��
  if(!TshapeBlock(StartRow,StartCol,LadderRangeCol)) return false;
  return true;
}

private boolean TshapeBlock(int row,int col1,int col2)
{ int i,j,k,first;                    // �ˬd���p�϶��������@���u���|�W�X�϶��d��
  boolean loop=true;
  if(col1 > col2)
   {i=col1;col1=col2;col2=i;}

//String stcol(col1);
//String endcol(col2);
//String scr="�q"+stcol+"��"+endcol;
//SetCursor(row,col1);
//Application->MessageBox(scr.c_str(),"T�ΰt�u���t�@��",MB_OK);

  i=row;
  while(loop)
   {first=col1+1;
    for(k=col2-1;k>=col1+1;k--)
     if(matrix[i][k].type==LadderCell.T_Wire && matrix[i][k].state==LadderCell.G_Tshape &&
        matrix[i][k].Tshapepos==col1)
      {if(!TshapeBlock(i,k,matrix[i][k].Tshapepos)) return false;
       first=k+1;
       break;
      }
    for(j=first;j<col2;j++)
     {
      if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Tshape)
       {if(matrix[i][j].Tshapepos > col2 || matrix[i][j].Tshapepos < col1)
         {SetCursor(i,j);
          JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s8"));

          return false;
         }                // �˥X���p�϶��������p�϶�,�Ļ��^�覡
        if(!TshapeBlock(i,j,matrix[i][j].Tshapepos)) return false;
        if(matrix[i][j].Tshapepos > j) j=matrix[i][j].Tshapepos;
       }
     }
    i=NextLine(i,col1,col2);
    if(i==-1) break;
   }
  return true;
}

private boolean SetTshape(int row,int col)
{ int irow;     // ���XT�ΰt�u���t�@��,�s�bmatrix[][].switchtype��,
                //  ���V�s�� matrix[][].label ��
  boolean dir;    // true: for Left
  irow=row+1;
  while(true)        // �����X���p�϶����Ĥ��V
     {if(CheckRightEnd(matrix[irow][col].state))
       {matrix[row][col].dir="Left";dir=true;break;}
      if(CheckLeftEnd(matrix[irow][col].state))
       {matrix[row][col].dir="Right";dir=false;break;}
      irow++;
     }
  matrix[row][col].Tshapepos=col;
  int ecol=0;
  while(true)
   {if(matrix[irow][col].type!=LadderCell.T_Wire || !CheckUpPass(matrix[irow][col].state)) break;
    if(CheckRightEnd(matrix[irow][col].state))
     {if(!dir)
       {SetCursor(irow,col);         // �T�w���@T�Τ����U���t�u���󳣦b�P�@���V
        JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s10"));

        return false;
       }
      ecol=EndCol(irow,col,dir);
     }
    if(CheckLeftEnd(matrix[irow][col].state))
     {if(dir)
       {SetCursor(irow,col);         // �T�w���@T�Τ����U���t�u���󳣦b�P�@���V
        JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s10"));

        return false;
       }
      ecol=EndCol(irow,col,dir);
     }
    if(dir)
     { if(ecol < matrix[row][col].Tshapepos) matrix[row][col].Tshapepos=ecol;}
    else
     { if(ecol > matrix[row][col].Tshapepos) matrix[row][col].Tshapepos=ecol;}
    irow++;
   }

//String stcol(col);
//String endcol(matrix[row][col].switchtype);
//String scr="�q"+stcol+"��"+endcol;
//SetCursor(row,col);
//Application->MessageBox(scr.c_str(),"T�ΰt�u���t�@��",MB_OK);
  return true;
}

private int EndCol(int row, int col, boolean dir)
{ int icol;
  icol=col;
  while(true)        // ���X���䪺�t�@��
   {if(dir)  // dir=true for Left
     {icol--;
      if(matrix[row][icol].type==LadderCell.T_Wire && CheckLeftEnd(matrix[row][icol].state))
       break;
     }
    else
     {icol++;
      if(matrix[row][icol].type==LadderCell.T_Wire && CheckRightEnd(matrix[row][icol].state))
       break;
     }
   }
// String stcol(col);
//String endcol(icol);
//String scr="�q"+stcol+"��"+endcol;
//SetCursor(row,col);
//Application->MessageBox(scr.c_str(),"T�ΰt�u���䪺�t�@��",MB_OK);
  return icol;
}

private boolean CheckOutput()    // �ˬd�t���O�_����?
{ int i,j,ii,jj,ocount,ocol;
  boolean hasGround,tempbool;
  if(!OutBlockCheck(StartRow,StartCol,LadderRangeCol)) return false;
  for(i=StartRow;i<=LadderRangeRow;i++)
   { if(matrix[i][LadderRangeCol].type==LadderCell.T_Wire &&
        matrix[i][LadderRangeCol].state==LadderCell.G_Ground) hasGround=true;
     else
       hasGround=false;
     ocount=0;
     ocol=LadderRangeCol;
     for(j=StartCol;j<=LadderRangeCol;j++)
      if(IsLoad(i,j)) {ocount++;ocol=j;}
     if(ocount > 1)
      {SetCursor(i,ocol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s12"));

       return false;
      }
     if(hasGround && ocount == 0)
      {SetCursor(i,ocol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s14"));

       return false;
      }
     if(!hasGround && ocount >= 1)
      {SetCursor(i,ocol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s15"));

       return false;
      }
     for(j=ocol+1;j<LadderRangeCol;j++)
      {if(matrix[i][j].type > LadderCell.T_Wire)
        {SetCursor(i,j);
         JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s16"));

         return false;
        }
      }
   }
  for(i=StartRow;i<=LadderRangeRow;i++)
   {for(j=StartCol;j<=LadderRangeCol;j++)
     {if(IsLoad(i,j))
       {for(ii=i+1;ii<=LadderRangeRow;ii++)
         for(jj=StartCol;jj<=LadderRangeCol;jj++)
          {if(matrix[ii][jj].ced!=null)
            tempbool=(matrix[ii][jj].ced==matrix[i][j].ced);
           else if(matrix[ii][jj].cdo!=null)
            tempbool=(matrix[ii][jj].cdo==matrix[i][j].cdo);
           else tempbool=false;
           if(IsLoad(ii,jj) && matrix[ii][jj].type == matrix[i][j].type &&  tempbool &&
             matrix[ii][jj].state==matrix[i][j].state)
            {SetCursor(ii,jj);
             JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s17"));

             return false;
            }
          }
       }
     }
   }

//String strow(i);
//String onumber(ocount);
//String enumber(ecount);
//String scr="�D�u: Row:"+strow+"  �t����:"+onumber+"  �}����:"+enumber;
//SetCursor(i,StartCol);
//Application->MessageBox(scr.c_str(),"�D�u�ˬd",MB_OK);

  return true;
}

private boolean OutBlockCheck(int row,int col1,int col2)
{ int i,j,k,first,ecount,ocount,ocol;        // �˥X���p�϶����t���t�u�O�_���T
  boolean hasGround,noTshape;       // hasGround=true�ɥN���t���϶�
  boolean Gblock;
  if(col1 > col2)
   {i=col1;col1=col2;col2=i;}
  if(matrix[row][col2].type==LadderCell.T_Wire && matrix[row][col2].state==LadderCell.G_Ground)
   hasGround=true;
  else hasGround=false;

  i=row;
  while(true)
   {ecount=0;ocount=0;noTshape=true;Gblock=false;ocol=LadderRangeCol-1;
    first=col1+1;
    for(k=col2-1;k>col1;k--)
     if(matrix[i][k].type==LadderCell.T_Wire && matrix[i][k].state==LadderCell.G_Tshape &&
        matrix[i][k].Tshapepos==col1)
      {if(!OutBlockCheck(i,k,matrix[i][k].Tshapepos)) return false;
       first=k+1;
       noTshape=false;
       break;
      }
    for(j=first;j<col2;j++)
     {if(matrix[i][j].type > LadderCell.T_Wire)
       {if(IsLoad(i,j)) {ocount++;ocol=j;}
        else ecount++;
       }
      if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Tshape)
       {                             // �˥X���p�϶��������p�϶�,�Ļ��^�覡
        if(!OutBlockCheck(i,j,matrix[i][j].Tshapepos)) return false;
        if(matrix[i][j].Tshapepos==LadderRangeCol) Gblock=true;
        if(matrix[i][j].Tshapepos > j) j=matrix[i][j].Tshapepos;
 //        if(matrix[i][j].switchtype==col1) ecount=0;
        noTshape=false;
       }
     }

    if(!hasGround && ocount>0)   // �Y�D�t���϶�,�h�϶������঳�t��
     {SetCursor(i,ocol);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s18"));

     return false;
     }                        // �Y���䤺�L�϶�,�h clear=true
    if(!hasGround && noTshape && ecount==0)  // �L�϶�������,�����n���}������,�_�h�u��,�϶��L��
     {
      SetCursor(i,col1);
      if(col1==StartCol && col2==LadderRangeCol)
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s19"));

      else
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s20"));

      return false;
     }
    if(hasGround && ocount>1)   // �Y�O�t���϶�,�h�϶����u�঳�@�t��
     {SetCursor(i,ocol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s21"));

      return false;
     }
    if(hasGround && !Gblock && ocount==0)      // �D�u�S�t��,�i���|�Ϩt�εu��
     {SetCursor(i,col2);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s14"));

      return false;
     }
    if(hasGround && noTshape && ecount==0 && matrix[i][col1].state==LadderCell.G_Power)
     {SetCursor(i,ocol);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s22"));

      return false;
     }
    if(hasGround && noTshape && ecount == 0 && ocount==0)  // �L�϶�������,�����n���}������,�_�h�u��,�϶��L��
     {SetCursor(i,col1);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s23"));

      return false;
     }
    if(hasGround)
     {for(j=ocol+1;j<=LadderRangeCol;j++) // �t������,�������a
       if(matrix[i][j].type > LadderCell.T_Wire)
        {SetCursor(i,ocol);
          JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s24"));

         return false;
        }
     }
//String stcol(col1);
//String endcol(col2);
//String onumber(ocount);
//String enumber(ecount);
//String scr="�q"+stcol+"��"+endcol+"  �t����:"+onumber+"  �}����:"+enumber;
//SetCursor(i,col1);
//Application->MessageBox(scr.c_str(),"�t�����϶��ˬd",MB_OK);
    i=NextLine(i,col1,col2);
    if(i==-1) break;
   }                  // �˥X�϶�������
  return true;
}

private int NextLine(int row,int col1,int col2)
{int crow=row+1;
  while(true)
   { if(matrix[crow][col1].type!=LadderCell.T_Wire || matrix[crow][col2].type !=LadderCell.T_Wire ||
        !CheckUpPass(matrix[crow][col1].state) ||
        !CheckUpPass(matrix[crow][col2].state)) return -1;
     if(matrix[crow][col1].type==LadderCell.T_Wire && matrix[crow][col2].type==LadderCell.T_Wire &&
        CheckLeftEnd(matrix[crow][col1].state) &&
        CheckRightEnd(matrix[crow][col2].state) &&
        NoUpPass(crow,col1+1,col2-1)) return crow;
     crow++;
     if(crow>MaxRows) return -1;
   }
//  return -1;
}

private boolean NoUpPass(int row,int col1,int col2)
{ int i;
  for(i=col1;i<=col2;i++)
   if(CheckUpPass(row,i)) return false;
  return true;  // true: no uppass elemnet in col1 to col2 at row
}

private boolean IsLoad(int row, int col)
{ boolean load=false;           // �ˬd�����ϤW���m(col,row)������,�O�_���t��
  switch(matrix[row][col].type)
   { case LadderCell.T_EDevice:
       if((matrix[row][col].state==LadderCell.G_ESOL1 || matrix[row][col].state==LadderCell.G_ESOL2) &&
           matrix[row][col].ced!=null) load=true;
       break;
     case LadderCell.T_System:
       if((matrix[row][col].state==LadderCell.G_SSOL1 || matrix[row][col].state==LadderCell.G_SSOL2) &&
           matrix[row][col].cdo!=null) load=true;
       break;
   }
  return load;
}

public void SetCursor(int row, int col)
{// if(PLCRunFlag) {LadderForm->Hint="�Х������ʺA����!!";return;}
  if(row < 0 || row > MaxRows) return;
  if(col < 0 || col > MaxColumns) return;
  dragPoint1.x=col;               // �]�w���Ц��m
  dragPoint2.x=dragPoint1.x;
  dragPoint1.y=row;
  dragPoint2.y=dragPoint1.y;
  repaint();
}

private boolean CheckPowerLine() // �ˬd�����ϤW���u�t�m�����~
{ int i,j,prow,pcol,pcount;
                          // �ˬd�Ĥ@�����O�_�����u
  if(matrix[StartRow][StartCol].type != LadderCell.T_Wire || matrix[StartRow][StartCol].state != LadderCell.G_Power)
   {SetCursor(StartRow,StartCol);
//     MessageBox(Config.getString("electricPanel.s25"),Config.getString("electricPanel.s9"));
    return false;
   }                      // �ˬd�O�_���u�����u
  prow=StartRow;pcol=StartCol;
  while(matrix[prow+1][pcol].type == LadderCell.T_Wire)
   { if(matrix[prow+1][pcol].state != LadderCell.G_Vline && matrix[prow+1][pcol].state != LadderCell.G_Power)
      {SetCursor(prow+1,pcol);
       JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s25"));

       return false;
      }
     prow++;
   }                       // �C�@�C�W�u���@���u����
  for(i=StartRow;i<=LadderRangeRow;i++)
   {pcount=0;
    for(j=0;j<=LadderRangeCol;j++)
     if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Power)
      {prow=i;pcol=j;pcount++;}
    if(pcount>1)
     {SetCursor(prow,pcol);
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s26"));

      return false;
     }
   }
  for(i=StartRow;i<=LadderRangeRow;i++) // �ɺ����u
   if(!(matrix[i][StartCol].type==LadderCell.T_Wire && matrix[i][StartCol].state == LadderCell.G_Power))
    {matrix[i][StartCol].type = LadderCell.T_Wire;
     matrix[i][StartCol].state=LadderCell.G_Vline;
    }
  return true;
}
private void deleteBrokenVline()
 {boolean over=overWrite;
   overWrite=true;
   for(int j=StartCol;j<=LadderRangeCol;j++)
    for(int i=StartRow;i<=LadderRangeRow;i++)
     if(matrix[i][j].type == LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Vline)
      {
//System.err.println("Vline"+":"+i+":"+j);
        if(i==0)
         {if(!CheckUpPass(i+1,j)) ClearCell(i,j);}
        else if(i==(MaxRows-1))
         {if(!CheckDownPass(i-1,j)) ClearCell(i,j);}
        else
         {if(!CheckUpPass(i+1,j) && !CheckDownPass(i-1,j)) ClearCell(i,j);}
     }
    overWrite=over;
  }
private boolean CheckBrokenLine()    // �ˬd�u���t���O�_���_�u ?
{ int i,j;
  boolean returnflag=true;     // �T�w�Ĥ@�C�W���S���W��������
  for(i=StartCol;i<=LadderRangeCol;i++)
   if(matrix[0][i].type == LadderCell.T_Wire)
     {if(matrix[0][i].state==LadderCell.G_Power || matrix[0][i].state==LadderCell.G_Ground) continue;
      else if(CheckUpPass(matrix[0][i].state))
        { SetCursor(0,i);
          returnflag=false;
          break;
        }
     }
 if(returnflag)             // �T�w�Ĥ@���W���S������������
  {
   for(i=StartRow;i<=LadderRangeRow;i++)
    if(matrix[i][0].type == LadderCell.T_Wire)
      { if(CheckLeftPass(matrix[i][0].state))
         { SetCursor(i,0);
           returnflag=false;
           break;
         }
      }
  }
 if(returnflag)      // �ѤW�ӤU,�ѥ��ӥk,�ˬd�C�@�����k���P�U��������
  {
   for(i=StartRow;i<=LadderRangeRow;i++)
   {for(j=StartCol;j<=LadderRangeCol;j++)
     {if(CheckRightPass(i,j)!=CheckLeftPass(i,j+1))
       {SetCursor(i,j+1);returnflag=false;break;}
//      if(i==LadderRangeRow && (matrix[i][j].state==LadderCell.G_Power ||
//                               matrix[i][j].state==LadderCell.G_Ground)) continue;
      if(i==LadderRangeRow && (j==StartCol || j==LadderRangeCol)) continue;
      if(CheckDownPass(i,j)!=CheckUpPass(i+1,j))
       {SetCursor(i+1,j);returnflag=false;break;}
     }
    if(!returnflag) break;
   }
  }
 if(!returnflag)
  JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s27"));
// if(returnflag) Application->MessageBox("�q���j���t�u�L�~!!","�j���ˬd",MB_OK);
 return returnflag;
}

private boolean CheckRightPass(int state)
  {if(state==LadderCell.G_Power || state==LadderCell.G_Hline || state==LadderCell.G_Tshape || state==LadderCell.G_Lshape || state==LadderCell.G_LTshape ||
       state==LadderCell.G_NO || state==LadderCell.G_NC || state==LadderCell.G_ESOL1 ||
       state==LadderCell.G_ESOL2 || state==LadderCell.G_SSOL1 || state==LadderCell.G_SSOL2)
     return true;
    else
     return false;
  }
private boolean CheckRightPass(int row,int col)
{                    //�ˬd�g������(row,col)���m�W�������O�_���k�q���t�u
  if(matrix[row][col].type == LadderCell.T_None) return false;
  else if(matrix[row][col].type == LadderCell.T_EDevice || matrix[row][col].type==LadderCell.T_System) return true;
  else return CheckRightPass(matrix[row][col].state);
}
private boolean CheckLeftPass(int state)
  {if(state==LadderCell.G_Hline || state==LadderCell.G_Tshape || state==LadderCell.G_RTshape || state==LadderCell.G_RLshape || state==LadderCell.G_Ground
       || state==LadderCell.G_NO || state==LadderCell.G_NC || state==LadderCell.G_ESOL1 ||
       state==LadderCell.G_ESOL2 || state==LadderCell.G_SSOL1 || state==LadderCell.G_SSOL2)
     return true;
    else
     return false;
  }
private boolean CheckLeftPass(int row,int col)
{                   //�ˬd�g������(row,col)���m�W�������O�_�����q���t�u
  if(matrix[row][col].type == LadderCell.T_None) return false;
  else if(matrix[row][col].type == LadderCell.T_EDevice || matrix[row][col].type==LadderCell.T_System) return true;
  else return CheckLeftPass(matrix[row][col].state);
}
private boolean CheckUpPass(int state)
  {if(state==LadderCell.G_Power || state==LadderCell.G_Vline || state==LadderCell.G_RTshape || state==LadderCell.G_RLshape || state==LadderCell.G_Lshape || state==LadderCell.G_LTshape || state==LadderCell.G_Ground)
     return true;
    else
     return false;
  }
private boolean CheckUpPass(int row,int col)
{                   //�ˬd�g������(row,col)���m�W�������O�_���W�q���t�u
  if(matrix[row][col].type == LadderCell.T_None) return false;
  else if(matrix[row][col].type > LadderCell.T_Wire) return false;
  else
     return CheckUpPass(matrix[row][col].state);
}

private boolean CheckLeftEnd(int state)
  {if(state==LadderCell.G_Power || state==LadderCell.G_Lshape || state==LadderCell.G_LTshape
       || state==LadderCell.G_Vline)
     return true;
    else
     return false;
  }
private boolean CheckRightEnd(int state)
  {if(state==LadderCell.G_RTshape || state==LadderCell.G_RLshape || state==LadderCell.G_Ground
       || state==LadderCell.G_Vline)
     return true;
    else
     return false;
  }
private boolean CheckDownPass(int state)
  {if(state==LadderCell.G_Power || state==LadderCell.G_Vline ||  state==LadderCell.G_Tshape || state==LadderCell.G_RTshape || state==LadderCell.G_LTshape || state==LadderCell.G_Ground)
     return true;
    else
     return false;
  }

private boolean CheckDownPass(int row,int col)
{                   //�ˬd�g������(row,col)���m�W�������O�_���U�q���t�u
  if(matrix[row][col].type == LadderCell.T_None) return false;
  else if(matrix[row][col].type > LadderCell.T_Wire) return false;
  else return CheckDownPass(matrix[row][col].state);
}
//---------------------------------------------------------------------------
private void CommandBlock(int row,int col1,int col2)
{ int i,j,k,first,ecount;        // ����PLC���O
  boolean hasGround,noTshape;       // hasGround=true�ɥN���t���϶�

  int linenumber=0;
  if(col1 > col2)
   {i=col1;col1=col2;col2=i;}
  if(matrix[row][col2].type==LadderCell.T_Wire && matrix[row][col2].state==LadderCell.G_Ground)
//     !(matrix[row][col1].type==LadderCell.T_Wire && matrix[row][col1].state==LadderCell.G_Power))
   hasGround=true;
  else hasGround=false;

  if(hasGround && matrix[row][col1].state!=LadderCell.G_Power)
   {Temp--;                     //�Y���t���϶�,���x�s�B�⵲�G���Ȧs��
    FPLC.AddCode(PLC.C_OUT,null,null,Temp);
   }
//Application->MessageBox(IntToStr(col1).c_str(),IntToStr(col2).c_str(),MB_OK);
  i=row;
  while(true)
   {ecount=0;noTshape=true;    //�Y���t���϶�,�����J�x�s�b�Ȧs�����B�⵲�G
//    hasBlock=false;
//Application->MessageBox(IntToStr(i).c_str(),IntToStr(col2).c_str(),MB_OK);
    if(hasGround && matrix[row][col1].state!=LadderCell.G_Power)
     {FPLC.AddCode(PLC.C_LOAD,null,null,Temp);
      noTshape=false;
//Application->MessageBox(IntToStr(linenumber).c_str(),IntToStr(i).c_str(),MB_OK);
     }

    first=col1+1;
    for(k=col2-1;k>col1;k--)  //�ˬd�O�_���e�譫�|�����϶�
     if(matrix[i][k].type==LadderCell.T_Wire && matrix[i][k].state==LadderCell.G_Tshape &&
        matrix[i][k].Tshapepos==col1)
      {CommandBlock(i,k,matrix[i][k].Tshapepos); //�e�϶����B�z
       first=k+1;
       noTshape=false;
       break;
      }
    for(j=first;j<col2;j++)
     {if(matrix[i][j].type > LadderCell.T_Wire && FPLC.codeArray.size() < FPLC.lineLimit)
       {if(IsLoad(i,j))        //�O�_���t������
         {if(matrix[i][j].type==LadderCell.T_EDevice && matrix[i][j].ced!=null)
           {if(matrix[i][j].ced.actionType==CEDevice.TYPE_COUNTER && matrix[i][j].state==LadderCell.G_ESOL2)
             FPLC.AddCode(PLC.C_RST,matrix[i][j].ced,null,LadderCell.G_ESOL2);
            else if(matrix[i][j].ced.actionType==CEDevice.TYPE_COUNTER && matrix[i][j].state==LadderCell.G_ESOL1)
             FPLC.AddCode(PLC.C_CNT,matrix[i][j].ced,null,LadderCell.G_ESOL1);
            else if(matrix[i][j].ced.actionType==CEDevice.TYPE_TIMER && matrix[i][j].state==LadderCell.G_ESOL1)
             FPLC.AddCode(PLC.C_TIM,matrix[i][j].ced,null,LadderCell.G_ESOL1);
            else
             {if(matrix[i][j].state==LadderCell.G_ESOL2)
               FPLC.AddCode(PLC.C_OUT,matrix[i][j].ced,null,LadderCell.G_ESOL2);
              else
               FPLC.AddCode(PLC.C_OUT,matrix[i][j].ced,null,LadderCell.G_ESOL1);
             }
           }
          else if(matrix[i][j].type==LadderCell.T_System && matrix[i][j].cdo!=null)
           {if(matrix[i][j].state==LadderCell.G_SSOL2)
             FPLC.AddCode(PLC.C_OUT,null,matrix[i][j].cdo,LadderCell.G_SSOL2);
            else
             FPLC.AddCode(PLC.C_OUT,null,matrix[i][j].cdo,LadderCell.G_SSOL1);
           }
         }
        else
         {ecount++;
          if(ecount==1 && noTshape) //�D�t������,�B���Ĥ@������Load
           {switch(matrix[i][j].state)
             {case LadderCell.G_NO: FPLC.AddCode(PLC.C_LOAD,matrix[i][j].ced,null,LadderCell.G_NO); break;
              case LadderCell.G_NC: FPLC.AddCode(PLC.C_LOAD_NOT,matrix[i][j].ced,null,LadderCell.G_NO); break;
             }
           }
          else                      //�D�t������,�D�Ĥ@������And
           {switch(matrix[i][j].state)
             {case LadderCell.G_NO: FPLC.AddCode(PLC.C_AND,matrix[i][j].ced,null,LadderCell.G_NO); break;
              case LadderCell.G_NC: FPLC.AddCode(PLC.C_AND_NOT,matrix[i][j].ced,null,LadderCell.G_NO); break;
             }
           }
         }
       }
      if(matrix[i][j].type==LadderCell.T_Wire && matrix[i][j].state==LadderCell.G_Tshape)
       {                             // �˥X���p�϶��������p�϶�,�Ļ��^�覡
        CommandBlock(i,j,matrix[i][j].Tshapepos);
        if(matrix[i][j].Tshapepos > j) j=matrix[i][j].Tshapepos;
        noTshape=false;
       }
     }

//String stcol(col1);
//String endcol(col2);
//String onumber(ocount);
//String enumber(ecount);
//String scr="�q"+stcol+"��"+endcol+"  �t����:"+onumber+"  �}����:"+enumber;
//SetCursor(i,col1);
//Application->MessageBox(scr.c_str(),"�t�����϶��ˬd",MB_OK);
    i=NextLine(i,col1,col2);
    if(i>=0)      //���X�U�@����
     {
      linenumber++;
      if(linenumber!=1 && !hasGround && FPLC.codeArray.size() < FPLC.lineLimit)
       FPLC.AddCode(PLC.C_OR_LD,null,null,LadderCell.G_None);
     //  {Command[LineCount][0]=PLC.C_OR_LD;
     //   Command[LineCount][1]=PLC.C_None;
     //   Command[LineCount][2]=PLC.C_None;
     //   LineCount++;
     //  }
     }
    else
     {if(linenumber != 0 && !hasGround && FPLC.codeArray.size() < FPLC.lineLimit)
       FPLC.AddCode(PLC.C_OR_LD,null,null,LadderCell.G_None);
 //      {Command[LineCount][0]=PLC.C_OR_LD;
 //       Command[LineCount][1]=PLC.C_None;
 //       Command[LineCount][2]=PLC.C_None;
  //      LineCount++;
  //     }
       break;
     }
   }                  // �˥X�϶�������
  if(!hasGround && matrix[row][col1].state!=LadderCell.G_Power && FPLC.codeArray.size() < FPLC.lineLimit)
   FPLC.AddCode(PLC.C_AND_LD,null,null,LadderCell.G_None);
//   {Command[LineCount][0]=PLC.C_AND_LD;
//    Command[LineCount][1]=PLC.C_None;
//    Command[LineCount][2]=PLC.C_None;
//    LineCount++;
//   }
}



public void Simulation(java.util.Timer timer)
  {electriclistener.getEArrays().reset();

    CreatePLCCode();
//    EDevice.mode=EDevice.MODE_Simulation;
    electriclistener.getEArrays().setEDeviceMode(EDevice.MODE_Simulation);
    FPLC.codeReady=true;
    FPLC.startTimer(timer);
    if(electriclistener.getOpMode()==SCCAD.OP_CONTROL)
      electriclistener.setStatus(Config.getString("electricPanel.s28"));
    else
      electriclistener.setStatus(Config.getString("electricPanel.s29"));
  }

  public void startTimer(java.util.Timer timer)
   {electriclistener.getEArrays().reset();
    CreatePLCCode();
//    EDevice.mode=EDevice.MODE_Simulation;
    electriclistener.getEArrays().setEDeviceMode(EDevice.MODE_Simulation);
    FPLC.codeReady=true;
//    FPLC.start();
    FPLC.startTimer(timer);
    if(electriclistener.getOpMode()==SCCAD.OP_CONTROL)
    	electriclistener.setStatus(Config.getString("electricPanel.s28"));
    else
    	electriclistener.setStatus(Config.getString("electricPanel.s29"));
   }


/*
public void Stop()
 {FPLC.codeReady=false;
//   EDevice.mode=EDevice.MODE_Edit;
  ladderListener.setEDeviceMode(EDevice.MODE_Edit);
  if(ladderListener.getOpMode==WebLadderCAD.OP_CONTROL)
     ladderListener.setStatus(Config.getString("electricPanel.s30"));
  else
     ladderListener.setStatus(Config.getString("electricPanel.s31"));
  FPLC.stop();
 }
*/

public synchronized void CreatePLCCode()
{
//  LadderCheck();
//  if(!FChecked) return;         //�Y�j���ˬd���~,��������
  if(!LadderCheck())
   {
System.err.println("LadderCheck fail");
     return;
   }
  FPLC.codeArray.clear();
  FPLC.codeReady=false;
  rescale();
  Temp=0;                             //����PLC���O
  CommandBlock(StartRow,StartCol,LadderRangeCol);
  SetPos();
  FPLC.codeReady=true;
//if(WebLadderCAD.debug) System.out.println("size:"+FPLC.codeArray.size());

//Modules.setupInObject(electriclistener.getEArrays());
//Modules.setupOutObject(electriclistener.getEArrays());
FPLC.printCode();
}

   private class clearCommand extends Command
    {ArrayList cells;
	public clearCommand(Object ele,ArrayList cs)
     {super("ElectricPanel",ele,Command_clear);
      cells=cs;
     }
    public void undo()
     {int index=0;
	  for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
       for (int j=0;j<MaxColumns;j++)
        {matrix[i][j]=new LadderCell((LadderCell) cells.get(index));
         index++;
        }
     }
    public void redo()
     {for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
       for (int j=0;j<MaxColumns;j++)
        ClearCell(i,j);
     }
   }

public void Clear()
{ ArrayList cellsdeleted=new ArrayList();
  for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
   for (int j=0;j<MaxColumns;j++)
    {cellsdeleted.add(new LadderCell(matrix[i][j]));
	 ClearCell(i,j);
    }
  electriclistener.addCommand(new clearCommand(this,cellsdeleted));
//   matrix[0][0].type=LadderCell.T_Wire;            // �w�]���u���m
//   matrix[0][0].state=LadderCell.G_Power;
   dragPoint1.x=0;dragPoint1.y=0;
   dragPoint2.x=dragPoint1.x;
   dragPoint2.y=dragPoint1.y;
   repaint();
}

public void clearCEDevice(CEDevice ced)
 { for (int i=StartRow;i<=LadderRangeRow;i++)
     for (int j=StartCol;j<=LadderRangeCol;j++)
      if(matrix[i][j].ced==ced) ClearCell(i,j);
 }

public void clearCDOutput(CDOutput cdo)
 { for (int i=StartRow;i<=LadderRangeRow;i++)
     for (int j=StartCol;j<=LadderRangeCol;j++)
      if(matrix[i][j].cdo==cdo) ClearCell(i,j);
 }

  public void ClearCell(int i,int j)
{ matrix[i][j].type = LadderCell.T_None;
  matrix[i][j].state= LadderCell.G_None;
  matrix[i][j].ced=null;
  matrix[i][j].cdo=null;
  matrix[i][j].Tshapepos=0;
  matrix[i][j].dir="";
}

  private void SetPos()
   {
    EDevice ed;
//    Point cps;
    for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
     {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
       ed.ced.AList.clear();
       ed.ced.BList.clear();
       ed.ced.SP1.x=0;
       ed.ced.SP1.y=0;
       ed.ced.SP2.x=0;
       ed.ced.SP2.y=0;
     }
    ElectricFace sys;
    for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
     {sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
       if(sys.getCDOutput()==null) continue;
       sys.getCDOutput().SP1.x=0;
       sys.getCDOutput().SP1.y=0;
       sys.getCDOutput().SP2.x=0;
       sys.getCDOutput().SP2.y=0;
     }
    Point cpos=null;
    for (int i=StartRow;i<=LadderRangeRow;i++)
     for (int j=StartCol;j<=LadderRangeCol;j++)
      {if(matrix[i][j].type==LadderCell.T_EDevice && matrix[i][j].ced!=null)
      {switch(matrix[i][j].state)
        {case LadderCell.G_NO: cpos=new Point();
                    cpos.x=j; cpos.y=i; matrix[i][j].ced.AList.add(cpos);
                    break;
         case LadderCell.G_NC: cpos=new Point();
                    cpos.x=j; cpos.y=i; matrix[i][j].ced.BList.add(cpos);
                    break;
         case LadderCell.G_ESOL1: matrix[i][j].ced.SP1.x=j;matrix[i][j].ced.SP1.y=i;break;
         case LadderCell.G_ESOL2: matrix[i][j].ced.SP2.x=j;matrix[i][j].ced.SP2.y=i;break;
        }
      }
     else if(matrix[i][j].type==LadderCell.T_System && matrix[i][j].cdo!=null)
      {switch(matrix[i][j].state)
        {case LadderCell.G_SSOL1: matrix[i][j].cdo.SP1.x=j;matrix[i][j].cdo.SP1.y=i;break;
         case LadderCell.G_SSOL2: matrix[i][j].cdo.SP2.x=j;matrix[i][j].cdo.SP2.y=i;break;
        }
      }
    }
}
//---------------------------------------------------------------------------

  public void mouseClicked(MouseEvent e) {}
   public void mouseEntered(MouseEvent e) {}
   public void mouseExited(MouseEvent e) {}
   public void mousePressed(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }

  private class setCursorCommand extends Command
    {int newxcol;
     int newyrow;
     int oldxcol;
     int oldyrow;
	public setCursorCommand(Object ele,int newxcol,int newyrow,int oldxcol,int oldyrow)
     {super("ElectricPanel",ele,Command_setCursor);
      this.newxcol=newxcol;
      this.newyrow=newyrow;
      this.oldxcol=oldxcol;
      this.oldyrow=oldyrow;
     }
    public void undo()
     {dragPoint1.y=oldyrow;
      dragPoint1.x=oldxcol;
      dragPoint2.x=dragPoint1.x;
      dragPoint2.y=dragPoint1.y;
      electriclistener.setElectricsStatusPos("row "+oldyrow+" : col "+oldxcol);
     }
    public void redo()
     {dragPoint1.y=newyrow;
      dragPoint1.x=newxcol;
      dragPoint2.x=dragPoint1.x;
      dragPoint2.y=dragPoint1.y;
      electriclistener.setElectricsStatusPos("row "+newyrow+" : col "+newxcol);
     }
   }

  public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop)
   {
    if(left)     // ���U�ƹ�������,�Ұʩ��ʼҦ�,�}�l�i���X�϶�
     {int yrow=ey/cellhgt;
       int xcol=ex/cellwid;
       if(yrow >= MaxRows || xcol >= MaxColumns)
        {
          String str=Config.getString("electricPanel.s32")+Integer.toString(MaxRows)+Config.getString("electricPanel.s33")+
                                            Integer.toString(MaxColumns)+Config.getString("electricPanel.s34");
          JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+str);
          return;
        }
       electriclistener.addCommand(new setCursorCommand(this,xcol,yrow,dragPoint1.x,dragPoint1.y));
      dragPoint1.y=yrow;
      dragPoint1.x=xcol;
      dragPoint2.x=dragPoint1.x;
      dragPoint2.y=dragPoint1.y;
      electriclistener.setElectricsStatusPos("row "+yrow+" : col "+xcol);
      repaint();
     }
    else
     {maybeShowPopup(pop,ex,ey);
     }
//    rescale();
   }

   public void mouseReleased(MouseEvent e) {
//System.err.println("release");
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }

   private class blockCommand extends Command
    {Point newp1;
     Point newp2;
     Point oldp1;
     Point oldp2;
	public blockCommand(Object ele,Point np1,Point np2,Point op1,Point op2)
     {super("ElectricPanel",ele,Command_block);
      newp1=np1;
      newp2=np2;
      oldp1=op1;
      oldp2=op2;
     }
    public void undo()
     {dragPoint1.y=oldp1.y;
      dragPoint1.x=oldp1.x;
      dragPoint2.x=oldp2.x;
      dragPoint2.y=oldp2.y;
     }
    public void redo()
     {dragPoint1.y=newp1.y;
      dragPoint1.x=newp1.x;
      dragPoint2.x=newp2.x;
      dragPoint2.y=newp2.y;
     }
   }

   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop)
    {
      if(left)
       {if(dragging)
         {int moveRow=ey/cellhgt;
           int moveCol=ex/cellwid;
           Point oldp1=new Point(dragPoint1);
           Point oldp2=new Point(dragPoint2);
           if(dragPoint2.x == moveCol && dragPoint2.y == moveRow) return;
           if(dragPoint1.x>moveCol && dragPoint1.y > moveRow)
            {dragPoint2.x=dragPoint1.x;
              dragPoint2.y=dragPoint1.y;
              dragPoint1.x=moveCol;
              dragPoint1.y=moveRow;
            }
           else
            {dragPoint2.x = moveCol;
              dragPoint2.y = moveRow;
            }
           electriclistener.setElectricsStatusPos("row "+moveRow+" : col "+moveCol);
          dragging=false;
          Point newp1=new Point(dragPoint1);
          Point newp2=new Point(dragPoint2);
          electriclistener.addCommand(new blockCommand(this,newp1,newp2,oldp1,oldp2));
          repaint();
         }
       }
      else
       {maybeShowPopup(pop,ex,ey);
       }
     rescale();
    }

  public Point popupPoint=null;
  private void maybeShowPopup(boolean pop, int ex, int ey)
   {
      if(pop)
       {
         if(cellGroup!=null && cellGroup.size()>0)
          mp.setEnabled(true);
         else
          mp.setEnabled(false);
         if(!overWrite) mm.setText(Config.getString("Status.overwrite"));
         else mm.setText(Config.getString("Status.insert"));
         popupPoint=new Point(ex,ey);
//        popup.show(e.getComponent(), e.getX(), e.getY());
        popup.show(this,ex,ey);
       }
        dragging=false;
      rescale();
   }

   public void mouseDragged(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseDragged(e,left,e.getX(),e.getY(),pop);
   }

   public void MouseDragged(MouseEvent e, boolean left, int ex, int ey, boolean pop)
    {
     if(left)
       {// �b�����ϤW���X�@���ϰ�
           int moveRow=ey/cellhgt;
           int moveCol=ex/cellwid;
           if(dragPoint2.x == moveCol && dragPoint2.y == moveRow) return;
           if(dragPoint1.x>moveCol && dragPoint1.y > moveRow)
            {dragPoint2.x=dragPoint1.x;
              dragPoint2.y=dragPoint1.y;
              dragPoint1.x=moveCol;
              dragPoint1.y=moveRow;
            }
           else
            {dragPoint2.x = moveCol;
              dragPoint2.y = moveRow;
            }
           electriclistener.setElectricsStatusPos("row "+moveRow+" : col "+moveCol);
          dragging=true;
          repaint();
       }
//      rescale();
   }

   public void mouseMoved(MouseEvent e) {}

/*
  public void keyTyped(KeyEvent e){}
  public void keyPressed(KeyEvent e)
    {int kcode=e.getKeyCode();
     KeyPressed(e,kcode);
    }
  public void KeyPressed(KeyEvent e,int kcode)
   {Component[] coms=getComponents();
    Element ele=null;
    for(int i=0;i<coms.length;i++)
     {ele=(Element) coms[i];
      if((ele instanceof Valve))
       {Valve val=(Valve) ele;
        if(val.getForceType()==Valve.FORCE_MAN && val.getActivateKey()==kcode)
         {val.setForce(0,true);
     //      if(!pneumatics.allClient.nosend && pneumatics.allClient.connected) pneumatics.writeEvent(new PneumaticsEvent(-1,"keyPressed",e,null));
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
    Element ele=null;
    for(int i=0;i<coms.length;i++)
     {ele=(Element) coms[i];
      if((ele instanceof Valve))
       {Valve val=(Valve) ele;
        if(val.getForceType()==Valve.FORCE_MAN && val.getActivateKey()==kcode)
         {val.setForce(0,false);
  //         if(!pneumatics.allClient.nosend && pneumatics.allClient.connected) pneumatics.writeEvent(new PneumaticsEvent(-1,"keyReleased",e,null));
           break;
          }
       }
     }
//System.err.println(kcode);
   }
*/

  public void degroup()
    {dragPoint2.x=dragPoint1.x;
     dragPoint2.y=dragPoint1.y;
    }

  private void ShiftBlockLeft() // �ŤU(���h)�����ϤW�϶�������
   {if(overWrite) return;                                  // (�϶��k�������\�L�϶�)
     int k=dragPoint2.x-dragPoint1.x+1;
     for (int j=dragPoint1.x;j<MaxColumns-k;j++)
      for (int i=dragPoint1.y;i<=dragPoint2.y;i++)
       copyMatrix(i,j+k,i,j);
     for (int j=MaxColumns-k;j<MaxColumns;j++)    // ���}�����m�אּ�ť�
      for (int i=dragPoint1.y;i<=dragPoint2.y;i++)
       ClearCell(i,j);
  }

  private void ShiftBlockRight()
   {                        // ���n�K�W�ϰ줺�������k��
    if(overWrite) return;
    if(cellGroup==null || cellGroup.size()==0) return;
    int groupHeight=cellGroup.size()/groupWidth;
    if(dragPoint1.x+groupWidth+1 > MaxColumns || dragPoint1.y+groupHeight+1 > MaxRows)
     {String str=Config.getString("electricPanel.s32")+Integer.toString(MaxRows)+Config.getString("electricPanel.s33")+
                                            Integer.toString(MaxColumns)+Config.getString("electricPanel.s34");
      JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+str);
      return;
     }
    for(int j=MaxColumns-1;j>=dragPoint1.x+groupWidth;j--)
     for(int i=dragPoint1.y;i<dragPoint1.y+groupHeight;i++)
       copyMatrix(i,j-groupWidth,i,j);
    electriclistener.setModified(true);
  }

  private class pasteBoardToCommand extends Command
   {Point dp1;
    Point dp2;
    boolean editmode;
    int groupwidth;
    ArrayList cells;
	public pasteBoardToCommand(Object ele,Point p1,Point p2,boolean em,int gw,ArrayList cs)
     {super("ElectricPanel",ele,Command_pasteBoardTo);
      dp1=p1;
      dp2=p2;
      editmode=em;
      groupwidth=gw;
      cells=cs;
     }
    public void undo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      overWrite=editmode;
      groupWidth=groupwidth;
      if(!overWrite) ShiftBlockLeft();
      int k=0;
      LadderCell lc=null;
      for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
       for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
        {lc=(LadderCell) cells.get(k);
         matrix[i][j].type=lc.type;
         matrix[i][j].state=lc.state;
         matrix[i][j].ced=lc.ced;
         matrix[i][j].cdo=lc.cdo;
         k++;
        }
      checked=false;
      electriclistener.setModified(true);
      hasCommand=false;
     }
    public void redo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      overWrite=editmode;
      groupWidth=groupwidth;
	  if(!overWrite) ShiftBlockRight();  // ���n�K�W�ϰ줺�������k��
      int k=0;
      LadderCell lc=null;
      for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
       for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
        {lc=(LadderCell) cellGroup.get(k);
         matrix[i][j].type=lc.type;
         matrix[i][j].state=lc.state;
         matrix[i][j].ced=lc.ced;
         matrix[i][j].cdo=lc.cdo;
         k++;
        }
      checked=false;
      electriclistener.setModified(true);
      hasCommand=false;
     }
   }

  public void pasteBoardTo()
   {                  // ���ŶKï�W���F��,�K�b�����ϴ��Ъ����m�W
     if(cellGroup==null || cellGroup.size()==0) return;
     if(dragPoint1.x+groupWidth > MaxColumns)
      {JOptionPane.showMessageDialog(electriclistener.getFrame(),Config.getString("electricPanel.s35")+":"+Config.getString("electricPanel.s36"));
       return;
      }
    Point p1=new Point(dragPoint1);
    Point p2=new Point(dragPoint2);
    int gw=groupWidth;
    ArrayList cellsdeleted=new ArrayList();
//    LadderCell lc=null;
    for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
     for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
      {cellsdeleted.add(new LadderCell(matrix[i][j]));
      }
    if(!overWrite) ShiftBlockRight();  // ���n�K�W�ϰ줺�������k��
    int k=0;
    LadderCell lc=null;
    for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
     for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
      {lc=(LadderCell) cellGroup.get(k);
        matrix[i][j].type=lc.type;
        matrix[i][j].state=lc.state;
        matrix[i][j].ced=lc.ced;
        matrix[i][j].cdo=lc.cdo;
        k++;
      }
    checked=false;
    electriclistener.setModified(true);
    hasCommand=false;
    electriclistener.addCommand(new pasteBoardToCommand(this,p1,p2,overWrite,gw,cellsdeleted));
    repaint();
  }

  private class copyToBoardCommand extends Command
    {ArrayList newcellgroup;
     ArrayList oldcellgroup;
     int newgroupwidth;
     int oldgroupwidth;
	public copyToBoardCommand(Object ele,ArrayList og,int ogw,ArrayList ng,int ngw)
     {super("ElectricPanel",ele,Command_copyToBoard);
      newcellgroup=ng;
      newgroupwidth=ngw;
      oldcellgroup=og;
      oldgroupwidth=ogw;
     }
    public void undo()
     {cellGroup=new ArrayList(oldcellgroup);
      groupWidth=oldgroupwidth;
     }
    public void redo()
     {cellGroup=new ArrayList(newcellgroup);
      groupWidth=newgroupwidth;
     }
   }

  public void copyToBoard()
   {                // �ƻs(�ŤU)�����ϤW���ϰ�,���ŶKï
     ArrayList oldcg=new ArrayList(cellGroup);
     int oldgw=groupWidth;
     cellGroup.clear();
     for(int i=dragPoint1.y;i<=dragPoint2.y;i++)
      for(int j=dragPoint1.x;j<=dragPoint2.x;j++)
       cellGroup.add(new LadderCell(matrix[i][j]));
     groupWidth=dragPoint2.x-dragPoint1.x+1;
     ArrayList newcg=new ArrayList(cellGroup);
     electriclistener.addCommand(new copyToBoardCommand(this,newcg,groupWidth,oldcg,oldgw));
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

   private class deleteBlockCommand extends Command
    {Point dp1;
     Point dp2;
     ArrayList cells;
	public deleteBlockCommand(Object ele,Point p1,Point p2,ArrayList cs)
     {super("ElectricPanel",ele,Command_deleteBlock);
      dp1=p1;
      dp2=p2;
      cells=cs;
     }
    public void undo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      if(!overWrite) ShiftBlockRight();
      int index=0;
      for(int i=dragPoint1.y;i<=dragPoint2.y;i++)
      for(int j=dragPoint1.x;j<=dragPoint2.x;j++)
       {matrix[i][j]=new LadderCell((LadderCell) cells.get(index));
        index++;
       }
     }
    public void redo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
	  for(int i=dragPoint1.y;i<=dragPoint2.y;i++)
      for(int j=dragPoint1.x;j<=dragPoint2.x;j++)
       {ClearCell(i,j);
       }
      if(!overWrite) ShiftBlockLeft();
     }
   }

  public void deleteBlock()
   {ArrayList cellsdeleted=new ArrayList();
    Point dpoint1=new Point(dragPoint1);
    Point dpoint2=new Point(dragPoint2);
	for(int i=dragPoint1.y;i<=dragPoint2.y;i++)
      for(int j=dragPoint1.x;j<=dragPoint2.x;j++)
       {LadderCell lc=new LadderCell(matrix[i][j]);
        cellsdeleted.add(lc);
	    ClearCell(i,j);
       }
     if(!overWrite) ShiftBlockLeft();
     electriclistener.addCommand(new deleteBlockCommand(this,dpoint1,dpoint2,cellsdeleted));
   }

  public void actionPerformed(ActionEvent e){
         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }

  private class changeEditModeCommand extends Command
    {boolean newmode;
	public changeEditModeCommand(Object ele,boolean nmode)
     {super("ElectricPanel",ele,Command_changeEditMode);
      newmode=nmode;
     }
    public void undo()
     {overWrite=!newmode;
	  String str=Config.getString("Status.overwrite");
      if(!overWrite) str=Config.getString("Status.insert");
      electriclistener.setElectricsStatusMode(str);
     }
    public void redo()
     {overWrite=newmode;
	  String str=Config.getString("Status.overwrite");
      if(!overWrite) str=Config.getString("Status.insert");
      electriclistener.setElectricsStatusMode(str);
     }
   }

  private class changeRatioCommand extends Command
    {double oldr;
     double newr;
	public changeRatioCommand(Object ele,double or,double nr)
     {super("ElectricPanel",ele,Command_changeRatio);
      oldr=or;
      newr=nr;
     }
    public void undo()
     {ratio=(double) oldr;
     }
    public void redo()
     {ratio=(double) newr;
     }
   }

  public void ActionPerformed(JMenuItem mi,String op,String input)
    {
      String option=mi.getText();
      if(option.equals(Config.getString("ElectricPanel.delete")))
         {deleteBlock();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("ElectricPanel.cut")))
         {
          copyToBoard();
          deleteBlock();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("ElectricPanel.copy")))
         {copyToBoard();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("ElectricPanel.paste")))
         {pasteBoardTo();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("Status.insert")) || option.equals(Config.getString("Status.overwrite")) )
         {overWrite=!overWrite;
          String str=Config.getString("Status.overwrite");
          if(!overWrite) str=Config.getString("Status.insert");
          electriclistener.setElectricsStatusMode(str);
          electriclistener.addCommand(new changeEditModeCommand(this,overWrite));
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("ElectricPanel.selectall")))
         {rescale();
          Point oldp1=new Point(dragPoint1);
          Point oldp2=new Point(dragPoint2);
          dragPoint1.x=StartCol;
          dragPoint1.y=StartRow;
          dragPoint2.x=LadderRangeCol;
          dragPoint2.y=LadderRangeRow;
          Point newp1=new Point(dragPoint1);
          Point newp2=new Point(dragPoint2);
          electriclistener.addCommand(new blockCommand(this,newp1,newp2,oldp1,oldp2));
         }
        else if(option.equals(Config.getString("ElectricPanel.clearall")))
         {Clear();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("ElectricPanel.zoom")))
         {double oldratio=ratio;
	      CustomDialog customDialog = new CustomDialog(electriclistener.getFrame(),Config.getString("ElectricPanel.zoom"),Config.getString("ElectricPanel.zoomfactor"),CustomDialog.VALUE_FLOAT);
          customDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
          customDialog.setTextField(Double.toString(ratio));
          customDialog.setVisible(true);
          ratio=(double) customDialog.getFloat();
          rescale();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
          electriclistener.addCommand(new changeRatioCommand(this,oldratio,ratio));
         }
        checked=false;
        electriclistener.setModified(true);
        hasCommand=false;
        dragging=false;
        repaint();
     }

//  public void processEvent(AWTEvent e)
//   {super.processEvent(e);}

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

//--------------------------------------------------------------------------------------------------------


public int readMatrix(String str,ArrayList tempEDeviceArray,ArrayList tempElectricFaceArray,ArrayList tempMatrixArray)
 { StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_matrix) return -1;
    int wid=Integer.parseInt(token.nextToken());
    int type=Integer.parseInt(token.nextToken());
    int state=Integer.parseInt(token.nextToken());
    int edno=Integer.parseInt(token.nextToken());
    int sysno=Integer.parseInt(token.nextToken());
    EDevice ed=null;
    ElectricFace sys=null;
    if(edno>=0) ed=(EDevice) tempEDeviceArray.get(edno);
    if(sysno>=0) sys=(ElectricFace) tempElectricFaceArray.get(sysno);
    tempMatrixArray.add(new LadderCell(type,state,ed.ced,sys.getCDOutput()));
//    pneumaticPanel.matrixArrayWidth=wid;
    return wid;
 }

public void readMatrix(String str)
  {StringTokenizer token=new StringTokenizer(str);
   int dtype=Integer.parseInt(token.nextToken());
   if(dtype!=SCCAD.Data_matrix) return;
   int i=Integer.parseInt(token.nextToken());
   int j=Integer.parseInt(token.nextToken());
   int type=Integer.parseInt(token.nextToken());
   int state=Integer.parseInt(token.nextToken());
   String edname=token.nextToken();
   String sysname=token.nextToken();
   
   CEDevice ced=electriclistener.getEArrays().findCEDeviceByName(edname);
   CDOutput cdo=electriclistener.getEArrays().findCDOutputByName(sysname);
   matrix[i][j].type=type;
   matrix[i][j].state=state;
   matrix[i][j].ced=ced;
   matrix[i][j].cdo=cdo;
//   electriclistener.getEArrays().tempMatrixArray.add(new LadderCell(type,state,ced,cdo));
//   pneumaticPanel.matrixArrayWidth=wid;
   return;
}

 public String writeMatrix()
  {
//System.out.println("ElectricPanel.write()");
	rescale();
    if(LadderRangeRow-StartRow<=0 && LadderRangeCol-StartCol < 2) return "";
    StringBuffer sb=new StringBuffer();
//    int width=LadderRangeCol-StartCol+1;
//    int edno=-1,sysno=-1;
    String edname="null";
    String sysname="null";
    for (int i=StartRow;i<=LadderRangeRow;i++)
     for (int j=StartCol;j<=LadderRangeCol;j++)
      {/*
    	 edno=-1;
        if(matrix[i][j].ced==null) edno=-1;
        else edno=electriclistener.getEArrays().getEDeviceNo(matrix[i][j].ced);
        sysno=-1;
        if(matrix[i][j].cdo==null) sysno=-1;
        else sysno=electriclistener.getEArrays().getCDOutputNo(matrix[i][j].cdo);
//        sb.append(SCCAD.Data_matrix+" "+width+" "+matrix[i][j].type+" "+matrix[i][j].state+" "+edno+" "+sysno+"\n");
        matrix[i][j].cdo.name
        matrix[i][j].ced.name;
        */
    	if(matrix[i][j].ced==null) edname="null";
    	else edname=matrix[i][j].ced.name;
    	if(matrix[i][j].cdo==null) sysname="null";
    	else sysname=matrix[i][j].cdo.name;
    	sb.append(SCCAD.Data_matrix+" "+i+" "+j+" "+matrix[i][j].type+" "+matrix[i][j].state+" "+edname+" "+sysname+"\n");
      }
//System.out.println(sb.toString());
    return sb.toString();
  }
//-------------------------------------------------------
//  public interface EDeviceListener
//   public ESystemBase getESystemBase(EDevice ed);
//   public void AddCell(int type,int state,EDevice ed,ESystemBase sys,boolean ShiftEnabled);
/*
   public void AddCell(int type,ESystemBase sys, EDevice ed,int direct, boolean ShiftEnabled)
    {WebLadderCAD.electrics.sequence.sequencePanel.AddCell(type,sys,ed,direct,ShiftEnabled);
    }
*/
 /*
   public void putEDevice(EDevice edevice)
    {String deviceName="";
     Component[]  comps=null;
     Actuator act=null;
     ESystem sys=null;
     ArrayList list=new ArrayList();
     
     DBDialog dbDialog = new DBDialog(electriclistener.getFrame(),list,Config.getString("ElectricPanel.SelectActuator"),false);
     dbDialog.pack();
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
     dbDialog.setVisible(true);
     deviceName=dbDialog.getText();

//     comps=edeviceListener.electrics.pneumatics.pneumaticPanel.getComponents();
     for(int i=0;i<comps.length;i++)
      {if(comps[i] instanceof Actuator)
        {act=(Actuator) comps[i];
         if(act.getName()!=null && act.getName().length()>0 && act.withLS() && act.getName().equals(deviceName))
          {act.addValve(edevice);break;}
//                 {act.addValve(this);actuator=act;break;}
        }
       if(comps[i] instanceof ESystem)
        {sys=(ESystem) comps[i];
         if(sys.getName()!=null && sys.getName().length()>0 && sys.withLS() &&  sys.getName().equals(deviceName))
          {sys.addValve(edevice);break;}
//                 {sys.addValve(this);esystem=sys;break;}
        }
      }
    }
   */
   /*
   public void connectEDevice(EDevice edevice)
    {
     ConnectionDialog cDialog = new ConnectionDialog(electriclistener.getFrame(),edevice.ced.name,true,edevice.ced.NAPKey,edevice.ced.NAPno);
     cDialog.pack();
     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     cDialog.setLocation(screenSize.width/2 - cDialog.getSize().width/2,screenSize.height/2 - cDialog.getSize().height/2);
     cDialog.setVisible(true);
     String rstr=cDialog.getText();
//System.err.println(rstr);
     if(rstr!=null && rstr.length()>0)
      {ModuleBase mb=null;
       if(edevice.ced.NAPKey!=null)
        {mb=(ModuleBase) Modules.modules.get(edevice.ced.NAPKey);
         if(mb!=null)
          {
//               m40.inObject[NAPno]=null;
           mb.inObject[edevice.ced.NAPno]=(Object)null;
          }
         edevice.ced.NAPKey=null;
//               m40.repaint();
        }
       if(rstr.equals("None")) edevice.ced.NAPKey=null;
       else
        {int ind=rstr.indexOf('_');
         if(ind==-1)
          {electriclistener.setStatus(Config.getString("EDevice.connection.returnstrerror"));
           return;
          }
         edevice.ced.NAPKey=rstr.substring(0,ind);
//System.err.println(edevice.NAPKey);
         edevice.ced.NAPno=Integer.parseInt(rstr.substring(ind+1,rstr.length()));
         mb=(ModuleBase) Modules.modules.get(edevice.ced.NAPKey);
         if(mb==null) {System.err.println("null m40?"); return;}
//               m40.inObject[NAPno]=this;
         mb.inObject[edevice.ced.NAPno]=(Object)edevice.ced;
//             m40.repaint();
        }
      }
     repaint();
    }
    */
   public void deleteEDevice(EDevice edevice)
    {
/*	   
	 Component[] comps=electriclistener.getPneumaticPanel().getComponents();
     Actuator act=null;
     ESystem sys=null;
     for(int i=0;i<comps.length;i++)
      {if(comps[i] instanceof Actuator)
        {act=(Actuator) comps[i];
         if(act.hasValve(edevice)) act.deleteValve(edevice);
        }
       if(comps[i] instanceof ESystem)
        {sys=(ESystem) comps[i];
         if(sys.hasValve(edevice)) sys.deleteValve(edevice);
        }
      }
  */
     clearCEDevice(edevice.ced);
     electriclistener.getEArrays().deleteEDevice(edevice);
     if(edevice.ced.actionType==CEDevice.TYPE_TIMER && electriclistener.hasSequence())
    	 electriclistener.sequenceRefreshSystemCombo();
    }
//---------------------------------------------
//    interface PLCListener
  
   public void setActuatorPosAccording2LS()
    {electriclistener.setActuatorPosAccording2LS();}
   
   public void changeStatus(EDevice ed)
    {repaint();
    }
 }
