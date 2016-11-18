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


/**
 * A Data Narrative context has the elements to create the knowledge base
 * @author dgarijo
 */
public class DataNarrativeContext {
    private final String workflowExecutionURI;
    private final String workflowTemplateURI;
    private final String doiFile;//file associating dois to artifacts
    private final String motifAnnotations;//anotatedMotis
    
    //TEMPORAL VARIABLE
    private final String ontoSoftAnnotations;//optional. Added until the OntoSoft integration is completed.

    public DataNarrativeContext(String workflowExecutionURI, String workflowTemplateURI, String doiFile, String motifAnnotations) {
        this.workflowExecutionURI = workflowExecutionURI;
        this.workflowTemplateURI = workflowTemplateURI;
        this.doiFile = doiFile;
        this.motifAnnotations = motifAnnotations;
        this.ontoSoftAnnotations = "";
    }

    public DataNarrativeContext(String workflowExecutionURI, String workflowTemplateURI, String doiFile, String motifAnnotations, String ontoSoftAnnotations) {
        this.workflowExecutionURI = workflowExecutionURI;
        this.workflowTemplateURI = workflowTemplateURI;
        this.doiFile = doiFile;
        this.motifAnnotations = motifAnnotations;
        this.ontoSoftAnnotations = ontoSoftAnnotations;
    }
    
    

    public String getDoiFile() {
        return doiFile;
    }

    public String getMotifAnnotations() {
        return motifAnnotations;
    }

    public String getWorkflowExecutionURI() {
        return workflowExecutionURI;
    }

    public String getWorkflowTemplateURI() {
        return workflowTemplateURI;
    }

    public String getOntoSoftAnnotations() {
        return ontoSoftAnnotations;
    }
    
    
}
