package tw.com.justiot.sequencecontrol;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Args {
  @Parameter(names = "-exampleURL", description = "exampleURL")
  public String exampleURL;

  @Parameter(names = "-debug", description = "Debug mode")
  public boolean debug = false;
  
  public Args(String[] args) {
    JCommander.newBuilder().addObject(this).build().parse(args);
  }
}