package narrator.reader;

public class FabulaEdge {
	private final int id;
	private String type;
	
	public FabulaEdge(int id){
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}
}
