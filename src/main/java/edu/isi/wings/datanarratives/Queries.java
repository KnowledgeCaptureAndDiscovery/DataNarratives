package edu.isi.wings.datanarratives;

import Elements.Resource;
import Elements.Step;

/**
 * Class for storing all the queries needed by the data narratives.
 * Some of these queries may overlap
 * @author dgarijo
 */
public class Queries {
    
    public static final String getValueForResourceProperty(Resource resource, String prop){
        return "select distinct ?v "+Constants.unionGraph+" where{"
                + "<"+resource.getUri()+">"+" <"+prop+"> ?v.}";
    }
    
    public static final String getStepDependencies(Step resource, String prop){
        return "select distinct ?step ?name "+Constants.unionGraph+" where{"
                + "<"+resource.getUri()+">"+" "+prop+" ?step."
                + "?step <"+Constants.RDFS_LABEL+"> ?name}";
    }
    
    public static final String getMethodMetadata(String resultURI){
        return "select ?wfname ?temp  "+Constants.unionGraph+" where {\n"
                + "<"+resultURI+">  <http://www.opmw.org/ontology/correspondsToTemplateArtifact> ?a.\n"
                + "?a <http://www.opmw.org/ontology/isVariableOfTemplate> ?temp.\n"
                + "OPTIONAL{?temp <http://www.w3.org/2000/01/rdf-schema#label> ?wfname.}\n"
                + "}";
    }
    
    public static final String getInputsOfMethod(String resultURI){
        return "select ?input ?l ?name "+Constants.unionGraph+" where {"
             + "<"+resultURI+">  <http://openprovenance.org/model/opmo#account> ?a.\n"
            + "?input a <http://www.opmw.org/ontology/WorkflowExecutionArtifact>."
            + "?input <http://openprovenance.org/model/opmo#account> ?a."
            + "?input <http://www.opmw.org/ontology/hasLocation>?l."
            + "?input <"+Constants.RDFS_LABEL + ">?name."
            + "filter not exists {"
            + "?input <http://purl.org/net/opmv/ns#wasGeneratedBy> ?p."
            + "}}";
    }
    
    public static final String getInputParametersOfMethod(String resultURI){
        return "select ?input ?v ?name  "+Constants.unionGraph+" where {"
                 + "<"+resultURI+">  <http://openprovenance.org/model/opmo#account> ?a.\n"
                + "?input a <http://www.opmw.org/ontology/WorkflowExecutionArtifact>."
                + "?input <http://openprovenance.org/model/opmo#account> ?a."
                + "?input <http://www.opmw.org/ontology/hasValue>?v."
                + "?input <"+Constants.RDFS_LABEL + ">?name."
                + "filter not exists {"
                + "?input <http://purl.org/net/opmv/ns#wasGeneratedBy> ?p."
                + "}}";
    }
    
    public static final String getVariableNameForResource(String resultURI){
        return "select ?name  "+Constants.unionGraph+" where{"
                + "<"+resultURI+"> <http://www.opmw.org/ontology/correspondsToTemplateArtifact> ?templVar."
                + "?templVar <http://www.w3.org/2000/01/rdf-schema#label> ?name}";
    }
    
    public static final String getOriginalSourcesForResource(String resultURI){
        return "select ?input ?loc ?name  "+Constants.unionGraph+" "
                + "where{"
                + "<"+resultURI+"> (<http://purl.org/net/opmv/ns#wasGeneratedBy>/<http://purl.org/net/opmv/ns#used>)* ?input."
                + "?input <http://www.opmw.org/ontology/hasLocation>?loc."
                + "?input <"+Constants.RDFS_LABEL + ">?name."
                + "FILTER NOT EXISTS {?input <http://purl.org/net/opmv/ns#wasGeneratedBy> ?process}."
                + "}";
    }
    
    public static final String getMethodProcesses(String resultURI){
        return "select ?process ?name ?motif "+Constants.unionGraph
                + " where{"
                + "<"+resultURI+"> <http://openprovenance.org/model/opmo#account> ?a."
                + "?p <http://openprovenance.org/model/opmo#account> ?a."
                + "?p a <http://www.opmw.org/ontology/WorkflowExecutionProcess>."
                + "?p <http://www.opmw.org/ontology/correspondsToTemplateProcess> ?process."
                + "?process <"+Constants.RDFS_LABEL + "> ?name."
                + "OPTIONAL {?process <http://purl.org/net/wf-motifs#hasMotif>"+"?m."
                + "?m a ?motif}"               
                + "}";
    }
    public static final String getMethodProcessForResult(String resultURI){
        return "select ?step  "+Constants.unionGraph+" "
                + "where{"
                + "<"+resultURI+"> <http://purl.org/net/opmv/ns#wasGeneratedBy> ?p."
                + "?p <http://www.opmw.org/ontology/correspondsToTemplateProcess> ?step."              
                + "}"; 
    }
    
    public static final String stepDependsOnStep(String p1, String p2){
        return "ASK  "+Constants.unionGraph+"  {<"+p1+"> (<http://www.opmw.org/ontology/uses>/<http://www.opmw.org/ontology/isGeneratedBy>)* <"+p2+">.}";
    }
}
