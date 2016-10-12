package natlang.rdg.model;

/**
 * A detail about a certain element in a plot element
 * 
 * @author Nanda
 *
 */
public class Detail
{
	private String subject;
	private String detail;
	
	/**
	 * Creates an empty detail object
	 *
	 */
	public Detail()
	{
		subject = "";
		detail = "";		
	}
	
	/**
	 * Creates a detail object
	 * @param sbj
	 * @param dtl
	 */
	public Detail(String sbj, String dtl)
	{
		subject = sbj;
		detail = dtl;
	}
	
	/**
	 * Returns the subject
	 */
	public String getSubject()
	{
		return subject;
	}
	
	/**
	 * Returns the detail
	 */
	public String getDetail()
	{
		return detail;
	}
	
	/**
	 * Sets the subject
	 * @param s
	 */
	public void setSubject(String s)
	{
		subject = s;
	}
	
	/**
	 * Sets the detail
	 * @param d
	 */
	public void setDetail(String d)
	{
		detail = d;
	}
}
