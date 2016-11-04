/*
Copyright to be added
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

    public DataNarrativeContext(String workflowExecutionURI, String workflowTemplateURI, String doiFile, String motifAnnotations) {
        this.workflowExecutionURI = workflowExecutionURI;
        this.workflowTemplateURI = workflowTemplateURI;
        this.doiFile = doiFile;
        this.motifAnnotations = motifAnnotations;
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
}
