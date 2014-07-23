package fr.ujm.tse.lt2c.satin.inferray.triplestore;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class SortingThread implements Runnable {

	LongPairArrayList toSort;

	public SortingThread(final LongPairArrayList toSort) {
		this.toSort = toSort;
	}

	@Override
	public void run() {
		toSort.totalSortingNoDuplicate();
	}

}
