package fr.ujm.tse.lt2c.satin.inferray.benchmark;

import java.io.IOException;

import fr.ujm.tse.lt2c.satin.inferray.configuration.ConfigurationBuilder;
import fr.ujm.tse.lt2c.satin.inferray.configuration.PropertyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Helper to test the speed of inferray
 *
 * @author Julien
 *
 */
public class InferraySpeed {
	/**
	 * Number of iterations to perform
	 */
	public static final int ITERATIONS = 1;

	public static void main(final String[] args) throws IOException {
		System.in.read();
		for (int i = 0; i < ITERATIONS; i++) {
			// Inferray inferray = new Inferray(
			// "C:/Users/Julien/Downloads/structure.rdf.u8");
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			final PropertyConfiguration config = builder
					.setDumpFileOnExit(false).setForceQuickSort(false)
					.setMultithread(true).setThreadpoolSize(8)
					.setAxiomaticTriplesDirectory("/tmp/").setFastClosure(true)
					.setRulesProfile(SupportedProfile.RDFSPLUS)
					.setDumpFileOnExit(false).build();
			final Inferray inferray = new Inferray(config);
			inferray.parse(args[0]);
			inferray.process();
			System.out.println(inferray.getInferredTriples() + " triples");
			System.out.println(inferray.getInferenceTime() + " ms");
			System.out.println();
			System.out.println("-------------Sorting-------------------");
			System.out.println("Insertion sorts "
					+ LongPairArrayList.insertions);
			System.out.println("Counting sorts " + LongPairArrayList.counting);
			System.out.println("Quick sorts " + LongPairArrayList.quicksort);
			System.out.println();
			System.out
			.println("-------------Object Sorting Cache-------------------");
			System.out
			.println("Cache hits " + LongPairArrayList.objectCacheHit);
			System.out.println("Cache misses "
					+ LongPairArrayList.objectCacheMiss);
			System.out
			.println("Hits ratio "
					+ (LongPairArrayList.objectCacheHit.get())
					/ ((double) LongPairArrayList.objectCacheHit.get() + (double) LongPairArrayList.objectCacheMiss
							.get()));
		}
	}
}
