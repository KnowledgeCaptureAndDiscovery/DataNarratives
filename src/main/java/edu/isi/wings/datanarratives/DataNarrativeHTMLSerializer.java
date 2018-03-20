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
import edu.isi.wings.elements.Software;
import edu.isi.wings.elements.Step;
import edu.isi.wings.elements.StepCollection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Class that takes a data narrative and creates an html serialization of it.
 * The error capture can be clearly improved. This is just a prototype.
 * @author dgarijo
 */
public class DataNarrativeHTMLSerializer {
    
    private enum resourceTypes {executionArtifact, parameter, template, motif, processAndMotif, templateStepAndDependency, executionAndImplentation,executionAndCode, string, other};//other refers to the default behavior
    
    public static void dataNarrativeToHTML(DataNarrative d, String outPath){
        String htmlPage = Constants.HTML_HEAD;
        htmlPage+=Constants.HTML_BODY_BEGIN;
        //add title: resource name and URI
        htmlPage+="<h3 class=\"text-muted\">Data narratives for result: <a href=\""+d.getResult().getLocation()+"\">"+GeneralMethods.splitCamelCase(d.getResult().getName())+"</a></h3>\n";
        
        
        htmlPage+="<div class=\"row\">\n";
        htmlPage+="<div class=\"col-xs-8 col-md-8\">\n" +
"	  <div class=\"panel-group\">\n";
        //insert data narratives here
        htmlPage+= getExecutionNarrative(d);
        //data view
        htmlPage+= getDataOrientedNarrative(d);
        //method view (motif)
        htmlPage+= getFunctionalityNarrative(d);
        //step view (abstract) TO DO
        htmlPage+= getAbstractDependencyNarrative(d);
        //implementation view
        htmlPage+= getImplementationdNarrative(d);
        htmlPage+= getSoftwareNarrative(d);
        
        htmlPage+="</div>\n" +
"	</div>";
        
        //Wf visualizations here
       htmlPage+="<div class=\"col-xs-6 col-md-4\">";
        //vis go here
        //replace visualizations with the right ones
        htmlPage+="<div class=\"panel panel-primary workflowTab template\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  <a data-toggle=\"collapse\" href=\"#collapseWf\">Workflow Visualization (Abstract method)</a>\n" +
"			</h4>\n" +
"		  </div>\n" +
"			<a href="+ d.getContext().getWorkflowExecutionURI() + " id='executionWorkflow' > </a> <a href="+ d.getContext().getWorkflowTemplateURI() + " id='templateWorkflow' > </a>"	+
"		  <div id=\"collapseWf\" class=\"panel-collapse collapse \">\n" +
"<div class=\"bigCanvas\">  \n" + 
" \n" + 
"    <div class=\"visualization-container\" id=\"viz\" style=\"width:100%;height:400px;\">\n" + 
"        <svg>\n" + 
"        </svg>\n" + 
//"        <img class=\"lazyload\" id='spinner' src=\"../images/spin.gif\"/ style=\"display:none\">\n" + 
"\n" + 
"    </div>" +
//"			<img src=\""+d.getWorkflowTemplateVisualization()+"\" width=\"100%\"/>\n" +
"		  </div>\n" +
"		</div>\n" +
"		</div>\n" +
"		<div class=\"panel panel-primary executionTab\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  <a data-toggle=\"collapse\" href=\"#collapseEx\">Workflow Execution Visualization</a>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapseEx\" class=\"panel-collapse collapse\">\n" +
"<div class=\"bigCanvas\">  \n" + 
" \n" + 
"    <div class=\"visualization-container2\" id=\"viz2\" style=\"width:100%;height:400px;\">\n" + 
"        <svg>\n" + 
"        </svg>\n" + 
//"        <img class=\"lazyload\" id='spinner' src=\"../images/spin.gif\"/ style=\"display:none\">\n" + 
"\n" + 
"    </div>" +
"		  </div>\n" +
"		  </div>\n" +
"		";
        htmlPage+="</div>";
        
        htmlPage+=Constants.HTML_BODY_END;
        
        try{
            File f = new File(outPath);
            f.createNewFile();
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
            out.write(htmlPage);
            out.close();
        }catch(IOException e){
            System.err.println("Error while creating the html serialization "+e.getMessage()+"\n");
        } 
    }
    
    /**
     * This function lists the resources separated by commas according to their type.
     * For example, if the list includes [a,b,c,d] of type t then it will list t a, t b, t c and t d.
     * The function is extended to support different types of resources.
     * This function just serialized a list. Summarizations of the list are not produced here.
     * 
     * @param d
     * @param resourcesToList
     * @param type
     * @param showLocation
     * @param showValue
     * @param showVariables
     * @return 
     */
    private static String serializeResourceList(DataNarrative d, ArrayList<?> resourcesToList,resourceTypes type, boolean showValue, boolean showVariables){
        String list = "";
        int currentResource=0;
        for (Object s:resourcesToList){
            currentResource++;
            switch(type){
                case executionArtifact:
                    list+= "dataset "+serializeResource(d,(Resource)s, type);
                    if(showVariables){
                        list+= hideText("(input "+d.getVariableNameForResource(((Resource)s))+")");
                    }
                    break;
                case parameter: 
                    list+=serializeResource(d,(Resource)s, type);//removed "parameter"
                    if(showValue){
                        list+= " set to "+((Resource)s).getValue();
                    }
                    break;
                case motif://motifs are just Strings with the URIs. Remove the parts that we don't want to show.
                    list+=((String)s).replace("http://purl.org/net/wf-motifs#","").replace("Data", "");
                    break;
                case string:
                    list+=s;
                    break;
                case templateStepAndDependency:
                    list+= serializeResource(d,(Resource)s, type);
                    ArrayList<Step> dependencies = d.getStepDependencies((Step)s, true);
                    if(!dependencies.isEmpty()){
                        list+=hideText(" (using data from "+serializeResourceList(d, dependencies, resourceTypes.other, false, false)+")");
                    }
                    break;
                case executionAndImplentation:
                    Step exec, abs;
                    exec = (Step)s;
                    abs = exec.getImplementationOf();
                    list += serializeResource(d,exec, type);
                    if(!exec.getName().equals(abs.getName())){//only show if implementation is different from the original
                        list+= hideText(" (implementation of "+serializeResource(d,abs, type)+")");
                    }
                    break;
                default:
                    list+= serializeResource(d,(Resource)s, type);
                    break;
            }
            if(resourcesToList.size()>=2 && currentResource < resourcesToList.size()){
                if(currentResource == resourcesToList.size()-1){
                    list+=" and ";
                }else{
                    list+=", ";
                }
            }
        }
        return list;
    }
    
    private static String serializeResource(DataNarrative d, Resource r, resourceTypes type){
        switch (type){
            case executionArtifact:
                return "<a href=\""+r.getLocation()+"\">"+GeneralMethods.getFileNameFromURL(r.getLocation())+"</a> ";
            case parameter:
                return "<a href=\""+r.getUri()+"\">"+GeneralMethods.getFileNameFromURL(r.getUri())+"</a> ";
            case template:
                //templates may have an hyphen, remove
                return "<a href=\""+r.getUri()+"\">"+GeneralMethods.splitCamelCase(GeneralMethods.removeHypen(r.getName()))+"</a> ";
            case processAndMotif:
                String serialization = serializeResource(d, r, resourceTypes.other);
                ArrayList<String> motifs = ((Step)r).getMotifs();
                if(motifs.size()>0){
                    serialization += hideText(" (a type of "+ serializeResourceList(null,motifs, resourceTypes.motif,false,false)+" step)");
                }
                return serialization;
            case executionAndCode:
                    String s = GeneralMethods.splitCamelCase((r.getName())) +" uses a <a href=\""+((Step)r).getCodeLocation()+"\">bash script</a> ";
                    //Note: labels HAVE to be EXACTLLY the same
                    Software soft = d.getSoftwareMetadata((Step)r);
                    if(!soft.getProgrammingLanguage().equals("")){
                        s+= "and a "+soft.getProgrammingLanguage()+" ";
                    }
                    if(!soft.getCodeLocation().equals("")){
                        s+="<a href=\""+soft.getCodeLocation()+"\">program</a> ";
                    }else{
                        s+="program ";
                    }
                    if(!soft.getWebsite().equals("")){
                        s+= hideText("(see the <a href=\""+soft.getWebsite()+"\">project website</a>) ");
                    }
                    s+="to perform its functionality.";
                    if(!soft.getLicense().equals("")){
                        s+="The software is licensed under a <a href=\""+soft.getLicense()+"\">"+soft.getLicense().replace("http://ontosoft.org/software#", "")+"</a> license.";
                    } 
                    s+= "<br/>";
                    return s;
            default:
                //by default, get the name plus URI, without camelcase.
                return "<a href=\""+r.getUri()+"\">"+GeneralMethods.splitCamelCase((r.getName()))+"</a>";
        }
    }
    /**
     * Method for serializing the steps of a workflow in order.
     * @param orderedList
     * @param type
     * @return 
     */
    private static String serializeOrderedSteps(DataNarrative d, ArrayList<ArrayList<Step>> orderedList, resourceTypes type){
        String serialization ="";
        int tasksNo = orderedList.size();
        switch (tasksNo){
            case 1:
                serialization += "The "+serializeResourceList(d, orderedList.get(0), type, false, false)+" produces the final results";
                break;
            case 2:
                serialization += "First, the input data is analyzed by "+serializeResourceList(d, orderedList.get(0), type, false, false);
                serialization += ", followed by "+ serializeResourceList(d, orderedList.get(1), type, false, false);
                break;
            default:// >2
                serialization += "First, the input data is analyzed by "+serializeResourceList(d, orderedList.get(0), type, false, false)+", followed by ";        
                int i=1;
                orderedList.remove(0);//the first one has already been listed
                for(ArrayList<Step> currList:orderedList){
                    serialization+=serializeResourceList(d, currList, type, false, false);
                    i++;
                    if(i==tasksNo -2 ){
                        serialization += ", and ";
                    }else{
                        if(i== tasksNo-1){
                         serialization  += ". The final results are produced by ";//cool end phrase   
                        }else{
                            if(i<tasksNo){
                                serialization +=", ";
                            }
                        }
                    }
                }
                break;
        }
        return serialization;
                
    }
    
    /**
     * Function to serialize the execution view of a data narrative. The template
     * followed is similar to:
     * The 'workflow label' method was run on the 'inputs of the workflow ', with
     * 'parameters used' set to 'parameter value'. The 'result label' results are stored online (DOI 'doi value')
     * @param d
     * @return 
     */
    private static String getExecutionNarrative(DataNarrative d){
        Resource result = d.getResult();
        Resource method= d.getMethodMetadata();
        String resultDOI = d.getValueForProperty(result, Constants.BIBO_DOI);        
        String narrative1 = Constants.getNarrativeStart("Data Narrative 1: Execution view", "1", Constants.TOOLTIP_NARRATIVE_EXEC);
        narrative1+= "	The "+serializeResource(d,method, resourceTypes.template)+" method was run on ";
        narrative1+=serializeResourceList(d, d.getMethodInputs(), resourceTypes.executionArtifact, false, true);
        ArrayList<Resource> params = d.getMethodInputParameters();
        if(!params.isEmpty()){
            narrative1 +=", with ";
        }
        narrative1+=serializeResourceList(d, params, resourceTypes.parameter, true, false);
        narrative1+=". The "+serializeResource(d,result,resourceTypes.other)+ " results are stored <a href=\""+d.getResult().getLocation()+"\">online</a>";
        if(resultDOI != null && !resultDOI.equals("")){
            narrative1+="(DOI <a href=\""+resultDOI+"\">"+resultDOI+"</a>)\n";
        }
        narrative1+="."+Constants.NARRATIVE_END;
        return narrative1;
    }
    
    public static String getDataOrientedNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative2 = Constants.getNarrativeStart("Data Narrative 2: Data view", "2", Constants.TOOLTIP_NARRATIVE_DATA);
        narrative2+=" The "+serializeResource(d,result, resourceTypes.other)+" results have been derived from the ";
        ArrayList<Resource> sources = d.getOriginalSourcesForResult(d.getResult().getUri());
        narrative2 += serializeResourceList(d, sources, resourceTypes.executionArtifact,  false, true);
        narrative2+="."+Constants.NARRATIVE_END;
        return narrative2;
    }
    
    /**
     * Template for generating a method view. The template is:
     * The W method performs N main types of analyses on the original datasets.
     * The (main Step 1, main step N) produce the main results of the workflow, 
     * after (motif enumeration goes here, grouped by motif and result respectively).
     * The R results are the result of the Step component, a Motif goes here step.
     * @param d
     * @return 
     */
    private static String getFunctionalityNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative3 = Constants.getNarrativeStart("Data Narrative 3: Functionality view", "3", Constants.TOOLTIP_NARRATIVE_METHOD);
        //retrieve: steps of the workflow (chain)
        StepCollection template = d.getWorkflowTemplate();
        narrative3 += "The method "+serializeResource(d,template, resourceTypes.template)+" ";
        ArrayList<Step> dataAnalysisMotifs = template.stepsWithMotif(Constants.MOTIF_DATA_ANALYSIS);
        switch (dataAnalysisMotifs.size()) {
            case 0: narrative3+= "does not have one main type of analysis step.";
                break;
            case 1: 
                Step mainStep = dataAnalysisMotifs.get(0);
                narrative3+= "performs a single main type of analysis on the input dataset through the "+serializeResource(d,mainStep, resourceTypes.other)+" step. ";
                //get steps with motifs, order them and serialize them
                narrative3+= hideText(serializeOrderedSteps(d,d.orderSteps(true,template.getStepsWithOneMotifOrMore()), resourceTypes.processAndMotif)+".");
                break;
            default: narrative3+= "performs "+dataAnalysisMotifs.size() +" main types of analysis on the input datasets." ;
                //we only summarize the important data analysis steps
                narrative3+= serializeOrderedSteps(d,d.orderSteps(true,dataAnalysisMotifs), resourceTypes.other)+".";
                break;
        }
        narrative3+="</p><p> The "+serializeResource(d,result, resourceTypes.other)+" results are the product of the ";
        Step processThatProducedTheResults = template.getStep(d.getMethodProcessForResult(result.getUri()));
        narrative3+=""+serializeResource(d,processThatProducedTheResults, resourceTypes.processAndMotif)+".";
        narrative3+=Constants.NARRATIVE_END;
        return narrative3;
    }
    
    
    private static String getAbstractDependencyNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative4 = Constants.getNarrativeStart("Data Narrative 4: Dependency view", "4", Constants.TOOLTIP_NARRATIVE_ABSTRACT);
        StepCollection template = d.getWorkflowTemplate();
        narrative4 += "The method "+serializeResource(d,template, resourceTypes.template)+" has "+template.getSteps().size() +" steps. Their dependency view is specified as follows. ";
        
        narrative4+= serializeOrderedSteps(d,d.orderSteps(true,template.getSteps()), resourceTypes.templateStepAndDependency)+".";
        
        narrative4+="</p><p> The "+serializeResource(d,result, resourceTypes.other)+" results are the product of ";
        Step processThatProducedTheResults = template.getStep(d.getMethodProcessForResult(result.getUri()));
        narrative4+=""+serializeResource(d,processThatProducedTheResults, resourceTypes.other)+".";
        narrative4+=Constants.NARRATIVE_END;
        return narrative4;
    }
    
    private static String getImplementationdNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative5 = Constants.getNarrativeStart("Data Narrative 5: Implementation view", "5", Constants.TOOLTIP_NARRATIVE_IMPL);
        StepCollection execution = d.getWorkflowExecution();
        Resource template = d.getMethodMetadata();
        narrative5 += "The method "+serializeResource(d,template, resourceTypes.template)+" has "+execution.getSteps().size() +" steps. They are implemented as follows"+hideText(" (showing in brackets the general step they implement, if any)")+". ";
        
        narrative5+= serializeOrderedSteps(d,d.orderSteps(false,execution.getSteps()), resourceTypes.executionAndImplentation)+".";
        
        Step processThatProducedTheResults = execution.getStep(d.getExecutionProcessForResult(result.getUri()));
        narrative5+="</p><p>"+hideText(" The "+serializeResource(d,result, resourceTypes.other)+" results are the product of "+serializeResource(d,processThatProducedTheResults, resourceTypes.other)+".");
        narrative5+=Constants.NARRATIVE_END;
        return narrative5;
    }
    
    /**
     * Similar to narrative 5, but instead of showing impl, shows the pointers to the scripts and software.
     * @param d
     * @return 
     */
    private static String getSoftwareNarrative(DataNarrative d){
        //Resource result = d.getResult();
        String narrative6 = Constants.getNarrativeStart("Data Narrative 6: Software view", "6", Constants.TOOLTIP_NARRATIVE_SOFTWARE);
        StepCollection execution = d.getWorkflowExecution();
        Resource template = d.getMethodMetadata();
        narrative6 += "The method "+serializeResource(d,template, resourceTypes.template)+" has "+execution.getSteps().size() +" steps. ";
        narrative6+= hideText(serializeOrderedSteps(d,d.orderSteps(false,execution.getSteps()), resourceTypes.other)+".");
        narrative6+= " The steps use the following software:</p><p>";
        //narrative6+= serializeResourceList(d, execution.getSteps(), resourceTypes.executionAndCode, false, false);
        for (Step s:execution.getSteps()){
            narrative6+= serializeResource(d,s, resourceTypes.executionAndCode);
        }
        narrative6+=Constants.NARRATIVE_END;
        return narrative6;
    }
    
    /**
     * Function designed to add a span around the text in case we want to hide some of the details
     * when pressing the see more/see less buttons.
     * @param inputText
     * @return 
     */
    private static String hideText(String inputText){
        return "<span class=\"see\">"+inputText+"</span>";
    }
    
    public static void main (String [] args){
        // EXAMPLES FOR THE WEB ANALYTICS SHOWCASE DOMAIN
        
        //DETECT TOPICS WORKFLOW
        String workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-DETECTTOPICS-7A1-BF5CF914-816C-4845-AF79-A56672C4BD17";
        String workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/DETECTTOPICS-D751713988987E9331980363E24189CE";
        String resultURI = "http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/IMAGE1476159251570";
        String doiFile = "examples//highlyConnectedDrugs//doiAnnotations.ttl";
        String motifAnnotations = "examples//highlyConnectedDrugs//motifAnnotations.ttl";
        doiFile = "examples//highlyConnectedDrugs//doiAnnotations.ttl";
        motifAnnotations = "examples//highlyConnectedDrugs//motifAnnotations.ttl";
       String ontoSoftFile = "examples//webAnalytics//ontosoftAnnotations.ttl";
      
       //ontoSoftFile =null;
//        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
//        DataNarrative d = new DataNarrative(dc, resultURI);
//        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "TOPICS-IMAGE1476159251570.html");
        
        //ACCESSIBILITY ANALYTICS
        workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-ACCESSIBILITYANA-28FE1AF0-4994-4B35-B8AE-5678E7F02B1C";
        workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/ACCESSIBILITYANALYTICS-D751713988987E9331980363E24189CE";
        resultURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/IMAGE1476147155422";
//        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
//        DataNarrative d = new DataNarrative(dc, resultURI);
//        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "ACCESSIBILITY-IMAGE1476147155422.html");
        
        //UNDERSTANDING USER ACTIVITY
//        workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-UNDERSTANDINGUSE-4787F99D-BC79-4914-BC40-343D254BB3CE";
//        workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/UNDERSTANDINGUSERACTIVITY-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/CLUSTERINGRESULT1475981346023";
////        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
////        DataNarrative d = new DataNarrative(dc, resultURI);
////        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "UNDERSTANDING-CLUSTERINGRESULT1475981346023.html");
//        
//        
//        //ADRESS PROBLEMATIC USERS
//        workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-ADDRESSPROBLEMAT-40285D81-78C6-4913-BE9D-89AC50B16161";
//        workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/ADDRESSPROBLEMATICUSERS-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/FILTEREDDOCUMENT11475981333362";
////        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
////        DataNarrative d = new DataNarrative(dc, resultURI);
////        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "ADDRESS-FILTEREDDOCUMENT11475981333362.html");
//        
//        //TRENDING WORDS VISUALIZATION
//        workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-TRENDINGWORDSVIS-9E34FF2A-1998-4BC8-B6D2-BBFB7212A562";
//        workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/TRENDINGWORDSVISUALIZATION-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/IMAGE1475981286849";
////        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
////        DataNarrative d = new DataNarrative(dc, resultURI);
////        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "TRENDING-IMAGE1475981286849.html");
//        
        //TEST RAVALI DOMAIN
//      workflowExecutionURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionAccount/ACCOUNT-PEPTIDE_SEARCH-A-7FA05399-1913-4E1A-919E-4490E1163AEA";
//     workflowTemplateURI="http://www.opmw.org/export/omics/resource/WorkflowTemplate/PEPTIDE_SEARCH-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionArtifact/MERGEDPEPXML1479478029209";
//                   http://www.opmw.org/export/omics/resource/WorkflowExecutionArtifact/MERGEDPEPXML1479478029209
        
//        //VARIANT CALLER
//       // motifAnnotations = "examples\\spellbook\\motifAnnotations.ttl";
//        workflowExecutionURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionAccount/ACCOUNT-VARIANTCALLING-2-278FE1EC-E086-4DBB-A1E0-1D49303A8E92";
//        workflowTemplateURI="http://www.opmw.org/export/omics/resource/WorkflowTemplate/VARIANTCALLING-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionArtifact/INDELCALLS1496098149950";
////        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
////        DataNarrative d = new DataNarrative(dc, resultURI);
////        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "TEST-VARIANT_CALLING.html");
//        
//        //VARIANT RESEQUENCING
//        workflowExecutionURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionAccount/ACCOUNT-VARIANTCALLING_R-472B670A-2813-4B6B-A2A7-BA72D0CDF00F";
//        workflowTemplateURI="http://www.opmw.org/export/omics/resource/WorkflowTemplate/VARIANTCALLING_RESEQUENCING-D751713988987E9331980363E24189CE";
//        resultURI="http://www.opmw.org/export/omics/resource/WorkflowExecutionArtifact/FILTDATAFILE1496101618223";
        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations, ontoSoftFile);
        DataNarrative d = new DataNarrative(dc, resultURI);
       // DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "TEST-VARIANT_CallingResequencing.html");
        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "ACCESSIBILITY-IMAGE_VIJSHRAV.html");
       
       
    }
    
   
    
}
