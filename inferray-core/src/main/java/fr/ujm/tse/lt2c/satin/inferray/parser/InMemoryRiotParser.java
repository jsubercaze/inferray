package fr.ujm.tse.lt2c.satin.inferray.parser;

import org.apache.jena.riot.RDFDataMgr;

/**
 * Read data from String stored in-memory. This parser is here for benchmark
 * purpose, to avoid I/O issues when reading from disk.
 * 
 * Takes a String containing the ontology, transforms into a stream that is
 * passed to {@link RDFDataMgr} then follow the same process as
 * {@link FullFileRIOTParser}
 * 
 * @author Julien
 * 
 */
public class InMemoryRiotParser {
	// FIXME complete class
}
