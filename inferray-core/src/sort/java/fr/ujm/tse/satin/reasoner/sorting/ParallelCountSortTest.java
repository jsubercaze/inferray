package fr.ujm.tse.satin.reasoner.sorting;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class ParallelCountSortTest {

	final static int WIDTH = 100_000;
	final static int ELEMS = 100_000_000;

	@Test
	public void test() {
		long bl = 0;
		long fj = 0, sq = 0;
		for (int i = 0; i < 100; i++) {
			final int[] random = randomArray();
			final int[] random1 = Arrays.copyOf(random, random.length);

			final int[] random2 = Arrays.copyOf(random, random.length);
			final long t1 = System.currentTimeMillis();
			ParallelCountSort.branchlesscountingSort1(random);
			bl += (System.currentTimeMillis() - t1);
			final long t2 = System.currentTimeMillis();
			ParallelCountSort.parallelCountSort(random1);
			fj += (System.currentTimeMillis() - t2);
			final long t3 = System.currentTimeMillis();
			ParallelCountSort.countingSort(random2);
			sq += (System.currentTimeMillis() - t3);
			// assertArrayEquals(random, randomCopy);
		}
		System.out.println("BL " + bl);
		System.out.println("FJ " + fj);
		System.out.println("sq " + sq);
	}

	public int[] randomArray() {
		final Random r = new Random();
		final int[] randomArray = new int[ELEMS];
		for (int i = 0; i < ELEMS; i++) {
			randomArray[i] = r.nextInt(WIDTH);
		}
		return randomArray;
	}

}
