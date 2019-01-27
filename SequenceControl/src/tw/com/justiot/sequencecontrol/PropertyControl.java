package tw.com.justiot.sequencecontrol;

import java.util.*;
import java.awt.Dimension;
import java.io.*;
import java.nio.file.Paths;

public class PropertyControl
  {private final String pfile=Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+"windows.properties";
   private final String[] winkeys={"win.x","win.y","win.w","win.h",
          "winseq.x","winseq.y","winseq.w","winseq.h",
          "winplc.x","winplc.y","winplc.w","winplc.h", "psplitx", "esplitx"};
   private final String langkey="language";
   private final String[] lookkeys={"lookfeel","theme"};
   private final int[] windefault={0,0,660,420,
          0,0,660,420,
          0,0,660,420, 300, 240};
   private final String[] lookdefault={"metal","0"};
   public Properties properties=null;

   private SCCAD ladder;
   public PropertyControl(SCCAD ladder)
    {this.ladder=ladder;
     getProperties();
//     if(Config.getBoolean("debug")) System.out.println("getProperties:"+pfile);
     System.out.println("getProperties:"+pfile);
    }
   public String getLang() {
		 return properties.getProperty(langkey);
	   }
   private void showProperties() {
	  for(int i=0;i<winkeys.length;i++) {
		  System.out.println(properties.getProperty(winkeys[i]));
	  }
	  for(int i=0;i<lookkeys.length;i++) {
		  System.out.println(properties.getProperty(lookkeys[i]));
	  }
   }
   
   public void getProperties() 
   {Properties defaults = new Properties();
    for(int i=0;i<winkeys.length;i++)
     defaults.put(winkeys[i],windefault[i]);
    for(int i=0;i<lookkeys.length;i++)
     defaults.put(lookkeys[i],lookdefault[i]);
    defaults.put(langkey,"tw");
    
    properties = new Properties(defaults); 
    FileInputStream in = null;
    try
     {in = new FileInputStream(pfile);
      properties.load(in);
     }
    catch (java.io.FileNotFoundException e) 
     {in = null;
      System.err.println("Can't find properties file. " +"Using defaults.");
     }
    catch (java.io.IOException e) 
     {System.err.println("Can't read properties file. " +"Using defaults.");} 
      finally 
       {if(in!=null) {try{in.close();} catch (java.io.IOException e) {} in = null;}
     }
 //   showProperties();
   }
   
   public void applyWinProperties() {
	 int wx=Integer.parseInt(properties.getProperty("win.x"));
	 int wy=Integer.parseInt(properties.getProperty("win.y"));
	 int ww=Integer.parseInt(properties.getProperty("win.w"));
	 int wh=Integer.parseInt(properties.getProperty("win.h"));
	 ladder.frame.setLocation(wx, wy);
	 ladder.frame.setPreferredSize(new Dimension(ww, wh));
   }
   public void applyWinSeqProperties() {
		 int wseqx=Integer.parseInt(properties.getProperty("winseq.x"));
		 int wseqy=Integer.parseInt(properties.getProperty("winseq.y"));
		 int wseqw=Integer.parseInt(properties.getProperty("winseq.w"));
		 int wseqh=Integer.parseInt(properties.getProperty("winseq.h"));
		 if(ladder.electrics.sequence!=null) {
			 ladder.electrics.sequence.setLocation(wseqx, wseqy);
			 ladder.electrics.sequence.setPreferredSize(new Dimension(wseqw, wseqh));
		 }
		 
	   }
   public void applyWinPLCProperties() {
		 int wplcx=Integer.parseInt(properties.getProperty("winplc.x"));
		 int wplcy=Integer.parseInt(properties.getProperty("winplc.y"));
		 int wplcw=Integer.parseInt(properties.getProperty("winplc.w"));
		 int wplch=Integer.parseInt(properties.getProperty("winplc.h"));
		 if(ladder.electrics.plcFrame!=null) {
			 ladder.electrics.plcFrame.setLocation(wplcx, wplcy);
			 ladder.electrics.plcFrame.setPreferredSize(new Dimension(wplcw, wplch));
		 }
	   }
   public void updateProperties()
    { 
    properties.put("win.x",Integer.toString(ladder.frame.getLocationOnScreen().x));
    properties.put("win.y",Integer.toString(ladder.frame.getLocationOnScreen().y));
    properties.put("win.w",Integer.toString(ladder.frame.getSize().width));
    properties.put("win.h",Integer.toString(ladder.frame.getSize().height));
    
    properties.put("psplitx",Integer.toString(ladder.psplitPane.getDividerLocation()));
    properties.put("esplitx",Integer.toString(ladder.electrics.esplitPane.getDividerLocation()));
    if(ladder.electrics!=null && ladder.electrics.sequence!=null)
     {
      properties.put("winseq.x",Integer.toString(ladder.electrics.sequence.getLocationOnScreen().x));
      properties.put("winseq.y",Integer.toString(ladder.electrics.sequence.getLocationOnScreen().y));
      properties.put("winseq.w",Integer.toString(ladder.electrics.sequence.getSize().width));
      properties.put("winseq.h",Integer.toString(ladder.electrics.sequence.getSize().height));
     }
    if(ladder.electrics!=null && ladder.electrics.plcFrame!=null)
     {if(!ladder.electrics.plcFrame.isVisible()) ladder.electrics.plcFrame.setVisible(true);
      properties.put("winplc.x",Integer.toString(ladder.electrics.plcFrame.getLocationOnScreen().x));
      properties.put("winplc.y",Integer.toString(ladder.electrics.plcFrame.getLocationOnScreen().y));
      properties.put("winplc.w",Integer.toString(ladder.electrics.plcFrame.getSize().width));
      properties.put("winplc.h",Integer.toString(ladder.electrics.plcFrame.getSize().height));
     }
    properties.put("lookfeel",ladder.currentLookAndFeel);
    properties.put("theme",Integer.toString(ladder.currentTheme));
    properties.put(langkey,ladder.lang);
    }

   public void saveProperties() 
    {updateProperties();
  //   showProperties();
     FileOutputStream out = null;
     try 
      {out = new FileOutputStream(pfile);
       properties.store(out,"Windows Properties");
      }
     catch (java.io.IOException e) 
      {System.err.println("Can't save properties. Oh well, it's not a big deal.");
      } 
     finally 
      {if(out!=null) {try{out.close();} catch(java.io.IOException ee){} out = null;}
      }
    }

}

