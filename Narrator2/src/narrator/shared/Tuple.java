package narrator.shared;

public class Tuple<X, Y> { 
	public final X x;
	public final Y y; 
	
	public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	} 
	
	@Override
	public boolean equals(Object other){
		if (other.getClass() != this.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Tuple t = (Tuple) other;
		return t.x.equals(this.x) && t.y.equals(this.y);
	}
	
	@Override
	public String toString(){
		return x+", "+y;
	}
	
	@Override
	public int hashCode(){
		return x.hashCode() + y.hashCode();
	}
} 