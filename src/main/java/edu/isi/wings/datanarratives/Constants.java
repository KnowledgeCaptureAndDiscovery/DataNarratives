package edu.isi.wings.datanarratives;

/**
 * Class with the constants for the DANA Project.
 * @author dgarijo
 */
public class Constants {
    /*constants for querying and accessing data*/
    //public static String endpoint = "http://www.opmw.org/sparql";
    public static String endpoint = "http://seagull.isi.edu:3030/ds/query";
    public static String unionGraph = "from <urn:x-arq:UnionGraph>";
    
    /*constants for properties*/
    public static final String BIBO_DOI= "http://purl.org/ontology/bibo/doi";
    public static final String RDFS_LABEL="http://www.w3.org/2000/01/rdf-schema#label";
    public static final String OPMW_LOCATION="http://www.opmw.org/ontology/hasLocation";
    public static final String MOTIF_DATA_ANALYSIS="http://purl.org/net/wf-motifs#DataAnalysis";
    public static final String OPMW_USES="http://www.opmw.org/ontology/uses";
    public static final String OPMW_IGB="http://www.opmw.org/ontology/isGeneratedBy";
    public static final String OPMW_USED="http://purl.org/net/opmv/ns#used";
    public static final String OPMW_WGB="http://purl.org/net/opmv/ns#wasGeneratedBy";
    
    /*Constants for serializing the html document*/
    public static final String TOOLTIP_NARRATIVE_EXEC="The <i><strong>Execution view</strong></i> shows the information of the overall method, inputs and parameters. If a DOI is present, it will be shown as well";
    public static final String TOOLTIP_NARRATIVE_DATA="The <i><strong>Data view</strong></i> shows on the inputs used to obtain the results";
    public static final String TOOLTIP_NARRATIVE_METHOD="The <i><strong>Method view</strong></i> shows the main steps of the workflow. If there is only one main step, this view will summarize the functionality of the rest of the steps";
    public static final String TOOLTIP_NARRATIVE_ABSTRACT="The <i><strong>Abstract method view</strong></i> describes the workflow at a general level, without going into the details";
    public static final String TOOLTIP_NARRATIVE_IMPL="The <i><strong>Implementation view</strong></i> explains the workflow in terms of how each abstract step is implemented";
    public static final String TOOLTIP_NARRATIVE_SOFTWARE="The <i><strong>Software view</strong></i> provides details on the software implementations used on each step of the workflow";
    public static final String getNarrativeStart(String narrativeTitle, String secNo, String tooltip){
            return "<div class=\"panel panel panel-info\">\n" +
"		  <div class=\"panel-heading\">\n" +
"			<h4 class=\"panel-title\">\n" +
"			  <span data-toggle=\"tooltip\" data-html=\"true\" title=\""+tooltip+"\">"+narrativeTitle+"</span>  &nbsp;&nbsp;&nbsp;&nbsp;  <button class=\"SeeMore2 btn btn-primary\" data-toggle=\"collapse\" href=\"#collapse"+secNo+"\">See More</button>\n" +
"			</h4>\n" +
"		  </div>\n" +
"		  <div id=\"collapse"+secNo+"\" class=\"panel-collapse collapse\">\n" +
"			<div class=\"panel-body\">\n" +
"			<p> \n";
    }
    
    public static final String NARRATIVE_END="			</p>\n" +
            "			</div>\n" +
            "			\n" +
            "		  </div>\n" +
            "		</div>";
    
    public static final String HTML_HEAD = "<head>\n" +
"    <meta charset=\"utf-8\">\n" +
"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"    <title>Data Narratives</title>\n" +
"\n" +
"	\n" +
"  <link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n" +
"  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\"></script>\n" +
"  <script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\n"
            + "<script>\n" +
"	$(document).ready(function(){\n" +
"		$('[data-toggle=\"tooltip\"]').tooltip();   \n" +
"	});\n" +
"	</script>" +
"\n" +
"  </head>";
    public static final String HTML_BODY_BEGIN = "<body>\n" +
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
    public static final String HTML_BODY_END = "</div> \n" +
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
    
}
