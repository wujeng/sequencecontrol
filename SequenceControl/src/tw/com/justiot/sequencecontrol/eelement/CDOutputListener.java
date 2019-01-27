package tw.com.justiot.sequencecontrol.eelement;

public interface CDOutputListener {
  public void solFStatusChanged(CDOutput ces);
  public void solBStatusChanged(CDOutput ces);
  public void nameChanged(CDOutput ces);
}
