package tw.com.justiot.sequencecontrol.eelement;

public class PLCCode
{int code;
  CEDevice ced;
  CDOutput cdo;
  int state;
  public PLCCode(int code,CEDevice ced,CDOutput cdo,int state)
   {this.code=code;
     this.ced=ced;
     this.cdo=cdo;
     this.state=state;
   }
}