/*
 *  Copyright 2016 Daniel Garijo Verdejo, Information Sciences Institute, USC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package edu.isi.wings.datanarratives;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;

/**
 * This class defines the basic operations for loading contents into the DANA 
 * contextualized knowledge base.
 * @author dgarijo
 */
public class KnowledgeBase {

    private final OntModel knowledgeBase;

    public KnowledgeBase(String kbFile) {
        System.out.println("Creating Knowledge base...");
        this.knowledgeBase = ModelFactory.createOntologyModel();
        readFileIntoKnowledgeBase(kbFile);
    }

    public KnowledgeBase(DataNarrativeContext context){
        System.out.println("Creating Knowledge base...");
        this.knowledgeBase = ModelFactory.createOntologyModel();
        System.out.println("Loading workflow execution data...");
        this.retrieveRemoteWorkflowExecutionData(context.getWorkflowExecutionURI());
        System.out.println("Loading template data...");
        this.retrieveRemoteWorkflowTemplateData(context.getWorkflowTemplateURI());
        String motifs = context.getMotifAnnotations();
        if(motifs!=null && !motifs.equals("")){
            System.out.println("Loading motifs descriptions...");
            this.readFileIntoKnowledgeBase(motifs);
            this.assignMotifsToSteps();
        }
        String dois = context.getDoiFile();
        if(dois!=null && !dois.equals("")){
            System.out.println("Loading DOIs descriptions...");
            this.readFileIntoKnowledgeBase(dois);
        }
        //to do: retrieve OntoSoft components.
        //to do get the ontosoft whole repo description and put it in the KB. test if this affect efficiency too much
        //at the moment: file
        String ontoSoftAnn = context.getOntoSoftAnnotations();
        if(ontoSoftAnn!=null && !ontoSoftAnn.equals("")){
            System.out.println("Loading OntoSoft annotation file...");
            this.readFileIntoKnowledgeBase(ontoSoftAnn);
        }
        this.readFileIntoKnowledgeBase(context.getMotifAnnotations());
        System.out.println("Load complete.");
    }
    
    private void retrieveRemoteWorkflowExecutionData(String execURI){
        constructWithOnlineRepository(Constants.endpoint, Queries.constructExecMetadata(execURI), knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, Queries.constructAccountResourceMetadata(execURI), knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, Queries.constructGetSoftwareMetadata(execURI), knowledgeBase);
//        knowledgeBase.write(System.out, "TTL");
    }
    
    //retrieve workflow template data (and connections)
    private void retrieveRemoteWorkflowTemplateData(String templ){
        constructWithOnlineRepository(Constants.endpoint, Queries.constructLoadTempMetadata(templ), knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, Queries.constructLoadTemplateProcesses(templ), knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, Queries.constructLoadTemplateVariables(templ), knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, Queries.constructLoadTemplateParameters(templ), knowledgeBase);
    }
    
    //method that will perform a construct queries to make all the steps that have a motif
    //in the form of hasMotif [a motif]
    private void assignMotifsToSteps(){
        //load motif Ontology.
        knowledgeBase.read("http://purl.org/net/wf-motifs#");
//        String q = "construct {\n"
//                + "?r <http://purl.org/net/wf-motifs#hasMotif> [a ?m].} \nwhere{\n"
//                + "?r a ?c.\n"
//                + "?c <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?m.\n"
//                + "?m <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://purl.org/net/wf-motifs#DataOperationMotif>\n"
//                + "FILTER (?m!=?c)"
//                + "FILTER (?m!=<http://purl.org/net/wf-motifs#DataOperationMotif>)"
//                + "FILTER (?m!=<http://purl.org/net/wf-motifs#DataPreparation>)}";
////        System.out.println(q);
        String q = "select distinct ?r ?m where {?r a ?c. "
                + "?c <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?m."
                + "?m <http://www.w3.org/2000/01/rdf-schema#subClassOf>+ <http://purl.org/net/wf-motifs#DataOperationMotif>."
                + "FILTER (?m!=<http://purl.org/net/wf-motifs#DataPreparation>)."
                + "FILTER (?m!=<http://purl.org/net/wf-motifs#DataOperationMotif>)."
                + "FILTER (?m!=?c)}";
        
        ResultSet rs = selectFromLocalRepository(q);
        //The construct returns too many results, this way I can filter the response
        OntModel tempModel = ModelFactory.createOntologyModel();
        while (rs.hasNext()){
            QuerySolution qs = rs.next();
            String m = qs.getResource("?m").getURI();
            Resource re= qs.getResource("?r");
            tempModel.add(qs.getResource("?r"), tempModel.createProperty("http://purl.org/net/wf-motifs#hasMotif"), tempModel.createClass(m).createIndividual() );
//            System.out.println(re.getURI()+ " "+ m);
        }
        knowledgeBase.addSubModel(tempModel, false);
        //knowledgeBase.write(System.out, "TTL");
    }
    
    /**
     * Method for querying a SPARQL repository
     * @param endpointURL repository URI
     * @param queryIn query to perform
     * @return result of the query
     */
    public static ResultSet queryOnlineRepository(String endpointURL, String queryIn){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        //qe.getContext().set(TDB.symUnionDefaultGraph, true);
        ResultSet rs = qe.execSelect();        
        return rs;
    }
    
    public static void constructWithOnlineRepository(String endpointURL, String queryIn, OntModel m){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        qe.getContext().set(TDB.symUnionDefaultGraph, true);
        qe.execConstruct(m);        
    }
    
    /**
     * Method to perform a SELECT query to a local repository
     * @param queryIn query to be launched at the model
     * @return results
     */
    public ResultSet selectFromLocalRepository(String queryIn){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, this.knowledgeBase);
        ResultSet rs = qe.execSelect(); 
        return rs;
    }
    
    public boolean askFromLocalRepository(String queryIn){
        Query query = QueryFactory.create(queryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, this.knowledgeBase);
        return qe.execAsk();
    }
    
    public Model constructInLocalRepository(String queryIn){
        Query query = QueryFactory.create(queryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, this.knowledgeBase);
        return qe.execConstruct();
        //.write(System.out, "TTL")
    }
    
    private void readFileIntoKnowledgeBase (String file){
        InputStream in = FileManager.get().open(file);
        if (in == null) {
            throw new IllegalArgumentException("File: " + file + " not found");
        }
        try{
            knowledgeBase.read(in, null, "TTL");
        }catch(Exception e){
            knowledgeBase.read(in, null);//rdf/xml by default
        }
    }
}
