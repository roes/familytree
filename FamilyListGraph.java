import java.util.ArrayList;

public class FamilyListGraph {
	private int numMembers;
	private ArrayList<Person> family;
	
	public FamilyListGraph(int numMembers) {
		this.numMembers = numMembers;
		family = new ArrayList<Person>();
	}
	public FamilyListGraph(int numMembers, ArrayList<Person> family) {
		this.numMembers = numMembers;
		this.family = family;
	}
	public int getNumMembers() {
		return numMembers;
	}
	public void setNumMembers(int numMembers) {
		this.numMembers = numMembers;
	}
	public ArrayList<Person> getFamily() {
		return family;
	}
	public void setFamily(ArrayList<Person> family) {
		this.family = family;
	}
	public void addMember(Person p){
		family.add(p);
		numMembers++;
	}
	public Person getPerson(String name){
		for(Person p : family){
			if(p.getName() == name){
				return p;
			}
		}
		return null;
	}
}
