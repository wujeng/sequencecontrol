package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.eelement.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JOptionPane;
/*
commandListener.add(new addCellCommand(this,ShiftEnabled,modified0,p0,type0,dir0,ed0,sys0,type,direct,ed,sys));
commandListener.add(new clearCommand(this,cellsdeleted));
commandListener.add(new setCursorCommand(this,xcol,yrow,dragPoint1.x,dragPoint1.y));
commandListener.add(new blockCommand(this,newp1,newp2,oldp1,oldp2));
commandListener.add(new pasteBoardToCommand(this,p1,p2,overWrite,gw,cellsdeleted));
commandListener.add(new copyToBoardCommand(this,newcg,groupWidth,oldcg,oldgw));
commandListener.add(new deleteBlockCommand(this,dpoint1,dpoint2,cellsdeleted));
commandListener.add(new changeEditModeCommand(this,overWrite));
commandListener.add(new changeRatioCommand(this,oldratio,ratio));
*/

public class SequencePanel extends JPanel implements MouseListener,MouseMotionListener,ActionListener
 {public static final int Command_addCell=1;
  public static final int Command_clear=2;
  public static final int Command_setCursor=3;
  public static final int Command_block=4;
  public static final int Command_pasteBoardTo=5;
  public static final int Command_copyToBoard=6;
  public static final int Command_deleteBlock=7;
  public static final int Command_changeEditMode=8;
  public static final int Command_changeRatio=9;

   public EDevice CondStart,CondStop,CondChoice,CondCounter;
   public int OperationMode;
   private Image[] markImage;
   private Image delayImage;
   private Image cursorImage;
//    private int PREFERRED_WIDTH = 300;
//    private int PREFERRED_HEIGHT = 400;
  private static final int MaxRows=100;
  private static final int MaxColumns=30;
  public static SequenceCell[][] cells=null;

public static final int  IG_None =-1;

private static final int IM_Next=0;
private static final int IM_End=1;
private static final int IM_None=2;

private static final int IO_Exist= 1;
private static final int IO_NoExist=0;

private static final int ReturnError= -345;
private static final int MaxStart= 20;
private static final int MaxStage= 20;
private static final int S_Aswitch =-9;
private static final int S_Bswitch =-99;

private int ComplexCount,GroupCount;
private int LayoutRow,LayoutCol;
private int[] OutState;
private int DelayCount;
//  TList *RelayList;
private EDevice[] RelayList;
private EDevice BufferRelay,StopRelay;

 private int CellRow,CellCol;
 private int[][] StartStage=new int[MaxStart][MaxStage];
 private int[] StopStage=new int[MaxStart];
 
 //private int[] StartExtra=new int[MaxStart];
 private CEDevice[] StartExtra=new CEDevice[MaxStart];
 //private int[] StopExtra=new int[MaxStart];
 private CEDevice[] StopExtra=new CEDevice[MaxStart];
 private EDevice RepeatN;
 private boolean[] ABswitch=new boolean[MaxStart];
 private boolean isRepeat;
  private int RangeCol,RangeRow,StartRow,StartCol;

  public double ratio=1.0;
  private boolean checked;
  public boolean overWrite;
  private Point dragPoint1=new Point(),dragPoint2=new Point();
  private int cellWidth,cellHeight;
  private int cellwid,cellhgt;

  private boolean dragging=false;
  public ArrayList cellGroup;
  public int groupWidth;

//  private int clipRows,clipColumns;
  private int WinRow1,WinRow2,WinCol1,WinCol2;
  private int[] SystemStep;
  public JPopupMenu popup;
  JMenuItem m,mp,mm,lsm,cm,tgm,tm;
  private int popRow,popCol;
  private ElectricListener electriclistener;
  private JScrollPane scrollpane;
  public void setJScrollPane(JScrollPane spane) {
	  scrollpane=spane;
  }
  public SequencePanel(ElectricListener electriclistener)
   {super();
   this.electriclistener=electriclistener;
     this.markImage=Sequence.markImage;
     this.delayImage=Sequence.delayImage;
     this.cursorImage=ElectricPanel.cursorImage;
    setLayout(null);
    setBackground(Color.white);
    init();
    addMouseListener(this);
    addMouseMotionListener(this);
 //   addKeyListener(this);
    popup = new JPopupMenu();
         m = new JMenuItem(Config.getString("SequencePanel.delete"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);
         m = new JMenuItem(Config.getString("SequencePanel.cut"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);
         mp = new JMenuItem(Config.getString("SequencePanel.paste"));
         mp.addActionListener(this);
         mp.addMouseListener(new menuItemMouseAdapter());
         popup.add(mp);
         m = new JMenuItem(Config.getString("SequencePanel.copy"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);
         popup.addSeparator();
         lsm = new JMenuItem(Config.getString("SequencePanel.limitswitch"));
         lsm.addActionListener(this);
         lsm.addMouseListener(new menuItemMouseAdapter());
         popup.add(lsm);
         cm = new JMenuItem(Config.getString("SequencePanel.counter"));
         cm.addActionListener(this);
         cm.addMouseListener(new menuItemMouseAdapter());
         popup.add(cm);
         tgm = new JMenuItem(Config.getString("SequencePanel.togglebutton"));
         tgm.addActionListener(this);
         tgm.addMouseListener(new menuItemMouseAdapter());
         popup.add(tgm);
         tm = new JMenuItem(Config.getString("SequencePanel.timer"));
         tm.addActionListener(this);
         tm.addMouseListener(new menuItemMouseAdapter());
         popup.add(tm);
         popup.addSeparator();
         overWrite=false;
         mm = new JMenuItem(Config.getString("Status.overwrite"));
         mm.addActionListener(this);
         mm.addMouseListener(new menuItemMouseAdapter());
         popup.add(mm);

        popup.addSeparator();
         m = new JMenuItem(Config.getString("SequencePanel.selectall"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);
         m = new JMenuItem(Config.getString("SequencePanel.clearall"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);

         popup.addSeparator();
         m = new JMenuItem(Config.getString("SequencePanel.zoom"));
         m.addActionListener(this);
         m.addMouseListener(new menuItemMouseAdapter());
         popup.add(m);

    cellGroup=new ArrayList();
    repaint();
   }

  public boolean isEmpty()
    {rescale();
      if(RangeCol>StartCol+1 || RangeRow>StartRow)
       return false;
      else
       return true;
   }

  private boolean inInputMark(int type)
    {if(type==SequenceCell.IT_Parallel || type==SequenceCell.IT_Choice ||
         type==SequenceCell.IT_Jump || type==SequenceCell.IT_Repeat)
        return true;
      else
       return false;
    }
  private boolean inOutputSet(int type)
    {if(type==SequenceCell.IT_System || type==SequenceCell.IT_Delay)
        return true;
      else
       return false;
    }

  private void init()
   {
    ratio=1.0;
    dragging=false;
    checked=false;                 // �������ˬd�X�m�O�X
    overWrite=false;                           // �л\�Ҧ��δ��J�Ҧ�
 //   sequence.setStatusMode(Config.getString("Status.insert"));
    cells=new SequenceCell[MaxRows][MaxColumns];
    for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
     for (int j=0;j<MaxColumns;j++)
      {cells[i][j]=new SequenceCell();}
    dragPoint1.x=0;dragPoint2.y=0;
    dragPoint2.x=dragPoint1.x;dragPoint2.y=dragPoint1.y;
    cellWidth=Integer.parseInt(Config.getString("Sequence.cellWidth"));
    cellHeight=Integer.parseInt(Config.getString("Sequence.cellHeight"));
    rescale();
}

  private void rescale()
   {
    RangeCol=MaxColumns-1;
    RangeRow=0;
    for(int j=RangeCol;j>=0;j--)
     for(int i=MaxRows-1;i>=0;i--)
      if (cells[i][j].type !=SequenceCell.IT_None)
       {if(i > RangeRow) RangeRow = i;
//        if(RangeCol==(MaxColumns-1)) RangeCol = j;
        if(j> RangeCol) RangeCol=j;
       }
    StartRow=RangeRow;
    StartCol=RangeCol;
    for(int j=0;j<=RangeCol;j++)
     for(int i=0;i<=RangeRow;i++)
      if(cells[i][j].type != SequenceCell.IT_None)
       {if(i < StartRow) StartRow=i;
//        if(StartCol==RangeCol) StartCol=j;
        if(j< StartCol) StartCol=j;
       }

  // �]�w�����ϥi�����d��(�G�����󪺽d��)
      // ���o�����Ϥ�,��ø�X���d��
    cellwid=(int) (cellWidth*ratio);
    cellhgt=(int) (cellHeight*ratio);
//System.out.println("cellwid:"+cellwid+":"+cellhgt);

    if(scrollpane!=null)
     {Rectangle rect=scrollpane.getViewport().getViewRect();
      WinCol1=rect.x/cellwid;
      WinCol2=WinCol1+rect.width/cellwid;
      WinRow1=rect.y/cellhgt;
      WinRow2=WinRow1+rect.height/cellhgt;
     }
    else
     {WinCol1=0;
      WinCol2=getSize().width/cellwid;
      WinRow1=0;
      WinRow2=getSize().height/cellhgt;
     }
     
//    setPreferredSize(new Dimension((RangeCol+1)*cellwid, (RangeRow+1)*cellhgt));
 //    if(Config.getBoolean("debug")) System.out.println("StartRow="+StartRow+" StartCol="+StartCol+" RangeRow="+RangeRow+" RangeCol="+RangeCol);
 //    if(Config.getBoolean("debug")) System.out.println("WinRow1="+WinRow1+" WinCol1="+WinCol1+" WinRow2="+WinRow2+" WinCol2="+WinCol2);
}

  private void PaintCell(Graphics g,int i,int j)
   {if(i<WinRow1 || i>WinRow2 || j<WinCol1 || j>WinCol2) return;
    if(i>=MaxRows || j>=MaxColumns) return;
    int x0=j*cellwid;
    int y0=i*cellhgt;
    if(cells[i][j].type >=SequenceCell.IT_Parallel && cells[i][j].type<=SequenceCell.IT_Delay) // copy ������
     {if(cells[i][j].type == SequenceCell.IT_Delay)
        g.drawImage(delayImage,x0,y0,cellwid,cellhgt,this);
      else
        g.drawImage(markImage[cells[i][j].type],x0,y0,cellwid,cellhgt,this);
      if(cells[i][j].type==SequenceCell.IT_Delay && cells[i][j].ed.ced.name!=null)   // �g�X�����W��
       {g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (7*ratio)));
         g.setColor(Color.blue);
         g.drawString(cells[i][j].ed.ced.name,x0+2,y0+cellhgt-2);
       }
    }
   else
    {g.setColor(Color.white);
      g.fillRect(x0,y0,cellwid,cellhgt);
      if(cells[i][j].type == SequenceCell.IT_System)
       {if(cells[i][j].sys!=null && cells[i][j].sys.getActuatorName()!=null)
          {String sname="";
            switch(cells[i][j].dir)
             {case SequenceCell.ID_Forward: sname=cells[i][j].sys.getActuatorName()+"+";break;
               case SequenceCell.ID_Backward: sname=cells[i][j].sys.getActuatorName()+"-";break;
               case SequenceCell.ID_None: sname=cells[i][j].sys.getActuatorName();break;
             }
            g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (10*ratio)));
            g.setColor(Color.blue);
            g.drawString(sname,x0+2,y0+12);
          }
        if(cells[i][j].ed!=null && cells[i][j].ed.ced.name!=null)
         {g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (10*ratio)));
           g.setColor(Color.blue);
           g.drawString(cells[i][j].ed.ced.name,x0+2,y0+cellhgt-2);
         }
       }
   }
/*
  if(cells[i][j].type!=SequenceCell.IT_None)
   {g.setFont(new Font("Small Fonts",Font.PLAIN,(int) (8*ratio)));
     g.setColor(Color.red);
     g.drawString(Integer.toString(cells[i][j].group),x0+2,y0+cellhgt/2+4);
   }
*/
}

private void MessageBox(String des,String type)
 {JOptionPane.showMessageDialog(electriclistener.getFrame(),type+":"+des);
 }

private void MarkArea(Graphics g)
{        // ���ܴ���(�϶�)
  for(int i=dragPoint1.x;i<=dragPoint2.x;i++)
   for(int j=dragPoint1.y;j<=dragPoint2.y;j++)
    {g.setXORMode(Color.yellow);
     g.drawImage(cursorImage,i*cellwid,j*cellhgt,cellwid,cellhgt,this);
    }
  electriclistener.setSequenceStatusPos("row "+dragPoint1.x+" : col "+dragPoint1.y);
}

private class addCellCommand extends Command
    {boolean modified0;
	 Point p0;
	 int type0;
     int dir0;
     EDevice ed0;
     ElectricFace sys0;
     int type1;
     int dir1;
     EDevice ed1;
     ElectricFace sys1;
     boolean ShiftEnabled;
  	 public addCellCommand(Object ele,boolean ShiftEnabled,boolean modified0,Point p0,
  	   int type0,int dir0,EDevice ed0,ElectricFace sys0,
  	   int type1,int dir1,EDevice ed1,ElectricFace sys1)
      {super("SequencePanel",ele,Command_addCell);
       this.ShiftEnabled=ShiftEnabled;
       this.modified0=modified0;
       this.p0=p0;
       this.type0=type0;
       this.dir0=dir0;
       this.ed0=ed0;
       this.sys0=sys0;
       this.type1=type1;
       this.dir1=dir1;
       this.ed1=ed1;
       this.sys1=sys1;
      }
    public void undo()
     {if(!overWrite && ShiftEnabled)
       {for(int j=p0.x;j<MaxColumns-2;j++)
        copyCell(p0.y,j+1,p0.y,j);
       }
      dragPoint1=new Point(p0);
      cells[dragPoint1.y][dragPoint1.x].type=type0;
      cells[dragPoint1.y][dragPoint1.x].dir=dir0;
      cells[dragPoint1.y][dragPoint1.x].ed=ed0;
      cells[dragPoint1.y][dragPoint1.x].sys=sys0;
//      rescale();
      repaint();
      electriclistener.setModified(modified0);
     }
    public void redo()
     {dragPoint1=new Point(p0);
	  if(!overWrite && ShiftEnabled)
       {for(int j=MaxColumns-2;j>=dragPoint1.x;j--)
         copyCell(dragPoint1.y,j,dragPoint1.y,j+1);
       }
//System.err.println("AddCell:"+type);
      cells[dragPoint1.y][dragPoint1.x].type=type1;
      cells[dragPoint1.y][dragPoint1.x].dir=dir1;
      cells[dragPoint1.y][dragPoint1.x].ed=ed1;
      cells[dragPoint1.y][dragPoint1.x].sys=sys1;
      ShiftCursor();
//      rescale();
      repaint();
      electriclistener.setModified(true);
     }
   }


public void AddCell(int type,ElectricFace sys, EDevice ed,int direct, boolean ShiftEnabled)
{Point p0=new Point(dragPoint1);
 int type0=cells[dragPoint1.y][dragPoint1.x].type;
 int dir0=cells[dragPoint1.y][dragPoint1.x].dir;
 EDevice ed0=cells[dragPoint1.y][dragPoint1.x].ed;
 ElectricFace sys0=cells[dragPoint1.y][dragPoint1.x].sys;
 boolean modified0=electriclistener.getModified();
//System.err.println("AddCell enter");
  if(!overWrite && ShiftEnabled)
   {for(int j=MaxColumns-2;j>=dragPoint1.x;j--)
       copyCell(dragPoint1.y,j,dragPoint1.y,j+1);
   }
//System.err.println("AddCell:"+type);
  cells[dragPoint1.y][dragPoint1.x].type=type;
  cells[dragPoint1.y][dragPoint1.x].dir=direct;
  cells[dragPoint1.y][dragPoint1.x].ed=ed;
  cells[dragPoint1.y][dragPoint1.x].sys=sys;
  ShiftCursor();
 // rescale();
  repaint();
  electriclistener.setModified(true);
  electriclistener.addCommand(new addCellCommand(this,ShiftEnabled,modified0,p0,type0,dir0,ed0,sys0,type,direct,ed,sys));
}
private void copyCell(int row1,int col1,int row2,int col2)
  {cells[row2][col2].type=cells[row1][col1].type;
    cells[row2][col2].dir=cells[row1][col1].dir;
    cells[row2][col2].ed=cells[row1][col1].ed;
    cells[row2][col2].sys=cells[row1][col1].sys;
    cells[row2][col2].group=cells[row1][col1].group;
  }

private void ShiftCursor() // �]�w�q�𤸥󪺤��e
{if(dragPoint1.x < MaxColumns -1)   // ���Хk���@��
   {dragPoint1.x++;dragPoint2.x=dragPoint1.x;}
  else if (dragPoint1.y < MaxRows -1 )
   {dragPoint1.y++;dragPoint1.x=0;
    dragPoint2.x=dragPoint1.x;      // �p�G���F����,���в����U�@�C���}�Y
    dragPoint2.y=dragPoint1.y;
   }
  else
   {electriclistener.setSequenceStatus(Config.getString("SequencePanel.exceedmaxrow"));
   }
}
public void SetCursor(int row, int col)
{// if(PLCRunFlag) {pneumaticPanel.Hint="�Х������ʺA����!!";return;}
  if(row < 0 || row > MaxRows) return;
  if(col < 0 || col > MaxColumns) return;
  dragPoint1.x=col;               // �]�w���Ц��m
  dragPoint2.x=dragPoint1.x;
  dragPoint1.y=row;
  dragPoint2.y=dragPoint1.y;
  repaint();
}

   private class clearCommand extends Command
    {ArrayList al;
	public clearCommand(Object ele,ArrayList al)
     {super("SequencePanel",ele,Command_clear);
      this.al=al;
     }
    public void undo()
     {int index=0;
	  for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
       for (int j=0;j<MaxColumns;j++)
        {cells[i][j]=new SequenceCell((SequenceCell) al.get(index));
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
{ArrayList cellsdeleted=new ArrayList();
 for (int i=0;i<MaxRows;i++)       // �]�Ҧ��� Cell ���ť�
   for (int j=0;j<MaxColumns;j++)
    {cellsdeleted.add(new SequenceCell(cells[i][j]));
	 ClearCell(i,j);
    }
 electriclistener.addCommand(new clearCommand(this,cellsdeleted));
   dragPoint1.x=0;dragPoint1.y=0;
   dragPoint2.x=dragPoint1.x;
   dragPoint2.y=dragPoint2.y;
   repaint();
}

public void degroup()
    {dragPoint2.x=dragPoint1.x;
      dragPoint2.y=dragPoint1.y;
    }

  private void ShiftBlockLeft() // �ŤU(���h)�����ϤW�϶�������
   {if(overWrite) return;                                  // (�϶��k�������\�L�϶�)
     int k=dragPoint2.x-dragPoint1.x+1;
     for (int j=dragPoint1.x;j<MaxColumns-k;j++)
      for (int i=dragPoint1.y;i<=dragPoint2.y;i++)
       copyCell(i,j+k,i,j);
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
     {MessageBox(Config.getString("SequencePanel.overrange")+"("+MaxRows+
                 Config.getString("SequencePanel.row")+" "+MaxColumns+
                 Config.getString("SequencePanel.col")+")!!",
                 Config.getString("SequencePanel.warn"));
      return;
     }
    for(int j=MaxColumns-1;j>=dragPoint1.x+groupWidth;j--)
     for(int i=dragPoint1.y;i<dragPoint1.y+groupHeight;i++)
       copyCell(i,j-groupWidth,i,j);
    electriclistener.setModified(true);
  }

  private class pasteBoardToCommand extends Command
   {Point dp1;
    Point dp2;
    boolean editmode;
    int groupwidth;
    ArrayList al;
	public pasteBoardToCommand(Object ele,Point p1,Point p2,boolean em,int gw,ArrayList cs)
     {super("SequencePanel",ele,Command_pasteBoardTo);
      dp1=p1;
      dp2=p2;
      editmode=em;
      groupwidth=gw;
      al=cs;
     }
    public void undo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      overWrite=editmode;
      groupWidth=groupwidth;
      if(!overWrite) ShiftBlockLeft();
      int k=0;
      SequenceCell lc=null;
      for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
       for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
        {lc=(SequenceCell) al.get(k);
         cells[i][j].type=lc.type;
         cells[i][j].dir=lc.dir;
         cells[i][j].ed=lc.ed;
         cells[i][j].sys=lc.sys;
         cells[i][j].group=lc.group;
         k++;
        }
      checked=false;
      electriclistener.setModified(true);
     }
    public void redo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      overWrite=editmode;
      groupWidth=groupwidth;
	  if(!overWrite) ShiftBlockRight();  // ���n�K�W�ϰ줺�������k��
      int k=0;
      SequenceCell lc=null;
      for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
       for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
        {lc=(SequenceCell) al.get(k);
         cells[i][j].type=lc.type;
         cells[i][j].dir=lc.dir;
         cells[i][j].ed=lc.ed;
         cells[i][j].sys=lc.sys;
         cells[i][j].group=lc.group;
         k++;
        }
      checked=false;
      electriclistener.setModified(true);
     }
   }

  public void pasteBoardTo()
   {                  // ���ŶKï�W���F��,�K�b�����ϴ��Ъ����m�W
     if(cellGroup==null || cellGroup.size()==0) return;
     if(dragPoint1.x+groupWidth > MaxColumns)
      {MessageBox(Config.getString("SequencePanel.overcol"),Config.getString("SequencePanel.warn"));
        return;
      }
    Point p1=new Point(dragPoint1);
    Point p2=new Point(dragPoint2);
    int gw=groupWidth;
    ArrayList cellsdeleted=new ArrayList();
//    LadderCell lc=null;
    for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
     for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
      {cellsdeleted.add(new SequenceCell(cells[i][j]));
      }
    if(!overWrite) ShiftBlockRight();  // ���n�K�W�ϰ줺�������k��
    int k=0;
    SequenceCell lc=null;
    for(int i=dragPoint1.y;i<dragPoint1.y+cellGroup.size()/groupWidth;i++)
     for(int j=dragPoint1.x;j<dragPoint1.x+groupWidth;j++)
      {lc=(SequenceCell) cellGroup.get(k);
        cells[i][j].type=lc.type;
        cells[i][j].dir=lc.dir;
        cells[i][j].ed=lc.ed;
        cells[i][j].sys=lc.sys;
        cells[i][j].group=lc.group;
        k++;
      }
    checked=false;
    electriclistener.setModified(true);
    electriclistener.addCommand(new pasteBoardToCommand(this,p1,p2,overWrite,gw,cellsdeleted));
    repaint();
  }

  private class copyToBoardCommand extends Command
    {ArrayList newcellgroup;
     ArrayList oldcellgroup;
     int newgroupwidth;
     int oldgroupwidth;
	public copyToBoardCommand(Object ele,ArrayList og,int ogw,ArrayList ng,int ngw)
     {super("SequencePanel",ele,Command_copyToBoard);
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
       cellGroup.add(new SequenceCell(cells[i][j]));
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
     ArrayList al;
	public deleteBlockCommand(Object ele,Point p1,Point p2,ArrayList cs)
     {super("SequencePanel",ele,Command_deleteBlock);
      dp1=p1;
      dp2=p2;
      al=cs;
     }
    public void undo()
     {dragPoint1=dp1;
      dragPoint2=dp2;
      if(!overWrite) ShiftBlockRight();
      int index=0;
      for(int i=dragPoint1.y;i<=dragPoint2.y;i++)
      for(int j=dragPoint1.x;j<=dragPoint2.x;j++)
       {cells[i][j]=new SequenceCell((SequenceCell) al.get(index));
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
       {SequenceCell lc=new SequenceCell(cells[i][j]);
        cellsdeleted.add(lc);
	    ClearCell(i,j);
       }
     if(!overWrite) ShiftBlockLeft();
     electriclistener.addCommand(new deleteBlockCommand(this,dpoint1,dpoint2,cellsdeleted));
   }

private void ClearCell(int i,int j)
{ cells[i][j].type = SequenceCell.IT_None;
  cells[i][j].ed=null;
  cells[i][j].sys=null;
  cells[i][j].group=-1;
  cells[i][j].dir=SequenceCell.ID_None;
}

//-------------------------------------------------------------------------------------------------

public void Condition()
 {ConditionDialog cdialog=new ConditionDialog(electriclistener.getFrame(),this,electriclistener.getEArrays().EDeviceArray);
 }
//---------------------------------------------------------------------------
public boolean InputCheck()
{Buffer buf=new Buffer();
  if(RangeCol <=0 && RangeRow <= 0)
   {MessageBox(Config.getString("SequencePanel.sequencefirst"),Config.getString("SequencePanel.inputcheck"));
    return false;
   }
  for(int i=StartRow;i<=RangeRow;i++)
   for(int j=StartCol;j<=RangeCol;j++) //�M���ʧ@�Ÿ������հO��
    cells[i][j].group=IG_None;
  checked=false;
  if(!CheckMarkPair()) return false;  // �ˬd�����j���аO�O�_���ﲣ��
//�O���P�ɰʧ@���Ÿ��bgroup,�̫��@�ӦP�ɰʧ@�O IM_End,�_�h�O IM_Next
//�O���P�ɰʧ@���Ÿ��bendmark,�̫��@�ӦP�ɰʧ@�O IM_End,�_�h�O IM_Next
  if(!SetNextEnd(StartRow,StartCol,RangeCol,RangeRow+1,buf)) return false;
  for(int i=StartRow;i<=RangeRow;i++)  //�Y�L�O���P�ɰʧ@���Ÿ�,�i�ର����(�a��)���Ÿ�
   for(int j=StartCol;j<=RangeCol;j++)
    if(inOutputSet(cells[i][j].type))
     {if(cells[i][j].group!=IM_Next && cells[i][j].group!=IM_End)
       {SetCursor(i,j);
        MessageBox(Config.getString("SequencePanel.wrongsymbol"),Config.getString("SequencePanel.motioncheck"));
        return false;
       }
     }
  if(!CheckSystemNoLS()) return false;
  if(!CheckParallel()) return false;   //�ˬd�����j��
  //�ˬd�ʧ@���ǬO�_�Ĭ�
  SystemStep=new int[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(int i=0;i<SystemStep.length;i++)
   SystemStep[i]=0;
//  ElectricFace sys;
//  for(int i=0;i<SystemStep.length;i++)
//   {sys=(ElectricFace) electricPanel.ElectricFaceArray.get(i);
//    sys.curImage=0;
//    SystemStep[i]=sys.curImage;
//   }
  if(!CheckSequence(StartRow,StartCol,RangeCol,RangeRow+1,SystemStep,buf)) return false;

  //�ˬd���ܰj�����ĤG����
  for(int i=0;i<SystemStep.length;i++)
   SystemStep[i]=0;
  if(!CheckChoice2(StartRow,StartCol,RangeCol,RangeRow+1,SystemStep,buf)) return false;
  //�ˬd���D�j��
  for(int i=0;i<SystemStep.length;i++)
   SystemStep[i]=0;
  if(!CheckJump(StartRow,StartCol,RangeCol,RangeRow+1,SystemStep,buf)) return false;
  //�ˬd�`���j��
  for(int i=0;i<SystemStep.length;i++)
   SystemStep[i]=0;
  if(!CheckRepeat(StartRow,StartCol,RangeCol,RangeRow+1,SystemStep,buf)) return false;
  if(!CheckMarkSwitch()) return false;
  if(!CheckDelayRepeat()) return false;
  checked=true;
  return true;
}

private boolean CheckDelayRepeat()
{
  EDevice tim;
  for(int i=StartRow;i<=RangeRow;i++)  //�Y�L�O���P�ɰʧ@���Ÿ�,�i�ର����(�a��)���Ÿ�
   for(int j=StartCol;j<=RangeCol;j++)
    if(cells[i][j].type==SequenceCell.IT_Delay)
     {tim=cells[i][j].ed;
      for(int ii=StartRow;ii<=RangeRow;ii++)
       for(int jj=StartCol;jj<=RangeCol;jj++)
        {if(ii==i && jj==j) continue;
         if(cells[ii][jj].type==SequenceCell.IT_Delay)
          {if(cells[ii][jj].ed==tim)
            {MessageBox(Config.getString("SequencePanel.repeattimer"),Config.getString("SequencePanel.error"));
             return false;
            }
          }
        }
     }
  return true;
}

private boolean CheckSystemNoLS()
{ int k=0;
  boolean findls=false;
  for(int i=StartRow;i<=RangeRow;i++)  //�Y�L�O���P�ɰʧ@���Ÿ�,�i�ର����(�a��)���Ÿ�
   for(int j=StartCol;j<=RangeCol;j++)
    if(cells[i][j].group==IM_End)
     {findls=false;
      k=i;
      while(k>=StartRow)
       {if(cells[k][j].type==SequenceCell.IT_Delay)
         {findls=true; break;}
        else if(cells[k][j].type==SequenceCell.IT_System)
         {if(cells[k][j].sys.withLS())
           {findls=true; break;}
         }
        if(k-1 < StartRow) break;
        if(cells[k-1][j].group==IM_End || cells[k-1][j].type==SequenceCell.IT_None) break;
        k--;
       }
      if(!findls)
       {SetCursor(i,j);
        MessageBox(Config.getString("SequencePanel.noLSsystem"),Config.getString("SequencePanel.SequencePanel.motioncheck"));
        return false;
       }
     }
  return true;
}

private boolean CheckMarkSwitch()
{int i=0,j=0;
  boolean trueflag=true;
  for(i=StartRow;i<=RangeRow;i++)
   {for(j=StartCol;j<=RangeCol;j++)
     {if(cells[i][j].type == SequenceCell.IT_Choice || cells[i][j].type == SequenceCell.IT_Jump ||
         cells[i][j].type == SequenceCell.IT_Repeat)
       {if(cells[i][j].ed==null)
        {trueflag=false; break;}
       }
     }
    if(!trueflag) break;
   }
  if(!trueflag)
   {SetCursor(i,j);
    MessageBox(Config.getString("SequencePanel.switchforcomplex"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  trueflag=true;
  if(CondStart==null) trueflag=false;
//  if(CondStop==null) trueflag=false;
  if(OperationMode==2 && CondChoice==null) trueflag=false;
  if(OperationMode==3 && CondCounter==null) trueflag=false;
  if(!trueflag)
   {
/*
    String str="�Х��]�n�ާ@����!!";
    SetCursor(i,j);
    MessageBox(str,"�ʧ@�����ˬd");
    return false;
*/
    boolean hasbutton=false;
    EDevice ed=null;
    for(i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
     {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
       if(ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO)
        {CondStart=ed;hasbutton=true;break;}
     }
    if(!hasbutton)
     {CondStart=new EDevice("PushButton", electriclistener);
      electriclistener.getEArrays().addEDevice(CondStart);
     }
   }
  return true;
}

private int StartMark(int row,int col,Buffer buf,int pos)
 {       //���X�����j�����Ĥ@�����}�Y
  int k=row;
  while(true)
   {if(k-1 < 0) break;
    if(!inInputMark(cells[k-1][col].type) && cells[k-1][col].type != SequenceCell.IT_LeftConnect) break;
    k--;
   }
  if(pos==0) buf.startrow=k;
  else buf.temp=k;
  while(true)
   {if(inInputMark(cells[k][col].type)) return k;
    if(k==RangeRow) break;
    if(!inInputMark(cells[k+1][col].type) && cells[k+1][col].type != SequenceCell.IT_LeftConnect) break;
    k++;
   }
  SetCursor(k,col);
  MessageBox(Config.getString("SequencePanel.invalidsymbol"),Config.getString("SequencePanel.motioncheck"));
  return ReturnError;
}

private int NextMark(int row,int col,Buffer buf)
{     //���X�����j���U�@�����}�Y
  if(row==RangeRow)
   {buf.lastrow=RangeRow; return -1;}
  int type0=cells[row][col].type;
  int k=row+1;
  while(true)
   {if(inInputMark(cells[k][col].type))
     {if(cells[k][col].type != type0)
       {SetCursor(k,col);
        MessageBox(Config.getString("SequencePanel.different"),Config.getString("SequencePanel.motioncheck"));
        return ReturnError;
       }
      else
       return k;
     }
    if(k==RangeRow) {buf.lastrow=RangeRow; break;}
    if(!inInputMark(cells[k][col].type) && cells[k][col].type != SequenceCell.IT_LeftConnect)
     {buf.lastrow=k-1; break;}
    k++;
   }
  return -1;
}

private void setElement(int row, int col, int lastrow)
{    //�]�w�ʧ@�Ÿ�Cell[row][col]�U���Ҧ��P�ɰʧ@���P�ɰʧ@�Ÿ�
  int k=row;
  while(true)
   {cells[k][col].group=IM_Next;
    if(k==RangeRow || k+1==lastrow)
     {cells[k][col].group=IM_End;
      break;
     }
    if(cells[k+1][col].type==SequenceCell.IT_None)
     {cells[k][col].group=IM_End;
      break;
     }
    k++;
   }
}

private boolean CheckMarkPair()
{ int k;       // �ˬd�����j�����аO��������
  int[] temp=new int[200];

  for(int i=StartRow;i<=RangeRow;i++)
   {k=0;
    for(int j=StartCol;j<=RangeCol;j++)
     {if(inInputMark(cells[i][j].type) || cells[i][j].type == SequenceCell.IT_LeftConnect)
       {temp[k]=j; k++;}
      if(cells[i][j].type==SequenceCell.IT_RightConnect)
       {if(k-1 < 0)
         {SetCursor(i,j);
          MessageBox(Config.getString("SequencePanel.pair"),Config.getString("SequencePanel.motioncheck"));
          return false;
         }
        else
         {cells[i][j].dir=temp[k-1];
          cells[i][cells[i][j].dir].dir=j;
          k--;
         }
       }
     }
    if(k!=0)
     {SetCursor(i,temp[k-1]);
      MessageBox(Config.getString("SequencePanel.pair"),Config.getString("SequencePanel.motioncheck"));
      return false;
     }
   }

  for(int i=StartRow;i<=RangeRow;i++)
   for(int j=StartCol;j<=RangeCol;j++)
    if(inInputMark(cells[i][j].type) || cells[i][j].type == SequenceCell.IT_LeftConnect
                                           || cells[i][j].type == SequenceCell.IT_RightConnect)
     cells[i][j].group=IG_None;
  ComplexCount=0;
  for(int i=StartRow;i<=RangeRow;i++)
   for(int j=StartCol;j<=RangeCol;j++)
    if(inInputMark(cells[i][j].type) && cells[i][j].group < 0)
     {setComplexCount(i,j);
      setComplexCount(i,cells[i][j].dir);
      ComplexCount++;
     }
  return true;
}

private void setComplexCount(int row, int col)
{        //�����������j���s��
  int row0=row;
  boolean left;
  if(inInputMark(cells[row][col].type)) left=true;
  else left=false;
  while(true)
   {cells[row0][col].group=ComplexCount;
    if(row0==RangeRow) break;
    if(left)
     {if(!inInputMark(cells[row0+1][col].type) &&
          cells[row0+1][col].type!=SequenceCell.IT_LeftConnect) break;
     }
    else
     {if(cells[row0+1][col].type!=SequenceCell.IT_RightConnect) break;
     }
    row0++;
   }
}

private boolean CheckParallel()
{                //�ˬd�����j���������ʧ@�O�_�۽Ĭ�
  boolean[] ComplexCheck=new boolean[50];

  for(int i=0;i<ComplexCount;i++) ComplexCheck[i]=false;

  for(int i=StartRow;i<=RangeRow;i++)
   for(int j=StartCol;j<=RangeCol;j++)
    {if(cells[i][j].type==SequenceCell.IT_Parallel && ComplexCheck[cells[i][j].group]==false)
      {if(!CheckOneParallel(i,j,cells[i][j].dir)) return false;
       ComplexCheck[cells[i][j].group]=true;
      }
    }
  return true;
}

private boolean CheckOneParallel(int row,int col1,int col2)
{ int n,row1;  //�ˬd�����j���������ʧ@�O�_�۽Ĭ�
  boolean loop;
  boolean[] SystemUsed=new boolean[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(int i=0;i<SystemUsed.length;i++) SystemUsed[i]=false;
  boolean[] SUsed=new boolean[SystemUsed.length];
  int row0=row;
  Buffer buf=new Buffer();
  while(true)
   {for(int i=0;i<SystemUsed.length;i++) SUsed[i]=false;
    n=NextMark(row0,col1,buf);
    if(n > 0) loop=true;
    else
     {loop=false;
      row1=row0+1;
      while(true)
       {if(row1-1==RangeRow) break;
        if(cells[row1][col1].type != SequenceCell.IT_LeftConnect &&
           cells[row1][col1].type != SequenceCell.IT_None ) break;
        row1++;
       }
      n=row1;
     }
    for(int j=row0;j<n;j++)
     for(int k=col1+1;k<col2;k++)
      if(cells[j][k].type==SequenceCell.IT_System) SUsed[getSystemNo(j,k)]=true;
    for(int j=0;j<SUsed.length;j++)
     if(SystemUsed[j] && SUsed[j])
      {SetCursor(row0,col1);
       MessageBox(Config.getString("SequencePanel.twooutsymbol"),Config.getString("SequencePanel.motioncheck"));
       return false;
      }
    for(int j=0;j<SUsed.length;j++)
     if(SUsed[j]) SystemUsed[j]=true;
    if(!loop) break;
    row0=n;
   }
  return true;
}

private int getSystemNo(int row, int col)
{ int n=-1; //���X�ʧ@�Ÿ�Cell[row][col]���������X�t�νs��
  boolean find=false;
  ElectricFace sys;
  for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
   {sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
    if(sys==cells[row][col].sys)
     {n=i;find=true;break;}
   }
  if(cells[row][col].type!=SequenceCell.IT_System)
   {find=false; n=-1;}
  if(!find && cells[row][col].type != SequenceCell.IT_Delay)
   {SetCursor(row,col);
    MessageBox(Config.getString("SequencePanel.noout"),Config.getString("SequencePanel.motioncheck"));
   }
  return n;
}

private boolean CheckStep(int row, int col, int state[])
{ int n,nextstep;//�ˬd�ʧ@�Ÿ�Cell[row][col]���ʧ@�U,�O�_���F�����w�������}��
  boolean fail=false;
  if(cells[row][col].type==SequenceCell.IT_System && cells[row][col].sys instanceof ESystem && cells[row][col].sys.withLS() && !cells[row][col].sys.getCDOutput().twoWay) return true;
  boolean[] Oexist=new boolean[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(int i=0;i<Oexist.length;i++) Oexist[i]=false;
  int row1=row;
  ElectricFace sys;
  while(true)
   {//if(cells[row1][col].type != SequenceCell.IT_Delay)
    if(cells[row1][col].type == SequenceCell.IT_System)
     {n=getSystemNo(row1,col);
      sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(n);
      if(sys.withLS())
       {
        if(cells[row1][col].ed==null)
         {SetCursor(row1,col);
          MessageBox(Config.getString("SequencePanel.in")+sys.getActuatorName()+Config.getString("SequencePanel.putLS"),Config.getString("SequencePanel.error"));
          return false;
         }
        else
         {nextstep=electriclistener.getLSPosition(cells[row1][col].ed);
           if(nextstep<0)
            {SetCursor(row1,col);
             MessageBox(Config.getString("SequencePanel.in")+sys.getActuatorName()+Config.getString("SequencePanel.putLS"),Config.getString("SequencePanel.error"));
             return false;
            }
         }
        switch(cells[row1][col].dir)
         {case SequenceCell.ID_Forward:
            if(nextstep <= state[n]) fail=true;
            break;
          case SequenceCell.ID_Backward:
            if(nextstep >= state[n]) fail=true;
            break;
         }
        if(fail)
         {SetCursor(row1,col);
          MessageBox(Config.getString("SequencePanel.wrongLSposition"),Config.getString("SequencePanel.motioncheck"));
          return false;
         }
        else
         {state[n]=nextstep;
 //     return true;
         }
        if(Oexist[n])
         {SetCursor(row1,col);
          MessageBox(Config.getString("SequencePanel.motionconflict"),Config.getString("SequencePanel.motioncheck"));
          return false;
         }
        else
         {Oexist[n]=true;}
       }
     }
    if(cells[row1][col].group==IM_End) break;
    row1++;
   }
  return true;
}

private boolean SetNextEnd(int row1,int col1,int col2,int row2,Buffer buf)
//�]�w�Ҧ��P�ɰʧ@���P�ɰʧ@�Ÿ�,�ð��ʧ@���ǰ����ˬd
{ int i=0,k=0,lineOutNo=0,type0=-1;
   boolean loop=false,simple=false;
  int markrow=0;
  boolean hasSubMark=false;
  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  int n=-1;
  int row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  if(n==-1)
   {SetCursor(row,col1);
    MessageBox(Config.getString("SequencePanel.nostart"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return false;
    else
     {
//String startn(n);
//Application->MessageBox(startn.c_str(),"",MB_OK);
      row=n;
      type0=cells[n][col1].type;

      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
  //    if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat || type0==SequenceCell.IT_Choice)

       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;
  int branchcount=1;
  int row20=0,sm=0;
  hasSubMark=false;
  Buffer buf0=new Buffer();
  while(true)
   {if(!simple)
     {
      n=NextMark(row,col1,buf);
      if(n==ReturnError) return false;
      else if(n >= 0 && loop==false)
       {SetCursor(n,col1);
        MessageBox(Config.getString("SequencePanel.oneforjump"),Config.getString("SequencePanel.motioncheck"));
        return false;
       }
      else if(n >= 0 && loop==true)
       {branchcount++;
        row20=n;
        if(branchcount>2 && type0==SequenceCell.IT_Choice)
         {SetCursor(n,col1);
          MessageBox(Config.getString("SequencePanel.twoforchoice"),Config.getString("SequencePanel.motioncheck"));
          return false;
         }
       }
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    lineOutNo=0;
    markrow=row;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       {setElement(row,i,row20);
        lineOutNo++;
       }
      else if(inInputMark(cells[row][i].type)|| cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return false;
        if(!SetNextEnd(row,i,cells[sm][i].dir,row20,buf0)) return false;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
        hasSubMark=true;
       }
     }
    if(lineOutNo==0 && !hasSubMark)
     {SetCursor(markrow,col1);
      MessageBox(Config.getString("SequencePanel.emptybranch"),Config.getString("SequencePanel.motioncheck"));
      return false;
     }
    if(!loop) break;
    row=n;
   }
  if(type0 == SequenceCell.IT_Parallel && branchcount==1)
   {SetCursor(row1,col1);
    MessageBox(Config.getString("SequencePanel.overtwo"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  if(type0 == SequenceCell.IT_Choice && branchcount != 2)
   {SetCursor(row1,col1);
    MessageBox(Config.getString("SequencePanel.oneforjumpcir"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  return true;
}

private boolean CheckSequence(int row1,int col1,int col2,int row2,int state[],Buffer buf)
//�ˬd�Ҧ��ʧ@�Ÿ����ʧ@�U,�O�_���F�����w�������}��
{ int i=0,k=0,type0=-1;
   boolean loop=false,simple=false;
  int[] state0=new int[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(i=0;i<state0.length;i++) state0[i]=state[i];
  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  int n=-1;
  int row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  if(n==-1)
   {SetCursor(row,col1);
    MessageBox(Config.getString("SequencePanel.nostart"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return false;
    else
     {
//String startn(n);
//Application->MessageBox(startn.c_str(),"",MB_OK);
      row=n;
      type0=cells[n][col1].type;

    //      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat || type0==SequenceCell.IT_Choice)

       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;

  int row20=0,sm=0;
  Buffer buf0=new Buffer();
  while(true)
   {if(!simple)
     {n=NextMark(row,col1,buf);
      if(n==ReturnError) return false;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {
      if(cells[row][i].type==SequenceCell.IT_System)
       {if(!CheckStep(row,i,state0)) return false;}
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return false;
        if(!CheckSequence(row,i,cells[sm][i].dir,row20,state0,buf0))
           return false;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(!loop) break;
    row=n;
    for(i=0;i<state.length;i++)
     state[i]=state0[i];
   }
  return true;
}

private boolean CheckChoice2(int row1,int col1,int col2,int row2,int state[],Buffer buf)
//�ˬd���ܰj�����ĤG����,�ʧ@�O�_�i�F���w�w�������}��
{ int i=0,k=0,type0=-1;
  boolean loop,simple=false;
  int[] state0=new int[state.length];
  for(i=0;i<state0.length;i++) state0[i]=state[i];
  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  int n=-1;
  int row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  if(n==-1)
   {SetCursor(row,col1);
    MessageBox(Config.getString("SequencePanel.nostart"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return false;
    else
     {
//String startn(n);
//Application->MessageBox(startn.c_str(),"",MB_OK);
      row=n;
      type0=cells[n][col1].type;

    //      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat || type0==SequenceCell.IT_Choice)

       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;

  int row20=0,sm=0;
  Buffer buf0=new Buffer();
  while(true)
   {if(!simple)
     {n=NextMark(row,col1,buf);
      if(n==ReturnError) return false;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       {if(!CheckStep(row,i,state0)) return false;}
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return false;
        if(!CheckSequence(row,i,cells[sm][i].dir,row20,state0,buf0))
           return false;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(!loop) break;
    row=n;
    for(i=0;i<state.length;i++)
     state[i]=state0[i];
   }
  return true;
}

private boolean CheckJump(int row1,int col1,int col2,int row2,int state[],Buffer buf)
//�ˬd���D�j�������D�ʧ@�U,�O�_���F�����w�������}��
{ int i=0,k=0,n=-1,row=0,type0=-1;
  boolean loop=false,simple=false;
  int[] state0=new int[state.length];
  for(i=0;i<state0.length;i++) state0[i]=state[i];
  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  if(n==-1)
   {SetCursor(row,col1);
    MessageBox(Config.getString("SequencePanel.nostart"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return false;
    else
     {
//String startn(n);
//Application->MessageBox(startn.c_str(),"",MB_OK);
      row=n;
      type0=cells[n][col1].type;
      if(type0==SequenceCell.IT_Jump) return true;
//      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat || type0==SequenceCell.IT_Choice)
       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;

  int row20=0,sm=0;
  Buffer buf0=new Buffer();
  while(true)
   {if(!simple)
     {n=NextMark(row,col1,buf);
      if(n==ReturnError) return false;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       {if(!CheckStep(row,i,state0)) return false;}
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return false;
        if(!CheckSequence(row,i,cells[sm][i].dir,row20,state0,buf0))
           return false;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(!loop) break;
    row=n;
    for(i=0;i<state.length;i++)
     state[i]=state0[i];
   }
  return true;
}

private boolean CheckRepeat(int row1,int col1,int col2,int row2,int state[],Buffer buf)
//�ˬd�`���j�����`���ʧ@�U,�O�_���F�����w�������}��
{ int i=0,k=0,n=-1,row=0,type0=-1,firstrow=0,repeatcount=0;
  boolean loop=false,simple=false;
  int[] state0=new int[state.length];
  for(i=0;i<state0.length;i++) state0[i]=state[i];
  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  if(n==-1)
   {SetCursor(row,col1);
    MessageBox(Config.getString("SequencePanel.nostart"),Config.getString("SequencePanel.motioncheck"));
    return false;
   }
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return false;
    else
     {
//String startn(n);
//Application->MessageBox(startn.c_str(),"",MB_OK);
      row=n;
      type0=cells[n][col1].type;

//      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat || type0==SequenceCell.IT_Choice)
       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;

  int row20=0,sm=0;
  Buffer buf0=new Buffer();
  firstrow=row;
  while(true)
   {if(!simple)
     {n=NextMark(row,col1,buf);
      if(n==ReturnError) return false;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       {if(!CheckStep(row,i,state0)) return false;}
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return false;
        if(!CheckSequence(row,i,cells[sm][i].dir,row20,state0,buf0))
           return false;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(type0==SequenceCell.IT_Repeat && repeatcount<1)
     {loop=true; n=firstrow; repeatcount++;}
    else
     {loop=false;}

    if(!loop) break;
    row=n;
    for(i=0;i<state.length;i++)
     state[i]=state0[i];
   }
  return true;
}

private void deleteRelay()
  {boolean clean=false;
    EDevice ed=null;
    while(!clean)
      {clean=true;
        for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
         {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
           if(ed.ced.actionType==CEDevice.TYPE_ELECTRIC)
             {electriclistener.getEArrays().deleteEDevice(ed);
               clean=false;
               break;
             }
         }
      }
    CEDevice.nELECTRIC=0;
  }

public void Design()
{ electriclistener.simulationStop();
  if(!InputCheck()) return;
  electriclistener.getElectricPanel().Clear();
  Buffer buf=new Buffer();
  OutState=new int[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(int i=0;i<OutState.length;i++) OutState[i]=IO_NoExist;
  DelayCount=0;
  GroupCount=0;
  rescale();
  Group(StartRow,StartCol,RangeCol,RangeRow+1,buf);
  GroupCount++;

  deleteRelay();
  RelayList=new EDevice[GroupCount];
  for(int i=0;i<GroupCount;i++)
   {RelayList[i]=new EDevice("Relay", electriclistener);
    electriclistener.getEArrays().addEDevice(RelayList[i]);
   }
  if(OperationMode >= 1 && GroupCount < 3)
   {if(BufferRelay==null)
      {BufferRelay=new EDevice("Relay", electriclistener);
       electriclistener.getEArrays().addEDevice(BufferRelay);
      }
   }
  else
   BufferRelay=null;

//  EDevice.mode=EDevice.MODE_Edit;
  electriclistener.getEArrays().setEDeviceMode(EDevice.MODE_Edit);
  boolean over=electriclistener.getElectricPanel().overWrite;
  electriclistener.getElectricPanel().overWrite=true;
  electriclistener.getElectricPanel().Clear();
  LayoutRow=0;LayoutCol=0;
  if(CondStop!=null) EmergencyLayout();

  LayoutStage(StartRow,StartCol,RangeCol,RangeRow+1,buf,-2,GroupCount-1);
  if(CondStop!=null)
   {electriclistener.getElectricPanel().SetCursor(2,2);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire, LadderCell.G_Tshape, null, null, true);
    electriclistener.getElectricPanel().SetCursor(LayoutRow-1,2);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire, LadderCell.G_Lshape,null, null, true);
   }

  LayoutOutput();

//  if(BufferRelay!=null) sequencePanelListener.addEDevice(BufferRelay);
//  if(StopRelay!=null) sequencePanelListener.addEDevice(StopRelay);
  rescale();
  electriclistener.getElectricPanel().overWrite=over;
  electriclistener.setModified(true);
//  pneumaticPanel.repaint();
}

private void GroupBound(GBBuffer buf,int row,int col)
{int i=0,j=0;
 boolean leftvalue=false;
 buf.rightmost=col;
 for(j=col+1;j<=RangeCol;j++)
  for(i=0;i<=RangeRow;i++)
   if(cells[i][j].group==cells[row][col].group)
    buf.rightmost=j;
 buf.leftmost=col;
 buf.left=col;
 if(col > 0)
  {for(j=col-1;j>=0;j--)
    for(i=0;i<=RangeRow;i++)
     if(cells[i][j].group==cells[row][col].group)
      {if(!leftvalue)
        {buf.left=j;
         leftvalue=true;
        }
       buf.leftmost=j;
      }
  }
 }

private void LayoutOutput()
{int i=0,j=0,n=0;
//System.err.println("layoutoutput 1");
  boolean[] OutD=new boolean[electriclistener.getEArrays().ElectricFaceArray.size()];
  for(i=0;i<OutD.length;i++) OutD[i]=false;
  LayoutCol=0;
  if(CondStop!=null)
   {for(i=3;i<LayoutRow;i++)
     {electriclistener.getElectricPanel().SetCursor(i,0);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Vline,null,null,true);
     }
   }
  for(i=StartRow;i<=RangeRow;i++)
   for(j=StartCol;j<=RangeCol;j++)
    if((cells[i][j].type==SequenceCell.IT_System) &&
       (n=getSystemNo(i,j)) >= 0) OutD[n]=true;
//System.err.println("layoutoutput 2");
  ElectricFace sys;
  for(i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
   {LayoutCol=0;
    sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
    if(OutD[i])
     {if(sys.withLS())
        {if(sys.getMemory())
           MemOut(i,SequenceCell.ID_Forward);
          else
           HoldOut(i,SequenceCell.ID_Forward);
          if(sys.getCDOutput().twoWay)
           {/*
             if(sys.getMemory())
              MemOut(i,SequenceCell.ID_Backward);
             else
              HoldOut(i,SequenceCell.ID_Backward);
             */
             MemOut(i,SequenceCell.ID_Backward);
           }
        }
       else
        NoLSOut(i);
//        NoLSOut(i,SequenceCell.ID_Forward);
     }
   }
  TimerOut();
}
private void MotorOut(int OdeviceNo)
{ int i=0,j=0,k=0;     //���u��4/2���m�Φ۫O�j������
   GBBuffer buf=new GBBuffer();
  int row=0,maxcol=0,rowstart=0;
  int startstate=0;
  EDevice edls=null;
  startstate=LadderCell.G_Power;
  row=LayoutRow;maxcol=LayoutCol;
  for(j=0;j<=RangeCol;j++)     //�ҰʰT��
   for(i=0;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo)
//       cells[i][j].dir==dir0)
     {edls=cells[i][j].ed;
      GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,(CDOutput)null,true);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
            electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,(CDOutput)null,true);
          }
       }
      if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
      row++;
     }
//Application->MessageBox("creat1","",MB_OK);

  EDevice ed=new EDevice("Relay", electriclistener);
  electriclistener.getEArrays().addEDevice(ed);
  electriclistener.getElectricPanel().SetCursor(row,LayoutCol);                  //�۫Oa���I
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,(CDOutput)null,true);
  row++;
  for(i=LayoutRow;i<row;i++)                 //��
   {for(j=maxcol-1;j>=LayoutCol;j--)
     {if(electriclistener.getElectricPanel().matrix[i][j].type != LadderCell.T_None) break;
      else
       {electriclistener.getElectricPanel().SetCursor(i,j);
        electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
       }
     }
    electriclistener.getElectricPanel().SetCursor(i,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
   }
  electriclistener.getElectricPanel().SetCursor(row-1,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
  rowstart=row;

  row=LayoutRow;
  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol+1);
  if(CondStop!=null)
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,StopRelay.ced,(CDOutput)null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,edls.ced,(CDOutput)null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,ed.ced,(CDOutput)null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,(CDOutput)null,true);

  electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  rowstart++;

  LayoutRow=rowstart;
}

private void TimerOut()
{ int i=0,j=0,k=0;
  GBBuffer buf=new GBBuffer();
  int row=0;
 // int state;
  int startstate=0;
//  if(EmergencyStop) startstate=LadderCell.G_LTshape;
//  else startstate=LadderCell.G_Power;
  startstate=LadderCell.G_Power;
  row=LayoutRow;
  for(j=0;j<=RangeCol;j++)
   for(i=0;i<=RangeRow;i++)
    if(cells[i][j].type==SequenceCell.IT_Delay)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,null,true);
//Application->MessageBox((IntToStr(left)).c_str(),(IntToStr(j)).c_str(),MB_OK);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
            electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,null,true);
          }
       }
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,cells[i][j].ed.ced,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
      row++;
     }

  LayoutRow=row;
}

private void MemOut(int OdeviceNo,int dir0)
{ int i=0,j=0,k=0;
   GBBuffer buf=new GBBuffer();
  int row=0,maxcol=0;
//  int solno,celltype;
  int startline=0;
  int startstate=0;
//  if(EmergencyStop) startstate=LadderCell.G_LTshape;
//  else startstate=LadderCell.G_Power;
  startstate=LadderCell.G_Power;
  row=LayoutRow;maxcol=LayoutCol;startline=0;
  for(j=0;j<=RangeCol;j++)
   for(i=0;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo &&
       cells[i][j].dir==dir0)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,(CDOutput)null,true);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
        	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,(CDOutput)null,true);
          }
       }
      if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
      row++;
      startline++;
     }
  if(CondStop!=null && dir0==SequenceCell.ID_Backward)
   {electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
//    sequencePanelListener.AddCell(LadderCell.T_EDevice,LadderCell.G_NC,(TEDevice *)RelayList->Items[GroupCount],null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,StopRelay.ced,(CDOutput)null,true);

    ElectricFace sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,sys.getFirstLimitswitch().ced,(CDOutput)null,true);
    if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
    startline++;
    row++;
   }
  if(startline > 1)
   {for(i=LayoutRow;i<row;i++)
     {for(j=maxcol-1;j>=LayoutCol;j--)
       {if(electriclistener.getElectricPanel().matrix[i][j].type !=LadderCell.T_None) break;
        else
         {electriclistener.getElectricPanel().SetCursor(i,j);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
         }
       }
      electriclistener.getElectricPanel().SetCursor(i,maxcol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
     }

    electriclistener.getElectricPanel().SetCursor(row-1,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
    electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
    maxcol++;
   }

  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
  if(dir0==SequenceCell.ID_Forward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  else if(dir0==SequenceCell.ID_Backward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL2,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  LayoutRow=row;
}

private void NoLSOut(int OdeviceNo)
{ int i=0,j=0,k=0;
   GBBuffer buf=new GBBuffer();
  int row=0,maxcol=0;
//  int solno,celltype;
  int startline=0;
  int startstate=0;
//  if(EmergencyStop) startstate=LadderCell.G_LTshape;
//  else startstate=LadderCell.G_Power;
  startstate=LadderCell.G_Power;
  row=LayoutRow;maxcol=LayoutCol;startline=0;
  for(j=0;j<=RangeCol;j++)
   for(i=0;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,null,true);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
        	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,null,true);
          }
       }
      if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
      row++;
      startline++;
     }

  if(startline > 1)
   {for(i=LayoutRow;i<row;i++)
     {for(j=maxcol-1;j>=LayoutCol;j--)
       {if(electriclistener.getElectricPanel().matrix[i][j].type !=LadderCell.T_None) break;
        else
         {electriclistener.getElectricPanel().SetCursor(i,j);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
         }
       }
      electriclistener.getElectricPanel().SetCursor(i,maxcol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
     }

    electriclistener.getElectricPanel().SetCursor(row-1,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
    electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
    maxcol++;
   }

  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,((ElectricFace)electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  LayoutRow=row;
}

private void NoLSOut(int OdeviceNo,int dir0)
{ int i=0,j=0,k=0;     //���u��4/2���m�Φ۫O�j������
  GBBuffer buf=new GBBuffer();
  int row=0,maxcol=0,rowstart=0,rowstop=0;
  int startstate=0;
  startstate=LadderCell.G_Power;
  row=LayoutRow;maxcol=LayoutCol;
  for(j=0;j<=RangeCol;j++)     //�ҰʰT��
   for(i=0;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo &&
       cells[i][j].dir==dir0)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,null,true);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
        	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,null,true);
          }
       }
      if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
      row++;
     }
//Application->MessageBox("creat1","",MB_OK);
  EDevice ed=new EDevice("Relay", electriclistener);
  electriclistener.getEArrays().addEDevice(ed);
  electriclistener.getElectricPanel().SetCursor(row,LayoutCol);                  //�۫Oa���I
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,null,true);
  row++;
  for(i=LayoutRow;i<row;i++)                 //��
   {for(j=maxcol-1;j>=LayoutCol;j--)
     {if(electriclistener.getElectricPanel().matrix[i][j].type != LadderCell.T_None) break;
      else
       {electriclistener.getElectricPanel().SetCursor(i,j);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
       }
     }
    electriclistener.getElectricPanel().SetCursor(i,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
   }
  electriclistener.getElectricPanel().SetCursor(row-1,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
  rowstart=row;

  row=LayoutRow;
  int coltop=0,colbottom=0;
  for(j=StartCol;j<=RangeCol;j++)
   for(i=StartRow;i<=RangeRow;i++)
    if(cells[i][j].type==SequenceCell.IT_System && getSystemNo(i,j)==OdeviceNo)
//       cells[i][j].dir==dir0)
     {
//ShowMessage(IntToStr(i)+"_"+IntToStr(j));
      coltop=i;
      while(true)
       {if(coltop-1 < StartRow) break;
        if(cells[coltop-1][j].group!=cells[i][j].group) break;
        coltop--;
       }
      colbottom=i;
      while(true)
       {if(colbottom+1 > RangeRow) break;
        if(cells[colbottom+1][j].group!=cells[i][j].group) break;
        colbottom++;
       }
      electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol+1);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RelayList[cells[i][j].group].ced,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
      row=LayoutRow+1;

      for(k=coltop;k<=colbottom;k++)
       {if(cells[k][j].type==SequenceCell.IT_System && cells[k][j].sys!=null && cells[k][j].ed!=null)
         {electriclistener.getElectricPanel().SetCursor(row,maxcol+1);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,cells[k][j].ed.ced,cells[k][j].sys.getCDOutput(),true);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
          row++;
         }
        else if(cells[k][j].type==SequenceCell.IT_Delay && cells[k][j].ed!=null)
         {electriclistener.getElectricPanel().SetCursor(row,maxcol+1);
         electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,cells[k][j].ed.ced,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
          row++;
         }
       }
      electriclistener.getElectricPanel().SetCursor(row-1,maxcol+1);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Lshape,null,null,true);
      electriclistener.getElectricPanel().SetCursor(row-1,maxcol+3);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
      maxcol=maxcol+3;
     }
  rowstop=row;

  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol+1);

  if(CondStop!=null)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,StopRelay.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,ed.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);

  for(i=0;i<(rowstop-rowstart);i++)
  {electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Vline,null,null,true);
   rowstart++;
  }
  electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,null,true);
  if(dir0==SequenceCell.ID_Forward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  else if(dir0==SequenceCell.ID_Backward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL2,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  rowstart++;
  LayoutRow=rowstart;
}

private void HoldOut(int OdeviceNo,int dir0)
{ int i=0,j=0,k=0;     //���u��4/2���m�Φ۫O�j������
  GBBuffer buf=new GBBuffer();
  int row=0,maxcol=0,revdir=0,rowstart=0,rowstop=0;
//  int solno,celltype,state,startstate;
  int startstate=0;
//  if(EmergencyStop) startstate=LadderCell.G_LTshape;
//  else startstate=LadderCell.G_Power;
  startstate=LadderCell.G_Power;
  row=LayoutRow;maxcol=LayoutCol;
  for(j=0;j<=RangeCol;j++)     //�ҰʰT��
   for(i=0;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo &&
       cells[i][j].dir==dir0)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(row,LayoutCol);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[i][j].group].ced,null,true);
      if(buf.left!=j)
       {for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
        	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[k][buf.left].ed.ced,null,true);
          }
       }
      if(electriclistener.getElectricPanel().dragPoint1.x > maxcol) maxcol=electriclistener.getElectricPanel().dragPoint1.x;
      row++;
     }
//Application->MessageBox("creat1","",MB_OK);
  EDevice ed=new EDevice("Relay", electriclistener);
  electriclistener.getEArrays().addEDevice(ed);
  electriclistener.getElectricPanel().SetCursor(row,LayoutCol);                  //�۫Oa���I
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,null,true);
  row++;
  for(i=LayoutRow;i<row;i++)                 //��
   {for(j=maxcol-1;j>=LayoutCol;j--)
     {if(electriclistener.getElectricPanel().matrix[i][j].type != LadderCell.T_None) break;
      else
       {electriclistener.getElectricPanel().SetCursor(i,j);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
       }
     }
    electriclistener.getElectricPanel().SetCursor(i,maxcol);
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
   }
  electriclistener.getElectricPanel().SetCursor(row-1,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
  rowstart=row;

  if(dir0==SequenceCell.ID_Forward) revdir=SequenceCell.ID_Backward;
  else if(dir0==SequenceCell.ID_Backward) revdir=SequenceCell.ID_Forward;
  row=LayoutRow;
  for(j=StartCol;j<=RangeCol;j++)
   for(i=StartRow;i<=RangeRow;i++)
    if(inOutputSet(cells[i][j].type) && getSystemNo(i,j)==OdeviceNo &&
       cells[i][j].dir==revdir)
     {GroupBound(buf,i,j);
      electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol+1);
      if(buf.left==j)
       {electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RelayList[cells[i][j].group].ced,null,true);
        maxcol++;
       }
      else
       {electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RelayList[cells[i][j].group].ced,null,true);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
        row=LayoutRow+1;
        for(k=0;k<=RangeRow;k++)
         if(cells[k][buf.left].group==cells[i][j].group)
          {if(cells[k][buf.left].ed!=null)
            {electriclistener.getElectricPanel().SetCursor(row,maxcol+1);
            electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
            electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,cells[k][buf.left].ed.ced,null,true);
            electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
             row++;
            }
//           row++;
          }
        electriclistener.getElectricPanel().SetCursor(row-1,maxcol+1);
        electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Lshape,null,null,true);
        electriclistener.getElectricPanel().SetCursor(row-1,maxcol+3);
        electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
        maxcol=maxcol+3;
       }
     }
  rowstop=row;

  electriclistener.getElectricPanel().SetCursor(LayoutRow,maxcol+1);

//  if(OperationMode==3)
//   setCell("R"+IntToStr(GroupCount),T_Relay,LadderCell.G_RNO,S_Aswitch);
  if(CondStop!=null)
//   sequencePanelListener.AddCell(T_EDevice,LadderCell.G_NO,(TEDevice *)RelayList->Items[GroupCount],null,true);
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,StopRelay.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,ed.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);

  for(i=0;i<(rowstop-rowstart);i++)
  {electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Vline,null,null,true);
   rowstart++;
  }
  electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,ed.ced,null,true);
  if(dir0==SequenceCell.ID_Forward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL1,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  else if(dir0==SequenceCell.ID_Backward)
	  electriclistener.getElectricPanel().AddCell(LadderCell.T_System,LadderCell.G_SSOL2,null,((ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(OdeviceNo)).getCDOutput(),true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  rowstart++;
  LayoutRow=rowstart;
}

private void EmergencyLayout()
{if(StopRelay==null)
      {StopRelay=new EDevice("Relay", electriclistener);
       electriclistener.getEArrays().addEDevice(StopRelay);
      }
  electriclistener.getElectricPanel().SetCursor(0,0);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Power,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,CondStart.ced,null,true);
//  setCell(Devices[StartButtonNo]->label,T_PushButton,LadderCell.G_PBNO,S_Aswitch);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,CondStop.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,StopRelay.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
  electriclistener.getElectricPanel().SetCursor(1,0);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Power,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,StopRelay.ced,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
  electriclistener.getElectricPanel().SetCursor(2,0);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Power,null,null,true);
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,StopRelay.ced,null,true);
  LayoutRow=2;LayoutCol=2;
//  SetCursor(LayoutRow,LayoutCol);
}

private void StageSignal(int n)
{ int i=0,j=0,group0=0;
  boolean breakflag=false;
//  i=EdeviceNo(T_Relay,"R"+IntToStr(n));
//  setCell(Devices[i]->label,Devices[i]->type,LadderCell.G_RNO,S_Aswitch);
//ShowMessage("StageSignal"+IntToStr(n));
  isRepeat=false;
  if(n==-2)
   {electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,CondStart.ced,null,true);
    group0=GroupCount-1;
   }
  else
   {electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[n].ced,null,true);
    group0=n;
   }
  for(j=RangeCol;j>=StartCol;j--)
   {for(i=StartRow;i<=RangeRow;i++)
     {
      if(cells[i][j].group==group0)
       {if(inOutputSet(cells[i][j].type))
         {if(n==-2)
           {if(cells[i][j].type==SequenceCell.IT_System)
             {if(cells[i][j].sys.withLS() && cells[i][j].sys.getCDOutput().twoWay && cells[i][j].ed!=null)
               electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[i][j].ed.ced,null,true);
             }
           }
          else
           if(cells[i][j].ed!=null)
              electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,cells[i][j].ed.ced,null,true);
//ShowMessage("signal");
         }
        else
         {if(!inInputMark(cells[i][cells[i][j].dir].type)) continue;
          if(cells[i][j].type==SequenceCell.IT_RightConnect && cells[i][cells[i][j].dir].type==SequenceCell.IT_Repeat)
           {//RepeatN=cells[i][cells[i][j].dir].LS;
            RepeatN=cells[i][cells[i][j].dir].ed;
            isRepeat=true;
           }
         }
//        ShowMessage(IntToStr(i)+"_"+IntToStr(j)+"group"+IntToStr(cells[i][j].group));
        breakflag=true;
       }
     }
    if(breakflag) break;
   }
}

// stoptype : 0 serial
//            1 parallel
private void OneStage(int stoptype,int self)
{
 int i=0,j=0,col=0,colmax=0,rowstart=0,rowstop=0;
 int startstate=-1,stype=-1;
 int[] colend=new int[MaxStart];
 boolean breakflag=false,isStartStage=false;
 for(i=0;i<MaxStart;i++) colend[i]=-1;
 electriclistener.getElectricPanel().SetCursor(LayoutRow,LayoutCol); //�w���Щ����Ŷ}�l�B
 col=LayoutCol;colmax=col;

 if(CondStop!=null) startstate=LadderCell.G_LTshape; //���Ū��}�l�Ÿ�
 else startstate=LadderCell.G_Power;

 rowstart=LayoutRow;                            //�۫O�j���}�ҳ���
 for(i=0;i<MaxStart;i++)           //���Ū��}�l�Ÿ�
  {electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
   if(StartStage[i][0]!=-1) electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
   else break;
   for(j=0;j<MaxStage;j++)      //�U�Ū�Ĳ�o�T��
    {if(StartStage[i][j]==-2) isStartStage=true;
//Application->MessageBox((IntToStr(GroupCount)).c_str(),"",MB_OK);
     if(StartStage[i][j]!=-1)
      {
       if(OperationMode >= 1 && GroupCount < 3 && self==1 && StartStage[i][j]==0)
        {electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,BufferRelay.ced,null,true);   //�H�������űҰ�
        }
       else
        {StageSignal(StartStage[i][j]);
         if(isRepeat)
          {
    //      pneumaticPanel.Hint=IntToStr(StartStage[i][j])+"  "+IntToStr(self);
 //    Application->MessageBox(IntToStr(StartStage[i][j]).c_str(),
   //              IntToStr(self).c_str(),MB_OK);
           if(self > StartStage[i][j])
            electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RepeatN.ced,null,true);
           else
            electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RepeatN.ced,null,true);
          }
        }
//Application->MessageBox(IntToStr(StartStage[i][j]).c_str(),"",MB_OK);
      }
     else break;
    }
   if(StartExtra[i] != null)      //�B�~�������}��
    {if(ABswitch[i]) stype=LadderCell.G_NO;      //�B�~�����}�������I����
     else stype=LadderCell.G_NC;
     electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,stype,StartExtra[i],null,true);
    }
   if(electriclistener.getElectricPanel().dragPoint1.x > colmax) colmax=electriclistener.getElectricPanel().dragPoint1.x;
   colend[i]=electriclistener.getElectricPanel().dragPoint1.x;
   rowstart++;
  }
                       //�۫O�j��
 electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
 electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
 if(self >= 0)
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[self].ced,null,true);
 else
  electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,BufferRelay.ced,null,true);
 for(j=LayoutCol+2;j<colmax;j++)
  electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
 electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
 rowstart++;

 for(i=0;i<MaxStart;i++)
  {if(colend[i]==-1) break;
   electriclistener.getElectricPanel().SetCursor(LayoutRow+i,colend[i]);
   for(j=colend[i];j<colmax;j++)
    electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Hline,null,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
  }
 electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax);
 electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
                                        //�۫O�j�����_����
 rowstop=LayoutRow;
 if(StopStage[0]!=-2)                      //���`�~�q�����_
  {if(stoptype==1 && StopStage[1] >= 0)
    {for(i=0;i<MaxStart;i++)
      {if(StopStage[i]==-1) break;
       electriclistener.getElectricPanel().SetCursor(LayoutRow+i,colmax+1);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RelayList[StopStage[i]].ced,null,true);
       electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
       rowstop++;
      }
//     electriclistener.getElectricPanel().SetCursor(pneumaticPanel.dragPoint1.yDragRect.Top,colmax+1);
     electriclistener.getElectricPanel().SetCursor(electriclistener.getElectricPanel().dragPoint1.y,colmax+1);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Lshape,null,null,true);
//     electriclistener.getElectricPanel().SetCursor(pneumaticPanel.DragRect.Top,colmax+3);
     electriclistener.getElectricPanel().SetCursor(electriclistener.getElectricPanel().dragPoint1.y,colmax+3);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
     electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+1);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
     electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+3);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
    }
   else
    {if(isStartStage && OperationMode >= 1 && GroupCount < 3)
      electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,BufferRelay.ced,null,true);   //�H�����������_
     else
      {for(i=0;i<MaxStart;i++)
        {if(StopStage[i]==-1) break;
         electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,RelayList[StopStage[i]].ced,null,true);
        }
      }
    }
  }
 else                        //�̫��@�ťѷ����}�����_
  {for(j=RangeCol;j>=StartCol;j--)
    {for(i=StartRow;i<=RangeRow;i++)
      if(cells[i][j].group==GroupCount-1)
       {if(inOutputSet(cells[i][j].type))
         {if(cells[i][j].ed!=null)
            {// String str=IntToStr(getEDeviceNo(cells[i][j].ed));
             // Application->MessageBox(str.c_str(),"",MB_OK);
             electriclistener.getElectricPanel().SetCursor(rowstop,colmax+1);
             electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
             electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,cells[i][j].ed.ced,null,true);
             electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RTshape,null,null,true);
             rowstop++;
            }
         }
        breakflag=true;
       }
     if(breakflag) break;
    }
   if(rowstop==LayoutRow+1)
    {boolean over=electriclistener.getElectricPanel().overWrite;
    electriclistener.getElectricPanel().overWrite=false;
      electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+1);

      electriclistener.getElectricPanel().deleteBlock();
     electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+2);
     electriclistener.getElectricPanel().deleteBlock();
     electriclistener.getElectricPanel().overWrite=over;
    }
   else
    {electriclistener.getElectricPanel().SetCursor(rowstop-1,colmax+1);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Lshape,null,null,true);
     electriclistener.getElectricPanel().SetCursor(rowstop-1,colmax+3);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_RLshape,null,null,true);
     electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+1);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
     electriclistener.getElectricPanel().SetCursor(LayoutRow,colmax+3);
     electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Tshape,null,null,true);
    }
  }
  if(OperationMode==3 && isStartStage)
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,CondCounter.ced,null,true);

   if(self>=0)
    electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,RelayList[self].ced,null,true); //�۫O�u��
   else
    electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,BufferRelay.ced,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);

 for(i=0;i<(rowstop-rowstart);i++)
  {electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
   rowstart++;
  }

 if(OperationMode==3 && self==GroupCount-1)
  {electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,CondStart.ced,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL2,CondCounter.ced,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
   rowstart++;
   electriclistener.getElectricPanel().SetCursor(rowstart,LayoutCol);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,startstate,null,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[GroupCount-1].ced,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,CondCounter.ced,null,true);
   electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
   rowstart++;
  }

 LayoutRow=rowstart;
  // ���ưʧ@�ӤS�u�����ծ�,�[�@��������
 if(isStartStage && OperationMode >= 1 && GroupCount < 3)
  {ClearStage();
   StartStage[0][0]=0;
   StopStage[0]=GroupCount-1;
   OneStage(0,-2);
  }
}

private void ClearStage()
{ int i=0,j=0;
  for(i=0;i<MaxStart;i++)
   {for(j=0;j<MaxStage;j++)
     StartStage[i][j]=-1;
    StartExtra[i]=null;
    ABswitch[i]=true;
    StopStage[i]=-1;
    StopExtra[i]=null;
   }
}

private void LayoutStage(int row1,int col1,int col2,int row2,Buffer buf,int lastgroup, int nextgroup)
//�ˬd�Ҧ��ʧ@�Ÿ����ʧ@�U,�O�_���F�����w�������}��
{ int i=0,j=0,k=0,n=0,row=0,type0=-1,row0=0;
  boolean loop=false,branchloop=false,endloop=false,simple=false;
  int branchNo=0,lastgroup0=0;
  int[] branchgroup=new int[MaxStart];
  int[] endgroup=new int[MaxStart];

  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  row=row1;
  for(i=row;i<=RangeRow;i++)   //���X�Ĥ@�������m
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  row=n;
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;        //�P�_�O�_�������j��
   }
  else
   {simple=false;
    if((n=StartMark(row,col1,buf,0))==ReturnError) return;
    else                           //���X�����j�����Ĥ@�������m
     {row=n;
      type0=cells[n][col1].type;
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;

  row0=row;
  for(i=0;i<MaxStart;i++) branchgroup[i]=-1;
  k=0;
  branchloop=loop;
  while(true)
   {n=NextMark(row,col1,buf);     //���X�C�@���䪺�}�l�ո�
    if(n==ReturnError) return;
    else if(n<0)
     branchloop=false;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type) ||
         inInputMark(cells[row][i].type) || cells[row][i].type == SequenceCell.IT_LeftConnect)
       {branchgroup[k]=cells[row][i].group;
        k++;
        break;
       }
     }
    if(!branchloop) break;
    row=n;
   }
  int row20=0,sm=0,nextgroup0=0;
  Buffer buf0=new Buffer();
  row=row0;
  for(i=0;i<MaxStart;i++) endgroup[i]=-1;
  j=0;
  endloop=loop;
  while(true)
   {n=NextMark(row,col1,buf);   //���X�C�@���䪺�`���ո�
    if(n==ReturnError) return;
    else if(n >= 0 && endloop==true)
     {row20=n;}
    else if(n<0)
     {row20=row2;endloop=false;}
    for(i=startcol;i<col2;i++)
     {if(inOutputSet(cells[row][i].type))
       lastgroup0=cells[row][i].group;
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return;
        lastgroup0=cells[sm][i].group;
         RowRange(row,i,cells[sm][i].dir,row20,buf0);
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    endgroup[j]=lastgroup0;
    j++;
    if(!endloop) break;
    row=n;
   }
  row=row0;
  if(simple)               //�Ұʦ���
   {ClearStage();
//    StartStage[0][0]=-2;
    StartStage[0][0]=lastgroup;
    StopStage[0]=cells[row][col1].group+1;
    switch(OperationMode)
     {case 0: break;
      case 1: StartStage[1][0]=GroupCount-1;break;
      case 2: StartStage[1][0]=GroupCount-1;StartExtra[1]=CondChoice.ced;break;
      case 3: StartStage[1][0]=GroupCount-1;StopExtra[0]=CondCounter.ced;break;
     }
    OneStage(0,cells[row][col1].group);
    lastgroup0=cells[row][col1].group;
   }
  else
   {ClearStage();          //�����j�����Ұʦ���
//    if(Cell[row][col1].group==0) StartStage[0][0]=-2;
//    else StartStage[0][0]=Cell[row][col1].group-1;
    StartStage[0][0]=lastgroup;
    switch(cells[row0][col1].type)
     {case SequenceCell.IT_Parallel:
        for(i=0;i<MaxStart;i++)
         {if(branchgroup[i] == -1) break;
          else StopStage[i]=branchgroup[i];
         }
        OneStage(1,cells[row][col1].group);
        break;
      case SequenceCell.IT_Choice:
        for(i=0;i<MaxStart;i++)
         {if(branchgroup[i] == -1) break;
          else StopStage[i]=branchgroup[i];
         }
        OneStage(0,cells[row][col1].group);
        break;
      case SequenceCell.IT_Jump:
        StopStage[0]=branchgroup[0];
        StopStage[1]=cells[row][col2].group;
        OneStage(0,cells[row][col1].group);
        break;
      case SequenceCell.IT_Repeat:
        StartStage[1][0]=cells[row][col2].group;
        StopStage[0]=branchgroup[0];
        OneStage(0,cells[row][col1].group);
        if(cells[row][col1].ed.ced.actionType==CEDevice.TYPE_COUNTER)
         {
          electriclistener.getElectricPanel().SetCursor(LayoutRow,LayoutCol);
          if(CondStop!=null)
           electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
          else
           electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Power,null,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[lastgroup].ced,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL2,cells[row][col1].ed.ced,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
          LayoutRow++;
         }
        break;
     }

   }

  branchNo=0;
  while(true)
   {if(!simple)
     {ClearStage();
      StartStage[0][0]=cells[row][col1].group;
      StopStage[0]=branchgroup[branchNo]+1;
      if(branchgroup[branchNo+1]>=0 && StopStage[0]>=branchgroup[branchNo+1])
       StopStage[0]=cells[row][col2].group;
      switch(cells[row0][col1].type)
       {case SequenceCell.IT_Parallel:
          OneStage(0,branchgroup[branchNo]);
          break;
        case SequenceCell.IT_Choice:
          StartExtra[0]=cells[row][col1].ed.ced;
          if(branchNo==0) ABswitch[0]=false;
          else ABswitch[0]=true;
          OneStage(0,branchgroup[branchNo]);
          break;
        case SequenceCell.IT_Jump:
          StartExtra[0]=cells[row][col1].ed.ced;
          ABswitch[0]=false;
          OneStage(0,branchgroup[branchNo]);
          break;
        case SequenceCell.IT_Repeat:
          OneStage(0,branchgroup[branchNo]);
          break;
       }
      lastgroup0=branchgroup[branchNo];

      n=NextMark(row,col1,buf);
      if(n==ReturnError) return;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       {if(cells[row][i].group != lastgroup0)
         {ClearStage();
          StartStage[0][0]=lastgroup0;
          if(cells[row][i].group==GroupCount-1)
           {StopStage[0]=-2;}
          else
           {StopStage[0]=cells[row][i].group+1;
            if(branchgroup[branchNo+1]>=0 && StopStage[0]>=branchgroup[branchNo+1])
             StopStage[0]=cells[row][col2].group;
           }
          OneStage(0,cells[row][i].group);
          lastgroup0=cells[row][i].group;
         }
       }
      else if(inInputMark(cells[row][i].type)|| cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return;
        nextgroup0=cells[row0][col2].group;
        for(k=sm;k<row20;k++)
         for(j=cells[sm][i].dir+1;j<col2;j++)
          {if(cells[k][j].group < nextgroup0) nextgroup0=cells[k][j].group;}

        LayoutStage(row,i,cells[sm][i].dir,row20,buf0,lastgroup0,nextgroup0);
        lastgroup0=cells[sm][cells[sm][i].dir].group;
        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(!loop) break;
    row=n;
    branchNo++;
   }
  if(!simple)
   {ClearStage();
    if(cells[row0][col2].group==GroupCount-1)
     {StopStage[0]=-2;}
    else
     {StopStage[0]=nextgroup;
      switch(cells[row0][col1].type)
       {case SequenceCell.IT_Parallel:
          for(i=0;i<=branchNo;i++)
           {
           StartStage[0][i]=endgroup[i];
//Application->MessageBox(IntToStr(endgroup[i]).c_str(),"",MB_OK);
}
//Application->MessageBox(IntToStr(branchNo).c_str(),"",MB_OK);
          OneStage(0,cells[row0][col2].group);
          break;
        case SequenceCell.IT_Choice:
          for(i=0;i<=branchNo;i++)
           StartStage[i][0]=endgroup[i];
          OneStage(0,cells[row0][col2].group);
          break;
        case SequenceCell.IT_Jump:
          StartStage[0][0]=endgroup[0];
          StartStage[1][0]=cells[row0][col1].group;
          StartExtra[1]=cells[row0][col1].ed.ced;
          ABswitch[1]=true;
          OneStage(0,cells[row0][col2].group);
          break;
        case SequenceCell.IT_Repeat:
          StartStage[0][0]=endgroup[0];
          StopStage[1]=cells[row0][col1].group;
          OneStage(0,cells[row0][col2].group);
          if(cells[row0][col1].ed.ced.actionType==CEDevice.TYPE_COUNTER)
         {
          electriclistener.getElectricPanel().SetCursor(LayoutRow,LayoutCol);
          if(CondStop!=null)
           electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_LTshape,null,null,true);
          else
           electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Power,null,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,RelayList[cells[row0][col2].group].ced,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,cells[row0][col1].ed.ced,null,true);
          electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,LadderCell.G_Ground,null,null,true);
          LayoutRow++;
         }
          break;
       }
     }
   }
}

private void RowRange(int row1,int col1,int col2,int row2,Buffer buf)
//�ˬd�Ҧ��ʧ@�Ÿ����ʧ@�U,�O�_���F�����w�������}��
{ int i=0,n=0,row=0,type0=-1;
  boolean loop=false;

  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  row=n;
  if(inOutputSet(cells[row][col1].type))
   loop=false;
  else
   {if((n=StartMark(row,col1,buf,0))==ReturnError) return;
    else
     {row=n;
      type0=cells[n][col1].type;
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
       loop=false;
      else
       loop=true;
     }
   }
 // int startcol=simple?col1:col1+1;
  while(true)
   {n=NextMark(row,col1,buf);
    if(n==ReturnError) return;
    else if(n<0) loop=false;
    if(!loop) break;
    row=n;
   }
}

private void Group(int row1,int col1,int col2,int row2,Buffer buf)
//�ˬd�Ҧ��ʧ@�Ÿ����ʧ@�U,�O�_���F�����w�������}��
{ int i=0,k=0,n=0,row=0,type0=-1;
  boolean loop=false,simple=false,addgroup=false;
  int rowptr=0,startgroup=0;
  int[] markstartrow=new int[20];
  ElectricFace sys=null;

  if(col1 > col2) {i=col1;col1=col2;col2=i;}
  row=row1;
  for(i=row;i<=RangeRow;i++)
   if(cells[i][col1].type != SequenceCell.IT_None) {n=i;break;}
  row=n;
//System.err.println("type:"+cells[row][col1].type);
  if(inOutputSet(cells[row][col1].type))
   {loop=false;simple=true;
   }
  else
   {simple=false;
    if((n=StartMark(row,col1,buf,0))==ReturnError) return;
    else
     {row=n;
      type0=cells[n][col1].type;
      if(type0==SequenceCell.IT_Jump || type0==SequenceCell.IT_Repeat)
       loop=false;
      else
       loop=true;
     }
   }
  int startcol=simple?col1:col1+1;
  if(!simple)
   {
    for(k=0;k<electriclistener.getEArrays().ElectricFaceArray.size();k++)
     {sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(k);
      if(sys.withLS()  && OutState[k]==IO_Exist) {addgroup=true;break;}
     }
    if(addgroup)
     {for(k=0;k<electriclistener.getEArrays().ElectricFaceArray.size();k++) OutState[k]=IO_NoExist;
      DelayCount=0;
      GroupCount++;
     }
    startgroup=GroupCount;
   }
  int row20=0,sm=0;
  Buffer buf0=new Buffer();
  rowptr=0;
  boolean firstbranch=true;
  while(true)
   {if(!simple)
     {markstartrow[rowptr]=row;
      rowptr++;
      cells[row][col1].group=startgroup;
if(firstbranch) {GroupCount++;firstbranch=false;}
//System.err.println("group1:"+row+":"+col1+":"+startgroup);
//SetCursor(row,col1);
// String str;
// str="���b��"+IntToStr(startgroup)+"��";
// Application->MessageBox(str.c_str(),"",MB_OK);
      n=NextMark(row,col1,buf);
      if(n==ReturnError) return;
      else if(n >= 0 && loop==true)
       {row20=n;}
      else if(n<0)
       {row20=row2;loop=false;}
     }
    else row20=row2;
    for(i=startcol;i<=col2;i++)
     {if(inOutputSet(cells[row][i].type))
       AddGroup(row,i);
      else if(inInputMark(cells[row][i].type)||cells[row][i].type==SequenceCell.IT_LeftConnect)
       {if((sm=StartMark(row,i,buf0,1))==ReturnError) return;
        Group(row,i,cells[sm][i].dir,row20,buf0);

        for(k=buf0.startrow;k<=buf0.lastrow;k++)
         if(cells[k][cells[sm][i].dir+1].type != SequenceCell.IT_None)
          {row=k;break;}
        i=cells[sm][i].dir;
       }
     }
    if(!loop) break;
    row=n;
    for(k=0;k<OutState.length;k++) OutState[k]=IO_NoExist;
    DelayCount=0;
    GroupCount++;
   }

  if(!simple)
   {addgroup=false;
    for(k=0;k<OutState.length;k++)
     {//sys=(TSystem *)FSystemList->Items[k];
      if(OutState[k]==IO_Exist) {addgroup=true;break;}
     }
    if(DelayCount>0) addgroup=true;
    if(addgroup)
     {GroupCount++;
      for(k=0;k<OutState.length;k++) OutState[k]=IO_NoExist;
      DelayCount=0;
     }

    for(k=0;k<rowptr;k++)
     {cells[markstartrow[k]][col2].group=GroupCount;
//System.err.println("group2:"+k+":"+col2+":"+GroupCount);
//  SetCursor(markstartrow[k],col2);
//  String str;
//  str="���b��"+IntToStr(GroupCount)+"��";
//  Application->MessageBox(str.c_str(),"",MB_OK);
     }
    for(k=0;k<OutState.length;k++) OutState[k]=IO_NoExist;
    DelayCount=0;
    GroupCount++;
   }
}

private void AddGroup(int row, int col)
{ int i=0,n=0,row1=0;//�ˬd�ʧ@�Ÿ�Cell[row][col]���ʧ@�U,�O�_���F�����w�������}��
  boolean samegroup=true;
  boolean loop=false,hasmotor=false;

  row1=row;
  while(true)
   {if(cells[row1][col].type != SequenceCell.IT_Delay)
     {if((n=getSystemNo(row1,col)) < 0) return;
      if(OutState[n]==IO_Exist) {samegroup=false;break;}
//      if(cells[row1][col].type == SequenceCell.IT_System &&
//       !cells[row1][col].sys.getTwoWay()) {samegroup=false;hasmotor=true;break;}
      if(cells[row1][col].type == SequenceCell.IT_System && cells[row1][col].sys instanceof ESystem &&
       cells[row1][col].sys.withLS() && !cells[row1][col].sys.getCDOutput().twoWay) {samegroup=false;hasmotor=true;break;}
     }
    else
     DelayCount++;
    if(cells[row1][col].group==IM_End) break;
    row1++;
   }
  if(!samegroup)
   {for(i=0;i<OutState.length;i++) OutState[i]=IO_NoExist;
    DelayCount=0;
    GroupCount++;
   }
  loop=true;
  row1=row;
  while(loop)
   {if(cells[row1][col].group==IM_End) loop=false;
    cells[row1][col].group=GroupCount;
//System.err.println("group3:"+row1+":"+col+":"+GroupCount);
    if(cells[row1][col].type==SequenceCell.IT_System)
     OutState[getSystemNo(row1,col)]=IO_Exist;
//  SetCursor(row1,col);
// String str;
//  str="���b��"+IntToStr(GroupCount)+"��";
//  Application->MessageBox(str.c_str(),"",MB_OK);
    row1++;
   }

 if(hasmotor && col!=RangeCol)
  {for(i=0;i<OutState.length;i++) OutState[i]=IO_NoExist;
   DelayCount=0;
   GroupCount++;
  }

}

/*
TEDevice* __fastcall TSequenceForm::getComplexSwitch(TSequenceCellType ind)
{ TEDevice *ed=null;
  TSelectEDevice *sls;

  switch(ind)
   {case SequenceCell.IT_Choice:
     sls=new TSelectEDevice(this,FEDList,"H");
     sls->Caption="�I���������ܰj�����}��";
     sls->Label1->Caption="�I�����ܶ}��";
     break;
    case SequenceCell.IT_Jump:
     sls=new TSelectEDevice(this,FEDList,"H");
     sls->Caption="�I���������D�j�����}��";
     sls->Label1->Caption="�I�����ܶ}��";
     break;
    case SequenceCell.IT_Repeat:
     sls=new TSelectEDevice(this,FEDList,"HC");
     sls->Caption="�I���`���j���������(���ܶ}���άO�p�ƾ�)";
     sls->Label1->Caption="�I�������";
     break;
   }

  sls->ShowModal();
  if(sls->ModalResult==mrCancel)
   {delete sls;
    sls=null;
    return null;
   }
  ed=sls->GetComboBox();
  delete sls;
  sls=null;

  return ed;
}


//---------------------------------------------------------------------------
void __fastcall TSequenceForm::Save(FILE *fp)
{ int i,j;
  fprintf(fp,"%d %d %d %d\n",StartRow,RangeRow,StartCol,RangeCol);
  for(i=StartRow;i<=RangeRow;i++)
   for(j=StartCol;j<=RangeCol;j++)
    {
     fprintf(fp,"%d %d %d %d %d \n",cells[i][j].type,
        cells[i][j].dir,cells[i][j].group,MainForm->getEDeviceNo(cells[i][j].ed),
                           MainForm->getSystemNo(cells[i][j].sys));
    }
  Modified=false;
}

String __fastcall TSequenceForm::Send()
{ String sendstr;
  int i,j;
  sendstr=IntToStr(StartRow)+"_"+IntToStr(RangeRow)+"_"+IntToStr(StartCol)+"_"+
          IntToStr(RangeCol)+"_";
  for(i=StartRow;i<=RangeRow;i++)
   for(j=StartCol;j<=RangeCol;j++)
    {sendstr=sendstr+IntToStr(cells[i][j].type)+"_"+IntToStr(cells[i][j].dir)+"_"+
       IntToStr(cells[i][j].group)+"_"+IntToStr(MainForm->getEDeviceNo(cells[i][j].ed))+"_"+
       IntToStr(MainForm->getSystemNo(cells[i][j].sys))+"_";
    }
  return sendstr;
}

void __fastcall TSequenceForm::Receive()
{
  Clear();
  TMainForm *mf=(TMainForm *)Owner;
  StartRow=mf->FirstNElement(1,true).ToInt();
  RangeRow=mf->FirstNElement(1,true).ToInt();
  StartCol=mf->FirstNElement(1,true).ToInt();
  RangeCol=mf->FirstNElement(1,true).ToInt();
  int i,j,edn,sysn;
  for(i=StartRow;i<=RangeRow;i++)
   for(j=StartCol;j<=RangeCol;j++)
    {
     cells[i][j].type=(TSequenceCellType) mf->FirstNElement(1,true).ToInt();
     cells[i][j].dir=mf->FirstNElement(1,true).ToInt();
     cells[i][j].group=mf->FirstNElement(1,true).ToInt();
     edn=mf->FirstNElement(1,true).ToInt();
     sysn=mf->FirstNElement(1,true).ToInt();
     if(edn<0) cells[i][j].ed=null;
     else
      cells[i][j].ed=(TEDevice *)FEDList->Items[edn];
     if(sysn<0) cells[i][j].sys=null;
     else
      cells[i][j].sys=(TSystem *)FSystemList->Items[sysn];
    }
  Repaint();
  Modified=false;
}

void __fastcall TSequenceForm::Read(FILE *fp)
{
  Clear();
  fscanf(fp,"%d %d %d %d\n",&StartRow,&RangeRow,&StartCol,&RangeCol);
  int i,j,edn,sysn;
  for(i=StartRow;i<=RangeRow;i++)
   for(j=StartCol;j<=RangeCol;j++)
    {
     fscanf(fp,"%d %d %d %d %d \n",&cells[i][j].type,&cells[i][j].dir,&cells[i][j].group,&edn,&sysn);

     if(edn<0) cells[i][j].ed=null;
     else
      cells[i][j].ed=(TEDevice *)FEDList->Items[edn];
     if(sysn<0) cells[i][j].sys=null;
     else
      cells[i][j].sys=(TSystem *)FSystemList->Items[sysn];
    }
  Modified=false;
  Checked=false;
  Repaint();
}
//---------------------------------------------------------------------------------------
*/

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
        // �e�X�����Ϧb LadderPaint �ϭ�    !InputMode
         // �e�X�ʧ@���Ǧb LadderPaint �ϭ�   InputMode
  rescale();
//System.err.println(WinRow1+":"+WinRow2+":"+WinCol1+":"+WinCol2);
  for (int i=WinRow1;i<=WinRow2;i++)
   for (int j=WinCol1;j<=WinCol2;j++)
    PaintCell(g,i,j);
// if(!PLCRunFlag) MarkArea(g);
  MarkArea(g);
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
     {super("SequencePanel",ele,Command_setCursor);
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
          MessageBox(Config.getString("SequencePanel.overrange")+MaxRows+" "+
                     Config.getString("SequencePanel.row")+MaxColumns+" "+
                     Config.getString("SequencePanel.col"),Config.getString("SequencePanel.warn"));
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
    rescale();
   }

   public void mouseReleased(MouseEvent e) {
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
     {super("SequencePanel",ele,Command_block);
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
         popRow=ey/cellhgt;
         popCol=ex/cellwid;
         lsm.setEnabled(false);cm.setEnabled(false);tgm.setEnabled(false);tm.setEnabled(false);
         if(cells[popRow][popCol].type==SequenceCell.IT_System)
          lsm.setEnabled(true);
         if(cells[popRow][popCol].type==SequenceCell.IT_Delay)
          tm.setEnabled(true);
         if(cells[popRow][popCol].type==SequenceCell.IT_Choice)
          tgm.setEnabled(true);
         if(cells[popRow][popCol].type==SequenceCell.IT_Jump)
          tgm.setEnabled(true);
         if(cells[popRow][popCol].type==SequenceCell.IT_Repeat)
          {tgm.setEnabled(true);
            cm.setEnabled(true);
          }
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
           electriclistener.setSequenceStatusPos("row "+moveRow+" : col "+moveCol);
          dragging=true;
//System.err.println("drag");
        repaint();
       }
      rescale();
   }

   public void mouseMoved(MouseEvent e) {}

  public void actionPerformed(ActionEvent e){
         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }

  private class changeEditModeCommand extends Command
    {boolean newmode;
	public changeEditModeCommand(Object ele,boolean nmode)
     {super("SequencePanel",ele,Command_changeEditMode);
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
     {super("SequencePanel",ele,Command_changeRatio);
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
      if(option.equals(Config.getString("SequencePanel.delete")))
         {deleteBlock();
           dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.cut")))
         {
          copyToBoard();
          deleteBlock();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.copy")))
         {copyToBoard();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.paste")))
         {pasteBoardTo();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.limitswitch")))
         {cells[popRow][popCol].ed=getEDevicebyDialog(CEDevice.TYPE_MECHANIC);
           dragPoint2.x=dragPoint1.x;
           dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.togglebutton")))
         {cells[popRow][popCol].ed=getEDevicebyDialog(CEDevice.TYPE_MANUAL_TOGGLE);
           dragPoint2.x=dragPoint1.x;
           dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.timer")))
         {cells[popRow][popCol].ed=getEDevicebyDialog(CEDevice.TYPE_TIMER);
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.counter")))
         {cells[popRow][popCol].ed=getEDevicebyDialog(CEDevice.TYPE_COUNTER);
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
         else if(option.equals(Config.getString("SequencePanel.selectall")))
         {rescale();
          Point oldp1=new Point(dragPoint1);
          Point oldp2=new Point(dragPoint2);
          dragPoint1.x=StartCol;
          dragPoint1.y=StartRow;
          dragPoint2.x=RangeCol;
          dragPoint2.y=RangeRow;
          Point newp1=new Point(dragPoint1);
          Point newp2=new Point(dragPoint2);
          electriclistener.addCommand(new blockCommand(this,newp1,newp2,oldp1,oldp2));
         }
        else if(option.equals(Config.getString("SequencePanel.clearall")))
         {Clear();
          dragPoint2.x=dragPoint1.x;
          dragPoint2.y=dragPoint1.y;
         }
        else if(option.equals(Config.getString("SequencePanel.zoom")))
         {double oldratio=ratio;
	      CustomDialog customDialog = new CustomDialog(electriclistener.getFrame(),Config.getString("SequencePanel.zoom"),Config.getString("SequencePanel.zoomfactor"),CustomDialog.VALUE_FLOAT);
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

   private EDevice getEDevicebyDialog(int type)
    {Hashtable table=new Hashtable();
      EDevice ed=null;
       for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
        {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
          if(ed.ced.actionType==type && ed.ced.name!=null && ed.ced.name.length()>0)
            {table.put(ed.ced.name,ed);}
       }
      ArrayList list=new ArrayList();
      Enumeration e = table.keys();
      while (e.hasMoreElements())
       list.add((String) e.nextElement());
    DBDialog dbDialog = new DBDialog(new JFrame(),list,Config.getString("SequencePanel.PickEDevice"),false);
    dbDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dbDialog.setLocation(
		screenSize.width/2 - dbDialog.getSize().width/2,
		screenSize.height/2 - dbDialog.getSize().height/2);
    dbDialog.setVisible(true);
    String nam=dbDialog.getText();
    if(nam==null || nam.length()==0) return null;
    return (EDevice) table.get(nam);
   }

  public int readCell(String str)
 { 
System.out.println(str);	  
	StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_cells) return -1;
    int wid=Integer.parseInt(token.nextToken());
    int i = Integer.parseInt(token.nextToken());
    int j = Integer.parseInt(token.nextToken());
    int type=Integer.parseInt(token.nextToken());
    int dir=Integer.parseInt(token.nextToken());
    int group=Integer.parseInt(token.nextToken());
    String edname=token.nextToken();
    String cdoname=token.nextToken();
    cells[i][j].type=type;
    cells[i][j].dir=dir;
    cells[i][j].group=group;
    cells[i][j].ed=electriclistener.getEArrays().findEDeviceByName(edname);
    cells[i][j].sys=electriclistener.getEArrays().findElectricFaceByName(cdoname);
    
    System.out.println(cells[i][j].toString());
    repaint();
    return wid;
 }

 public String write()
  {StringBuffer sb=new StringBuffer();
	 rescale();
	 
    if(RangeRow-StartRow<=0 && RangeCol-StartCol < 2) return "";
    int width=RangeCol-StartCol+1;
//    int edno=-1,sysno=-1;
    String edname="null";
    String efname="null";
    for (int i=StartRow;i<=RangeRow;i++)
     for (int j=StartCol;j<=RangeCol;j++)
      { if (cells[i][j].type ==SequenceCell.IT_None) continue;
       
       if(cells[i][j].ed==null) edname="null";
       else edname=cells[i][j].ed.ced.getName();  // ed Component not name
       
       if(cells[i][j].sys==null) efname="null";
       else efname=cells[i][j].sys.getActuatorName();
    	
       sb.append(SCCAD.Data_cells+" "+width+" "+i+" "+j+" "+cells[i][j].type+" "+cells[i][j].dir+" "+cells[i][j].group+" "+edname+" "+efname+"\n");
      }
      
    return sb.toString();
  }

 }

class GBBuffer
 {int leftmost;
   int left;
   int rightmost;
   public GBBuffer()
    {leftmost=0;
      left=0;
      rightmost=0;
    }
 }

class Buffer
 {int startrow;
   int lastrow;
   int temp;
   public Buffer()
     {startrow=0;
       lastrow=0;
       temp=0;
     }
 }
