package tw.com.justiot.sequencecontrol;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.beans.*;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;

public class WebBrowser extends JFrame
    implements HyperlinkListener, PropertyChangeListener
{
    private static URL helpURL;
    private static URL helpDirURL;

    JEditorPane dirPane;
    JEditorPane textPane;      // Where the HTML is displayed
    JLabel messageLine;        // Displays one-line messages

    // These fields are used to maintain the browsing history of the window
    java.util.List history = new ArrayList();  // The history list
    int currentHistoryPage = -1;               // Current location in it
    public static final int MAX_HISTORY = 50;  // Trim list when over this size

    public WebBrowser(Image iconimage) {
	        super();
            setIconImage(iconimage);
            String curdir=Paths.get("").toAbsolutePath().toString()+File.separator;
        	try {
        	helpURL=(new File(curdir+Config.getString("helpURL"))).toURI().toURL();
            helpDirURL=(new File(curdir+Config.getString("helpDirURL"))).toURI().toURL();
            System.out.println(helpURL.toString());
        	} catch(Exception e) {
        		e.printStackTrace();
        	}

            dirPane=new JEditorPane();
        	dirPane.setContentType("text/html;charset=UTF-8");
            dirPane.setEditable(false);
            dirPane.setPreferredSize(new Dimension(140,600));
	textPane = new JEditorPane();
	textPane.setContentType("text/html;charset=UTF-8");
//	textPane.setContentType("text/html;charset=Big5");
	textPane.setEditable(false);
            dirPane.addHyperlinkListener(this);
            JScrollPane dirScroll=new JScrollPane(dirPane);

            try{dirPane.setPage(helpDirURL);  }
            catch(Exception ee) {}

	textPane.addHyperlinkListener(this);
	textPane.addPropertyChangeListener(this);
//            this.getContentPane().add(new JScrollPane(dirPane),BorderLayout.WEST);
            JScrollPane scrollPane=new JScrollPane(textPane);
            scrollPane.setPreferredSize(new Dimension(800,600));
//	this.getContentPane().add(scrollPane,BorderLayout.CENTER);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,dirScroll,scrollPane);
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(150);
            Dimension minimumSize = new Dimension(100, 50);
            dirScroll.setMinimumSize(minimumSize);
            scrollPane.setMinimumSize(minimumSize);
            this.getContentPane().add(splitPane,BorderLayout.CENTER);

	messageLine = new JLabel(" ");
	this.getContentPane().add(messageLine, BorderLayout.SOUTH);

	JToolBar toolbar = createToolBar();
	this.getContentPane().add(toolbar, BorderLayout.NORTH);


            displayPage(helpURL);
            pack();
            setVisible(true);

    }

    JButton backButton,forButton,reloadButton,homeButton;
    private JToolBar createToolBar()
     {JToolBar toolBar = new JToolBar();
       Insets inset=new Insets(1,1,1,1);

       backButton=new JButton("Back",(Icon) new ImageIcon(Config.getString("helpImageBack")));
       backButton.setMargin(inset);
       backButton.setToolTipText(Config.getString("tooltip.back"));
       backButton.setActionCommand("back");
       backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              back();
            }
         });
       toolBar.add(backButton);

       forButton=new JButton("Forward",(Icon) new ImageIcon(Config.getString("helpImageFor")));
       forButton.setMargin(inset);
       forButton.setToolTipText(Config.getString("tooltip.forward"));
       forButton.setActionCommand("forward");
       forButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              forward();
            }
         });
       toolBar.add(forButton);

       reloadButton=new JButton("Reload",(Icon) new ImageIcon(Config.getString("helpImageReload")));
       reloadButton.setMargin(inset);
       reloadButton.setToolTipText(Config.getString("tooltip.reload"));
       reloadButton.setActionCommand("reload");
       reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              reload();
            }
         });
       toolBar.add(reloadButton);

       homeButton=new JButton("Home",(Icon) new ImageIcon(Config.getString("helpImageHome")));
       homeButton.setMargin(inset);
       homeButton.setToolTipText(Config.getString("tooltip.home"));
       homeButton.setActionCommand("home");
       homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	displayPage(helpURL);
            }
         });
       toolBar.add(homeButton);

       return toolBar;
     }

    /** Go back to the previously displayed page. */
    public void back() {
	if (currentHistoryPage > 0)  // go back, if we can
	    visit((URL)history.get(--currentHistoryPage));
            backButton.setEnabled((currentHistoryPage > 0));
            forButton.setEnabled((currentHistoryPage < history.size()-1));
	// Enable or disable actions as appropriate
//	backAction.setEnabled((currentHistoryPage > 0));
//	forwardAction.setEnabled((currentHistoryPage < history.size()-1));
    }

    /** Go forward to the next page in the history list */
    public void forward() {
	if (currentHistoryPage < history.size()-1)  // go forward, if we can
	    visit((URL)history.get(++currentHistoryPage));
            backButton.setEnabled((currentHistoryPage > 0));
            forButton.setEnabled((currentHistoryPage < history.size()-1));
	// Enable or disable actions as appropriate
//	backAction.setEnabled((currentHistoryPage > 0));
//	forwardAction.setEnabled((currentHistoryPage < history.size()-1));
    }

    /** Reload the current page in the history list */
    public void reload() {
	if (currentHistoryPage != -1)
	    visit((URL)history.get(currentHistoryPage));
    }

    boolean visit(URL url) {
	try {
	    String href = url.toString();
	    // Start animating.  Animation is stopped in propertyChanged()
	    startAnimation("Loading " + href + "...");
	    textPane.setPage(url);   // Load and display the URL
	    this.setTitle(href);     // Display URL in window titlebar
	    return true;             // Return success
	}
	catch (IOException ex) {     // If page loading fails
	    stopAnimation();
	    messageLine.setText("Can't load page: " + ex.getMessage());
	    return false;            // Return failure
	}
    }

    public void displayPage(URL url) {
	if (visit(url)) {    // go to the specified url, and if we succeed:
	    history.add(url);       // Add the url to the history list
	    int numentries = history.size();
	    if (numentries > MAX_HISTORY+10) {  // Trim history when too large
		history = history.subList(numentries-MAX_HISTORY, numentries);
		numentries = MAX_HISTORY;
	    }
	    currentHistoryPage = numentries-1;  // Set current history page
	    // If we can go back, then enable the Back action
	    if (currentHistoryPage > 0) backButton.setEnabled(true);
	}
    }

    public void displayPage(String href) {
	try {
	    displayPage(new URL(href));
	}
	catch (MalformedURLException ex) {
	    messageLine.setText("Bad URL: " + href);
	}
    }

    public void close() {
	this.setVisible(false);             // Hide the window
	this.dispose();                     // Destroy the window
    }


    /**
     * This method implements HyperlinkListener.  It is invoked when the user
     * clicks on a hyperlink, or move the mouse onto or off of a link
     **/
    public void hyperlinkUpdate(HyperlinkEvent e) {
	HyperlinkEvent.EventType type = e.getEventType();  // what happened?
	if (type == HyperlinkEvent.EventType.ACTIVATED) {     // Click!
	    displayPage(e.getURL());   // Follow the link; display new page
	}
	else if (type == HyperlinkEvent.EventType.ENTERED) {  // Mouse over!
	    // When mouse goes over a link, display it in the message line
	    messageLine.setText(e.getURL().toString());
	}
	else if (type == HyperlinkEvent.EventType.EXITED) {   // Mouse out!
	    messageLine.setText(" ");  // Clear the message line
	}
    }

    /**
     * This method implements java.beans.PropertyChangeListener.  It is
     * invoked whenever a bound property changes in the JEditorPane object.
     * The property we are interested in is the "page" property, because it
     * tells us when a page has finished loading.
     **/
    public void propertyChange(PropertyChangeEvent e) {
	if (e.getPropertyName().equals("page")) // If the page property changed
	    stopAnimation();              // Then stop the loading... animation
    }

    /**
     * The fields and methods below implement a simple animation in the
     * web browser message line; they are used to provide user feedback
     * while web pages are loading.
     **/
    String animationMessage;  // The "loading..." message to display
    int animationFrame = 0;   // What "frame" of the animation are we on
    String[] animationFrames = new String[] {  // The content of each "frame"
	"-", "\\", "|", "/", "-", "\\", "|", "/",
	",", ".", "o", "0", "O", "#", "*", "+"
    };

    /** This object calls the animate() method 8 times a second */
    javax.swing.Timer animator =
	new javax.swing.Timer(125, new ActionListener() {
		public void actionPerformed(ActionEvent e) { animate(); }
	    });

    /** Display the next frame. Called by the animator timer */
    void animate() {
	String frame = animationFrames[animationFrame++];    // Get next frame
	messageLine.setText(animationMessage + " " + frame); // Update msgline
	animationFrame = animationFrame % animationFrames.length;
    }

    /** Start the animation.  Called by the visit() method. */
    void startAnimation(String msg) {
	animationMessage = msg;     // Save the message to display
	animationFrame = 0;         // Start with frame 0 of the animation
	animator.start();           // Tell the timer to start firing.
    }

    /** Stop the animation.  Called by propertyChanged() method. */
    void stopAnimation() {
	animator.stop();            // Tell the timer to stop firing events
	messageLine.setText(" ");   // Clear the message line
    }
}
