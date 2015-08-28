package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.hybrid.HybridInsertionCountingMSDSort;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.CountingSortLongPairOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.InsertionSortPairsOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.MSDLongPairsOptimAdaptativeOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.CountingSortLongPairSO;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.InsertionSortPairsSO;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.MSDLongPairsOptimAdaptativeSO;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

/**
 * Wrapper for sorting algorithms
 * 
 * 
 * @author Julien
 * 
 */
public class SortingUtils {

	public static void sortOS(final LongPairArrayList list,
			final SortingAlgorithm algorithm) {
		switch (algorithm) {
		case COUNTING:
			CountingSortLongPairOS
			.objectCountSort(list.elements(), list.size());
			break;
		case INSERTION:
			InsertionSortPairsOS.pairInsertionObjectSort(list.elements(),
					list.size());
			break;
		case MSD:
			MSDLongPairsOptimAdaptativeOS.sort(list.elements(), list.size());
			break;
		case HYBRID_IMD:
			HybridInsertionCountingMSDSort.sortOS(list.elements(), list.size());
			break;
		default:
			throw new UnsupportedOperationException();
		}

	}

	public static int sortSO(final LongPairArrayList list,
			final SortingAlgorithm algorithm) {
		switch (algorithm) {
		case COUNTING:
			return CountingSortLongPairSO.sort(list.elements(), list.size());
		case INSERTION:
			return InsertionSortPairsSO.pairInsertionSortNoDuplicate(
					list.elements(), list.size());
		case MSD:
			return MSDLongPairsOptimAdaptativeSO.sort(list.elements(),
					list.size());
		case HYBRID_IMD:
			return HybridInsertionCountingMSDSort.sortSO(list.elements(),
					list.size());
		default:
			throw new UnsupportedOperationException();
		}

	}

}
