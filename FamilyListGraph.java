import java.util.ArrayList;

public class FamilyListGraph {
	private int antalMedlemmar;
	private ArrayList<Person> family;
	
	public FamilyListGraph(int antalMedlemmar) {
		this.antalMedlemmar = antalMedlemmar;
		family = new ArrayList<Person>();
	}
	public FamilyListGraph(int antalMedlemmar, ArrayList<Person> family) {
		this.antalMedlemmar = antalMedlemmar;
		this.family = family;
	}
	public int getAntalMedlemmar() {
		return antalMedlemmar;
	}
	public void setAntalMedlemmar(int antalMedlemmar) {
		this.antalMedlemmar = antalMedlemmar;
	}
	public ArrayList<Person> getFamily() {
		return family;
	}
	public void setFamily(ArrayList<Person> family) {
		this.family = family;
	}
	public void addMember(Person p){
		family.add(p);
		antalMedlemmar++;
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
