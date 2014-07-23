package fr.ujm.tse.lt2c.satin.inferray.correctness;

public class TestInferrayOnLocalOntology {

	public static void main(final String[] args) {
		final String location = "c:/libs/inferray/subClassOf10.nt";
		InferrayCorrectness
		.checkInferrayCorrectnessRDFSAndDumpToFiles(location);
	}
}
