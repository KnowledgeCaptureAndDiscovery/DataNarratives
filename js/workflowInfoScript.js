var processInfosCount = 0; // keep track of how many process info divs are shown
var processInfosIndex = 0;
var variableInfosCount = 0;
var variableInfosIndex = 0;

var variableSectionsShowing = [];
var variableSectionsShowingIds = [];
var sectionsShowing = [];
var sectionsShowingIds = [];
var $template = $(".template");
var $templateVariables = $(".templ");
var $vis = $(".visualization-container");

$(".accordion-toggle").click(function(event, ui) {
	if($(this).attr("class")=="accordion-toggle collapsed") {
		if($(this).attr("id") == "togglesummarylegendlink") {
			highlightPuts(summaryList);
		}
		setTimeout(function() {
    		fitinScreen($(this));
    	}, 100);
    }
    else if($(this).attr("id") == "togglesummarylegendlink")  {
    	unhighlightPuts(summaryList);
    }
  });

function closeLegend(elementid)  {
    if ($("#"+elementid).attr("class")=="panel-collapse collapse in")  {
        $("#"+elementid).collapse('hide');
    }
}

function checkOversize(currentelement)  {
    var processnodes = document.getElementById("accordionInfo");
    var variable = document.getElementById("accordionVariables");
    var toplegend = document.getElementsByClassName("rightCanvas")[0];
    var left = document.getElementById("viz");
    var left2= document.getElementById("viz2");
    if(processnodes.clientHeight + variable.clientHeight + toplegend.clientHeight + 100> left.clientHeight)  {
        return true;
    }
    else  if(processnodes.clientHeight + variable.clientHeight + toplegend.clientHeight + 100> left2.clientHeight)  {
        return true;
    }
    //console.log(processnodes.clientHeight, variable.clientHeight,toplegend.clientHeight,left.clientHeight);
    return false;
}

function fitinScreen(currentelement)  {
    if(checkOversize(currentelement) && currentelement.attr("id") != "togglelegendlink")  {
        closeLegend("collapseLegend");
    }
    if(checkOversize(currentelement) && currentelement.attr("id") != "collapseSummaryLegend")  {
    	closeLegend("collapseSummaryLegend");
    }
    for(var i=processInfosIndex-processInfosCount;i<processInfosIndex;i++)  {
        if(!checkOversize(currentelement)) return;
        if($('#'+sectionsShowing[i])!=currentelement)  {
            var thisid = '#' + i.toString();
            $(thisid).collapse('hide');
        }
    }
    for(var i=variableInfosIndex-variableInfosCount;i<variableInfosIndex;i++)  {
        //console.log(3);
        if(!checkOversize(currentelement)) return;
        if($('#'+variableSectionsShowing[i])!=currentelement)  {
            var thisid = '#v' + i.toString();
            $(thisid).collapse('hide');
        }
    }
    /*if(checkOversize(currentelement))  {
    	var temp = $("#viz").height()+100;
    	$("#viz").height(temp);
    }*/
}

function addProcessInfo(processURI, inputsArray, outputsArray) {
	// get name of node
	var processName = stripNameFromURI(processURI);
	console.log(processName);
		
	var alreadyShowing = false;
	// Check that section is not already showing
	for (var i = 0; i < sectionsShowing.length; i++) {
			if (sectionsShowing[i] == processName) {
				alreadyShowing = true;
			}
	}
	if (alreadyShowing) {
			console.log("process is already showing!");
	}
	
	// panel isn't already displayed on page, so add it
	if (!alreadyShowing) {
		// check that process div thing is not over count
		if($("#viz").height() > 650)  {
			if (processInfosCount == 3) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}
		else if($("#viz").height() > 550){
			if (processInfosCount == 2) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}
		else {
			if (processInfosCount == 1) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}
		if($("#viz2").height() > 650)  {
			if (processInfosCount == 3) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}
		else if($("#viz2").height() > 550){
			if (processInfosCount == 2) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}
		else {
			if (processInfosCount == 1) {
				// remove a process
				removeProcessInfo(sectionsShowing[0]);
			}
		}

		
		
		sectionsShowing[processInfosCount] = processName;
		var $newPanel = $template.clone();
		
		
//		$newPanel.find(".collapse").removeClass("in");
		// set header name
        var accordionToggle = $newPanel.find(".accordion-toggle");

        accordionToggle.click(function() {
            unhighlightAllPuts();
            highlightPuts(inputsArray);
            highlightPuts(outputsArray);
            if($(this).attr('class')=='accordion-toggle collapsed') {
                setTimeout(function() {
                	//console.log("timeout");
                	fitinScreen($newPanel);
                }, 100);
            }
        });
        
		$newPanel.find(".accordion-toggle").attr("href", "#" + (processInfosIndex)).text("Process: " + processName);
		$newPanel.attr("id", processName);
		// link clicking on process name to expand collapse
		$newPanel.find(".panel-collapse").attr("id", processInfosIndex);
        
        //add link to RDFImage
        $newPanel.find("#RDFImage-variable-link").prop("href", processURI);
		// Populate the table with variable data
		var l = inputsArray.length;
		if (outputsArray.length > l) {
				l = outputsArray.length;
		}
		// adding inputs and outputs to the table
		var tableBody = $newPanel.find("table")[0];
		var newTableBody = document.createElement("tbody");
		newTableBody.setAttribute("class", "process_info_table_body");
		for (var i = 0; i < l; i++) {
				var row = newTableBody.insertRow(-1);
				// add input variable name and add output variable name (if applicable)
				var cell1 = row.insertCell(0);
				var cell2 = row.insertCell(1);
				if (i < inputsArray.length) {
						cell1.innerHTML = stripNameFromURI(inputsArray[i]);
				}
				if (i < outputsArray.length) {
						cell2.innerHTML = stripNameFromURI(outputsArray[i]);
				}
			}
		tableBody.append(newTableBody);

		if ($("ul.nav li.active").index() == 0) {
			$($newPanel.find("#DownloadImage-variable-link")).hide();
		}
		else {
			var code = 'select ?software from <urn:x-arq:UnionGraph> where {<'
    				+ processURI +'> <http://www.opmw.org/ontology/hasExecutableComponent> ?e. ?e <http://www.opmw.org/ontology/hasLocation> ?software }'
			var codeURI = endpoint + 'query?query=' + escape(code) + '&format=json'; 
			$.get(codeURI, function(data,status)  {
				//console.log(data);
				$newPanel.find("#DownloadImage-variable-link").attr("href", data.results.bindings[0].software.value);
				$newPanel.find("#DownloadImage-variable-link").attr("download", data.results.bindings[0].software.value);
			});
		}
		// add new panel to the page
		processInfosCount = processInfosCount + 1;
		processInfosIndex = processInfosIndex + 1;
		fitinScreen($newPanel);
		$("#accordionInfo").append($newPanel.fadeIn("slow"));
	}
}
    

// FUNCTION TO ADD NEW VARIABLE INFORMATION SECTIONS
function addVariableInfo(variableURI, usedBy, generatedBy, variableType, artifactValues, variablehasvalue ,variabletypes) {
	// get name of node
	var variableName = stripNameFromURI(variableURI);
	//console.log(variableName);
		
	var alreadyShowing = false;
	// Check that section is not already showing
	for (i = 0; i < variableSectionsShowing.length; i++) {
			if (variableSectionsShowing[i] == variableName) {
				alreadyShowing = true;
			}
	}
	if (alreadyShowing) {
			console.log("variable is already showing!");
	}
	
	// panel isn't already displayed on page, so add it
	if (!alreadyShowing) {
		// check that variable div thing is not over count
		if($("#viz").height() > 650)  {
			if (variableInfosCount == 3) {
				// remove a variable
				removeVariableInfo(variableSectionsShowing[0]);
			}
		}
		else if($("#viz").height() > 550)  {
			if (variableInfosCount == 2) {
				// remove a variable
				removeVariableInfo(variableSectionsShowing[0]);
			}
		}
		else  {
			if (variableInfosCount == 1) {
				// remove a variable
				removeVariableInfo(variableSectionsShowing[0]);
			}
		}

		variableSectionsShowing[variableInfosCount] = variableName;
		var $newPanel = $templateVariables.clone();

		// set header name
		var variableTypeHeading = variableType.charAt(0).toUpperCase() + variableType.slice(1);
		if ($("ul.nav li.active").index() == 0) {
			$newPanel.find(".accordion-toggle").attr("href", "#v" + (variableInfosIndex)).text(variableTypeHeading + " Variable: " + variableName);
		}
		else  {
			$newPanel.find(".accordion-toggle").attr("href", "#v" + (variableInfosIndex)).text(variableTypeHeading + ": " + variableName);
		}
        $newPanel.find(".accordion-toggle").click(function()  {
        	if($(this).attr('class')=='accordion-toggle collapsed') {
        		setTimeout(function() {
        			//console.log("timeout");
            		fitinScreen($newPanel);
            	}, 100);
            }
        });

		$newPanel.attr("id", variableName);
		// link clicking on process name to expand collapse
		$newPanel.find(".panel-collapse").attr("id", "v" + variableInfosIndex);
        
        //add link to RDFImage
        $newPanel.find("#RDFImage-variable-link").prop("href", variableURI);
		
		// Generated By
		var textGeneratedBy = $newPanel.find("ul")[1];
		var newGeneratedBy = document.createElement("li");
		//newGeneratedBy.innerHTML = " - ";
		// ----------TODO----------
		if (typeof generatedBy != 'undefined') {
			newGeneratedBy.innerHTML = stripNameFromURI(generatedBy[0]);
		} else {
			newGeneratedBy.innerHTML = "-";
			$($newPanel.find(".row.variable_row")[1]).hide();
			$($newPanel.find(".col-md-9.col-md-offset-3")[1]).hide();
		}
		textGeneratedBy.append(newGeneratedBy);
		
		// show list of used by processes
		var listUsedBy = $newPanel.find("ul")[2];
		if (typeof usedBy != 'undefined') {
			for (var i = 0; i < usedBy.length; i++) {
				var newListItem = document.createElement("li");
				newListItem.innerHTML = stripNameFromURI(usedBy[i]);
				listUsedBy.append(newListItem);
			}
		}
		else {
			$($newPanel.find(".row.variable_row")[2]).hide();
			$($newPanel.find(".col-md-9.col-md-offset-3")[1]).hide();
		}
		
		// Check which tab is showing based on the selected/active index
		// if index 1 is selected, we are on the execution tab
		// otherwise, on the index 0 tab we are on the main workflow page and do not want any file info
		// displayed for variables
		var variableValue = $newPanel.find("div")[12];
		if ($("ul.nav li.active").index() == 0) {
			variableValue.setAttribute("style", "display:none");
			$($newPanel.find("#DownloadImage-variable-link")).hide();
		}
		else {
				if(artifactValues.bindings[0]!=null)  {
					$newPanel.find("#DownloadImage-variable-link")[0].setAttribute("href", artifactValues.bindings[0].file.value);
					$newPanel.find("#DownloadImage-variable-link")[0].setAttribute("download", artifactValues.bindings[0].file.value);
				}
				else  {
					$($newPanel.find("#DownloadImage-variable-link")).hide();
				}
				//console.log(variablehasvalue);
				if(typeof variablehasvalue.bindings!='undefined')  {
					if(variablehasvalue.bindings.length!=0)  {
						variableValue.setAttribute("style", "display:block");
						$(variableValue).find("span").text(variablehasvalue.bindings[0].value.value);
					}
					else {
						variableValue.setAttribute("style", "display:none");
					}
				}
				else  {
					variableValue.setAttribute("style", "display:none");
				}
		}

		//add types to variable
		
		if(typeof variabletypes != 'undefined')  {
			var typelist = variabletypes.results.bindings;
			var cnt=0;
			for (var i = 0; i < typelist.length; i++) {
				var typetext = stripTypeFromURI(typelist[i].type.value);
				if (typetext == -1) continue;
				var newListItem = document.createElement("li");
				newListItem.innerHTML = typetext;
				$newPanel.find("ul")[0].append(newListItem);
				cnt++;
			}
			if(!cnt) {
				$($newPanel.find(".row.variable_row")[0]).hide();
				$($newPanel.find(".col-md-9.col-md-offset-3")[0]).hide();
			}
		}
		else  {
			$($newPanel.find(".row.variable_row")[0]).hide();
			$($newPanel.find(".col-md-9.col-md-offset-3")[0]).hide();
		}
		
				
		// add new panel to the page
		variableInfosCount = variableInfosCount + 1;
		variableInfosIndex = variableInfosIndex + 1;
		fitinScreen($newPanel);
		$("#accordionVariables").append($newPanel.fadeIn("slow"));
	}
}


function clearAllPanels() {
	// remove all panels from the page
	var sectionsShowingCopy = sectionsShowing.slice();
	var variableSectionsShowingCopy = variableSectionsShowing.slice();
	unhighlightAllPuts();
	for (var i = 0; i < sectionsShowingCopy.length; i++) {
		removeProcessInfo(sectionsShowingCopy[i]);	
	}
	for (var j = 0; j < variableSectionsShowingCopy.length; j++) {
		removeVariableInfo(variableSectionsShowingCopy[j]);
	}
	// reset any counters and other things
	processInfosCount = 0
	variableInfosCount = 0;
	processInfosIndex = 0;
	variableInfosIndex = 0;
	sectionsShowing = [];
	sectionsShowingIds = [];
	variableSectionsShowing = [];
	variableSectionsShowingIds = [];
}

$(document).on('click', '.glyphicon-remove-circle', function () {
	var processName = $(this).parent().parent().attr("id");
  $(this).parents('.panel').get(0).remove();
	processInfosCount = processInfosCount - 1;
	for (var i = 0; i < sectionsShowing.length; i++) {
		if (sectionsShowing[i] == processName) {
			sectionsShowingIds.splice(i, 1);
			sectionsShowing.splice(i, 1);
			break;
		}
	}
	/*if (processInfosCount == 0 &&  variableInfosCount == 0) {
		// resize the visualization container
		console.log("should reize");
		unhighlightAllPuts();
		$vis.animate({
                "width": "65%"
            }, "slow");
	}*/
});


$(document).on('click', '.glyphicon-remove-sign', function () {
	var variableName = $(this).parent().parent().attr("id");
  $(this).parents('.panel').get(0).remove();
	variableInfosCount = variableInfosCount - 1;
	for (var i = 0; i < variableSectionsShowing.length; i++) {
		if (variableSectionsShowing[i] == variableName) {
			variableSectionsShowingIds.splice(i, 1);
			variableSectionsShowing.splice(i, 1);
			break;
		}
	}
	/*if (processInfosCount == 0 &&  variableInfosCount == 0) {
		// resize the visualization container
		console.log("should reize");
		unhighlightAllPuts();
		$vis.animate({
                "width": "65%"
            }, "slow");
	}*/
});

function removeVariableInfo(removeID) {
	var divToRemove = document.getElementById(removeID);
	//console.log(divToRemove);
	divToRemove.remove();
	// remove from lists that are keeping track of which processes are currently showing on page
	variableInfosCount = variableInfosCount - 1;
	for (var i = 0; i < variableSectionsShowing.length; i++) {
		if (variableSectionsShowing[i] == removeID) {
			variableSectionsShowingIds.splice(i, 1);
			variableSectionsShowing.splice(i, 1);
			break;
		}
	}

	/*if (processInfosCount == 0 && variableInfosCount == 0) {
		// resize the visualization container
		console.log("should reize");
		unhighlightAllPuts();
		$vis.animate({
						"width": "65%"
					}, "slow");
	}*/
}


function removeProcessInfo(removeID) {
    var divToRemove = document.getElementById(removeID);
		console.log(divToRemove);
    divToRemove.remove();
		// remove from lists that are keeping track of which processes are currently showing on page
		processInfosCount = processInfosCount - 1;
		for (var i = 0; i < sectionsShowing.length; i++) {
			if (sectionsShowing[i] == removeID) {
				sectionsShowingIds.splice(i, 1);
				sectionsShowing.splice(i, 1);
				break;
			}
		}
	
	/*if (processInfosCount == 0 && variableInfosCount == 0) {
		// resize the visualization container
		console.log("should reize");
		unhighlightAllPuts();
		$vis.animate({
                "width": "65%"
            }, "slow");
	}*/
}

function toggleChevron(e) {
    $(e.target)
        .prev('.panel-heading')
        .find("i.indicator")
        .toggleClass('glyphicon-chevron-down glyphicon-chevron-up');
}
$('#accordion').on('hidden.bs.collapse', toggleChevron);
$('#accordion').on('shown.bs.collapse', toggleChevron);

