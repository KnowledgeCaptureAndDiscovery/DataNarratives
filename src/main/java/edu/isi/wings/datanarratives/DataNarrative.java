/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.wings.datanarratives;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;

/**
 *
 * @author dgarijo
 */
public class DataNarrative {
    //components of a data narrative
    private final String workflowExecutionURI;
    private final String workflowTemplateURI;
    private String ontoSoftRepository;//ontosoft repo where the software definitions are stored
    private String stepLabelsFile;//labels for steps and artifacts
    private String doiFile;//file associating dois to artifacts
    private String motifAnnotations;//anotatedMotis
    private final String resultURI; //main result from which we want to produce the narrative
    private OntModel knowledgeBase;
    //other attributes useful for handling the narrative without doing queries constantly
    private String resultName;

    //for testing purposes
    public DataNarrative() {
        this.workflowExecutionURI="http://www.opmw.org/export/resource/WorkflowExecutionAccount/ACCOUNT1348628778528";
        this.workflowTemplateURI="http://www.opmw.org/export/resource/WorkflowTemplate/ABSTRACTGLOBALWORKFLOW2";
        this.resultURI = "http://www.opmw.org/export/resource/WorkflowExecutionArtifact/DE58909D2E17DF26F0BF79D75E12C2D6";
        this.doiFile = "C:\\Users\\dgarijo\\Dropbox\\NetBeansProjects\\DatNarratives\\examples\\highlyConnectedDrugs\\doiAnnotations.ttl";
        this.motifAnnotations = "C:\\Users\\dgarijo\\Dropbox\\NetBeansProjects\\DatNarratives\\examples\\highlyConnectedDrugs\\motifAnnotations.ttl";
        buildKnowledgeBase();
    }

    
    public DataNarrative(String workflowExecutionURI, String workflowTemplateURI, String ontoSoftRepository, String stepLabelsFile, String doiFile, String motifAnnotations, String resultURI) {
        this.workflowExecutionURI = workflowExecutionURI;
        this.workflowTemplateURI = workflowTemplateURI;
        this.ontoSoftRepository = ontoSoftRepository;
        this.stepLabelsFile = stepLabelsFile;
        this.doiFile = doiFile;
        this.motifAnnotations = motifAnnotations;
        this.resultURI = resultURI;
        buildKnowledgeBase();
    }

    /**
     * This method builds the knowledge base for the data narrative from the information that is available.
     */
    private void buildKnowledgeBase() {
        System.out.println("Creating Knowledge base...");
        this.knowledgeBase = ModelFactory.createOntologyModel();
        System.out.println("Loading workflow data...");
        this.retrieveRemoteWorkflowExecutionData(workflowExecutionURI);
        System.out.println("Loading template data...");
        this.retrieveRemoteWorkflowTemplateData(workflowTemplateURI);
        System.out.println("Loading DOI descriptions...");
        this.retrieveDOIs(doiFile);
        System.out.println("Loading Motif descriptions...");
        this.retrieveDOIs(motifAnnotations);
        System.out.println("Load complete.");
    }
    
    
    //retrieve workflow execution data. We assume it's from the endpoint of URIs.
    //we assume it's in opmw.
    private void retrieveRemoteWorkflowExecutionData(String execURI){
        String queryLoadExecMetadata = "construct {<"+execURI+"> ?p ?o} where {\n" +
        "<"+execURI+"> ?p ?o" +
        "}";
        String queryLoadAccountResourceMetadata = "construct { "
        + "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o} where {\n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o\n" +
        "}";
        String queryGetSoftwareMetadata = "construct { \n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. \n" +
        "?s <http://www.opmw.org/ontology/hasExecutableComponent> ?o.\n" +
        "?o ?p ?q.\n" +
        "} \n" +
        "where \n" +
        "{\n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. \n" +
        "?s <http://www.opmw.org/ontology/hasExecutableComponent> ?o.\n" +
        "?o ?p ?q\n" +
        "}";
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadExecMetadata, knowledgeBase);
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadAccountResourceMetadata, knowledgeBase);
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryGetSoftwareMetadata, knowledgeBase);
        //knowledgeBase.write(System.out, "TTL");
    }
    
    //retrieve workflow template data (and connections)
    private void retrieveRemoteWorkflowTemplateData(String templ){
        String queryLoadTempMetadata = "construct {<"+templ+"> ?p ?o} where {\n" +
        "<"+templ+"> ?p ?o" +
        "}";
        String queryLoadTemplateProcesses = "construct { "
        + "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o} where {\n" +
        "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        String queryLoadTemplateVariables = "construct { "
        + "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o} where {\n" +
        "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        String queryLoadTemplateParameters = "construct { "
        + "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o} where {\n" +
        "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o\n" +
        "}";
        
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadTempMetadata, knowledgeBase);
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateProcesses, knowledgeBase);
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateVariables, knowledgeBase);
        GeneralMethods.constructWithOnlineRepository(Constants.endpoint, queryLoadTemplateParameters, knowledgeBase);
    }
    
    private void retrieveMotifs(String motifFile){
        GeneralMethods.readFileIntoKnowledgeBase(knowledgeBase, motifFile);
    }
    
    
    private void retrieveDOIs(String doiFile) {
        GeneralMethods.readFileIntoKnowledgeBase(knowledgeBase, doiFile);
    }
    
    //for all ontoSoftentries, retrieve the main description and data and version and license

    public OntModel getKnowledgeBase() {
        return knowledgeBase;
    }

    public String getResultURI() {
        return resultURI;
    }
    
    public String getResultName(){
        if(resultName!=null){
            return resultName;
        }
        String queryResulName = "select ?name where {"
                + "<"+this.resultURI+"> <http://www.opmw.org/ontology/correspondsToTemplateArtifact> ?templVar."
                + "?templVar <http://www.w3.org/2000/01/rdf-schema#label> ?name}";
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, queryResulName);
        if(rs.hasNext()){
            return rs.nextSolution().getLiteral("name").getString().replace("Data variable ", "");
        }else{
            return "resource without label";
        }
    }
    
    public String getWorkflowExecutionVisualization(){
        String queryVis = "select distinct ?vis where{"
                + "<"+this.workflowExecutionURI+">"+" <http://www.opmw.org/ontology/hasExecutionDiagram>"+"?vis.}";
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, queryVis);
        if (rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getLiteral("?vis").getString();
        }
        return null;
    }
    
    public String getWorkflowTemplateVisualization(){
        String queryVis = "select distinct ?vis where{"
                + "<"+this.workflowTemplateURI+">"+" <http://www.opmw.org/ontology/hasTemplateDiagram>"+"?vis.}";
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, queryVis);
        if (rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getLiteral("?vis").getString();
        }
        return null;
    }
    
    /**
     * if a DOI exists for the resource in the knowledge base, this method returns it
     * @param resourceURI
     * @return 
     */
    public String getDOI(String resourceURI){
        String queryDOI = "select distinct ?doi where{"
                + "<"+resourceURI+">"+" <http://purl.org/ontology/bibo/doi>"+"?doi.}";
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, queryDOI);
        if (rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getLiteral("?doi").getString();
        }
        return null;
    }
    
    public String getMotifForProcess(String processURI){
        String queryDOI = "select distinct ?motif where{"
                + "<"+processURI+">"+" <http://purl.org/net/wf-motifs#hasMotif>"+"?m."
                + "?m a ?motif}";
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, queryDOI);
        String motifs = "";
        while (rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            motifs+= qs.getResource("?motif").getURI()+",";
        }
        if(!"".equals(motifs)){
            motifs = motifs.substring(0, motifs.length()-1);//remove last ","
        }
        return motifs;
    }
    /**
     * debug query
     * @param query
     * @return 
     */
    public void resultsForQuery(String query){
        ResultSet rs = GeneralMethods.queryLocalRepository(knowledgeBase, query);
        ResultSetFormatter.out(System.out, rs);
    }
    public static void main(String[] args){
        DataNarrative d = new DataNarrative();
//        String q = "select ?label ?wf ?templ ?loc ?doi "
//                + "where{"
//                + "<http://www.opmw.org/export/resource/WorkflowExecutionArtifact/DE58909D2E17DF26F0BF79D75E12C2D6> <http://www.w3.org/2000/01/rdf-schema#label> ?label;"
//                + "<http://openprovenance.org/model/opmo#account> ?wf;"
//                + "<http://www.opmw.org/ontology/hasLocation> ?loc;"
//                + "<http://purl.org/ontology/bibo/doi> ?doi."
//                + "?wf <http://www.opmw.org/ontology/correspondsToTemplate> ?templ}";
//        String q = "select ?input ?loc ?value "
//                + "where{"
//                + "<http://www.opmw.org/export/resource/WorkflowExecutionArtifact/DE58909D2E17DF26F0BF79D75E12C2D6> (<http://purl.org/net/opmv/ns#wasGeneratedBy>/<http://purl.org/net/opmv/ns#used>)* ?input."
//                + "optional{?input <http://www.opmw.org/ontology/hasValue> ?value}."
//                + "optional{?input <http://www.opmw.org/ontology/hasLocation>?loc}."
//                + "FILTER NOT EXISTS {?input <http://purl.org/net/opmv/ns#wasGeneratedBy> ?process}."
//                + "}";
        String q = "select ?step ?f ?i ?o "
                + "where{"
                + "?step <http://purl.org/net/wf-motifs#hasMotif> ?m."
                + "?m a ?f."
                + "?step <http://www.opmw.org/ontology/uses> ?i."
                + "?o <http://www.opmw.org/ontology/isGeneratedBy> ?step."
                + "}";
        d.resultsForQuery(q);
        //System.out.println(d.getDOI(d.resultURI));
        //System.out.println(d.getMotifForProcess("http://www.opmw.org/export/resource/WorkflowTemplateProcess/ABSTRACTGLOBALWORKFLOW2_SIGRESULTMERGER"));
    }

}



