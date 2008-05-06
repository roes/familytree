import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.awt.Container;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FamilyTreeViewer{
	// Some GUI constants
    private static final int VIEWER_WIDTH = 1000;
    private static final int VIEWER_HEIGHT = 600;
    private static final int NODE_WIDTH = 180;
    private static final int NODE_HEIGHT = 80;
    private static final int NODE_MARGIN = 20;
    private static final int ROW_HEIGHT = NODE_HEIGHT + NODE_MARGIN;
    // These ones should probably go away eventually
    private static final int SELF_ROW = ROW_HEIGHT + NODE_MARGIN;
    private static final int PARENT_ROW = SELF_ROW - ROW_HEIGHT;
    private static final int CHILD_ROW = SELF_ROW + ROW_HEIGHT;
    private static final int SELF_POS = VIEWER_WIDTH/2 - NODE_WIDTH/2;
    private static final int FATHER_POS = SELF_POS + (NODE_WIDTH + NODE_MARGIN) / 2;
    private static final int MOTHER_POS = SELF_POS - (NODE_WIDTH + NODE_MARGIN) / 2;
    private static final int SPOUSE_POS = SELF_POS - NODE_WIDTH - NODE_MARGIN;

	JFrame viewer;
	FamilyListGraph familyTree;
	File file;
	
	public FamilyTreeViewer() {
		createViewer();
	}
	// Skapar fönstret
	private void createViewer(){
		viewer = new JFrame("FamilyTree Viewer");
		viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		createMenubar();
		viewer.getContentPane().setLayout(null);

		viewer.setSize(VIEWER_WIDTH, VIEWER_HEIGHT);
		viewer.setVisible(true);
	}
	// Skapar meny
	private void createMenubar(){
		JMenuBar menu = new JMenuBar();
		viewer.setJMenuBar(menu);
		JMenu filemenu = new JMenu("File");
		menu.add(filemenu);
		JMenuItem open = new JMenuItem("Open file");
	    open.setMnemonic(KeyEvent.VK_O);
	    open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		open.addActionListener(new OpenActionListener());
		filemenu.add(open);
		JMenuItem freebase = new JMenuItem("Freebase import");
	    freebase.setMnemonic(KeyEvent.VK_I);
	    freebase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
		freebase.addActionListener(new FreebaseActionListener());
		filemenu.add(freebase);
		JMenuItem graphvizExport = new JMenuItem("Graphviz export");
	    graphvizExport.setMnemonic(KeyEvent.VK_E);
	    graphvizExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		graphvizExport.addActionListener(new GraphvizExportActionListener());
		filemenu.add(graphvizExport);
	    // do some fancy stuff with the Quit item
	    JMenuItem quitItem = new JMenuItem("Quit");
	    quitItem.setMnemonic(KeyEvent.VK_Q);
	    quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
	    quitItem.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) { System.exit(0); }
	    });
	    filemenu.add(quitItem);

	}
	// Ritar ut närmaste familjen
	// TODO Lägg till pilar
	private void drawFamily(int refnr){
		Container tree = viewer.getContentPane();
		tree.removeAll();
		Font font;
		Person root = familyTree.getFamily().get(refnr);
		if(root.hasMother()){
			JButton mother = new JButton(root.getMother().getSexName());
			tree.add(mother);
			mother.setBounds(MOTHER_POS, PARENT_ROW, NODE_WIDTH, NODE_HEIGHT);
			font = mother.getFont();
			mother.setFont(new Font(font.getName(), font.getStyle(), font.getSize()*5/6));
			mother.addActionListener(new ButtonListener());
		}
		if(root.hasFather()){
			JButton father = new JButton(root.getFather().getSexName());
			tree.add(father);
			father.setBounds(FATHER_POS, PARENT_ROW, NODE_WIDTH, NODE_HEIGHT);
			font = father.getFont();
			father.setFont(new Font(font.getName(), font.getStyle(), font.getSize()*5/6));
			father.addActionListener(new ButtonListener());
		}
		JButton me = new JButton(root.getSexName());
		tree.add(me);
		me.setBounds(SELF_POS, SELF_ROW, NODE_WIDTH, NODE_HEIGHT);
		font = me.getFont();
		me.setFont(new Font(font.getName(), font.getStyle(), font.getSize()*5/6));
		me.addActionListener(new MainButtonListener());
		if(root.hasSpouse()){
			JButton spouse = new JButton(root.getSpouse().getSexName());
			tree.add(spouse);
			spouse.setBounds(SPOUSE_POS, SELF_ROW, NODE_WIDTH, NODE_HEIGHT);
			font = spouse.getFont();
			spouse.setFont(new Font(font.getName(), font.getStyle(), font.getSize()*5/6));
			spouse.addActionListener(new ButtonListener());
		}
		if(root.hasChildren()){
			ArrayList<Person> children = root.getChildren();
			int pos = children.size();
			pos = SELF_POS - (pos - 1)*(NODE_WIDTH + NODE_MARGIN)/2;
			if(pos < 0){
				pos = NODE_MARGIN;
			}
			for(Person c : children){
				JButton child = new JButton(c.getSexName());
				tree.add(child);
				child.setBounds(pos, CHILD_ROW, NODE_WIDTH, NODE_HEIGHT);
				font = child.getFont();
				child.setFont(new Font(font.getName(), font.getStyle(), font.getSize()*5/6));
				pos += NODE_WIDTH + NODE_MARGIN;
				child.addActionListener(new ButtonListener());
			}
		}
		tree.repaint();
		viewer.setVisible(true);
	}
	// Skriver ut info om personen i mitten
	public class MainButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JButton pushed = (JButton) e.getSource();
			String name = pushed.getText();
			Person root = familyTree.getPerson(name);
			StringBuilder info = new StringBuilder();
			info.append("Name: " + root.getName() + "\n");
			if (root.getSex() != null) {
				info.append("Sex: " + (root.getSex().name().toLowerCase()) + "\n");
			}
			info.append("Birthdate: " + root.getBirthdate() +"\n");
			info.append("Deathdate: " + root.getDeathdate() + "\n");
			JOptionPane.showMessageDialog(viewer, info);
		}
	}
	// Byter person som är i mitten
	public class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JButton pushed = (JButton) e.getSource();
			String name = pushed.getText();
			Person root = familyTree.getPerson(name);
			drawFamily(root.getRefnr());
		}
	}
	// Öppnar fil
	public class OpenActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("Gedcom files", "ged");
			chooser.setFileFilter(filter);
			int status = chooser.showOpenDialog(viewer);
			if(status == JFileChooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				familyTree = new FamilyTreeImporter().importGedcom(file);
				if (familyTree.getNumMembers() > 0) {
					drawFamily(0);
				} else {
					JOptionPane.showMessageDialog(viewer, "No family members found.",
		                       "No family members found", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
	
	// Import from freebase
	public class FreebaseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			String id;
		    String name = JOptionPane.showInputDialog(viewer,
				 "Who do you want to import?",
				 "Enter name",
				 JOptionPane.QUESTION_MESSAGE);
		    if (name == null) {
		    	return;
		    }
			FreeBaseImporter importer = new FreeBaseImporter();
			if ((id = importer.getId(name)) != null) {
    			familyTree = importer.importFreebase(id);
    			if (familyTree.getNumMembers() > 0) {
    				drawFamily(0);
    			} else {
    				JOptionPane.showMessageDialog(viewer, "No family members found.",
    	                       "No family members found", JOptionPane.WARNING_MESSAGE);
    			}
			} else {
				JOptionPane.showMessageDialog(viewer, "No such person found.",
	                       "No such person found", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	// Export to graphviz file
	public class GraphvizExportActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			JFileChooser chooser = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("Graphviz files", "dot");
			chooser.setFileFilter(filter);
			int status = chooser.showSaveDialog(viewer);
			if(status == JFileChooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				try {
					GraphvizExporter.export(familyTree, file);
				}
				catch (IOException ie) {
					JOptionPane.showMessageDialog(viewer, "IO Exception",
		                       e.toString(), JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
	public static void main(String[] args) {
		new FamilyTreeViewer();
	}
}
