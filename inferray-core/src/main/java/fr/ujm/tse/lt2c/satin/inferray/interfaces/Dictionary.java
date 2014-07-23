package fr.ujm.tse.lt2c.satin.inferray.interfaces;

/**
 * Interface for Dictionary
 * 
 * @author Jules Chevalier
 * 
 *         Documentation : Julien Subercaze
 * 
 */
public interface Dictionary {

	/**
	 * Add a resource identified by URI into the dictionary
	 * 
	 * @param s
	 *            URI
	 * @return the associated numerical value
	 */
	public abstract long add(String s);

	/**
	 * 
	 * @param index
	 *            the numerical value
	 * @return the associated URI
	 */
	public abstract String get(long index);

	/**
	 * 
	 * @param s
	 *            a URI already present in the dictionary
	 * @return the associated numerical value
	 */
	public abstract long get(String s);

	/**
	 * 
	 * @return the size of the dictionary
	 */
	public abstract long size();

	/**
	 * For the
	 * 
	 * @return
	 */
	public abstract int hashCode();

	public abstract boolean equals(Object obj);

	/**
	 * Human friendly output
	 * 
	 * @param c
	 *            the URI
	 * @return a human readable version
	 */
	public String printConcept(String c);

	

}
