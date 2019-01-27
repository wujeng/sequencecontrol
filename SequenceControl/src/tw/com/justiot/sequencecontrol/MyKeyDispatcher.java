package tw.com.justiot.sequencecontrol;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import tw.com.justiot.sequencecontrol.eelement.EDevice;

public class MyKeyDispatcher  implements KeyEventDispatcher {
    private SCCAD sccad;
	public MyKeyDispatcher(SCCAD sccad) {
    	this.sccad=sccad;
    }
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		int kcode=e.getKeyCode();
		
		 if (e.getID() == KeyEvent.KEY_PRESSED) {
			 EDevice ed=null;
			    for(int i=0;i<sccad.earrays.EDeviceArray.size();i++)
			     {ed=(EDevice) sccad.earrays.EDeviceArray.get(i);
			      if(ed.ced.modelName.equals("PushButton") || ed.ced.modelName.equals("ToggleButton"))
			      {
			    	  if(ed.activateKey==kcode) ed.ced.setStatus(true);  
			      }       
			     }
	        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
	        	EDevice ed=null;
	    	    for(int i=0;i<sccad.earrays.EDeviceArray.size();i++)
	    	     {ed=(EDevice) sccad.earrays.EDeviceArray.get(i);
	    	      if(ed.ced.modelName.equals("PushButton") || ed.ced.modelName.equals("ToggleButton"))
	    	      {
	    	    	  if(ed.activateKey==kcode) ed.ced.setStatus(false);  
	    	      }       
	    	     }
	        } else if (e.getID() == KeyEvent.KEY_TYPED) {
	            System.out.println("3test3");
	        }
	        return false;
	}
}