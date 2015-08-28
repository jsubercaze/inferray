package fr.ujm.tse.satin.reasoner.sorting;

import java.util.Random;

import org.junit.Test;

import cern.colt.Arrays;

public class PartialSortTest {

	final static int WIDTH = 100;
	final static int ELEMS = 20;

	@Test
	public void test() {
		for (int i = 0; i < 10; i++) {
			final long[] random = randomArray();
			System.out.println(Arrays.toString(random));
			ParallelCountSort.partialCountingSort(random, 0, 14);
			System.out.println(Arrays.toString(random));
			System.out.println("----------------------------------");
		}
	}

	public long[] randomArray() {
		final Random r = new Random();
		final long[] randomArray = new long[ELEMS];
		for (int i = 0; i < ELEMS; i++) {
			randomArray[i] = r.nextInt(WIDTH);
		}
		return randomArray;
	}

}
