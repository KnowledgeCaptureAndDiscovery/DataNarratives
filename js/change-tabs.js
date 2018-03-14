$(document).ready(function() {
    $(".executionTab").click(function() {
//        $("#summaryLegend").show();
//        $("#DownloadTemplate-link").hide();
        $('.nav-tabs a[href="#execution"]').tab('show');
        // remove any panels showing on page 
        clearAllPanels();
        getExecutionIDs(getWorkflowURI(), function(res, executionID) {
            renderVisualization(res, true,false,true);
            getExecutionMetadata(executionID, function(res) {
                setExecutionMetadata(res);
            })
        })
//        $("#selecttrace").show();
//        document.getElementById('workflow-name').style.display = "none";
//        document.getElementById('execution-name').style.display = "inline-block";
//        document.getElementById('RDFImage-bar1').style.display = "none";
//        document.getElementById('RDFImage-bar2').style.display = "inline-block";
    });
    
    $(".workflowTab").click(function() {
//        $("#summaryLegend").hide();
//        $("#selecttrace").hide();
        // remove any panels showing on page 
        clearAllPanels();
//        $("#DownloadTemplate-link").show();
        $('.nav-tabs a[href="#workflow"]').tab('show');  
        getWorkflowData(workflowURI, function(res) {
            renderVisualization(res, false);
        });
        getWorkflowMetadata(workflowURI, function(res)  {
            setWorkflowMetadata(res);
        });
                
//        $("#dropdown-content").children().remove();
//        document.getElementById('execution-name').style.display = "none";
//        document.getElementById('workflow-name').style.display = "inline-block";
//        document.getElementById('execution-name').innerHTML = "";
//        document.getElementById('RDFImage-bar1').style.display = "inline-block";
//        document.getElementById('RDFImage-bar2').style.display = "none";
    });
});

var setExecutionMetadata = function(res) {
    if (res.results.hasOwnProperty('bindings')) {
//        document.getElementById('status-value').textContent = 'status: ' + res.results.bindings[0].status.value.toLowerCase();
//        document.getElementById('placeholder2').innerHTML = 'status: ' + res.results.bindings[0].status.value.toLowerCase();
//        document.getElementById('label-value').textContent = 'label: '+res.results.bindings[0].label.value;
//        document.getElementById('start-time-value').textContent = 'start time: ' + new Date(res.results.bindings[0].start.value).toString();
//        document.getElementById('end-time-value').textContent = 'end time: ' + new Date(res.results.bindings[0].end.value).toString();
//        document.getElementById('rights-value').textContent = 'license: ' + res.results.bindings[0].rights.value;
//        document.getElementById('controller-value').textContent = 'controller: '+res.results.bindings[0].controller.value.split("Agent/")[1].toLowerCase();
//    } else {
//        document.getElementById('status-value').textContent = 'N/A';
//        document.getElementById('label-value').textContent = 'N/A';
//        document.getElementById('start-time-value').textContent = 'N/A';
//        document.getElementById('end-time-value').textContent = 'N/A';
//        document.getElementById('rights-value').textContent = 'N/A';
//        document.getElementById('controller-value').textContent = 'N/A';
    }
}

var setWorkflowMetadata = function(res) {
//    console.log(res);
//    if(res.results.bindings[0].contributer.value != null)  {
//        document.getElementById('contributer-value').textContent = 'contributor: ' + res.results.bindings[0].contributer.value.substring(6);
//        document.getElementById('placeholder1').innerHTML = 'contributor: ' + res.results.bindings[0].contributer.value.substring(6);
//    } 
//    else {
//        document.getElementById('contributer-value').textContent = 'N/A';
//    }
//    if(res.results.bindings[0].modified.value != null)  {
//        document.getElementById('modified-value').textContent = 'modified on: ' + new Date(res.results.bindings[0].modified.value).toString();
//    }
//    else {
//        document.getElementById('modified-value').textContent = 'N/A';
//    }
//    if(res.results.bindings[0].system.value != null)  {
//        document.getElementById('system-value').textContent = 'created by system: ' + res.results.bindings[0].system.value;
//    }
//    else {
//        document.getElementById('system-value').textContent = 'N/A';
//    }
//    if(res.results.bindings[0].version.value != null)  {
//        document.getElementById('version-value').textContent = 'version Number: ' + res.results.bindings[0].version.value;
//    }
//    else {
//        document.getElementById('version-value').textContent = 'N/A';
//    }
//    if(res.results.bindings[0].download.value != null)  {
//        document.getElementById('DownloadTemplate-link').href = res.results.bindings[0].download.value;
//        document.getElementById('DownloadTemplate-link').download = res.results.bindings[0].download.value;
//    }
}


var workflowURI = document.getElementById('templateWorkflow').href; //"http://www.opmw.org/export/omics/resource/WorkflowTemplate/PEPTIDE_SEARCH-D751713988987E9331980363E24189CE";



//document.getElementById("RDFImage-link1").href = workflowURI;
//document.getElementById('workflow-name').innerHTML
//    = "Selected template: " + stripNameFromURI(workflowURI).replace(/\-d.*/g,""); 

//populateSearchBar(function(res) { 
//    //executes after ajax call returns
//    searchbarAutocomplete(parseAutocomplete(res));
//});

getWorkflowData(workflowURI, function(res) {
    renderVisualization(res, false, false);
});

getWorkflowMetadata(workflowURI, function(res)  {
    setWorkflowMetadata(res);
});

$(".metadata-icon").mouseenter(function() {
    //$(this).nextAll('.placeholder').show();
    $(this).nextAll('.placeholder').html($("#"+$(this).attr('id').replace("icon","value")).text());
    //console.log($("#"+$(this).attr('id').replace("icon","value")).text())
});


$(window).resize(function() {
    var canvasheight = $(window).height() - $("#myTopnav").height() - $("#switchtabs").height();
    /*if($("#viz svg").height()<canvasheight) {
        $("#viz svg").css("min-height", canvasheight);
    }*/
    $("#viz svg").height(canvasheight-1);
    $("#viz").height(canvasheight-1);
    $("#viz2 svg").height(canvasheight-1);
    $("#viz2").height(canvasheight-1);
});

$("#viz").bind("DOMSubtreeModified",function(){
    var canvasheight = $(window).height() - $("#myTopnav").height() - $("#switchtabs").height();
    /*if($("#viz svg").height()<canvasheight) {
        $("#viz svg").css("min-height", canvasheight);
    }*/
    $("#viz svg").height(canvasheight-1);
    $("#viz").height(canvasheight-1);
});


$("#viz2").bind("DOMSubtreeModified",function(){
    var canvasheight = $(window).height() - $("#myTopnav").height() - $("#switchtabs").height();
  
    $("#viz2 svg").height(canvasheight-1);
    $("#viz2").height(canvasheight-1);
});