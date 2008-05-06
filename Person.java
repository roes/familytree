import java.util.ArrayList;

public class Person implements Comparable<Person>{
	public static enum Sex {
		MALE,
		FEMALE;
	}
	
	private int refnr;
	private String name;
	private Sex sex;
	private Date birthdate;
	private Date deathdate;
	private Person father;
	private Person mother;
	private Person spouse;
	private ArrayList<Person> children;
	private ArrayList<Person> exspouses;
	
	public Person(int refnr, String name, Sex sex){
		this.refnr = refnr;
		this.name = name;
		this.sex = sex;
		children = new ArrayList<Person>();
		exspouses = new ArrayList<Person>();
	}
	public Person(int refnr, String name, Sex sex, Date birthdate){
		this.refnr = refnr;
		this.name = name;
		this.sex = sex;
		this.birthdate = birthdate;
		children = new ArrayList<Person>();
		exspouses = new ArrayList<Person>();
	}
	public Person(int refnr, String name, Sex sex, Date birthdate, Date deathdate) {
		this.refnr = refnr;
		this.name = name;
		this.sex = sex;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		children = new ArrayList<Person>();
		exspouses = new ArrayList<Person>();
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public Date getDeathdate() {
		return deathdate;
	}
	public void setDeathdate(Date deathdate) {
		this.deathdate = deathdate;
	}
	/**
	 * @return Name including symbol for the sex.
	 */
	public String getSexName() {
		if (this.getSex() == Sex.MALE) {
			return "\u2642 " + name;
		} else if (this.getSex() == Sex.FEMALE) {
			return "\u2640 " + name;
		} else {
			return name;
		}
	}
	public String getName() {
			return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRefnr() {
		return refnr;
	}
	public void setRefnr(int refnr) {
		this.refnr = refnr;
	}
	public Sex getSex() {
		return sex;
	}
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	public boolean hasChildren(){
		if(children.size() == 0)
			return false;
		return true;
	}
	public ArrayList<Person> getChildren() {
		return children;
	}
	public void setChildren(Person child) {
		if (!children.contains(child)) {
			children.add(child);
		}
	}
	public boolean hasExspouses(){
		if(exspouses.size() == 0)
			return false;
		return true;
	}
	public ArrayList<Person> getExspouses() {
		return exspouses;
	}
	public void setExspouses(Person exspouse) {
		exspouses.add(exspouse);
	}
	public boolean hasFather(){
		if(father == null)
			return false;
		return true;
	}
	public Person getFather() {
		return father;
	}
	public void setFather(Person father) {
		this.father = father;
	}
	public boolean hasMother(){
		if(mother == null)
			return false;
		return true;
	}
	public Person getMother() {
		return mother;
	}
	public void setMother(Person mother) {
		this.mother = mother;
	}
	public boolean hasSpouse(){
		if(spouse == null)
			return false;
		return true;
	}
	public Person getSpouse() {
		return spouse;
	}
	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}
	public int compareTo(Person p) {
		return refnr - p.refnr;
	}
}
