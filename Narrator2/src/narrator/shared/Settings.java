package narrator.shared;

import natlang.rdg.libraries.LibraryConstants;

public class Settings implements LibraryConstants{
	public static boolean FLASHBACK 		= true;
	public static boolean INTRODUCTION		= true;
	public static boolean SHOWINIDOC 		= false;
	public static boolean SHOWFINALDOC 		= false;
	public static boolean SHOWINIRDG 		= false;
	public static boolean SHOWFINALRDG 		= false;
	public static boolean SHOWDTS			= false;
	public static boolean REFEXP 			= true;
	public static boolean BRIDGING 			= true;
	public static boolean ADDADJECTIVES 	= true;
	public static boolean TRANSFORMRELS 	= true;	
	public static boolean BGIS 				= true;
	public static boolean ADDDISCMARKERS 	= true;
	public static boolean TRANSFORMSTATES 	= true;
	public static boolean MOOD 				= true;
	public static boolean BRANCH 			= false;
	public static boolean ELLIPT			= false;
	public static boolean PASTTENSE			= false;
	public static String LANGUAGE			= "NL";
	public static String FOCALIZEDCHARACTER = "bystander2";
	public static boolean FOCALIZATION		= false;
	public static String PERSPECTIVE		= THIRD;
	public static double PRONOUNCHOICE  	= 1.0;
	
	public static void setPastTense(){
		PASTTENSE = true;
	}
	
	public static void setPresentTense(){
		PASTTENSE = false;
	}
}
