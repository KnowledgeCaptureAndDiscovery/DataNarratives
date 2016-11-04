package edu.isi.wings.datanarratives;

import Elements.Resource;
import Elements.Step;
import Elements.WorkflowTemplate;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that takes a data narrative and creates an html serialization of it.
 * The error capture can be clearly improved. This is just a prototype.
 * @author dgarijo
 */
public class DataNarrativeHTMLSerializer {
    
    private enum resourceTypes {executionArtifact, parameter, template, motif, processAndMotif, templateStepAndDependency, string, other};//other refers to the default behavior
    
    public static void dataNarrativeToHTML(DataNarrative d, String outPath){
        String htmlPage = Constants.HTML_HEAD;
        htmlPage+=Constants.HTML_BODY_BEGIN;
        //add title: resource name and URI
        htmlPage+="<h3 class=\"text-muted\">Data narratives for result: <a href=\""+d.getResult().getLocation()+"\">"+GeneralMethods.splitCamelCase(d.getResult().getName())+"</a></h3>\n";
        
        
        htmlPage+="<div class=\"row\">\n";
        htmlPage+="<div class=\"col-xs-12 col-md-12\">\n" +
"	  <div class=\"panel-group\">\n";
        //insert data narratives here
        htmlPage+= getExecutionNarrative(d);
        //data view
        htmlPage+= getDataOrientedNarrative(d);
        //method view (motif)
        htmlPage+= getMethodNarrative(d);
        //step view (abstract) TO DO
        htmlPage+= getAbstractMethodNarrative(d);
        //implementation view
        //htmlPage+= getImplementationdNarrative(d);
        
        //software view TO DO
        htmlPage+="</div>\n" +
"	</div>";
        
        //Wf visualizations here
/*        htmlPage+="<div class=\"col-xs-6 col-md-4\">";
        //vis go here
        //replace visualizations with the right ones
        htmlPage+="<div class=\"panel panel-primary\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  <a data-toggle=\"collapse\" href=\"#collapseWf\">Workflow Visualization (Abstract method)</a>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapseWf\" class=\"panel-collapse collapse \">\n" +
"			<img src=\""+d.getWorkflowTemplateVisualization()+"\" width=\"100%\"/>\n" +
"		  </div>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"panel panel-primary\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  <a data-toggle=\"collapse\" href=\"#collapseEx\">Workflow Execution Visualization</a>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapseEx\" class=\"panel-collapse collapse\">\n" +
"			<img src=\""+d.getWorkflowExecutionVisualization()+"\" width=\"100%\"/>\n" +
"		  </div>\n" +
"		</div>";
        htmlPage+="</div>";*/
        
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
                    list+= "dataset "+serializeResource((Resource)s, type);
                    if(showVariables){
                        list+= "(variable "+d.getVariableNameForResource(((Resource)s))+")";
                    }
                    break;
                case parameter: 
                    list+="parameter "+serializeResource((Resource)s, type);
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
                    list+= serializeResource((Resource)s, type);
                    ArrayList<Step> dependencies = d.getStepDependencies((Step)s, true);
                    if(!dependencies.isEmpty()){
                        list+=" (using data from "+serializeResourceList(d, dependencies, resourceTypes.other, false, false)+")";
                    }
                    break;
                default:
                    list+= serializeResource((Resource)s, type);
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
    
    private static String serializeResource(Resource r, resourceTypes type){
        switch (type){
            case executionArtifact:
                return "<a href=\""+r.getLocation()+"\">"+GeneralMethods.getFileNameFromURL(r.getLocation())+"</a> ";
            case parameter:
                return "<a href=\""+r.getUri()+"\">"+GeneralMethods.getFileNameFromURL(r.getUri())+"</a> ";
            case template:
                //templates may have an hyphen, remove
                return "<a href=\""+r.getUri()+"\">"+GeneralMethods.splitCamelCase(GeneralMethods.removeHypen(r.getName()))+"</a> ";
            case processAndMotif:
                String serialization = serializeResource(r, resourceTypes.other);
                ArrayList<String> motifs = ((Step)r).getMotifs();
                if(motifs.size()>0){
                    serialization += " ("+ serializeResourceList(null,motifs, resourceTypes.motif,false,false)+" motif)";
                }
                return serialization;
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
        narrative1+= "	The "+serializeResource(method, resourceTypes.template)+" method was run on ";
        narrative1+=serializeResourceList(d, d.getMethodInputs(), resourceTypes.executionArtifact, false, true);
        ArrayList<Resource> params = d.getMethodInputParameters();
        if(!params.isEmpty()){
            narrative1 +=", with ";
        }
        narrative1+=serializeResourceList(d, params, resourceTypes.parameter, true, false);
        narrative1+=". The "+serializeResource(result,resourceTypes.other)+ " results are stored <a href=\""+d.getResult().getLocation()+"\">online</a>";
        if(resultDOI != null && !resultDOI.equals("")){
            narrative1+="(DOI <a href=\""+resultDOI+"\">"+resultDOI+"</a>)\n";
        }
        narrative1+="."+Constants.NARRATIVE_END;
        return narrative1;
    }
    
    public static String getDataOrientedNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative2 = Constants.getNarrativeStart("Data Narrative 2: Data view", "2", Constants.TOOLTIP_NARRATIVE_DATA);
        narrative2+=" The "+serializeResource(result, resourceTypes.other)+" results have been derived from the ";
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
    private static String getMethodNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative3 = Constants.getNarrativeStart("Data Narrative 3: Method view", "3", Constants.TOOLTIP_NARRATIVE_METHOD);
        //retrieve: steps of the workflow (chain)
        WorkflowTemplate template = d.getWorkflowTemplate();
        narrative3 += "The method "+serializeResource(template, resourceTypes.template)+" ";
        ArrayList<Step> dataAnalysisMotifs = template.stepsWithMotif(Constants.MOTIF_DATA_ANALYSIS);
        switch (dataAnalysisMotifs.size()) {
            case 0: narrative3+= "does not have one main type of analysis step.";
                break;
            case 1: 
                Step mainStep = dataAnalysisMotifs.get(0);
                narrative3+= "performs a single main type of analysis on the input dataset through the "+serializeResource(mainStep, resourceTypes.other)+" step. ";
                //get steps with motifs, order them and serialize them
                narrative3+= serializeOrderedSteps(d,d.orderSteps(template.getStepsWithOneMotifOrMore()), resourceTypes.processAndMotif)+".";
                break;
            default: narrative3+= "performs "+dataAnalysisMotifs.size() +" main types of analysis on the input datasets." ;
                //we only summarize the important data analysis steps
                narrative3+= serializeOrderedSteps(d,d.orderSteps(dataAnalysisMotifs), resourceTypes.other)+".";
                break;
        }
        narrative3+="</p><p> The "+serializeResource(result, resourceTypes.other)+" results are the product of the ";
        Step processThatProducedTheResults = template.getStep(d.getMethodProcessForResult(result.getUri()));
        narrative3+=""+serializeResource(processThatProducedTheResults, resourceTypes.processAndMotif)+".";
        narrative3+=Constants.NARRATIVE_END;
        return narrative3;
    }
    
    
    private static String getAbstractMethodNarrative(DataNarrative d){
        Resource result = d.getResult();
        String narrative4 = Constants.getNarrativeStart("Data Narrative 4: Abstract method view", "4", Constants.TOOLTIP_NARRATIVE_ABSTRACT);
        WorkflowTemplate template = d.getWorkflowTemplate();
        narrative4 += "The method "+serializeResource(template, resourceTypes.template)+" has "+template.getSteps().size() +" steps. ";
        
        narrative4+= serializeOrderedSteps(d,d.orderSteps(template.getSteps()), resourceTypes.templateStepAndDependency);
        
        narrative4+="</p><p> The "+serializeResource(result, resourceTypes.other)+" results are the product of the ";
        Step processThatProducedTheResults = template.getStep(d.getMethodProcessForResult(result.getUri()));
        narrative4+=""+serializeResource(processThatProducedTheResults, resourceTypes.other)+".";
        narrative4+=Constants.NARRATIVE_END;
        return narrative4;
    }
    
    /**
     * Template for generating an implementation narrative. The template is similar to the 
     * method one, but stating the software used for each of the steps:
     * The W method performs N main types of analyses on the original datasets.
     * The (main Step 1, main step N) produce the main results of the workflow, 
     * after (motif enumeration goes here, grouped by motif and result respectively).
     * The R results are the result of the Step component, a Motif goes here step.
     * @param d
     * @return 
     */
    private static String getImplementationdNarrative(DataNarrative_OLD d){
        //THIS NARRATIVE IS MISSING SOME DETAILS ON THE IMPL, and mixing it with motifs
        // TO REVIEW!!!
        String narrative4 = "<div class=\"panel panel panel-info\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  Data Narrative 4: Implementation view  &nbsp;&nbsp;&nbsp;&nbsp;  <button class=\"SeeMore2 btn btn-primary\" data-toggle=\"collapse\" href=\"#collapse4\">See More</button>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapse4\" class=\"panel-collapse collapse\">\n" +
"			<div class=\"panel-body\">\n" +
"			<p> \n";
        //retrieve: steps of the workflow (chain)
        ArrayList<String> allProcesses = d.getProcesses();
        narrative4 += "The method <a href=\""+d.getMethodURI()+"\">"+GeneralMethods.splitCamelCase(d.getMethodName())+"</a> ";
        //motif, steps that are that motif.
        HashMap<String, ArrayList<String>> motifsAndProcesses = new HashMap<>();
        ArrayList<String> process;
        for(String p:allProcesses){
            ArrayList<String> motifs = d.getMotifsForProcess(p);
            if(!motifs.isEmpty()){
                for(String m:motifs){
                    if(motifsAndProcesses.containsKey(m)){
                        process = motifsAndProcesses.get(m);
                    }else{
                        process = new ArrayList<>();
                    }
                    process.add(p);
                    motifsAndProcesses.put(m, process);
                }
            }
        }
        String implementations =" ";
        int dataAnalysisMotifs = 0;
        if(motifsAndProcesses.containsKey("http://purl.org/net/wf-motifs#DataAnalysis")){
                dataAnalysisMotifs = motifsAndProcesses.get("http://purl.org/net/wf-motifs#DataAnalysis").size();
        }
        if(dataAnalysisMotifs>0){
            
            //if there is only one step, add the rest of the motifs like: after blah and blah, the step is executed.
            ArrayList<String> m = motifsAndProcesses.get("http://purl.org/net/wf-motifs#DataAnalysis");
            //dataAnalysisMotifs = 1; //for tests 
            if(dataAnalysisMotifs == 1){
                String mainProcess = m.get(0);
                //tests
                //mainProcess = "http://www.opmw.org/export/resource/WorkflowTemplateProcess/ABSTRACTGLOBALWORKFLOW2_COMPAREDISSIMILARPROTEINSTRUCTURES";
                narrative4+= "performs "+dataAnalysisMotifs +" main type of analysis on the input datasets. The "+
                        " <a href=\""+mainProcess+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(mainProcess))+"</a> step produces the main results of the workflow";
                ArrayList<String> dependencies = d.getDependenciesForWorkflowStep(mainProcess);
                //remove the main step
                dependencies.remove(mainProcess);//just in case
                if(dependencies.size()>0){
                    ArrayList<String> motifs;
                    if(dependencies.size() ==1){
                        String dep = dependencies.get(0);
                        motifs = d.getMotifsForProcess(dep);
                        if(motifs.size()>0){//at this stage we only consider 1 motif per step.
                            narrative4+=", after a "+motifs.get(0).replace("http://purl.org/net/wf-motifs#","").replace("Data", "")+" step ("+GeneralMethods.getFileNameFromURL(dep)+").";
                        }
                    }else{
                        String textToAdd ="";
                        for(String dep:dependencies){
                            motifs = d.getMotifsForProcess(dep);
                            if("".equals(textToAdd) && !motifs.isEmpty()){
                                textToAdd +=", after";
                            }
                            if(dependencies.indexOf(dep)==dependencies.size()-1){
                                if(!motifs.isEmpty()){//at this stage we only consider 1 motif per step.
                                    textToAdd+="and a "+motifs.get(0).replace("http://purl.org/net/wf-motifs#","").replace("Data", "")+" step ("+GeneralMethods.splitCamelCase(d.getNameForStep(dep))+").";
                                }
                            }else
                                if(!motifs.isEmpty()){//at this stage we only consider 1 motif per step.
                                    textToAdd+=" a "+motifs.get(0).replace("http://purl.org/net/wf-motifs#","").replace("Data", "")+" step ("+GeneralMethods.splitCamelCase(d.getNameForStep(dep))+"),";
                                }
                        }
                        narrative4 += textToAdd;
                    }
                }else{
                    narrative4+=".";
                }
                //retrieve de steps that depend on the main one (easy query)
            }else{
                narrative4+= "performs "+dataAnalysisMotifs +" main types of analysis on the input datasets. ";
                //if there are more, then just say their order
                
                //reorder m according to the dependencies.
                m = d.reorderResults(m);
                for(String currentStep:m){
                    int i = m.indexOf(currentStep);
                    if(i==0){
                        narrative4+="First, the <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a> step processes the datasets, then the data is analyzed by the";
                    }
                    else if(i>=1 &&i<m.size()-1){
                        narrative4+=" <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a>,";
                    }else{
                        if(m.size()>2){
                            narrative4 = narrative4.substring(0, narrative4.length()-1);
                            narrative4 +=" steps and finally, ";
                        }
                        narrative4+=" <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a> produces the end results.";
                    }
                    implementations +=" The "+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+ " component has been implemented with the <a href=\""+d.getImplementationURL(currentStep)+"\">" +d.getImplementationName(currentStep)+"</a> software.";
                }
            }
        }else{
            narrative4+= "does not have one main type of analysis step.";
        }
        narrative4 += implementations;
        narrative4+="</p><p> The <a href=\"" +d.getResultURI()+"\">"+GeneralMethods.splitCamelCase(d.getResultName())+"</a> results are the product of the ";
        String [] aux = d.getMethodProcessForResult(d.getResultURI()).split(",");
        String methodStepURI = aux[0];
        String methodName = GeneralMethods.splitCamelCase(aux[1]);
        ArrayList<String> motifsOfStep = d.getMotifsForProcess(methodStepURI);
        narrative4+="<a href=\""+methodStepURI+"\">"+methodName+"</a>";
        if(!motifsOfStep.isEmpty()){
            narrative4+=", a ";
            if(motifsOfStep.size()>1){
                for(String a:motifsOfStep){
                    if(motifsOfStep.indexOf(a) == motifsOfStep.size()-1){
                        narrative4+=" and "+"<a href=\""+a+"\">"+GeneralMethods.splitCamelCase(a.replace("http://purl.org/net/wf-motifs#", ""))+"</a>";
                    }else{
                        narrative4+="<a href=\""+a+"\">"+GeneralMethods.splitCamelCase(a.replace("http://purl.org/net/wf-motifs#", ""))+"</a>"+",";
                    }
                }
            }else{
                narrative4+="<a href=\""+motifsOfStep.get(0)+"\">"+GeneralMethods.splitCamelCase(motifsOfStep.get(0).replace("http://purl.org/net/wf-motifs#", ""))+"</a>";
            }
            narrative4+=" step.";
        }
        //implementation
        narrative4 += " The "+methodName+" component has been implemented with the <a href=\""+d.getImplementationURL(methodStepURI)+"\">" +d.getImplementationName(methodStepURI)+"</a> software.";
        
        narrative4+="			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
        return narrative4;
    }
    
    public static void main (String [] args){
        //test skeleton
        String workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-DETECTTOPICS-7A1-BF5CF914-816C-4845-AF79-A56672C4BD17";
        String workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/DETECTTOPICS-D751713988987E9331980363E24189CE";
        String resultURI = "http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/IMAGE1476159251570";
        String doiFile = "examples\\webAnalytics\\doiAnnotations.ttl";
        String motifAnnotations = "examples\\webAnalytics\\motifAnnotations.ttl";
        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations);
        DataNarrative d = new DataNarrative(dc, resultURI);
        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "test.html");
    }
    
   
    
}
