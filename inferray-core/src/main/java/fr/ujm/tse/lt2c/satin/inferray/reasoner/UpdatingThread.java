package fr.ujm.tse.lt2c.satin.inferray.reasoner;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;

/**
 * Thread handling the update of a given property between various triple stores
 * 
 * @author Julien
 * 
 */
public class UpdatingThread implements Runnable {

	private final Logger logger = Logger.getLogger(UpdatingThread.class);

	/**
	 * Property to merge
	 */
	int property;
	/**
	 * Main triple store
	 */
	CacheTripleStore mainTripleStore;
	/**
	 * Future new triples
	 */
	CacheTripleStore newTriples;
	/**
	 * Triples previously inferred
	 */
	CacheTripleStore outputTriples;
	/**
	 *
	 */
	NodeDictionary dictionary;
	/**
	 * In case of binding
	 */
	private final CacheTripleStore exportTriples;
	/**
	 * Export new triples ?
	 */
	private final boolean export;

	/**
	 * Faster way to update the triple stores - With one counting sort and one
	 * insertion//duplicate parallel
	 * 
	 * @param property
	 * @param mainTriples
	 * @param newTriples
	 * @param outputTriples
	 * @param dictionary
	 */

	public UpdatingThread(final int property,
			final CacheTripleStore mainTriples,
			final CacheTripleStore newTriples,
			final CacheTripleStore outputTriples,
			final CacheTripleStore exportTriples, final boolean export,
			final NodeDictionary dictionary) {
		super();
		this.property = property;
		this.mainTripleStore = mainTriples;
		this.newTriples = newTriples;
		this.outputTriples = outputTriples;
		this.exportTriples = exportTriples;
		this.export = export;
		this.dictionary = dictionary;
	}

	@Override
	public void run() {
		updateOnProperty(property);
	}

	/**
	 * Update the triple stores for the property i
	 * 
	 * @param i
	 */
	private void updateOnProperty(final int i) {
		final LongPairArrayList _inferred = outputTriples
				.getbyPredicateRawIndex(i);
		final LongPairArrayList _main = mainTripleStore
				.getbyPredicateRawIndex(i);
		// final boolean subclass = Integer.MAX_VALUE - i ==
		// AbstractDictionary.rdfssubClassOf;

		if (mainTripleStore.equals(outputTriples)) {
			mainTripleStore.getbyPredicateRawIndex(i).totalSortingNoDuplicate();
			logger.info("Skipping update");
			return;
		}
		// Usual checks
		if (_inferred == null || _inferred.isEmpty()) {
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("---------------------------------------------------------------------------------");
			logger.trace("Property active output "
					+ dictionary.get(NodeDictionary.SPLIT_INDEX - i) + " :"
					+ _inferred);
		}
		// In any case sort and removes duplicates from inferred triples without
		// keeping them
		_inferred.totalSortingNoDuplicate();

		if (logger.isTraceEnabled()) {
			logger.trace("_output after sorting and duplicate removal "
					+ _inferred);
		}
		// Get the property in newTriples to add the duplicates
		/**
		 * No existing property table in the main triple store
		 */
		if (_main == null) {
			// Sort and add
			if (logger.isTraceEnabled()) {
				logger.trace("Adding a new input for " + i);
			}
			final LongPairArrayList _copy = _inferred.copy();
			final LongPairArrayList _copy2 = _inferred.copy();
			mainTripleStore.setPropertyTriples(i, _copy2);
			newTriples.setPropertyTriples(i, _copy);
			// Export support
			if (export) {
				if (exportTriples.getbyPredicateRawIndex(i) == null) {
					exportTriples.setPropertyTriples(i, _inferred.copy());
				} else {
					// Batch insertion
					exportTriples.batchInsertionRawIndex(i, _inferred);
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Main after input " + mainTripleStore);
			}
		} else {
			/**
			 * 
			 * <pre>
			 * 1) Duplicates are removed from _output which is sorted
			 * 2) Main TS is also sorted
			 * 2) Allocate a new LongPairArrayList for the main ts
			 * 3) Traverse both array in // for insertion and duplicate removal
			 * 
			 * </pre>
			 */
			final LongPairArrayList futureMain = new LongPairArrayList(
					this.mainTripleStore.getSortingAlgorithm());
			final LongPairArrayList newTriplesList = new LongPairArrayList(
					_inferred.size() / 5,
					this.mainTripleStore.getSortingAlgorithm());
			// Traverse the arrays
			int indexInferred = -1;
			int indexMain = -1;
			boolean advanceMain = true, advanceInferred = true;
			long sm = 0, om = 0, si = 0, oi = 0;
			int lastCase = -1;
			if (logger.isTraceEnabled()) {
				logger.trace("Inf  " + _inferred);
				logger.trace("Main " + _main);
			}
			while (indexInferred < _inferred.size() && indexMain < _main.size()) {
				if (advanceMain) {
					if (indexMain == _main.size() - 1) {
						break;
					}
					sm = _main.getQuick(++indexMain);
					om = _main.getQuick(++indexMain);
				}
				if (advanceInferred) {
					if (indexInferred == _inferred.size() - 1) {
						break;
					}
					si = _inferred.getQuick(++indexInferred);
					oi = _inferred.getQuick(++indexInferred);

				}
				advanceInferred = false;
				advanceMain = false;
				/**
				 * Cases : 1) Dupl => Add to main, advance both 2)
				 */
				if (sm < si) {
					// The value is only in the main
					// copy in the new main
					// Advance main
					futureMain.add(sm);
					futureMain.add(om);
					advanceMain = true;
					lastCase = 1;
				} else if (sm > si) {
					// The new triple is not in the main
					// Add only the new into both
					// Advance inferred
					futureMain.add(si);
					futureMain.add(oi);
					newTriplesList.add(si);
					newTriplesList.add(oi);
					advanceInferred = true;
					lastCase = 2;
				} else { // sm==si
					if (om == oi) {
						// Duplicate
						// add into the main
						// Advance both
						futureMain.add(si);
						futureMain.add(oi);
						advanceInferred = true;
						advanceMain = true;
						lastCase = 3;
					} else if (om < oi) {
						// The value is only in the main
						// copy in the new main
						// Advance main
						futureMain.add(sm);
						futureMain.add(om);
						advanceMain = true;
						lastCase = 1;
					} else {// om>oi
						// The new triple is not in the main
						// Add only the new into both
						// Advance inferred
						futureMain.add(si);
						futureMain.add(oi);
						newTriplesList.add(si);
						newTriplesList.add(oi);
						advanceInferred = true;
						lastCase = 2;
					}
				}

			}

			if (logger.isTraceEnabled()) {
				logger.trace("Last case " + lastCase);
				logger.trace("Index inferred " + indexInferred);
				logger.trace("Index main " + indexMain);
			}
			//

			final boolean isMainExhausted = (indexMain == _main.size() - 1);
			final boolean isInferredExhausted = (indexInferred == _inferred
					.size() - 1);

			/**
			 * Finished main
			 */
			if (!isInferredExhausted && isMainExhausted) {
				if (logger.isTraceEnabled()) {
					logger.trace("Main exhausted");
				}

				if (lastCase == 1) {
					// Add all the inferred from
					futureMain.addAllOfFromTo(_inferred, indexInferred - 1,
							_inferred.size() - 1);
					newTriplesList.addAllOfFromTo(_inferred, indexInferred - 1,
							_inferred.size() - 1);
				} else if (lastCase == 2) {
					// The current value of sm/si has not been added
					futureMain.addAllOfFromTo(_main, indexMain - 1,
							_main.size() - 1);
					futureMain.addAllOfFromTo(_inferred, indexInferred + 1,
							_inferred.size() - 1);
					newTriplesList.addAllOfFromTo(_inferred, indexInferred + 1,
							_inferred.size() - 1);
				} else if (lastCase == 3) {
					// a duplicate was added, did not advance inferred - add the
					// rest
					futureMain.addAllOfFromTo(_inferred, indexInferred + 1,
							_inferred.size() - 1);
					newTriplesList.addAllOfFromTo(_inferred, indexInferred + 1,
							_inferred.size() - 1);
				}
			}
			/**
			 * Finished inferred
			 */
			if (!isMainExhausted && isInferredExhausted) {
				if (logger.isTraceEnabled()) {
					logger.trace("Inferred exhausted");
				}

				if (lastCase == 1) {
					// Not possible to happen
				} else if (lastCase == 2) {
					futureMain.addAllOfFromTo(_main, indexMain - 1,
							_main.size() - 1);
				} else if (lastCase == 3) {
					// Back from 1
					futureMain.addAllOfFromTo(_main, indexMain - 1,
							_main.size() - 1);

				}
			}
			/**
			 * Both finished
			 */
			if (isMainExhausted && isInferredExhausted) {
				if (logger.isTraceEnabled()) {
					logger.trace("Inferred exhausted");
				}

				if (lastCase == 1) {
					// Add the last of inferred
					futureMain.add(_inferred.getQuick(indexInferred - 1));
					futureMain.add(_inferred.getQuick(indexInferred));
					newTriplesList.add(_inferred.getQuick(indexInferred - 1));
					newTriplesList.add(_inferred.getQuick(indexInferred));
				} else if (lastCase == 2) {
					// Add the last of main
					futureMain.add(_main.getQuick(indexMain - 1));
					futureMain.add(_main.getQuick(indexMain));
				} else if (lastCase == 3) {
					// Do Nothing
				}
			}
			// Switch main and new main. Clear other - If export, append to the
			// export TS
			_inferred.clear();
			if (!newTriplesList.isEmpty()) {
				newTriples.setPropertyTriples(i, newTriplesList);
				if (export) {
					if (exportTriples.getbyPredicateRawIndex(i) == null) {
						exportTriples.setPropertyTriples(i,
								newTriplesList.copy());
					} else {
						// Batch insertion
						exportTriples.batchInsertionRawIndex(i, newTriplesList);
					}
				}
			}
			mainTripleStore.setPropertyTriples(i, futureMain);
			if (logger.isTraceEnabled()) {
				logger.trace(newTriplesList);
				logger.trace(futureMain);
			}

		}
	}

}
