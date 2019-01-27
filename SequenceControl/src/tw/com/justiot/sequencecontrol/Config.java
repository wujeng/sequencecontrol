package tw.com.justiot.sequencecontrol;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class Config {
//  private static String filename="application.properties";
  private static String filename="application";
  private static com.typesafe.config.Config conf;
  static {
	  String s = Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+filename;
	  File file=new File(s);
	  conf=ConfigFactory.parseFile(file); 
  }
  public Config(String lang) {
	  String s=null;
	  if(lang==null) s=Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+filename+"-en.properties";
	  else s=Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+filename+"-"+lang+".properties";
	  File file=new File(s);
	  System.out.println(s);
	  conf=ConfigFactory.parseFile(file); 
  }
  public static String getString(String key) {
	//  if(getBoolean("debug")) System.out.println("getString:"+key);
	  return conf.getString(key);
  }
  public static boolean getBoolean(String key) {
	  return conf.getBoolean(key);
  }
  public static int getInt(String key) {
	  return conf.getInt(key);
  }
  public static double getDouble(String key) {
	  return conf.getDouble(key);
  }
  
  public static void saveProperties(String fn, Properties anotherproperties) {
      Properties properties = new Properties();
      Set<Entry<String, ConfigValue>> set=conf.entrySet();
      for (Entry<String, ConfigValue> s : set) {
    	  properties.setProperty(s.getKey(), conf.getString(s.getKey()));
    	}
      if(anotherproperties!=null) {
    	  Enumeration<String> enums = (Enumeration<String>) anotherproperties.propertyNames();
    	    while (enums.hasMoreElements()) {
    	      String key = enums.nextElement();
    	      String value = anotherproperties.getProperty(key);
    	      properties.setProperty(key, value);
    	    }
      }
      File f = new File(Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+fn);
      try {
      OutputStream out = new FileOutputStream( f );
      properties.store(out, "This is an optional header comment string");
      } catch(Exception e) {
    	  
      }
  }
  public static void saveProperties(Properties anotherproperties) {
      Properties properties = new Properties();
      Set<Entry<String, ConfigValue>> set=conf.entrySet();
      for (Entry<String, ConfigValue> s : set) {
    	  properties.setProperty(s.getKey(), conf.getString(s.getKey()));
    	}
      if(anotherproperties!=null) {
    	  Enumeration<String> enums = (Enumeration<String>) anotherproperties.propertyNames();
    	    while (enums.hasMoreElements()) {
    	      String key = enums.nextElement();
    	      String value = anotherproperties.getProperty(key);
    	      properties.setProperty(key, value);
    	    }
      }
      String s = Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+filename;
	  File f=new File(s);
      try {
      OutputStream out = new FileOutputStream( f );
      properties.store(out, "This is an optional header comment string");
      } catch(Exception e) {
    	  
      }
  }
}
