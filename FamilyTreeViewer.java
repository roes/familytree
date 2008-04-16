import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FamilyTreeViewer{
	// Some GUI constants
    private static final int VIEWER_WIDTH = 600;
    private static final int VIEWER_HEIGHT = 400;
    private static final int NODE_WIDTH = 100;
    private static final int NODE_HEIGHT = 40;
    private static final int NODE_MARGIN = 20;
    private static final int ROW_HEIGHT = NODE_HEIGHT + NODE_MARGIN;
    // These ones should probably go away eventually
    private static final int SELF_ROW = 80;
    private static final int PARENT_ROW = SELF_ROW - ROW_HEIGHT;
    private static final int CHILD_ROW = SELF_ROW + ROW_HEIGHT;
    private static final int SELF_POS = 250;
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
		open.addActionListener(new OpenActionListener());
		filemenu.add(open);
	}
	// Ritar ut närmaste familjen
	// TODO Lägg till pilar
	private void drawFamily(int refnr){
		Container tree = viewer.getContentPane();
		tree.removeAll();
		Person root = familyTree.getFamily().get(refnr);
		if(root.hasMother()){
			JButton mother = new JButton(root.getMother().getName());
			tree.add(mother);
			mother.setBounds(MOTHER_POS, PARENT_ROW, NODE_WIDTH, NODE_HEIGHT);
			mother.addActionListener(new ButtonListener());
		}
		if(root.hasFather()){
			JButton father = new JButton(root.getFather().getName());
			tree.add(father);
			father.setBounds(FATHER_POS, PARENT_ROW, NODE_WIDTH, NODE_HEIGHT);
			father.addActionListener(new ButtonListener());
		}
		JButton me = new JButton(root.getName());
		tree.add(me);
		me.setBounds(SELF_POS, SELF_ROW, NODE_WIDTH, NODE_HEIGHT);
		me.addActionListener(new MainButtonListener());
		if(root.hasSpouse()){
			JButton spouse = new JButton(root.getSpouse().getName());
			tree.add(spouse);
			spouse.setBounds(SPOUSE_POS, SELF_ROW, NODE_WIDTH, NODE_HEIGHT);
			spouse.addActionListener(new ButtonListener());
		}
		if(root.hasChildren()){
			ArrayList<Person> children = root.getChildren();
			int pos = children.size();
			pos = SELF_POS - pos*NODE_WIDTH/2 + NODE_WIDTH/2;
			if(pos < 0){
				pos = NODE_MARGIN;
			}
			for(Person c : children){
				JButton child = new JButton(c.getName());
				tree.add(child);
				child.setBounds(pos, CHILD_ROW, NODE_WIDTH, NODE_HEIGHT);
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
			info.append("Sex: " + (root.getSex().name().toLowerCase()) + "\n");
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
	public static void main(String[] args) {
		new FamilyTreeViewer();
	}
}
