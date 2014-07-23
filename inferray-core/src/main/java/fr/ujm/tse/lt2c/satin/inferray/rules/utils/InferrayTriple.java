package fr.ujm.tse.lt2c.satin.inferray.rules.utils;

/**
 * Simple Bean for triples
 * 
 * @author Julien Subercaze
 * 
 *         Jan 2014
 */
public class InferrayTriple {
	/**
	 * Complete URI of the subject
	 */
	String subject;
	/**
	 * Complete URI of the property
	 */
	String property;
	/**
	 * Complete URI of the object
	 */
	String object;

	public InferrayTriple(String subject, String property, String object) {
		super();
		this.subject = subject;
		this.property = property;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "InferrayTriple [subject=" + subject + ", property=" + property
				+ ", object=" + object + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InferrayTriple other = (InferrayTriple) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

}
