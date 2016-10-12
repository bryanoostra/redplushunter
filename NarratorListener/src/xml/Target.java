package xml;

public class Target {
	public CharacterX character;
	public Location location;
	public String text;
	
	public String toString(){
		String result ="";
		if (text!=null) result+=text;
		if (character!=null) result+=character.id;
		if (location!=null) result+=location.id;
		return result;
	}
	
	public Target(String character){
		CharacterX tmpChar = new CharacterX();
		this.character = tmpChar;
		tmpChar.id = character;
	}
}
