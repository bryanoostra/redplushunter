package xml;

public class Instrument {
	public CharacterX character;
	public String text;
	
	public String toString(){
		String result ="";
		if (text!=null) result+=text;
		if (character!=null) result+=character.id;
		return result;
	}
}
