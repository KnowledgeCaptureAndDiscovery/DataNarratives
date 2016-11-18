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

import edu.isi.wings.elements.Resource;
import edu.isi.wings.elements.Step;

/**
 * Class for storing all the queries needed by the data narratives.
 * Some of these queries may overlap
 * @author dgarijo
 */
public class Queries {
    
    public static final String constructExecMetadata(String execURI){
        return "construct {<"+execURI+"> ?p ?o}  "+Constants.unionGraph+" where {\n" +
        "<"+execURI+"> ?p ?o" +
        "}";
    }
    public static final String constructAccountResourceMetadata(String execURI){
        return "construct { "
        + "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o}  "+Constants.unionGraph+"  where {\n" +
        "?s <http://openprovenance.org/model/opmo#account> <"+execURI+">. ?s ?p ?o\n" +
        "}";
    }
    public static final String constructGetSoftwareMetadata(String execURI){
        return "construct { \n" +
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
    }
    public static final String constructLoadTempMetadata(String templ){
        return "construct {<"+templ+"> ?p ?o} "
                    + Constants.unionGraph+" where {\n" +
            "<"+templ+"> ?p ?o" +
            "}";
    }
    public static final String constructLoadTemplateProcesses(String templ){
        return "construct { "
            + "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o} "
                    + Constants.unionGraph+" where {\n" +
            "?s <http://www.opmw.org/ontology/isStepOfTemplate> <"+templ+">. ?s ?p ?o\n" +
            "}";
    }
    public static final String constructLoadTemplateVariables(String templ){
        return "construct { "
            + "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o} "
                    + Constants.unionGraph+" where {\n" +
            "?s <http://www.opmw.org/ontology/isParameterOfTemplate> <"+templ+">. ?s ?p ?o\n" +
            "}";
    }
    
    public static final String constructLoadTemplateParameters(String templ){
         return "construct { "
            + "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o} "
                    + Constants.unionGraph+" where {\n" +
            "?s <http://www.opmw.org/ontology/isVariableOfTemplate> <"+templ+">. ?s ?p ?o\n" +
            "}";
    }
    
    
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
                + "?m a ?motif."
                 + "?motif <http://www.w3.org/2000/01/rdf-schema#subClassOf>+ <http://purl.org/net/wf-motifs#DataOperationMotif>."
                + "FILTER (?motif!=<http://purl.org/net/wf-motifs#DataPreparation>)."
                + "FILTER (?motif!=<http://purl.org/net/wf-motifs#DataOperationMotif>)."
                + "}"               
                + "}";
    }
    public static final String getMethodProcessForResult(String resultURI){
        return "select ?step  "+Constants.unionGraph+" "
                + "where{"
                + "<"+resultURI+"> <http://purl.org/net/opmv/ns#wasGeneratedBy> ?p."
                + "?p <http://www.opmw.org/ontology/correspondsToTemplateProcess> ?step."              
                + "}"; 
    }
    
    public static final String getExecutionProcessForResult(String resultURI){
        return "select ?step  "+Constants.unionGraph+" "
                + "where{"
                + "<"+resultURI+"> <http://purl.org/net/opmv/ns#wasGeneratedBy> ?step."              
                + "}"; 
    }
    
    public static final String stepDependsOnStep(boolean isTemplate, String p1, String p2){
        if(isTemplate)
            return "ASK  "+Constants.unionGraph+"  {<"+p1+"> (<http://www.opmw.org/ontology/uses>/<http://www.opmw.org/ontology/isGeneratedBy>)* <"+p2+">.}";
        else return "ASK  "+Constants.unionGraph+"  {<"+p1+"> (<http://purl.org/net/opmv/ns#used>/<http://purl.org/net/opmv/ns#wasGeneratedBy>)* <"+p2+">.}";
    }
    
    public static final String getExecutionMetadata(String resultURI){
        return "select ?wfname ?uri  "+Constants.unionGraph+" where {\n"
                + "<"+resultURI+">  <http://openprovenance.org/model/opmo#account> ?uri.\n"
                + "OPTIONAL{?temp <http://www.w3.org/2000/01/rdf-schema#label> ?wfname.}\n"
                + "}";
    }
    
    //we return the implementation as well to facilitate the implementation view.
    public static final String getExecutionProcesses(String resultURI){
        return "select ?process ?name ?impl ?impln ?code "+Constants.unionGraph
                + " where{"
                + "<"+resultURI+"> <http://openprovenance.org/model/opmo#account> ?a."
                + "?process <http://openprovenance.org/model/opmo#account> ?a."
                + "?process <http://www.opmw.org/ontology/hasExecutableComponent> ?comp."
                + "?comp <http://www.opmw.org/ontology/hasLocation> ?code."
                + "?process a <http://www.opmw.org/ontology/WorkflowExecutionProcess>."
                + "?process <http://www.opmw.org/ontology/correspondsToTemplateProcess> ?impl."
                + "?impl <"+Constants.RDFS_LABEL + "> ?impln."
                + "?process <"+Constants.RDFS_LABEL + "> ?name."               
                + "}";
    }
    
    public static final String getSoftwareMetadata(String stepName){
        return"select ?lic ?lan ?code ?web "+Constants.unionGraph
            + "where {"
            + "?s a <http://ontosoft.org/software#Software>."
            + "?s <"+Constants.RDFS_LABEL +"> \""+ stepName+"\"."
            + "OPTIONAL{?s <http://ontosoft.org/software#hasImplementationLanguage> ?lan.}." 
            + "OPTIONAL{?s <http://ontosoft.org/software#hasLicense> ?lic.}."
            + "OPTIONAL{?s <http://ontosoft.org/software#hasProjectWebsite>/<http://ontosoft.org/software#hasURI> ?web.}."
            + "OPTIONAL{?s <http://ontosoft.org/software#hasCodeLocation>/<http://ontosoft.org/software#hasURI> ?code.}"
                + "}";
    }
}
