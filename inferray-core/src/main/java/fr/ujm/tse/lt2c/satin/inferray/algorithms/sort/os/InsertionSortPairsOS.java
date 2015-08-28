package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os;

/**
 * Insertion sort for pairs on object,subject order
 * 
 * @author Julien
 * 
 */
public class InsertionSortPairsOS {

	public static void pairInsertionObjectSort(final long[] elements,
			final int size) {
		for (int i = 3; i < size; i += 2) {
			int j = i;
			while ((j > 1)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 3] > elements[j - 1]))) {
				swap(elements, j, j - 2);
				swap(elements, j - 1, j - 3);
				j -= 2;
			}
		}
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
