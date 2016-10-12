package narrator.reader;

/**
 * A property of a character, consisting of a type and value
 * 
 * @author Nanda Slabbers
 *
 */
public class Property 
{
	private String prop;
	private String value;
	
	/**
	 * Creates a property element
	 * @param p
	 * @param v
	 */
	public Property(String p, String v)
	{
		prop = p;
		value = v;
	}
	
	/**
	 * Returns the kind of property
	 */
	public String getProp()
	{
		return prop;
	}
	
	/**
	 * Returns the value of the property
	 */
	public String getValue()
	{
		return value;
	}
}
