Inferray
========

Inferray is cache-friendly forward-chaining reasoner that supports the following rule sets : 
* RDFS Full
* RDFS Default
* RhoDF
* RDFSPlus

Inferray can be used either as a standalone reasoner (project inferray-core) or as a reasoner behind Jena (inferray bindings).

## Sample Use

### Standalone

    Inferray inferray = new Inferray();
    inferray.parse(myOntology);
    inferray.process();
    
Export as a Jena Model : 

    ExportUtils.exportToJenaModel(inferray);
    
### Jena Reasoner

    OntModel model = ModelFactory.createOntologyModel(InferrayReasonerFactory.INFERRAY_RDFSPLUS);
		... make some changes ...
		model.rebind();
				
Then use the OntModel as usual with Jena. Inferray performs incremental inference when new triples are added and restart
a full inference 

## Maven dependencies

Coming soon

## Correctness

You can check the correctness of Inferray, using Jena as ground truth using the inferray-correctness project.
