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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
        System.out.println("Loading workflow data...");
        this.retrieveRemoteWorkflowExecutionData(context.getWorkflowExecutionURI());
        System.out.println("Loading template data...");
        this.retrieveRemoteWorkflowTemplateData(context.getWorkflowTemplateURI());
        String motifs = context.getMotifAnnotations();
        if(motifs!=null && !motifs.equals("")){
            System.out.println("Loading motifs descriptions...");
            this.readFileIntoKnowledgeBase(motifs);
        }
        String dois = context.getDoiFile();
        if(dois!=null && !dois.equals("")){
            System.out.println("Loading DOIs descriptions...");
            this.readFileIntoKnowledgeBase(dois);
        }
        this.readFileIntoKnowledgeBase(context.getMotifAnnotations());
        System.out.println("Load complete.");
    }
    
    private void retrieveRemoteWorkflowExecutionData(String execURI){
        String queryLoadExecMetadata = "construct {<"+execURI+"> ?p ?o}  "+Constants.unionGraph+" where {\n" +
        "<"+execURI+"> ?p ?o" +
        "}";
        String queryLoadAccountResourceMetadata = "construct { "
        + "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o}  "+Constants.unionGraph+"  where {\n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o\n" +
        "}";
        String queryGetSoftwareMetadata = "construct { \n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. \n" +
        "?s <http://www.opmw.org/ontology/hasExecutableComponent> ?o.\n" +
        "?o ?p ?q.\n" +
        "} "+Constants.unionGraph+" \n" +
        "where \n" +
        "{\n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. \n" +
        "?s <http://www.opmw.org/ontology/hasExecutableComponent> ?o.\n" +
        "?o ?p ?q\n" +
        "}";
        constructWithOnlineRepository(Constants.endpoint, queryLoadExecMetadata, knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, queryLoadAccountResourceMetadata, knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, queryGetSoftwareMetadata, knowledgeBase);
        //knowledgeBase.write(System.out, "TTL");
    }
    
    //retrieve workflow template data (and connections)
    private void retrieveRemoteWorkflowTemplateData(String templ){
        String queryLoadTempMetadata = "construct {<"+templ+"> ?p ?o} "
                + Constants.unionGraph+" where {\n" +
        "<"+templ+"> ?p ?o" +
        "}";
        String queryLoadTemplateProcesses = "construct { "
        + "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o} "
                + Constants.unionGraph+" where {\n" +
        "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        String queryLoadTemplateVariables = "construct { "
        + "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o} "
                + Constants.unionGraph+" where {\n" +
        "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        String queryLoadTemplateParameters = "construct { "
        + "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o} "
                + Constants.unionGraph+" where {\n" +
        "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        
//        ResultSet rd = GeneralMethods.queryOnlineRepository(Constants.endpoint, "Select ?p ?o  from <urn:x-arq:UnionGraph> where {\n" +
//        "<"+templ+"> ?p ?o}");
//        System.out.println("Select ?p ?o  from <urn:x-arq:UnionGraph> where {\n" +
//        "<"+templ+"> ?p ?o}");
//        while (rd.hasNext()){
//            QuerySolution qs = rd.nextSolution();
//            System.out.println(qs.getResource("p").getURI());
//        }
        
        constructWithOnlineRepository(Constants.endpoint, queryLoadTempMetadata, knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateProcesses, knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateVariables, knowledgeBase);
        constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateParameters, knowledgeBase);
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
    
    private void readFileIntoKnowledgeBase (String file){
        InputStream in = FileManager.get().open(file);
        if (in == null) {
            throw new IllegalArgumentException("DOI File: " + file + " not found");
        }
        try{
            knowledgeBase.read(in, null, "TTL");
        }catch(Exception e){
            knowledgeBase.read(in, null);//rdf/xml by default
        }
    }
}
