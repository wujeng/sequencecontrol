package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.pelement.*;
public class SequenceCell
  {
  public static final int IT_Parallel=0;
  public static final int IT_Choice=1;
  public static final int IT_Jump=2;
  public static final int IT_Repeat=3;
  public static final int IT_LeftConnect=4;
  public static final int IT_RightConnect=5;
  public static final int IT_Delay=6;
  public static final int IT_System=7;
  public static final int IT_None=8;

  public static final int ID_Forward=0;
  public static final int ID_Backward=1;
  public static final int ID_None=2;

  public static final int IM_Next=0;
  public static final int IM_End=1;
  public static final int IM_None=2;
   
   public int type;
   public int dir,group;
   public EDevice ed;
   public ElectricFace sys;

  public SequenceCell(int type, int dir, int group, EDevice ed, ElectricFace sys)
    {this.type=type;
      this.dir=dir;
      this.group=group;
      this.ed=ed;
      this.sys=sys;
    }

   public String toString()
   { String edname="null";
     if(ed!=null) edname=ed.getName();
     String efname="null";
     if(sys!=null) efname=sys.getActuatorName();
	 return type+" "+dir+" "+group+" "+edname+" "+efname;
   }
  
   public SequenceCell()
     {type=IT_None;
       dir=ID_None;
       group=SequencePanel.IG_None;
     }
   public SequenceCell(SequenceCell sc)
     {this.type=sc.type;
       this.dir=sc.dir;
       this.group=sc.group;
       this.ed=sc.ed;
       this.sys=sc.sys;
     }
}