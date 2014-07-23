package fr.ujm.tse.satin.inferray.test.dictionary;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.triplestore.SortedCacheObliviousTripleStore;

public class NodeDictionaryTest {
	/**
	 * Dictionary under test
	 */
	NodeDictionary dictionary;

	@Before
	public void init() {
		final CacheTripleStore ts = new SortedCacheObliviousTripleStore(50);
		dictionary = new NodeDictionary(ts);
		dictionary.add("http://test.com/resource");
		dictionary.addProperty("http://test.com/property");
	}

	@Test
	public void testCountProperties() {
		assertEquals(56, dictionary.countProperties());
	}

	@Test
	public void testAdd() {
		final long rs = dictionary.getCntResources();
		dictionary.add("http://gros.com");
		assertEquals(rs + 1, dictionary.getCntResources());
	}

	@Test
	public void testAddProperty() {
		final long rs = dictionary.countProperties();
		dictionary.addProperty("http://gros.com/property");
		assertEquals(rs + 1, dictionary.countProperties());
	}

	@Test(expected=NullPointerException.class)
	public void testGetLong() {
		final String s = dictionary.get(NodeDictionary.SPLIT_INDEX+1);
		assertNotNull(s);
	}

	@Test
	public void testGetString() {
		final long s = dictionary.get("http://test.com/resource");
		assertThat(-1L,not(s));
	}

	@Test
	public void testSize() {
		assertEquals(2, dictionary.size()-70);
	}

	@Test
	public void testGetCntResources() {
		assertEquals(NodeDictionary.SPLIT_INDEX+16L, dictionary.getCntResources());
	}





}
