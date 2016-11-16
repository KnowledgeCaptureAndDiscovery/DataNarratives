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
import edu.isi.wings.elements.StepCollection;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import edu.isi.wings.elements.Software;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the main class for using Data Narratives.
 * A Data narrative has a context and a knowledge base. 
 * Then, it has a series of operations for retrieving relevant stuff from the KB
 * @author dgarijo
 */
public class DataNarrative {
    //A data narrative has a context and a knowledge base. Then everything is built from there.
    //The DANA also has all the operations for the serializer to build up the text.
    private final KnowledgeBase kb;
    private final DataNarrativeContext context;
    private Resource result; //result to track, i.e., the subject of the narrative.

    public DataNarrative(DataNarrativeContext context, String resultURI) {
        this.context = context;
        //normally it would be created from the context; this is a test.
        kb = new KnowledgeBase("C:\\Users\\dgarijo\\Dropbox (OEG-UPM)\\NetBeansProjects\\DatNarratives\\kb.ttl");
        //end test
        this.result = new Resource(resultURI, resultURI);
        //result.setName(this.getVariableNameForResource(result));
        //I like this more than the variable name for the results. It can be changed
        result.setName(this.getValueForProperty(result, Constants.RDFS_LABEL).replace("Workflow execution artifact: ", ""));
        result.setLocation(this.getValueForProperty(result, Constants.OPMW_LOCATION));        
    }

    public Resource getResult() {
        return result;
    }

    public DataNarrativeContext getContext() {
        return context;
    }

    public KnowledgeBase getKb() {
        return kb;
    }

    public void setResult(Resource result) {
        this.result = result;
    }
    
    /**
     * Generic method for retrieving a SINGLE value for a property in a resource.
     * If there are several values for the property, only the first one will be returned.
     * @param r
     * @param property
     * @return 
     */
    public String getValueForProperty(Resource r, String property){
        String q = Queries.getValueForResourceProperty(r, property);
        ResultSet rs = kb.selectFromLocalRepository(q);
        if (rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getLiteral("?v").getString();
        }
        return null;
    }
    
    /**
     * Lightweight method for retrieving the template name and uri
     * @return 
     */
    public Resource getMethodMetadata(){
        String q = Queries.getMethodMetadata(this.result.getUri());
        ResultSet rs = kb.selectFromLocalRepository(q);
        if(rs.hasNext()){
            String methodName = null, methodURI = null;
            QuerySolution qs = rs.next();
            methodURI = qs.getResource("temp").getURI();
            methodName = qs.getLiteral("wfname").getString();
            return new Resource(methodName, methodURI);
        }else{
            return null;
        }
    }
    
    public StepCollection getWorkflowTemplate(){
        Resource r = getMethodMetadata();
        StepCollection temp = new StepCollection(r.getName(), r.getUri());
        //add all the processes. Add all the motifs
        ArrayList<Step> processes = new ArrayList<>();
        String q = Queries.getMethodProcesses(this.result.getUri());
        ResultSet rs = kb.selectFromLocalRepository(q);
        HashMap<String,ArrayList<String>> steps = new HashMap<>();
        while(rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            String process, name, motif=null; 
            process = qs.getResource("process").getURI();
            //THIS SHOULD BE DONE MORE GENERICALLY WHEN SERIALIZING, NOT HERE.
            name = qs.getLiteral("name").getString().replace("Workflow template process ", "").replace("Node", "");
            try{
                motif = qs.getResource("motif").getURI();
            }catch(Exception e){
                //no need to catch
            }
            if(steps.containsKey(process)){
                if(motif!=null && !"".equals(motif)){
                    steps.get(process).add(motif);
                }
            }else{
                Step s  = new Step(name, process);
                ArrayList<String> motifs = new ArrayList<>();
                s.setMotifs(motifs);
                if(motif!=null && !"".equals(motif)){
                    motifs.add(motif);
                }
                steps.put(process,motifs);
                processes.add(s);
            }
        }
        //since we have already initialized this with the previous loop, there
        //is no need to re read the hashMap
        temp.setSteps(processes);
        return temp;
    }
    
    /**
     * Lightweight method for retrieving the execution name and uri
     * -future improvement: probably can be merged with the template one-
     * @return 
     */
    public Resource getExecutionMetadata(){
        String q = Queries.getExecutionMetadata(this.result.getUri());
        ResultSet rs = kb.selectFromLocalRepository(q);
        if(rs.hasNext()){
            String methodName = null, methodURI = null;
            QuerySolution qs = rs.next();
            methodURI = qs.getResource("uri").getURI();
            methodName = qs.getLiteral("wfname").getString();
            return new Resource(methodName, methodURI);
        }else{
            return null;
        }
    }
    
    //this method could probably merged with getWorkflowTemplate, as we are retrieving steps here as well.
    public StepCollection getWorkflowExecution(){
        Resource r = getExecutionMetadata();
        StepCollection temp = new StepCollection(r.getName(), r.getUri());
        //add all the processes. Add all the motifs
        ArrayList<Step> processes = new ArrayList<>();
        String q = Queries.getExecutionProcesses(this.result.getUri());
        ResultSet rs = kb.selectFromLocalRepository(q);
        HashMap<String,ArrayList<String>> steps = new HashMap<>();
        while(rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            String process, name, impl, implName,code; 
            process = qs.getResource("process").getURI();
            impl = qs.getResource("impl").getURI();
            //THIS SHOULD BE DONE MORE GENERICALLY WHEN SERIALIZING, NOT HERE.
            name = qs.getLiteral("name").getString().replace("Execution process ", "").replace("Node", "");
            implName = qs.getLiteral("impln").getString().replace("Workflow template process ", "").replace("Node", "");
            code = qs.getLiteral("code").getString();
            Step s = new Step(name, process);
            s.setImplementationOf(new Step(implName, impl));
            s.setCodeLocation(code);
            processes.add(s);
        }
        temp.setSteps(processes);
        return temp;
     }
    
    /**
     * Given a step name, this method aims to find if there is any information
     * with the step metadata. The Ontosoft vocabulary is used.
     * Only one software entry is returned.
     * @param step
     * @return 
     */
    public Software getSoftwareMetadata(Step step){
        //labels have to be exactly the same as in ontosoft. otherwise this wont work
        String q = Queries.getSoftwareMetadata(GeneralMethods.splitCamelCase(step.getName()));
        ResultSet rs = kb.selectFromLocalRepository(q);
        Software s = new Software(step.getName(),"");//we dont know the software URI at this stage
        String lic="", lan="", code="", web="";
        if(rs.hasNext()){
           QuerySolution qs = rs.nextSolution();
           //all are optional.
           try{
                lic = qs.getResource("lic").getURI();
           }catch(Exception e){}
           try{
                lan = qs.getResource("lan").getURI().replace("http://ontosoft.org/software#", "");
           }catch(Exception e){}
           try{
                code = qs.getLiteral("code").getString();
           }catch(Exception e){}
           try{
                web = qs.getLiteral("web").getString();
           }catch(Exception e){}
        }
        s.setCodeLocation(code);
        s.setLicense(lic);
        s.setProgrammingLanguage(lan);
        s.setWebsite(web);
        return s;
    }
   
    
    /**
     * This function helps finding variable names in results, so as to make them more eye catchy.
     * @param r
     * @return 
     */
    public String getVariableNameForResource(Resource r){
        String query = Queries.getVariableNameForResource(r.getUri());
        ResultSet rs = kb.selectFromLocalRepository(query);
        if (rs.hasNext()){
          return rs.next().getLiteral("name").getString().replace("Data variable", "").trim();
        }
        return null;
    }
    
    public ArrayList<Resource> getMethodInputs(){
        String query = Queries.getInputsOfMethod(this.result.getUri());
        ArrayList inputs = new ArrayList<>();
        ResultSet rs = kb.selectFromLocalRepository(query);
        while(rs.hasNext()){
            String input="", loc ="#", name= "";
            QuerySolution qs = rs.nextSolution();
            input=qs.getResource("input").getURI();
            name=qs.getLiteral("name").getString();
            Literal l = qs.getLiteral("l");
            if (l!=null){
                loc = l.getString();
            }
            Resource r = new Resource(name, input);
            r.setLocation(loc);
            inputs.add(r);
        }
        return inputs;
    }
    
    public ArrayList<Resource> getMethodInputParameters(){
        String query = Queries.getInputParametersOfMethod(this.result.getUri());
        ArrayList inputs = new ArrayList<>();
        ResultSet rs = kb.selectFromLocalRepository(query);
        while(rs.hasNext()){
            String input="", value ="#", name="";
            QuerySolution qs = rs.nextSolution();
            input=qs.getResource("input").getURI();
            Literal l = qs.getLiteral("v");
            name = qs.getLiteral("name").getString();
            if (l!=null){
                value = l.getString();
            }
            Resource r = new Resource(name, input);
            r.setValue(value);
            inputs.add(r);
        }
        return inputs;
    }
    
    
    /**
     * Given a URI of a resource, this method will extract the original sources
     * from which it was derived.
     * @param uri
     * @return 
     */
    public ArrayList<Resource> getOriginalSourcesForResult(String uri){
        String q = Queries.getOriginalSourcesForResource(uri);
        ArrayList<Resource> originalSources= new ArrayList<>();
        ResultSet rs = kb.selectFromLocalRepository(q);
        while(rs.hasNext()){
            String input="", loc ="#", name="";
            QuerySolution qs = rs.nextSolution();
            input=qs.getResource("input").getURI();
            name = qs.getLiteral("loc").getString();
            Literal l = qs.getLiteral("loc");
            if (l!=null){
                loc = l.getString();
            }
            Resource r = new Resource(name, input);
            r.setLocation(loc);
            originalSources.add(r);
        }
        return originalSources;
    }
    
    /**
     * Method that returns the step of the workflow (template) which produced the result
     * in the execution. We return the URI, name
     * @param result 
     * @return  
     */
    public String getMethodProcessForResult(String result){
        String q = Queries.getMethodProcessForResult(result);
        ResultSet rs = kb.selectFromLocalRepository(q);
        if(rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getResource("step").getURI();
        }
        return null;
    }
    
    /**
     * Method that returns the step of the workflow (execution) which produced the result
     * in the execution. We return the URI, name
     * @param result 
     * @return  
     */
    public String getExecutionProcessForResult(String result){
        String q = Queries.getExecutionProcessForResult(result);
        ResultSet rs = kb.selectFromLocalRepository(q);
        if(rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            return qs.getResource("step").getURI();
        }
        return null;
    }
    
    /**
     * Method that returns the step dependencies in the template or execution
     * @param step
     * @param isTemplate
     * @return 
     */
    public ArrayList<Step> getStepDependencies(Step step, boolean isTemplate){
        String query;
        ArrayList<Step> deps = new ArrayList<>();
        if(isTemplate){
            query = Queries.getStepDependencies(step, "<"+Constants.OPMW_USES+">/<"+Constants.OPMW_IGB+">");
        }else{//execution
            query = Queries.getStepDependencies(step, "<"+Constants.OPMW_USED+">/<"+Constants.OPMW_WGB+">");
        }
        ResultSet rs = kb.selectFromLocalRepository(query);
        while(rs.hasNext()){
            QuerySolution qs = rs.nextSolution();
            Step s = new Step(qs.getLiteral("name").getString().replace("Workflow template process ", "").replace("Workflow execution process ", "").replace("Node", ""),qs.getResource("step").getURI());
            deps.add(s);
        }
        return deps;
    }
    
    /**
     * Method that returns true is the process p1 depends on p2 (at any length in the graph)
     * @param isTemplate
     * @param p1
     * @param p2
     * @return 
     */
    public boolean workflowStepDependsOn(boolean isTemplate, Step p1, Step p2){
        String query = Queries.stepDependsOnStep(isTemplate, p1.getUri(), p2.getUri());
        return kb.askFromLocalRepository(query);
    }
    
    /**
     * Method that returns the steps in the workflow in the order of execution.
     * Each ArrayList of steps in the result represent the parallel steps that can be executed simultaneously.
     * Example: [1,5][2,4][3,6] would represent that first steps 1 and 5 are executed, then 2 and 4 depend somehow on them,
     * and then 3 and 6 depend on 2 or 4.
     * Each sublist cannot have steps that depend on each other. Therefore, a workflow with 3 consecutive steps [1,2,3]
     * will result in [1][2][3]
     * @param isTemplate indicates if we are ordering template steps or execution steps
     * @param inputSteps collection of steps to order (it is assumed they belong to the same template)
     * @return 
     */
    public ArrayList<ArrayList<Step>> orderSteps(boolean isTemplate, ArrayList<Step> inputSteps) {
        //clone the steps into a new structure
        ArrayList<Step>steps = new ArrayList<>();
        for(Step s:inputSteps){
            steps.add(s);//no need to clone the steps themselves, just the list
        }
        //retrieve the processes in m that don't depend on the rest.
        ArrayList<ArrayList<Step>> orderedList = new ArrayList<>();
        int i=0;
        ArrayList<Step> independentSteps= new ArrayList<>();
        while(steps.size()>0){
            boolean depends = false;
            Step p1 = steps.get(i);
            for(Step p:steps){
                if(!p.equals(p1)){
                    if(workflowStepDependsOn(isTemplate, p1, p)){
                        depends = true;
                        //System.out.println(p1.getUri()+"depends on "+p.getUri());
                    }
                }
            }
            if(!depends){
                independentSteps.add(p1);
            }
            i++;
            //if we have reached the end, save current list and delete object from step
            //since we have checked that all the steps in indepSteps are independent, 
            //there is no need to check whether they depend on each other.
            if(i==steps.size()){//not size-1 because we increment de i just now.
                orderedList.add(independentSteps);
                //delete from steps
                steps.removeAll(independentSteps);
                independentSteps = new ArrayList<>();
                i=0;
            }
        }
        return orderedList;
    }
    
    public static void main(String[] args){
        String workflowExecutionURI="http://www.opmw.org/export/4.0/resource/WorkflowExecutionAccount/ACCOUNT-DETECTTOPICS-7A1-BF5CF914-816C-4845-AF79-A56672C4BD17";
        String workflowTemplateURI="http://www.opmw.org/export/4.0/resource/WorkflowTemplate/DETECTTOPICS-D751713988987E9331980363E24189CE";
        String resultURI = "http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/IMAGE1476159251570";
        String doiFile = "examples\\webAnalytics\\doiAnnotations.ttl";
        String motifAnnotations = "examples\\webAnalytics\\motifAnnotations.ttl";
        DataNarrativeContext dc = new DataNarrativeContext(workflowExecutionURI, workflowTemplateURI, doiFile, motifAnnotations);
        DataNarrative test = new DataNarrative(dc, resultURI);
        
//        Resource r = test.getMethod();
//        System.out.println(r.getName()+" \n"+ r.getUri());
//        
//        System.out.println(test.getResult().getName());
        System.out.println(test.getMethodInputs().get(0).getLocation());
    }
}
