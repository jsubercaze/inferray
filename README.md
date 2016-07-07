Inferray
========

Inferray is cache-friendly forward-chaining reasoner that supports the following rule sets : 
* RDFS Full
* RDFS Default
* RhoDF
* RDFSPlus

## Citation

When using Inferray, please use the following citation :

    Julien Subercaze, Christophe Gravier, Jules Chevalier, Frédérique Laforest:
    Inferray: fast in-memory RDF inference. PVLDB 9(6): 468-479 (2016)
    
Bibtex :

     @article{DBLP:journals/pvldb/SubercazeGCL16,
     author    = {Julien Subercaze and
               Christophe Gravier and
               Jules Chevalier and
               Fr{\'{e}}d{\'{e}}rique Laforest},
     title     = {Inferray: fast in-memory {RDF} inference},
     journal   = {{PVLDB}},
     volume    = {9},
     number    = {6},
     pages     = {468--479},
     year      = {2016},
     url       = {http://www.vldb.org/pvldb/vol9/p468-subercaze.pdf}
    }


Inferray can be used either as a standalone reasoner (project inferray-core) or as a reasoner behind Jena (inferray bindings).

## Sample Use

### Standalone

Instantiate the reasoner, load an ontology and process the inference.

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
a full inference whenever triples are deleted from the Model. 

## Configuration

You can configure advanced parameters of Inferray using the Configuration Builder. Parameters are self explanatory and listed in the following snippet : 

    final ConfigurationBuilder builder = new ConfigurationBuilder();
    final PropertyConfiguration config = builder
					.setDumpFileOnExit(false).setForceQuickSort(false)
					.setMultithread(true).setThreadpoolSize(8)
					.setFastClosure(true)
					.setRulesProfile(SupportedProfile.RDFSPLUS)
					.build();
    final Inferray inferray = new Inferray(config);

## Maven dependencies

First you need to setup the server in your pom.xml :


    <repositories>
      <repository>
        <id>inferray-mvn-repo</id>
        <url>https://raw.github.com/jsubercaze/inferray/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
      </repository>
    </repositories>

Then use the following dependency :

    <dependency>
      <groupId>fr.ujm.tse.lt2c.satin</groupId>
      <artifactId>inferray</artifactId>
      <version>0.0.2-SNAPSHOT</version>
    </dependency>

To load only the core engine :

    <dependency>
      <groupId>fr.ujm.tse.lt2c.satin</groupId>
      <artifactId>inferray-core</artifactId>
      <version>0.0.2-SNAPSHOT</version>
    </dependency>
    
## Correctness

You can check the correctness of Inferray, using Jena as ground truth using the inferray-correctness project.
