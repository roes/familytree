import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Queue;
import java.util.LinkedList;

public class FreeBaseImporter {
	Scanner scan;
	HashMap<String, Pattern> freebasePatterns;
    HashMap<String, Integer> ref;
	ArrayList<Person> family;
	Queue<String> personQueue;
	ArrayList<String []> pc; // Parent-Child relations
	
	public FreeBaseImporter() {
		freebasePatterns = new HashMap<String, Pattern>();
		createFreebasePatterns();
	}
	
	public FamilyListGraph importFreebase(String id){
		ref = new HashMap<String, Integer>();
		family = new ArrayList<Person>();
		pc = new ArrayList<String []>();
		personQueue = new LinkedList<String>();
		personQueue.add(id);
		while (!personQueue.isEmpty()) {
			String pid = personQueue.remove();
			if (!ref.containsKey(pid)) {
    			int ppos = family.size();
    			family.add(readPerson(pid));
    			ref.put(pid, ppos);
			}
		}
		for (String [] rel : pc) {
			Person p, c;
			p = family.get(ref.get(rel[0]));
			c = family.get(ref.get(rel[1]));
			
			if (p.getSex() == Person.Sex.MALE) {
				c.setFather(p);
			} else {
				c.setMother(p);
			}
			p.setChildren(c);
		}
		return new FamilyListGraph(family.size(), family);
	}
	
	public Person readPerson(String id) {
		try {
			URL url = new URL("http://www.freebase.com/api/service/mqlread?q={%20%22query%22%20:%20[%20{%20%22children%22%20:%20[%20{}%20],%20%22gender%22%20:%20null,%20%22id%22%20:%20%22" + id + "%22,%20%22name%22%20:%20null,%20%22parents%22%20:%20[%20{}%20],%20%22type%22%20:%20%22/people/person%22%20}%20]%20}");
    		scan = new Scanner(url.openStream());
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}
		String name = null;
		Person.Sex sex = null;
		
		while(scan.hasNextLine()){
			String line = scan.nextLine();
			Matcher nm = freebasePatterns.get("name").matcher(line);
			Matcher cm = freebasePatterns.get("children").matcher(line);
			Matcher pm = freebasePatterns.get("parents").matcher(line);
			Matcher mm = freebasePatterns.get("male").matcher(line);
			Matcher fm = freebasePatterns.get("female").matcher(line);
			if (mm.matches()) {
				sex = Person.Sex.MALE;
			}
			if (fm.matches()) {
				sex = Person.Sex.FEMALE;
			}
			if (nm.matches()) {
				name = nm.group(1);
			}
			
			if (cm.matches()){
				int nest = 0;
				while(scan.hasNextLine()) {
					line = scan.nextLine();
					if (freebasePatterns.get("beginlist").matcher(line).matches()) {
						nest++;
					}
					if (freebasePatterns.get("endlist").matcher(line).matches()) {
						nest--;
						if (nest<0)
							break;
					}
					Matcher im = freebasePatterns.get("id").matcher(line);
					if (im.matches()){
						System.out.println("Child");
						System.out.println(im.group(1));
						personQueue.add(im.group(1));
						// Add the relation
						String [] rel = new String [2];
						rel[0] = id;
						rel[1] = im.group(1);
						pc.add(rel);
					}
				}
			}
			if (pm.matches()){
				int nest = 0;
				while(scan.hasNextLine()) {
					line = scan.nextLine();
					if (freebasePatterns.get("beginlist").matcher(line).matches()) {
						nest++;
					}
					if (freebasePatterns.get("endlist").matcher(line).matches()) {
						nest--;
						if (nest<0)
							break;
					}
					System.out.println("Parent debug");
					Matcher im = freebasePatterns.get("id").matcher(line);
					if (im.matches()){
						System.out.println("Parent");
						System.out.println(im.group(1));
						personQueue.add(im.group(1));
						// Add the relation
						String [] rel = new String [2];
						rel[0] = im.group(1);
						rel[1] = id;
						pc.add(rel);
					}
				}
			}
		}
		System.out.println(name);
		System.out.println("--------------------");
		return new Person(family.size(), name, sex, new Date(), new Date());
	}
	
	public void createFreebasePatterns() {
		freebasePatterns.put("name", Pattern.compile("\\s*\"name\"\\s*:\\s*\"(.*)\",?\\s*"));
		freebasePatterns.put("id", Pattern.compile("\\s*\"id\"\\s*:\\s*\"(.*)\",?\\s*"));
		freebasePatterns.put("male", Pattern.compile("\\s*\"gender\"\\s*:\\s*\"Male\",?\\s*"));
		freebasePatterns.put("female", Pattern.compile("\\s*\"gender\"\\s*:\\s*\"Female\",?\\s*"));
		freebasePatterns.put("children", Pattern.compile("\\s*\"children\"\\s*:\\s*\\[\\s*"));
		freebasePatterns.put("parents", Pattern.compile("\\s*\"parents\"\\s*:\\s*\\[\\s*"));
		freebasePatterns.put("beginlist", Pattern.compile(".*\\[.*"));
		freebasePatterns.put("endlist", Pattern.compile(".*\\].*"));
		freebasePatterns.put("enditem", Pattern.compile(".*},?.*"));
	}
}
