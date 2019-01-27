package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.eelement.*;

public class LadderCell
  {
  public static final int G_Power=0;
  public static final int G_Hline=1;
  public static final int G_Vline=2;
  public static final int G_Tshape=3;
  public static final int G_RTshape=4;
  public static final int G_RLshape=5;
  public static final int G_Lshape=6;
  public static final int G_LTshape=7;
  public static final int G_Ground=8;
  public static final int G_NO=9;
  public static final int G_NC=10;
  public static final int G_ESOL1=11;
  public static final int G_ESOL2=12;
  public static final int G_SSOL1=13;
  public static final int G_SSOL2=14;
  public static final int G_None=15;
/*  
  public static final int G_SFNO=16;
  public static final int G_SFNC=17;
  public static final int G_SBNO=18;
  public static final int G_SBNC=19;
*/  
  public static final int T_None=0;
  public static final int T_Wire=1;
  public static final int T_EDevice=2;
  public static final int T_System=3;
   
   public int type;
   public int state;
   public CEDevice ced;
   public CDOutput cdo;
   public int Tshapepos;
   public String dir;

  public LadderCell(int type,int state,CEDevice ced,CDOutput cdo)
    {this.type=type;
      this.state=state;
      this.ced=ced;
      this.cdo=cdo;
    }

   public LadderCell()
     {type=T_None;
      state=G_None;
     }
   public LadderCell(LadderCell lc)
     {this.type=lc.type;
       this.state=lc.state;
       this.ced=lc.ced;
       this.cdo=lc.cdo;
       this.Tshapepos=lc.Tshapepos;
       this.dir=lc.dir;
     }
}