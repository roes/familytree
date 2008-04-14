import java.util.ArrayList;
import junit.framework.TestCase;


public class FamilyListGraphTest extends TestCase {
	private FamilyListGraph g0, g1;
	private ArrayList<Person> family;
	private Person father, mother, child, gfather;

	public FamilyListGraphTest() {;
	}

	protected void setUp() throws Exception {
		g0 = new FamilyListGraph(0);
		ArrayList<Person> family = familyCreation();
		g1 = new FamilyListGraph(4, family);
	}

	protected ArrayList<Person> familyCreation(){
		family = new ArrayList<Person>();
		father = new Person(0, "Moo", 0);
		family.add(father);
		mother = new Person(1, "Wii", 1);
		mother.setSpouse(father);
		father.setSpouse(mother);
		family.add(mother);
		child = new Person(2, "Bää", 1);
		family.add(child);
		child.setFather(father);
		child.setMother(mother);
		father.setChildren(child);
		mother.setChildren(child);
		gfather = new Person(3, "Buzz", 0);
		family.add(gfather);
		father.setFather(gfather);
		gfather.setChildren(father);
		return family;
	}
	protected void tearDown() throws Exception {
	}

	public void addFamily(){
		g0.addMember(father);
		g0.addMember(mother);
		g0.addMember(child);
		g0.addMember(gfather);
	}
	
	public void testFamilyGraph(){
		addFamily();
		assertEquals(g0.getAntalMedlemmar(), 4);
		assertEquals(g1.getAntalMedlemmar(), 4);
		assertEquals(g0.getFamily(), family);
		assertEquals(g1.getFamily(), family);
		assertEquals(g0.getFamily().get(0), g1.getFamily().get(0));
		assertEquals(g0.getFamily().get(2).getMother().getName(), "Wii");
		
	}
}
