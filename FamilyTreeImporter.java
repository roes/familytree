import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FamilyTreeImporter {
	Scanner scan;
	HashMap<String, Pattern> gedcomPatterns;
	
	public FamilyTreeImporter(){
		gedcomPatterns = new HashMap<String, Pattern>();
		createGedcomPatterns();
	}
	
	public FamilyListGraph importGedcom(File file){
		ArrayList<Person> family = new ArrayList<Person>();
		int antal = 0;
		HashMap<Integer, Integer> ref = new HashMap<Integer, Integer>();
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(scan.hasNext()){
			String line = scan.nextLine();
			Matcher pm = gedcomPatterns.get("person").matcher(line);
			Matcher fm = gedcomPatterns.get("family").matcher(line);
			if(pm.matches()){
				int nr = Integer.parseInt(pm.group(1));
				ref.put(nr, antal);
				String name = "";
				Person.Sex sex = Person.Sex.MALE;
				int birth = 0;
				int death = 0;
				while(!scan.hasNext(gedcomPatterns.get("entity"))){
					line = scan.nextLine();
					Matcher nm = gedcomPatterns.get("name").matcher(line);
					Matcher sm = gedcomPatterns.get("sex").matcher(line);
					Matcher bm = gedcomPatterns.get("birth").matcher(line);
					Matcher dm = gedcomPatterns.get("death").matcher(line);
					if(nm.matches()){
						name = nm.group(1).replaceAll("/", "");
					}
					else if(sm.matches()){
						sex = sm.group(1).equals("M") ? Person.Sex.MALE : Person.Sex.FEMALE;
					}
					else if(bm.matches()){
						while(!scan.hasNext(gedcomPatterns.get("topic"))){
							line = scan.nextLine();
							Matcher datem = gedcomPatterns.get("date").matcher(line);
							if(datem.matches()){
								birth = createDate(datem.group(1));
							}
						}
					}
					else if(dm.matches()){
						while(!scan.hasNext(gedcomPatterns.get("topic"))){
							line = scan.nextLine();
							Matcher datem = gedcomPatterns.get("date").matcher(line);
							if(datem.matches()){
								death = createDate(datem.group(1));
							}
						}
					}
				}
				Person person = new Person(antal, name, sex, birth, death);
				family.add(person);
				antal++;
			}
			else if(fm.matches()){
				int xmarrige = -1;
				int husband = -1;
				int wife = -1;
				ArrayList<Integer> children = new ArrayList<Integer>();
				while(!scan.hasNext(gedcomPatterns.get("entity"))){
					line = scan.nextLine();
					Matcher mm = gedcomPatterns.get("marrige").matcher(line);
					Matcher divm = gedcomPatterns.get("divorce").matcher(line);
					Matcher hm = gedcomPatterns.get("husband").matcher(line);
					Matcher wm = gedcomPatterns.get("wife").matcher(line);
					Matcher cm = gedcomPatterns.get("child").matcher(line);
					if(mm.matches()){
						xmarrige = 0;
					}
					else if(divm.matches()){
						xmarrige = 1;
					}
					else if(hm.matches()){
						husband = Integer.parseInt(hm.group(1));
					}
					else if(wm.matches()){
						wife = Integer.parseInt(wm.group(1));
					}
					else if(cm.matches()){
						children.add(Integer.parseInt(cm.group(1)));
					}
				}
				Person h;
				Person w;
				ArrayList<Person> c = new ArrayList<Person>();
				if(!(xmarrige == -1)){
					h = family.get(ref.get(husband));
					w = family.get(ref.get(wife));
					for(int child : children){
						c.add(family.get(ref.get(child)));
					}
					if(xmarrige == 1){
						h.setExspouses(w);
						w.setExspouses(h);
					}
					else{
						h.setSpouse(w);
						w.setSpouse(h);
					}
					for(Person child : c){
						child.setFather(h);
						child.setMother(w);
						h.setChildren(child);
						w.setChildren(child);
					}
				}
				else if(husband == -1){
					w = family.get(ref.get(wife));
					for(int child : children){
						c.add(family.get(ref.get(child)));
					}
					for(Person child : c){
						child.setMother(w);
						w.setChildren(child);
					}
				}
				else if(wife == -1){
					h = family.get(ref.get(husband));
					for(int child : children){
						c.add(family.get(ref.get(child)));
					}
					for(Person child : c){
						child.setFather(h);
						h.setChildren(child);
					}
				}
			}
		}
		return new FamilyListGraph(antal, family);
	}
	public int createDate(String s){
		String[] date = s.split("\\s");
		date[1] = getMonth(date[1]);
		StringBuilder bd = new StringBuilder();
		bd.append(date[2]);
		bd.append(date[1].length() == 1 ? "0" + date[1] : date[1]);
		bd.append(date[0].length() == 1 ? "0" + date[0] : date[0]);
		return Integer.parseInt(bd.toString());
	}
	public String getMonth(String s){
		if(s.equals("JAN"))
			return "1";
		if(s.equals("FEB"))
			return "2";
		if(s.equals("MAR"))
			return "3";
		if(s.equals("APR"))
			return "4";
		if(s.equals("MAY"))
			return "5";
		if(s.equals("JUN"))
			return "6";
		if(s.equals("JUL"))
			return "7";
		if(s.equals("AUG"))
			return "8";
		if(s.equals("SEP"))
			return "9";
		if(s.equals("OCT"))
			return "10";
		if(s.equals("NOV"))
			return "11";
		return "12";
	}
	public void createGedcomPatterns(){
		gedcomPatterns.put("person", Pattern.compile("0\\s@I(\\d+)@\\sINDI"));
		gedcomPatterns.put("family", Pattern.compile("0\\s@\\w+@\\sFAM"));
		gedcomPatterns.put("name", Pattern.compile("1\\sNAME\\s(.+)"));
		gedcomPatterns.put("sex", Pattern.compile("1\\sSEX\\s(\\w)"));
		gedcomPatterns.put("birth", Pattern.compile("1\\sBIRT.*"));
		gedcomPatterns.put("date", Pattern.compile("2\\sDATE\\s(.+)"));
		gedcomPatterns.put("death", Pattern.compile("1\\sDEAT.*"));
		gedcomPatterns.put("entity", Pattern.compile("0.*"));
		gedcomPatterns.put("topic", Pattern.compile("1.*"));
		gedcomPatterns.put("marrige", Pattern.compile("1\\sMARR.*"));
		gedcomPatterns.put("divorce", Pattern.compile("1\\sDIV.*"));
		gedcomPatterns.put("husband", Pattern.compile("1\\sHUSB\\s@I(\\d+).*"));
		gedcomPatterns.put("wife", Pattern.compile("1\\sWIFE\\s@I(\\d+).*"));
		gedcomPatterns.put("child", Pattern.compile("1\\sCHIL\\s@I(\\d+).*"));
		
	}
}
