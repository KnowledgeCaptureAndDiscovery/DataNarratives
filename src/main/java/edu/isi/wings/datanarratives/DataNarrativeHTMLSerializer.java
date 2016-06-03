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
import javax.swing.JOptionPane;

/**
 * Class that takes a data narrative and creates an html serialization of it.
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
"    <!-- Bootstrap -->\n" +
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
"              <a class=\"navbar-brand\">Data Narratives</a>\n" +
"            </div>\n" +
"			<div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-1\">\n" +
"			</div>\n" +
"        </div>\n" +
"      </nav>";
    private static final String bodyEnd = "</div> \n" +
"    </div>\n" +
"  </body>\n" +
"</html>";
    
    
    public static void dataNarrativeToHTML(DataNarrative d, String outPath){
        String htmlPage = head;
        htmlPage+=bodyBegin;
        //add title: resource name and URI
        htmlPage+="<h3 class=\"text-muted\">Data narratives for result: <a href=\""+d.getResultURI()+"\">"+d.getResultName()+"</a></h3>\n";
        htmlPage+="<div class=\"row\">";
        
        htmlPage+="<div class=\"row\">";
        //here go all data narratives
        htmlPage+="<div class=\"col-xs-12 col-md-8\">\n" +
"	  <div class=\"panel-group\">";
        //insert data narratives here
        htmlPage+="</div>\n" +
"	</div>";
        
        //Wf visualizations here
        htmlPage+="<div class=\"col-xs-6 col-md-4\">";
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
        htmlPage+="</div>";
        
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
    
    public static void main (String [] args){
        //test skeleton
        DataNarrative d = new DataNarrative();
        DataNarrativeHTMLSerializer.dataNarrativeToHTML(d, "test.html");
    }
    
   
    
}
