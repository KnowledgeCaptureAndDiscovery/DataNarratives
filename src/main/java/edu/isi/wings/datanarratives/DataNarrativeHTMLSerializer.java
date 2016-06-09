/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.wings.datanarratives;

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
    
    private static final String head = "<head>\n" +
"    <meta charset=\"utf-8\">\n" +
"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"    <title>Data Narratives</title>\n" +
"\n" +
"	\n" +
"  <link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n" +
"  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\"></script>\n" +
"  <script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\n" +
"\n" +
"  </head>";
    private static final String bodyBegin = "<body>\n" +
"    <div class=\"container\">\n" +
"      <nav class=\"navbar navbar-inverse\" role=\"navigation\">\n" +
"            <div class=\"container-fluid\">\n" +
"            <div class=\"navbar-header\">\n" +
"              <a class=\"navbar-brand\">DANA: DAta NArratives</a>\n" +
"            </div>\n" +
"			<div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-1\">\n" +
"			</div>\n" +
"        </div>\n" +
"      </nav>";
    private static final String bodyEnd = "</div> \n" +
"    </div>\n" +
"  </body>\n"+
"  <script>\n" +
"  $('.SeeMore2').click(function(){\n" +
"		var $this = $(this);\n" +
"		$this.toggleClass('SeeMore2');\n" +
"		if($this.hasClass('SeeMore2')){\n" +
"			$this.text('See More');			\n" +
"		} else {\n" +
"			$this.text('See Less');\n" +
"		}\n" +
"	});\n" +
"  </script>" +
"</html>";
    
    
    public static void dataNarrativeToHTML(DataNarrative d, String outPath){
        String htmlPage = head;
        htmlPage+=bodyBegin;
        //add title: resource name and URI
        htmlPage+="<h3 class=\"text-muted\">Data narratives for result: <a href=\""+d.getResultURI()+"\">"+GeneralMethods.splitCamelCase(d.getResultName())+"</a></h3>\n";
        
        
        htmlPage+="<div class=\"row\">\n";
        htmlPage+="<div class=\"col-xs-12 col-md-12\">\n" +
"	  <div class=\"panel-group\">\n";
        //insert data narratives here
        htmlPage+= getExecutionView(d);
        //data view
        htmlPage+= getDataView(d);
        //method view
        htmlPage+= getMethodView(d);
        //implementation view
        //step view
        //software view
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
        
        htmlPage+=bodyEnd;
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
     * Function to serialize the execution view of a data narrative. The template
     * followed is similar to:
     * The 'workflow label' method was run on the 'inputs of the workflow ', with
     * 'parameters used' set to 'parameter value'. The 'result label' results are stored online (DOI 'doi value')
     * @param d
     * @return 
     */
    private static String getExecutionView(DataNarrative d){
        String resultDOI = d.getDOI(d.getResultURI());
        String narrative1 = "<div class=\"panel panel panel-info\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  Data Narrative 1: Execution view  &nbsp;&nbsp;&nbsp;&nbsp;  <button class=\"SeeMore2 btn btn-primary\" data-toggle=\"collapse\" href=\"#collapse1\">See More</button>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapse1\" class=\"panel-collapse collapse\">\n" +
"			<div class=\"panel-body\">\n" +
"			<p> \n";
        narrative1+= "	The <a href=\""+d.getMethodURI()+"\">"+GeneralMethods.splitCamelCase(d.getMethodName())+
                "</a> method was run on the ";
        ArrayList<String> a = d.getMethodInputsAndURIs();
        for (String s:a){
            String[] uriAndLoc = s.split(",");
            narrative1+="<a href=\""+uriAndLoc[0]+"\">"+GeneralMethods.getFileNameFromURL(uriAndLoc[1])+"</a>, ";
        }
        narrative1 = narrative1.substring(0, narrative1.length()-2);//remove the las ", "
        narrative1+=" dataset";
        if(a.size()>1)narrative1+="s";
        a = d.getMethodInputsParametersAndValues();
        if(!a.isEmpty()){
            narrative1 +=", whith ";
            if(a.size()>1){
                for (String s:a){
                    String[] paramAndValue = s.split(",");
                    if(a.indexOf(s) == a.size()-1){
                        narrative1+= " and <a href=\""+paramAndValue[0]+"\">"+GeneralMethods.getFileNameFromURL(paramAndValue[0])+"</a> set to "+paramAndValue[1]+". ";
                    }else{
                        narrative1+= "<a href=\""+paramAndValue[0]+"\">"+GeneralMethods.getFileNameFromURL(paramAndValue[0])+"</a> set to "+paramAndValue[1]+", ";
                    }
                }
            }else{
                //just one
                String[] paramAndValue = a.get(0).split(",");
                narrative1+= "parameter <a href=\""+paramAndValue[0]+"\">"+GeneralMethods.getFileNameFromURL(paramAndValue[0])+"</a> set to "+paramAndValue[1]+", ";
            }
            
        }
        
         narrative1+=" The <a href=\""+d.getResultURI()+"\">"+GeneralMethods.splitCamelCase(d.getResultName())+"</a> results are stored <a href=\""+d.getResultLocation()+"\">online</a> (DOI <a href=\""+resultDOI+"\">"+resultDOI+"</a>). \n" +
            "			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
        return narrative1;
    }
    
    public static String getDataView(DataNarrative d){
        String narrative2 = "<div class=\"panel panel panel-info\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  Data Narrative 2: Data view  &nbsp;&nbsp;&nbsp;&nbsp;  <button class=\"SeeMore2 btn btn-primary\" data-toggle=\"collapse\" href=\"#collapse2\">See More</button>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapse2\" class=\"panel-collapse collapse\">\n" +
"			<div class=\"panel-body\">\n" +
"			<p> \n";
        narrative2+=" The <a href=\"" +d.getResultURI()+"\">"+GeneralMethods.splitCamelCase(d.getResultName())+"</a> results have been derived from the ";
        ArrayList<String> sources = d.getOriginalSourcesForResult(d.getResultURI());
        if(sources.size()>1){
            for (String currentSource:sources){
                String[] aux = currentSource.split(",");
                //0: uri; 1: location
                if(sources.indexOf(currentSource) == sources.size()-1){
                    narrative2+= "and <a href=\""+aux[0]+"\">"+GeneralMethods.getFileNameFromURL(aux[1])+"</a> datasets.";
                }else{
                    narrative2+= "<a href=\""+aux[0]+"\">"+GeneralMethods.getFileNameFromURL(aux[1])+"</a>, ";
                }
            }
        }else{
            //this can be improved
            String[] aux = sources.get(0).split(",");
            narrative2+= "<a href=\""+aux[0]+"\">"+GeneralMethods.getFileNameFromURL(aux[1])+"</a> dataset.";
            
        }
        narrative2+="			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
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
    private static String getMethodView(DataNarrative d){
        String narrative3 = "<div class=\"panel panel panel-info\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  Data Narrative 3: Method view  &nbsp;&nbsp;&nbsp;&nbsp;  <button class=\"SeeMore2 btn btn-primary\" data-toggle=\"collapse\" href=\"#collapse3\">See More</button>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapse3\" class=\"panel-collapse collapse\">\n" +
"			<div class=\"panel-body\">\n" +
"			<p> \n";
        //retrieve: steps of the workflow (chain)
        ArrayList<String> allProcesses = d.getProcesses();
        narrative3 += "The method <a href=\""+d.getMethodURI()+"\">"+GeneralMethods.splitCamelCase(d.getMethodName())+"</a> ";
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
        int dataAnalysisMotifs = 0;
        if(motifsAndProcesses.containsKey("http://purl.org/net/wf-motifs#DataAnalysis")){
                dataAnalysisMotifs = motifsAndProcesses.get("http://purl.org/net/wf-motifs#DataAnalysis").size();
        }
        if(dataAnalysisMotifs>0){
            narrative3+= "performs "+dataAnalysisMotifs +" main types of analysis on the input datasets. ";
            //if there is only one step, add the rest of the motifs like: after blah and blah, the step is executed.
            if(dataAnalysisMotifs == 1){
                //TO DO!!!:
                //retrieve de steps that depend on the main one (easy query)
            }else{
                //if there are more, then just say their order
                ArrayList<String> m = motifsAndProcesses.get("http://purl.org/net/wf-motifs#DataAnalysis");
                //reorder m according to the dependencies.
                m = d.reorderResults(m);
                for(String currentStep:m){
                    int i = m.indexOf(currentStep);
                    if(i==0){
                        narrative3+="First, the <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a> step processes the datasets, then the data is analyzed by the";
                    }
                    else if(i>=1 &&i<m.size()-1){
                        narrative3+=" <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a>,";
                    }else{
                        if(m.size()>2){
                            narrative3 = narrative3.substring(0, narrative3.length()-1);
                            narrative3 +=" steps and finally, ";
                        }
                        narrative3+=" <a href=\""+currentStep+"\">"+GeneralMethods.splitCamelCase(d.getNameForStep(currentStep))+"</a> produces the end results.";
                    }
                }
            }
        }else{
            narrative3+= "does not have one main type of analysis step.";
        }
        
        narrative3+="</p><p> The <a href=\"" +d.getResultURI()+"\">"+GeneralMethods.splitCamelCase(d.getResultName())+"</a> results are the product of the ";
        String [] aux = d.getMethodProcessForResult(d.getResultURI()).split(",");
        String methodStepURI = aux[0];
        String methodName = GeneralMethods.splitCamelCase(aux[1]);
        ArrayList<String> motifsOfStep = d.getMotifsForProcess(methodStepURI);
        narrative3+="<a href=\""+methodStepURI+"\">"+methodName+"</a>";
        if(!motifsOfStep.isEmpty()){
            narrative3+=", a ";
            if(motifsOfStep.size()>1){
                for(String a:motifsOfStep){
                    if(motifsOfStep.indexOf(a) == motifsOfStep.size()-1){
                        narrative3+=" and "+"<a href=\""+a+"\">"+GeneralMethods.splitCamelCase(a.replace("http://purl.org/net/wf-motifs#", ""))+"</a>";
                    }else{
                        narrative3+="<a href=\""+a+"\">"+GeneralMethods.splitCamelCase(a.replace("http://purl.org/net/wf-motifs#", ""))+"</a>"+",";
                    }
                }
            }else{
                narrative3+="<a href=\""+motifsOfStep.get(0)+"\">"+GeneralMethods.splitCamelCase(motifsOfStep.get(0).replace("http://purl.org/net/wf-motifs#", ""))+"</a>";
            }
            narrative3+=" step.";
        }
        narrative3+="			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
        return narrative3;
    }
    
    public static void main (String [] args){
        //test skeleton
        DataNarrative d = new DataNarrative();
        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "test.html");
    }
    
   
    
}
