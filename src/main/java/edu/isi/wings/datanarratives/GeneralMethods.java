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

/**
 *
 * @author dgarijo
 */
public class GeneralMethods {
    /*leaving this for compatibility, but will be removed**/
    public static ResultSet queryLocalRepository(OntModel model, String queryIn){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet rs = qe.execSelect(); 
        return rs;
    }
    
    public static String splitCamelCase(String input){
        String[] result = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        String stringWithSpaces ="";
        for (String s : result) {
            stringWithSpaces+=s+" ";
        }
        return stringWithSpaces.trim();
    }
    
    public static String getFileNameFromURL(String input){
        return splitCamelCase(input.substring(input.lastIndexOf("/")+1).replace("_", " ")).trim();
        
    }
    
    /**
     * This function removes everything after the hypen of a string. Used to clean up names.
     * @param input
     * @return 
     */
    public static String removeHypen(String input){
        String clean = input;
        if(input.contains("-")){
            clean = clean.substring(0,input.lastIndexOf("-"));
        }
        return clean;
        
    }
    
//    public static void main(String[] args){
//        System.out.println(splitCamelCase("highlyConnectedDrugsVALUE"));
//        System.out.println(getFileNameFromURL("http://www.opmw.org/export/4.0/resource/WorkflowExecutionArtifact/WORDFILE1475897421962"));
//    }
}
