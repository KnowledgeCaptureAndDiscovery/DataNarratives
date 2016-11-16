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
package edu.isi.wings.elements;

import java.util.ArrayList;

/**
 * Class to represent a workflow template. The purpose of this class is to agilize
 * several operations on step collections: get a step from a collection, number of motifs
 * of the steps, etc.
 * @author dgarijo
 */
public class StepCollection extends Resource{

    ArrayList<Step> steps;
    
    public StepCollection (String name, String uri){
        super(name, uri);   
    }
    
    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }
    
    /**
     * Given a URI, it will return the step with that URI. Otherwise it will return null
     * @param uri
     * @return 
     */
    public Step getStep(String uri){
        for(Step c:steps){
            if(c.getUri().equals(uri)){
                return c;
            }
        }
        return null;
    }
    
    /**
     * Function to get the steps of one particular type of motif
     * @param motifType
     * @return 
     */
    public ArrayList<Step> stepsWithMotif(String motifType){
        ArrayList<Step> stepsWithMotif = new ArrayList<>();
        for(Step c:steps){
            ArrayList<String> motifsForStep = c.getMotifs();
            for (String motif:motifsForStep){
                if(motif.equals(motifType)){
                    stepsWithMotif.add(c);
                    break;
                }
            }
        }
        return stepsWithMotif;
    }
    
    /**
     * Function that retrieves those steps which have at least one motif  
     * @return 
     */
    public ArrayList<Step> getStepsWithOneMotifOrMore(){
        ArrayList<Step> stepsWithMotif = new ArrayList<>();
        for(Step c:steps){
            if(!c.getMotifs().isEmpty()){
                stepsWithMotif.add(c);
            }
        }
        return stepsWithMotif;
    }
    
}
