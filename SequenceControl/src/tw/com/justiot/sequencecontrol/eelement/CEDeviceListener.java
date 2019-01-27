package tw.com.justiot.sequencecontrol.eelement;
public interface CEDeviceListener {
  public void statusChanged(CEDevice ced);
  public void valueChanged(CEDevice ced);
}
