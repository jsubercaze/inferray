package fr.ujm.tse.satin.reasoner.sorting;

/*
 RadixSort.java :  Sorts 32-bit integers with O(n*k) runtime performance.
 Where k is the max number of digits of the numbers being
 sorted.
 (i.e. k=10 digits for 32-bit integers.)

 Copyright (C) 2013 Yeison Rodriguez ( github.com/yeison )

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.util.Arrays;
import java.util.Random;

import fr.ujm.tse.satin.reasoner.sorting.pairs.MSDIntPairs;

public class SortingBenchmark {

	// Main function to test performance sorting 1 million integers.
	// Results in about 220 ms on a 2.3 Ghz Core i5 processor w/4GB 1333 Mhz RAM
	public static void main(final String[] args) {
		final int SIZE = 5_000_000;
		final int RANGE = 1_000_000;

		final Random r = new Random();
		final int[] test = new int[SIZE];

		for (int i = 0; i < SIZE; i++) {
			test[i] = r.nextInt(RANGE);
		}

		final int[] test2 = Arrays.copyOf(test, test.length);
		final int[] test3 = Arrays.copyOf(test, test.length);
		final int[] test4 = Arrays.copyOf(test, test.length);
		final int[] test5 = Arrays.copyOf(test, test.length);
		// System.out.println(Arrays.toString(test));

		long start = System.currentTimeMillis();
		Arrays.sort(test);
		// System.out.println(Arrays.toString(test));
		long end = System.currentTimeMillis();
		System.out.println("Java / Parallel " + (end - start));

		start = System.currentTimeMillis();
		//Arrays.sort(test2);

		// ParallelCountSort.countingSort(test2);
		end = System.currentTimeMillis();
		System.out.println("Java / Arrays.sort " + (end - start));

		start = System.currentTimeMillis();
		ParallelCountSort.countingSort(test3);
		end = System.currentTimeMillis();
		// System.out.println(Arrays.toString(test3));
		System.out.println("Counting Sort " + (end - start));

		start = System.currentTimeMillis();
		LSD.sort(test4);
		end = System.currentTimeMillis();
		// System.out.println(Arrays.toString(sorted));
		System.out.println("LSD " + (end - start));
		start = System.currentTimeMillis();
		MSDIntPairs.sort(test5);
		end = System.currentTimeMillis();
		// System.out.println(Arrays.toString(sorted));
		System.out.println("MSD " + (end - start));




		System.out.println(Arrays.equals(test, test2));
		System.out.println(Arrays.equals(test, test3));
		System.out.println(Arrays.equals(test, test4));
		System.out.println(Arrays.equals(test, test5));
		// for (final Integer i : test){
		// System.out.println(i);
		// }

	}

}