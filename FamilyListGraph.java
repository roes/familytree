import java.util.ArrayList;

public class FamilyListGraph {
	private int numMembers;
	private ArrayList<Person> family;
	
	public FamilyListGraph(int antalMedlemmar) {
		this.numMembers = antalMedlemmar;
		family = new ArrayList<Person>();
	}
	public FamilyListGraph(int antalMedlemmar, ArrayList<Person> family) {
		this.numMembers = antalMedlemmar;
		this.family = family;
	}
	public int getNumMembers() {
		return numMembers;
	}
	public void setNumMembers(int antalMedlemmar) {
		this.numMembers = antalMedlemmar;
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
