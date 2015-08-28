package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so;

public class InsertionSortPairsSO {
	/**
	 * 
	 * @param elements
	 * @param size initial size before sorting and duplicate removal
	 * @return the new size of the array
	 */
	public static int pairInsertionSortNoDuplicate(final long[] elements,
			int size) {

		for (int i = 2; i < size - 1; i += 2) {
			int j = i;

			while ((j > 0)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 1] > elements[j + 1]))) {
				swap(elements, j, j - 2);
				swap(elements, j + 1, j - 1);
				j -= 2;
			}
		}
		// One pass for removing duplicates,same as quicksort
		long previouss = -1;
		long previouso = -1;
		int shifting = 0;
		for (int i = 0; i < size; i++) {
			final long s = elements[i];
			final long o = elements[++i];
			if (s == previouss && o == previouso) {
				shifting++;
				continue;
			}
			if (shifting > 0) {
				elements[(i - 1) - (shifting * 2)] = s;
				elements[i - (shifting * 2)] = o;
			}
			previouss = s;
			previouso = o;
		}
		size -= shifting * 2;
		return size;
	}

	private static void swap(final long[] elements, final int posa,
			final int posb) {
		if (posa == posb) {
			return;
		}
		final long tmp = elements[posa];
		elements[posa] = elements[posb];
		elements[posb] = tmp;
	}
}
