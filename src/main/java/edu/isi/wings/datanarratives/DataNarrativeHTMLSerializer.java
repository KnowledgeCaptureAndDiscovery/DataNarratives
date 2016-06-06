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
        narrative2+=" The \n" +
            "			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
        return narrative2;
    }
    
    public static void main (String [] args){
        //test skeleton
        DataNarrative d = new DataNarrative();
        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "test.html");
    }
    
   
    
}
