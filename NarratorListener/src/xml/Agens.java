package xml;

public class Agens {
	public CharacterX character;
	public String text;
	
	public String toString(){
		String result ="";
		if (text!=null) result+=text;
		if (character!=null) result+=character.id;
		return result;
	}
	
	public Agens(String character){
		CharacterX tmpChar = new CharacterX();
		this.character = tmpChar;
		tmpChar.id = character;
	}
}
