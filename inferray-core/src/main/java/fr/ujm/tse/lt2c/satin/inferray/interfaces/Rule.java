package fr.ujm.tse.lt2c.satin.inferray.interfaces;

import org.apache.log4j.Logger;


public interface Rule extends Runnable{
	
	public void run();
	
	public int hashCode();
	
	public boolean equals(Object obj);
	
	public Logger getLogger();

}
