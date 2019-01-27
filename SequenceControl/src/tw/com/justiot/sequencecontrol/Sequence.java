package tw.com.justiot.sequencecontrol;

import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.net.*;

public class Sequence extends JPanel {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	// The preferred size of the demo
    private int PREFERRED_WIDTH = 350;
    private int PREFERRED_HEIGHT = 300;

    // Box spacers
    private Dimension HGAP = new Dimension(1,5);
    private Dimension VGAP = new Dimension(5,1);

    private JLabel statusMode,statusPos,status;

    private ButtonGroup toolbarGroup = new ButtonGroup();

    // Menus
    public JMenuBar menuBar = null;

    // Used only if Sequence is an application
    public JFrame frame = null;

    // contentPane cache, saved from the applet or application frame
    private Container contentPane = null;

    private JMenuItem pasteMenuItem,overwriteMenuItem;
    private JMenuItem simuMenuItem,stopMenuItem;
    public JScrollPane scrollPane;

    public static Image[] markImage;
    private static String[] markString;
    private static Image[] cmarkImage;
    public static Image delayImage;

    private Hashtable systable;
    private Hashtable lstable;
    public JComboBox sysList;
    public JComboBox lsList;
    private JButton addButton;
    public JComboBox dirList;

   public JMenu selectedMenu=null;

    private void readMark()
   {if(markImage!=null) return;
//System.out.println("readMark");
    URL imageURL = null;
    markImage=new Image[6];
    cmarkImage=new Image[6];
    markString=new String[6];
    markString[0]=Config.getString("sequence.parallel");
    markString[1]=Config.getString("sequence.choice");
    markString[2]=Config.getString("sequence.jump");
    markString[3]=Config.getString("sequence.circular");
    markString[4]=Config.getString("sequence.left");
    markString[5]=Config.getString("sequence.right");
//    Toolkit tk=Toolkit.getDefaultToolkit();
    String path=null;
    for(int i=0;i<markImage.length;i++)
     {try
       {//Image image=util.loadImage(File.separator+"resources"+File.separator+"images"+File.separator+"new.gif");
    	 path=File.separator+"resources"+File.separator+"images"+File.separator+"mark"+File.separator+"mark"+Integer.toString(i+1)+".gif";
//        markImage[i]=tk.getImage(getClass().getResource(path));
        markImage[i]=util.loadImage(path);
        path=File.separator+"resources"+File.separator+"images"+File.separator+"mark"+File.separator+"mark"+Integer.toString(i+1)+"c.gif";
//        cmarkImage[i]=tk.getImage(getClass().getResource(path));
        cmarkImage[i]=util.loadImage(path);
       }
      catch (Exception e)
       {java.lang.System.err.println("Please verify your Mark imageURL.");}
     }
    try
       {path=File.separator+"resources"+File.separator+"images"+File.separator+"mark"+File.separator+"delay.gif";
//        delayImage=tk.getImage(getClass().getResource(path));
        delayImage=util.loadImage(path);
       }
      catch (Exception e)
       {java.lang.System.err.println("Please verify your Mark imageURL.");}
   }

  private JToolBar createToolBar()
   {JToolBar toolBar = new JToolBar();
     Insets inset=new Insets(1,1,1,1);
     JToggleButton button = null;
     Image img=null;
     for(int i=0;i<markImage.length;i++)
      {img=cmarkImage[i];
//System.err.println("toolBar"+i);
        if(img!=null)
         button=new JToggleButton(markString[i],(Icon) new ImageIcon(img));
        else
         button=new JToggleButton(markString[i]);
        button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(markString[i]);
        button.setActionCommand(Integer.toString(i));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JToggleButton b=(JToggleButton) e.getSource();
                int ind=Integer.parseInt(b.getActionCommand());
//System.err.println("command:"+ind);
                sequencePanel.AddCell(ind,null,getCellEDevice(ind),SequenceCell.ID_None,true);
            }
           });
        toolBar.add(button);
        toolbarGroup.add(button);
      }
     return toolBar;
   }

    public EDevice getCellEDevice(int ind)
     {
      if(ind==SequenceCell.IT_Choice || ind == SequenceCell.IT_Jump || ind == SequenceCell.IT_Repeat)
        {ArrayList list=new ArrayList();
          EDevice ed=null;
          for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
           {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
             if(ed.ced.name!=null && ed.ced.name.length()>0)
              {if(ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE)
                 list.add(ed.ced.name);
                else if(ed.ced.actionType==CEDevice.TYPE_COUNTER && ind==SequenceCell.IT_Repeat)
                 list.add(ed.ced.name);
               }
           }
           DBDialog dbDialog = new DBDialog(new JFrame(),list,Config.getString("sequence.s1"),false);
           dbDialog.pack();
           Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
           dbDialog.setLocation(screenSize.width/2 - dbDialog.getSize().width/2,screenSize.height/2 - dbDialog.getSize().height/2);
           dbDialog.setVisible(true);
           String deviceName=dbDialog.getText();
           if(deviceName==null || deviceName.length()==0) return null;
           for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
            {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
              if((ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE ||
                    ed.ced.actionType==CEDevice.TYPE_COUNTER) &&  ed.ced.name!=null && ed.ced.name.equals(deviceName))
               {//sequencePanel.AddCell(ind,null,ed,SequenceCell.ID_None,true);
                 return ed;
               }
            }
            return null;
          }
         else
          return null;
       }

    //   public static Sequence self;
//    public electriclistener electriclistener;
    ElectricListener electriclistener;
    public SequencePanel sequencePanel=null;

    public Sequence(ElectricListener electriclistener)
     {super();
      this.electriclistener=electriclistener;
      setLayout(new BorderLayout());
      setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
      initialize();
      showSequence();
     }

    public void refreshSystemCombo()
      {//sysList.hide();
 //       if(electriclistener.getEArrays().ElectricFaceArray.size()==0 || electriclistener.getEArrays().EDeviceArray.size()==0) return;
        sysList.removeAllItems();
        systable.clear();
 //       Piston piston=null;
  //      for(int i=0;i<electriclistener.getEArrays().PistonArray.size();i++)
  //      {
  //        piston=electriclistener.getEArrays().PistonArray.get(i);
  //        System.out.println(piston.getActuator().getName());
  //        systable.put(piston.getActuator().getName(), piston.getEValve());
  //        sysList.addItem(piston.getActuator().getName());
  //      }
      
        ElectricFace sys=null;
        String aname=null;
        for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
         {sys=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
           aname=sys.getActuatorName();
           if(aname!=null && aname.length()>0)
            {systable.put(aname,sys);
              sysList.addItem(aname);
            }
         }
//System.err.println("refreshSystemCombo:ESystemBase");

       EDevice ed=null;
       for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
         {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
           if(ed.ced.actionType==CEDevice.TYPE_TIMER)
            {systable.put(ed.ced.name,ed);
              sysList.addItem(ed.ced.name);
            }
         }
//System.err.println("refreshSystemCombo:edevice");
       if(sysList.getItemCount()>0)
        {sysList.setSelectedIndex(0);
          sysList.setVisible(true);
          dirList.setVisible(true);
          addButton.setVisible(true);
          refreshLSCombo();
        }
       else
        {
//          addButton.hide();
//          sysList.hide();
//          dirList.hide();
//          lsList.hide();
          addButton.setVisible(false);
          sysList.setVisible(false);
          dirList.setVisible(false);
          lsList.setVisible(false);
       }
     }

    public void refreshLSCombo()
      {String aName = (String) sysList.getSelectedItem();
        lstable.clear();
        lsList.removeAllItems();
        if(aName==null || aName.length()==0) return;
        Object obj=systable.get(aName);
        if(obj instanceof EDevice)
          {lsList.setVisible(false);
            dirList.setVisible(false);
          }
        else if(obj instanceof ElectricFace)
         {ElectricFace sys=(ElectricFace) obj;
           dirList.removeAllItems();
           if(sys.withLS())
            {
              if(sys.getCDOutput().ActuatorTwoWay)
               {dirList.addItem(Config.getString("sequence.Forward"));
                 dirList.addItem(Config.getString("sequence.Backward"));
               }
              else
                dirList.addItem(Config.getString("sequence.Forward"));
              dirList.setVisible(true);
//              dirList.show();
            }
           else
            dirList.setVisible(false);

           lsList.removeAllItems();
           ArrayList valveArray=sys.getValveArray();
           if(valveArray==null) return;
           EDevice ed=null;
           for(int i=0;i<valveArray.size();i++)
            {if(valveArray.get(i) instanceof EDevice)
               {ed=(EDevice) valveArray.get(i);
                 if(!lstable.containsKey(ed.ced.name))
                  {lstable.put(ed.ced.name,ed);
                    lsList.addItem(ed.ced.name);
                  }
               }
            }
           if(lstable.size()==0) lsList.setVisible(false);
           else lsList.setVisible(true); //lsList.show();
         }
     }

    public void initialize()
      {
//System.out.println("enter initialize");
       readMark();
//System.out.println("readMark o.k.");
       sequencePanel=new SequencePanel(electriclistener);
       JToolBar toolBar=createToolBar();
       JPanel top = new JPanel();
       top.setLayout(new BorderLayout());
       if(electriclistener.getExampleURL()==null) add(top, BorderLayout.NORTH);
       menuBar = createMenus();
       top.add(menuBar, BorderLayout.NORTH);
       top.add(toolBar, BorderLayout.CENTER);
//System.out.println("toolbar o.k.");
       JPanel addPanel=new JPanel();
       addPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
       if(dirList==null) dirList=new JComboBox();
       dirList.addItem(Config.getString("sequence.Forward"));
       dirList.addItem(Config.getString("sequence.Backward"));

       if(systable==null) systable=new Hashtable();
       if(lstable==null) lstable=new Hashtable();
       if(sysList==null) sysList = new JComboBox();
       if(lsList==null) lsList=new JComboBox();
       if(addButton==null) addButton=new JButton(Config.getString("sequence.add"));
       refreshSystemCombo();
       refreshLSCombo();
//System.out.println("refreshLSCombo o.k.");
/*
           sysList.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                     refreshSystemCombo();
                 }
               });
*/
       sysList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int ind=sysList.getSelectedIndex();
                    refreshLSCombo();
                 }
               });
       lsList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int ind=lsList.getSelectedIndex();
                }
               });
       dirList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int ind=dirList.getSelectedIndex();
                }
               });

       addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   addButtonAction();
                 }
               });
       addPanel.add(addButton);
//System.out.println("addButton");
       addPanel.add(new JLabel("  "));
       addPanel.add(sysList);
       addPanel.add(new JLabel("  "));
       addPanel.add(dirList);
       addPanel.add(new JLabel("  "));
       addPanel.add(lsList);
       top.add(addPanel, BorderLayout.SOUTH);
//System.out.println("addPanel");
       scrollPane=new JScrollPane(sequencePanel);
       sequencePanel.setJScrollPane(scrollPane);
       add(scrollPane,BorderLayout.CENTER);
//System.out.println("scrollpane");
       JPanel statusPanel=new JPanel();
       statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
       statusMode=new JLabel(Config.getString("Status.insert"));
       statusPos=new JLabel("row 0 : col 0");
       status=new JLabel();
       statusPanel.add(statusMode);
       statusPanel.add(new JLabel("   "));
       statusPanel.add(statusPos);
       statusPanel.add(new JLabel("   "));
       statusPanel.add(status);
       statusMode.setBackground(Color.white);
       statusPos.setBackground(Color.white);
       status.setBackground(Color.white);
       add(statusPanel, BorderLayout.SOUTH);
//System.out.println("status");
    }

    public void addButtonAction()
      {if(sysList.getItemCount()==0) return;
        String sysName = (String)sysList.getSelectedItem();

        if(sysName==null || sysName.length()==0) sysName=(String) sysList.getItemAt(0);
        Object obj=systable.get(sysName);
        if(obj instanceof EDevice)
         sequencePanel.AddCell(SequenceCell.IT_Delay,null,(EDevice) obj,SequenceCell.ID_None,true);
        if(obj instanceof ESystemBase || obj instanceof EValve)
         {String dirName=(String) dirList.getSelectedItem();
           if(dirName==null || dirName.length()==0) dirName="";
           int dirint=SequenceCell.ID_None;
           if(dirName.equals(Config.getString("sequence.Backward"))) dirint=SequenceCell.ID_Backward;
           if(dirName.equals(Config.getString("sequence.Forward"))) dirint=SequenceCell.ID_Forward;
           String lsName=(String) lsList.getSelectedItem();
           if(lsName==null || lsName.length()==0)
             sequencePanel.AddCell(SequenceCell.IT_System,(ElectricFace) obj,null,dirint,true);
           else
             sequencePanel.AddCell(SequenceCell.IT_System,(ElectricFace) obj,(EDevice) lstable.get(lsName),dirint,true);
          }
        electriclistener.setModified(true);
     }
    /**
     * Create menus
     */
    private menuItemMouseAdapter allmenuItemMouseAdapter;
    public JMenuBar createMenus()
     {
	  JMenuBar menuBar = new JMenuBar();
      allAction aAction=new allAction(sequencePanel);
      allMenuListener allmenuListener=new allMenuListener();
      allmenuItemMouseAdapter=new menuItemMouseAdapter();
	  menuBar.getAccessibleContext().setAccessibleName(Config.getString("MenuBar.accessible_description"));
		// ***** create Edit menu
	  JMenu editMenu = (JMenu) menuBar.add(new JMenu(Config.getString("editMenu.edit_label")));
      editMenu.setMnemonic(electriclistener.getMnemonic("editMenu.edit_mnemonic"));
	  editMenu.getAccessibleContext().setAccessibleDescription(Config.getString("editMenu.accessible_description"));
//                editMenu.addMenuListener(new EditMenuListener());
      editMenu.setActionCommand("edit");
      editMenu.addMenuListener(allmenuListener);
      createMenuItem(editMenu,"del", "editMenu.del_label", "editMenu.del_mnemonic","editMenu.del_accessible_description",aAction);
      createMenuItem(editMenu,"cut", "editMenu.cut_label", "editMenu.cut_mnemonic","editMenu.cut_accessible_description", aAction);
      pasteMenuItem=createMenuItem(editMenu,"paste", "editMenu.paste_label", "editMenu.paste_mnemonic","editMenu.paste_accessible_description", aAction);
      createMenuItem(editMenu,"copy", "editMenu.copy_label", "editMenu.copy_mnemonic","editMenu.copy_accessible_description", aAction);
      editMenu.addSeparator();
      overwriteMenuItem=createMenuItem(editMenu,"overwrite", "editMenu.overwrite_label", "editMenu.overwrite_mnemonic","editMenu.overwrite_accessible_description", aAction);
                              // ***** create Sequence menu
	  JMenu seqMenu = (JMenu) menuBar.add(new JMenu(Config.getString("seqMenu.label")));
      seqMenu.setMnemonic(electriclistener.getMnemonic("seqMenu.mnemonic"));
	  seqMenu.getAccessibleContext().setAccessibleDescription(Config.getString("seqMenu.accessible_description"));
//                seqMenu.addMenuListener(new seqMenuListener());
      seqMenu.setActionCommand("seq");
      seqMenu.addMenuListener(allmenuListener);
      createMenuItem(seqMenu,"check", "seqMenu.check_label", "seqMenu.check_mnemonic","seqMenu.check_accessible_description", aAction);
      createMenuItem(seqMenu,"condition", "seqMenu.condition_label", "seqMenu.condition_mnemonic","seqMenu.condition_accessible_description", aAction);
      createMenuItem(seqMenu,"design", "seqMenu.design_label", "seqMenu.design_mnemonic","seqMenu.design_accessible_description", aAction);
      return menuBar;
    }

    public JMenuItem createMenuItem(JMenu menu,String acommand, String label, String mnemonic,String accessibleDescription, Action action)
     {
      JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(Config.getString(label)));
      mi.setActionCommand(acommand);
      mi.setMnemonic(electriclistener.getMnemonic(mnemonic));
	  mi.getAccessibleContext().setAccessibleDescription(Config.getString(accessibleDescription));
	  mi.addActionListener(action);
      mi.addMouseListener(allmenuItemMouseAdapter);
	  if(action == null) mi.setEnabled(false);
	  return mi;
    }

    public void showSequence()
      {if(getFrame()!=null) getFrame().setVisible(true); //getFrame().show();
       else
        {frame = new JFrame();
         frame.setIconImage(electriclistener.getIconImage());
         WindowListener l = new WindowAdapter() {public void windowClosing(WindowEvent e) {getFrame().setVisible(false);}};
         frame.addWindowListener(l);
         frame.setTitle(Config.getString("Frame.sequenceTitle"));
         frame.getContentPane().add(this, BorderLayout.CENTER);
         frame.pack();
/*
          if(electriclistener.propertyControl.winSw==0 || electriclistener.propertyControl.winSh==0)
           {Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
             getFrame().setLocation(screenSize.width/2 - frame.getSize().width/2,screenSize.height/2 - frame.getSize().height/2);
           }
          else
            {getFrame().setLocation(electriclistener.propertyControl.winSx,electriclistener.propertyControl.winSy);
              getFrame().setSize(electriclistener.propertyControl.winSw,electriclistener.propertyControl.winSh);
            }
*/
         getFrame().setVisible(true); //show();
        }
      }
    public JMenuBar getMenuBar() {return menuBar;}
    public ButtonGroup getToolBarGroup() {return toolbarGroup;}
    public JFrame getFrame() {return frame;}
    public Container getContentPane()
     {if(contentPane==null) contentPane = getFrame().getContentPane();
	  return contentPane;
     }
    public void setStatus(String s) {status.setText(s);}
    public void setStatusMode(String s) {statusMode.setText(s);}
    public void setStatusPos(String s) {statusPos.setText(s);}

  class allAction extends AbstractAction
    {
     SequencePanel sequencePanel;
      public allAction(SequencePanel sequencePanel)
       {
        this.sequencePanel=sequencePanel;
       }
      public void actionPerformed(ActionEvent e)
       {String com=((JMenuItem) e.getSource()).getActionCommand();
         ActionPerformed(com,sequencePanel);
       }
     }

  public void ActionPerformed(String com,SequencePanel sequencePanel)
   {if(com.equals("cut"))
     {if(sequencePanel!=null)
       {sequencePanel.copyToBoard();
        sequencePanel.deleteBlock();
        sequencePanel.degroup();
       }
     }
    else if(com.equals("copy"))
     {if(sequencePanel!=null)
       {sequencePanel.copyToBoard();
        sequencePanel.degroup();
       }
     }
    else if(com.equals("paste"))
     {if(sequencePanel!=null)
       {sequencePanel.pasteBoardTo();
        sequencePanel.degroup();
       }
     }
    else if(com.equals("del"))
     {if(sequencePanel!=null)
       {sequencePanel.deleteBlock();
        sequencePanel.degroup();
       }
     }
    else if(com.equals("overwrite"))
     {if(sequencePanel!=null)
       {sequencePanel.overWrite=!sequencePanel.overWrite;
        String str=Config.getString("Status.overwrite");
        if(!sequencePanel.overWrite) str=Config.getString("Status.insert");
        setStatusMode(str);
        sequencePanel.degroup();
       }
     }
    else if(com.equals("check"))
     {if(sequencePanel!=null) sequencePanel.InputCheck();
     }
    else if(com.equals("condition"))
     {if(sequencePanel!=null) sequencePanel.Condition();
     }
    else if(com.equals("design"))
     {if(sequencePanel!=null) sequencePanel.Design();
     }
//    else if(com.equals("
  }

  class allMenuListener implements MenuListener
     {
      public void menuSelected(MenuEvent e)
       {JMenu menu=(JMenu) e.getSource();
         selectedMenu=menu;
         int nmenu=-1;
         for(int i=0;i<menuBar.getMenuCount();i++)
          {if(menu==menuBar.getMenu(i)) {nmenu=i;break;}}
        String com=menu.getActionCommand();
        if(com.equals("edit"))
         {if(sequencePanel.cellGroup!=null && electriclistener.getElectricPanel().cellGroup.size()>0)
            pasteMenuItem.setEnabled(true);
           else
            pasteMenuItem.setEnabled(false);
           if(!sequencePanel.overWrite) overwriteMenuItem.setText(Config.getString("Status.overwrite"));
           else overwriteMenuItem.setText(Config.getString("Status.insert"));
         }
      }
      public void menuCanceled(MenuEvent e) {}
      public void menuDeselected(MenuEvent e) {}
     }

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {JMenuItem mitem=(JMenuItem) e.getSource();
         int n=-1;
         for(int i=0;i<selectedMenu.getItemCount();i++)
          if(selectedMenu.getItem(i)==mitem) {n=i;break;}
        if(n<0) return;
       }
   }

}
