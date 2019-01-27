package tw.com.justiot.sequencecontrol.dialog;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class CircuitFilter extends FileFilter {
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
	return true;
        }

        String extension = getExtension(f);
	if (extension != null) {
            if (extension.equals("pc")) {
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }


    // The description of this filter
    public String getDescription() {
        return Config.getString("CircuitFilter.CircuitFile");
    }
}

