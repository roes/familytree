/**
 * This class holds a date or date range. The date can be partial.
 * It does very little validation of the input dates.
 */
public class Date {
	enum Type {
		SIMPLE,
		AFTER,
		BEFORE,
		RANGE,
		PERIOD
	}
	private Type type;	// If this is not a range or period year[1], month[1] & day[1] are unused.
	private int [] year, month, day; // 0 means unknown
	
	/**
	 * An empty date.
	 */
	public Date() {
		this.year = new int[2];
		this.month = new int[2];
		this.day = new int[2];
	}
	
	/**
	 * Constructor for simple dates.
	 * @param year
	 * @param month
	 * @param day
	 */
	public Date(int year, int month, int day) {
		this();
		this.type = Type.SIMPLE;
		this.year[0] = year;
		if ((1 <= month) && (month <= 12)) {
			this.month[0] = month;
		}
		if ((1 <= day) && (day <= 31)) {
			this.day[0] = day;
		}
	}

	/**
	 * Constructor for date ranges.
	 * @param fromYear
	 * @param fromMonth
	 * @param fromDay
	 * @param toYear
	 * @param toMonth
	 * @param toDay
	 */
	public Date(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
		this();
		this.type = Type.RANGE;
		this.year[0] = fromYear;
		if ((1 <= fromMonth) && (fromMonth <= 12)) {
			this.month[0] = fromMonth;
		}
		if ((1 <= fromDay) && (fromDay <= 31)) {
			this.day[0] = fromDay;
		}
		this.year[1] = toYear;
		if ((1 <= toMonth) && (toMonth <= 12)) {
			this.month[1] = toMonth;
		}
		if ((1 <= toDay) && (toDay <= 31)) {
			this.day[1] = toDay;
		}
	}
	
	/**
	 * Generate a date from a gedcom date string.
	 * @param s
	 */
	public Date(String s) {
		this();
		if (s.startsWith("ABT") || s.startsWith("CAL") || s.startsWith("EST")) {
			this.type = Type.SIMPLE;
			s = s.substring(4);
		} else if (s.startsWith("BEF")) {
			this.type = Type.BEFORE;
			s = s.substring(4);
		} else if (s.startsWith("AFT")) {
			this.type = Type.AFTER;
			s = s.substring(4);
		} else if (s.startsWith("BET")) {
			this.type = Type.RANGE;
			s = s.substring(4);
		} else {
			this.type = Type.SIMPLE;
		}
		if (this.type == Type.RANGE) {
        	String [] dates = s.split(" AND ");
        	this.parseDate(dates[0], 0);
        	this.parseDate(dates[1], 1);
		} else {
			this.parseDate(s, 0);
		}
	}
	
	/**
	 * Used to parse a single date and store it.
	 * @param s Date to parse.
	 * @param pos Position to store date in.
	 */
	private void parseDate(String s, int pos) {
		String [] tokens = s.split(" ");
		
		if (tokens.length == 3) {
			this.year[pos] = parseYear(tokens[2]);
			this.month[pos] = parseMonth(tokens[1]);
			this.day[pos] = parseInt(tokens[0]);
		} else if (tokens.length == 2) {
			this.year[pos] = parseYear(tokens[1]);
			this.month[pos] = parseMonth(tokens[0]);
		} else if (tokens.length == 1) {
			this.year[pos] = parseYear(tokens[0]);
		}
	}
	
	/**
	 * Try to parse an integer, return 0 on failure.
	 * @param s String to parse.
	 */
	private static int parseInt(String s) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return 0;
			}
	}
	
	private static int parseYear(String s) {
		String y = s.split("/")[0].split(" ")[0];
		// Fixme, won't detect B.C. dates
		if (s.trim().endsWith("B.C.")) {
			return -parseInt(y);
		} else {
			return parseInt(y);
		}
	}
	
	private static int parseMonth(String s) {
		if(s.equals("JAN"))
			return 1;
		if(s.equals("FEB"))
			return 2;
		if(s.equals("MAR"))
			return 3;
		if(s.equals("APR"))
			return 4;
		if(s.equals("MAY"))
			return 5;
		if(s.equals("JUN"))
			return 6;
		if(s.equals("JUL"))
			return 7;
		if(s.equals("AUG"))
			return 8;
		if(s.equals("SEP"))
			return 9;
		if(s.equals("OCT"))
			return 10;
		if(s.equals("NOV"))
			return 11;
		if(s.equals("DEC"))
			return 12;
		return 0;
	}
	
	private String toString(int num) {
		String ret = "";
		if ((this.year[0] == 0) && (this.month[0] == 0) && (this.day[0] == 0)) {
			return "unknown";
		}
		if (this.year[num] != 0) {
			ret += String.valueOf(this.year[num]);
		}
		if (this.month[num] != 0) {
			if (ret.length() > 0)
				ret += " ";
			if (this.month[num] == 1) {
				ret += "January";
			} else if (this.month[num] == 2) {
				ret += "February";
			} else if (this.month[num] == 3) {
				ret += "March";
			} else if (this.month[num] == 4) {
				ret += "April";
			} else if (this.month[num] == 5) {
				ret += "May";
			} else if (this.month[num] == 6) {
				ret += "June";
			} else if (this.month[num] == 7) {
				ret += "July";
			} else if (this.month[num] == 8) {
				ret += "August";
			} else if (this.month[num] == 9) {
				ret += "September";
			} else if (this.month[num] == 10) {
				ret += "October";
			} else if (this.month[num] == 11) {
				ret += "November";
			} else if (this.month[num] == 12) {
				ret += "December";
			} else {
				return "";
			}
			if (this.day[num] != 0) {
				ret += " ";
				ret += String.valueOf(this.day[num]);
			}
		}
		return ret;
	}
	
	public String toString() {
		if (this.type == Type.SIMPLE) {
			return this.toString(0);
		} else if (this.type == Type.BEFORE) {
			return "Before " + this.toString(0);
		} else if (this.type == Type.AFTER) {
			return "After " + this.toString(0);
		} else if (this.type == Type.RANGE) {
			return "Between " + this.toString(0) + " and " + this.toString(1);
		} else if (this.type == Type.PERIOD) {
			return "From " + this.toString(0) + " to " + this.toString(1);
		} else {
			return "";
		}
	}

	public int getDay() {
		return day[0];
	}

	public void setDay(int day) {
		this.day[0] = day;
	}

	public int getMonth() {
		return month[0];
	}

	public void setMonth(int month) {
		this.month[0] = month;
	}

	public int getYear() {
		return year[0];
	}

	public void setYear(int year) {
		this.year[0] = year;
	}

}
