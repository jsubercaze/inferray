package fr.ujm.tse.satin.reasoner.sorting.longs;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Julien
 * 
 */
public class MainLong {

	private static final int SIZE = 50;
	private static final int RANGE = 1_000_000;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Random r = new Random();
		final long[] test = new long[SIZE];

		for (int i = 0; i < SIZE; i++) {
			test[i] = (long) Integer.MAX_VALUE + r.nextInt(RANGE);
		}

		final long[] test2 = Arrays.copyOf(test, test.length);
		final long[] test3 = Arrays.copyOf(test, test.length);
		final long[] test4 = Arrays.copyOf(test, test.length);

		long start = System.currentTimeMillis();
		Arrays.sort(test);
		// System.out.println(Arrays.toString(test));
		long end = System.currentTimeMillis();
		System.out.println("Java / Sort " + (end - start));

		start = System.currentTimeMillis();
		LongMSD.sort(test2);
		end = System.currentTimeMillis();
		System.out.println("Java / MSD " + (end - start));

		start = System.currentTimeMillis();
		LongCounting.countingSort(test3);
		end = System.currentTimeMillis();
		System.out.println("Java / Counting " + (end - start));

		start = System.currentTimeMillis();
		LongCounting.singleInsertionSort(test4);
		end = System.currentTimeMillis();
		System.out.println("Java / Counting Alternate" + (end - start));

		System.out.println(Arrays.equals(test, test2));
		System.out.println(Arrays.equals(test, test3));
		System.out.println(Arrays.equals(test, test4));
		// System.out.println(Arrays.toString(test));
		// System.out.println(Arrays.toString(test2));
	}
}
