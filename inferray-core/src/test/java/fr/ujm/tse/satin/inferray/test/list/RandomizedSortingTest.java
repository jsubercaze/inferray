package fr.ujm.tse.satin.inferray.test.list;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class RandomizedSortingTest {

	static final int TOTAL_SIZE = 25;
	static final int MAX_VALUE = 100;
	static final int TESTS = 50;

	@Test
	public void testCountSortFromTo() {
		for (int j = 0; j < TESTS; j++) {
			final LongPairArrayList l1 = new LongPairArrayList(TOTAL_SIZE,SortingAlgorithm.MSD);
			final LongPairArrayList l2 = new LongPairArrayList(TOTAL_SIZE,SortingAlgorithm.MSD);
			final Random r = new Random();
			for (int i = 0; i < TOTAL_SIZE; i++) {
				final int s = r.nextInt(MAX_VALUE);
				l1.add(s);
				l2.add(s);
			}
			//System.out.println(Arrays.toString(l1.elements()));
			// Randomly select where to start and stop sorting
			final int from = r.nextInt(TOTAL_SIZE / 2);
			final int to = TOTAL_SIZE / 2 + r.nextInt(TOTAL_SIZE / 2 - 1);
			l1.partialCountinSort(from, to);
			Arrays.sort(l2.elements(), from, to);
			//			System.out.println("From " + from);
			//			System.out.println("To " + to);
			//			System.out.println(Arrays.toString(l1.elements()));
			//			System.out.println(Arrays.toString(l2.elements()));
			assertArrayEquals(l1.elements(), l2.elements());
		}
	}

}
