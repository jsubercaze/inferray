package fr.ujm.tse.lt2c.satin.inferray.rules.profile;

import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.utils.InferrayTriple;

public interface RulesProfile {

	/**
	 *
	 * @return the array of rules for this profile
	 */
	public abstract AbstractFastRule[] getRules();

	/**
	 * Finalization may be require, as for RDFS for instance
	 */
	public void finalization();

	/**
	 *
	 * @return the name of the profile
	 */
	public abstract String getName();

	/**
	 * Add an axiomatic triple to the rule profile
	 *
	 * @param axiomaticTriple
	 *            the triple to add
	 */
	public void addAxiomaticTriple(InferrayTriple axiomaticTriple);

	/**
	 *
	 * @return whether this rules profile includes SCM-EQC or SCM-EQP that are
	 *         processed first at parsing time
	 */
	boolean hasSCMEQPSCMEQC();
	/**
	 *
	 * @return wether this rule set has a subclass closure
	 */
	public boolean hasSubClassClosure();
	/**
	 *
	 * @return wether this rule set has a subproperty closure
	 */
	public boolean hasSubPropertyClosure();



}