var svg = {};
var vis = {};
var zoom = null;
var processInputMapping = {};
var processOutputMapping = {};
var isVariableOfMapping = {};
var outputByMapping = {};
var processNodeIndices = {};
var putNodeIndices = {};
var summaryList = [];


function getWorkflowURI() {
//    var querystring = window.location.search;
    // Matches everything after the first '='
//    var regex = /=(.*)/;
//    var encryptedURI = regex.exec(querystring);
////    var workflowURI = CryptoJS.AES.decrypt(encryptedURI[1], "csci401-Spring-2017").toString(CryptoJS.enc.Utf8);
//    localStorage.setItem('workflow-uri', workflowURI);
//    workflowURI = document.getElementById('executionWorkflow').href;
    return workflowURI;
}

var renderVisualization = function (res, isTrace, isIndexpage,isExecution) {
    vis = {};
    svg = {};
    svgGroup = {};
    zoom = null;
    if(!isIndexpage)  {
        d3.select("svg").remove();
        if(isExecution)
        {
        		console.log("here");
        		d3.select('.visualization-container2').append('svg');
        }
        else
        		d3.select('.visualization-container').append('svg');
    }
    else {
        d3.selectAll('.visualization-container:nth-child('+(isIndexpage-1)+')').append('svg');
    }
    var results = res['results']['bindings'];
    processNodeIndices = {};
    putNodeIndices = {};
    processInputMapping = {};
    processOutputMapping = {};
    isVariableOfMapping = {};
    outputByMapping = {};

    /*
        @params: string processName, string inputName
        - inserts the inputName to the list of inputs that the processName maps
        to within the object processInputMapping
    */
    var addInputProcess = function(processName, inputName) {
        processInputMapping[processName] = processInputMapping[processName] || [];
        processInputMapping[processName].push(inputName);
    }

    /*
        @params: string processName, string outputName
        - inserts the outputName to the list of outputs that the processName maps
        to within the object processOutputMapping
    */
    var addOutputProcess = function(processName, outputName) {
        processOutputMapping[processName] = processOutputMapping[processName] || [];
        processOutputMapping[processName].push(outputName);
    }
    
    /*
        @params: string inputName, string processName
        - inserts the process URI to a list of processes in order to map what processes are associated with a given input
    */
    var mapInputToProcess = function(inputName, processName) {
        isVariableOfMapping[inputName] = isVariableOfMapping[inputName] || [];
        
        isVariableOfMapping[inputName].push(processName);
    }
    
    /*
        @params: string outputName, string processName
        - inserts the process URI to a list of processes in order to map what processes are associated with a given output
    */
    var mapOutputFromProcess = function(outputName, processName) {
        outputByMapping[outputName] = outputByMapping[outputName] || [];
        
        outputByMapping[outputName].push(processName);
    }

    /*
        @params: dagreD3 graph
        - pulls from JSON result and sets nodes according to whether or not they are labeled as inputs, outputs, or processes
    */
    var mapNodesEdges = function(graph) {
        var j = 0;
        for (var i = 0; i < results.length; i++) {
            //get URI of step from JSON
            var step = results[i].hasOwnProperty('step') ? results[i]['step']['value'] : null;

            //add step to processNodeIndices if the step hasn't been added yet
            if (processNodeIndices[step] == null) {
                processNodeIndices[step] = j;
                //get readable display for step name
                var stepToDisplay = stripNameFromURI(step);
                stepToDisplay = stepToDisplay.substring(0, stepToDisplay.lastIndexOf('node')) == '' ? stepToDisplay : stepToDisplay.substring(0, stepToDisplay.lastIndexOf('node'));
                vis.setNode(j, {
                    shape: 'process',
                    label: stepToDisplay,
                    labelStyle: "fill: #000",
                    style: "fill: #FFCC99;",
                    uri: step,
                    type: 'process'
                    
                });
                j++;
            }
            if (results[i].hasOwnProperty('input')) {
                //get URI of input from JSON
                var input = results[i]['input']['value'];
                if (putNodeIndices[input] == null) {
                    putNodeIndices[input] = j;
                    //get readable display for input name
                    var inputToDisplay = stripNameFromURI(input);
                    var isparam = false;
                    if(!isIndexpage)  {
                        if(isTrace)  {
                            var isparameter = 'select ?value from <urn:x-arq:UnionGraph> where {<'
                                + input +'><http://www.opmw.org/ontology/hasValue> ?value}';
                            var isparameterURI = endpoint + 'query?query=' + escape(isparameter) + '&format=json';
                            $.ajax({
                                url: isparameterURI,
                                type: 'GET',
                                cache: false,
                                async: false,
                                timeout: 30000,
                                error: function() {
                                },
                                success: function(res2) {
                                    if(typeof res2.results.bindings!='undefined')  {
                                        if(res2.results.bindings.length!=0)  {
                                            isparam = true;
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            var isparameter = 'select ?file from <urn:x-arq:UnionGraph> where {<'
                                + input +'><http://www.opmw.org/ontology/isParameterOfTemplate> ?file}';
                            var isparameterURI = endpoint + 'query?query=' + escape(isparameter) + '&format=json';
                            $.ajax({
                                url: isparameterURI,
                                type: 'GET',
                                cache: false,
                                async: false,
                                timeout: 30000,
                                error: function(){
                                },
                                success: function(res) {
                                    if(res.results.bindings[0]!=null)  {
                                        isparam = true;
                                    }
                                }
                            });
                        }
                    }
                    if(isparam)  {
                        vis.setNode(j, { 
                                    label: inputToDisplay,
                                    labelStyle: 'fill: black',
                                    shape: 'customEllipse',
                                    style: 'fill: #fddb9a;',
                                    uri: input,
                                    type: 'input'
                                });
                    }
                    else  {
                        vis.setNode(j, { 
                                    label: inputToDisplay,
                                    labelStyle: 'fill: #FFF',
                                    shape: 'customEllipse',
                                    style: 'fill: #003366;',
                                    uri: input,
                                    type: 'input'
                                });
                    }
                    j++;
                }
                addInputProcess(step, input);
                mapInputToProcess(input, step);
            } else if (results[i].hasOwnProperty('output')) {
                //get URI of output from JSON
                var output = results[i]['output']['value'];
                if (putNodeIndices[output] == null) {
                    putNodeIndices[output] = j;
                    //get readable display for output name
                    var outputToDisplay = stripNameFromURI(output);
                    vis.setNode(j, { 
                        label: outputToDisplay,
                        labelStyle: "fill: #FFF",
                        shape: 'customEllipse',
                        style: "fill: #003366;",
                        uri: output,
                        type: 'output'
                    });
                    j++;
                }
                addOutputProcess(step, output);
                mapOutputFromProcess(output, step);
            }
        }
        setGraphEdges(vis);
    }

    /*
        @params: dagreD3 vis
        - connects nodes to each other via node numbers from processInputMapping
        and processOutputMapping
    */
    var setGraphEdges = function (vis) {
        for (var process in processInputMapping) {
            for (var i = 0; i < processInputMapping[process].length; i++) {
                vis.setEdge(putNodeIndices[processInputMapping[process][i]], processNodeIndices[process], {
                    style: "stroke: #000; fill:none"
                });
            }
        }
        
        for (var process in processOutputMapping) {
            for (var i = 0; i < processOutputMapping[process].length; i++) {
                vis.setEdge(processNodeIndices[process], putNodeIndices[processOutputMapping[process][i]], {
                    style: "stroke: #000; fill:none"
                });
            }
        }
        if (!isTrace)  {
            if(!isIndexpage) { 
                formatInputs(vis, renderGraph);
            }
            else  {
                renderGraph(vis);
            }
        }
        else {
            renderGraph(vis);
        }
    }
    
    var renderGraph = function(vis) {
        // Create the renderer
        var render = new dagreD3.render();
        
        // Add dimensions to nodes if there are any
        addDimensions(render);
        // Set up an SVG group so that we can translate the final graph.
        if(!isIndexpage)  {
            svg = d3.select("svg").attr('width','100%').attr('height','100%'), svgGroup = svg.append("g");
        }
        else  {
            svg = d3.selectAll("svg:nth-child("+(isIndexpage-1)+')').attr('width','100%').attr('height','100%'), svgGroup = svg.append("g");
        }

        // Set up zoom support
        zoom = d3.behavior.zoom().on("zoom", function() {
            svgGroup.attr("transform", "translate(" + d3.event.translate + ")" +
                                        "scale(" + d3.event.scale + ")");
        });
        svg.call(zoom);

        // Run the renderer. This is what draws the final graph.
        render(svgGroup, vis);

        //centers graph and calculates top margin of graph based on screen size
        var xCenterOffset = (document.getElementsByClassName('visualization-container')[0].clientWidth / 1.25 - vis.graph().width) / 2;
        var yTopMargin = screen.height * .05;
        var scale = .75;
        zoom.translate([xCenterOffset, 20])
            .scale(scale)
            .event(svg);

        if(!isIndexpage)  {
            svg.attr('height', vis.graph().height * scale + yTopMargin);
            setupNodeOnClick(svg, vis);
            
        }   
    }
    
    /*
        @params: Graph 'vis', function 'callback' to execute when AJAX operation complete
        - styles input of workflow after querying endpoint
    */
    var formatInputs = function(vis, callback) {
        if(isIndexpage)  {
            workflowURI = exampleworkflowURI[isIndexpage-1];
        }
        getInputs(workflowURI, function (inputs) {
            for (var i = 0; i < inputs.length; i++) {
                var nodeIndex = putNodeIndices[inputs[i].input.value];
                var newLabel = vis.node(nodeIndex).label;
                var newURI = vis.node(nodeIndex).uri;
                vis.setNode(nodeIndex, { 
                    label: newLabel,
                    labelStyle: "fill: #FFF",
                    shape: 'customInputEllipse',
                    style: "fill: #336633;",
                    uri: newURI,
                    type: 'input'
                });
            }
            callback(vis);
        })
    }

    /*
        creates new graph and executes steps to render it
    */
    vis = new dagreD3.graphlib.Graph()
    .setGraph({
        nodesep: 10, //distance between nodes
        ranksep: 20, //distance between node hierarchy
    })
        .setDefaultEdgeLabel(function() { 
            return {} 
        });

    mapNodesEdges(vis);
    if(isTrace) {
        loadSummary(svg, vis);
    }
}   

var translateVisualization = function() {
    var x = 50;
    var y = 20;
    var scale = 0.75;
    
    d3.select('svg g')
    .transition()
    .duration(1000)
    .attr('transform', 'translate(' + x + ',' + y + ')scale(' + scale + ')');
    zoom.translate([x,y]).scale(scale);
}

var addTraces = function(traces) {
    var select = document.getElementById("dropdown-content");

    //populates selection box options
//    for(var i = 0; i < traces.length; i++) {
//        var opt = traces[i];
//        var el = document.createElement("a");
//        el.textContent = stripNameFromURI(opt.execution.value);
//        el.value = opt.execution.value;
//        select.appendChild(el);
//    }
//    $("#dropdown-content a").on('click', function() {
//        document.getElementById('execution-name').innerHTML = "Selected execution: " + $(this).text();
//        localStorage.setItem('workflow-uri', $(this).val());
//        $("#collapseSummaryLegend ul").children().remove();
//        summaryList = [];
//        $("#collapseSummaryLegend").collapse('hide');
//        getExecutionData($(this).val(), function(res, executionID) {
//            renderVisualization(res, true);
//            getExecutionMetadata(executionID, function(res) {
//                setExecutionMetadata(res);
//				clearAllPanels();
//            })
//        });
//        document.getElementById("RDFImage-link2").href = $(this).val();
//        //document.getElementById("RDFLink2").innerHTML = $(this).text();
//    });
    
    
    //select.selectedIndex = 0;
//    document.getElementById('execution-name').innerHTML = "Selected execution: " + $($("#dropdown-content a")[0]).text();
//    document.getElementById("RDFImage-link2").href = $($("#dropdown-content a")[0]).val();
    //document.getElementById("RDFLink2").innerHTML = select.options[select.selectedIndex].value;
}

var processClicked = function(){
	
	console.log(" process clicked ");
}

var highlightNode = function(nodeUri,color="red") {
	
	console.log(" heree for highlight" +nodeUri);
	svg.selectAll('g.node rect, g.node ellipse').each( function(uri) {
		var node = vis.node(uri);
		if(node.uri == nodeUri) {
			
              d3.select(this)
                  .attr('stroke', color)
                  .attr('stroke-width', '4px');
		}
      
	});
}

var unHighlightNode = function(nodeUri) {
	
	svg.selectAll('g.node rect, g.node ellipse').each( function(uri) {
		var node = vis.node(uri);
		if(node.uri == nodeUri) {
              d3.select(this)
                  .attr('stroke', null)
                  .attr('stroke-width', null);
		}
      
	});
}




var highlightPuts = function(putsArray) {
	
    svg.selectAll('g.node ellipse').each( function(uri) {
        var node = vis.node(uri);
//        d3.select(node)
//        .attr('stroke', 'red')
//        .attr('stroke-width', '4px');
        for (var i = 0; i < putsArray.length; i++) {
            if (putsArray[i] == node.uri) {
            	console.log(node.uri);
//            	console.log(this);
                d3.select(this)
                    .attr('stroke', 'red')
                    .attr('stroke-width', '4px');
            }
        }
    });
}

var unhighlightAllPuts = function() {
	
	
    svg.selectAll('g.node rect,g.node ellipse').each( function(uri) {
        var node = vis.node(uri);
        d3.select(this)
            .attr('stroke', null)
            .attr('stroke-width', null);
    });
}

var unhighlightPuts = function(putsArray) {
	
	
    svg.selectAll('g.node ellipse').each( function(id) {
        var node = vis.node(id);
        for (var i = 0; i < putsArray.length; i++) {
            if (putsArray[i] == node.uri) {
                d3.select(this)
                    .attr('stroke', null)
                    .attr('stroke-width', null);
            }
        }
    });
}

/*
    @params: d3 svg
    - setup on click process for each node
*/
var setupNodeOnClick = function (svg, vis) {
    //setup on click listeners for every node
	svg.on('click',function(){
		console.log(" svg click");
		
		
		unhighlightAllPuts();
		
		
		
	});
    svg.selectAll('g.node').on('click', function(id) {
    	
    		unhighlightAllPuts();
    		d3.event.stopPropagation();
        var node = vis.node(id);
        console.log(" was clicked" + node.uri);
        $("a[href^='http://'").css("background-color","white");
        $("a[id^='http://'").css("background-color","white");
        $("a[href$='"+ node.uri+ "']").css("background-color","yellow");
        $("a[id$='"+ node.uri+ "']").css("background-color","yellow");
        svg.selectAll('g.node ellipse').each(function() {
            d3.select(this)
                .attr('stroke', null)
                .attr('stroke-width', null);
        });    
        if (node.type == 'process') {
            addProcessInfo(node.uri, processInputMapping[node.uri], processOutputMapping[node.uri]);
            highlightPuts(processInputMapping[node.uri]);
            highlightPuts(processOutputMapping[node.uri]);
            highlightNode(node.uri,"purple");
            
        } else if (node.type == 'input') {
            if($($(this).find("ellipse")[0]).css("fill")=="rgb(253, 219, 154)") {
                getExecutionArtifactValues(addVariableInfo, node.uri, isVariableOfMapping[node.uri], outputByMapping[node.uri], 'parameter');
                highlightNode(node.uri,"purple");
            }
            else {
                getExecutionArtifactValues(addVariableInfo, node.uri, isVariableOfMapping[node.uri], outputByMapping[node.uri], 'input');
                highlightNode(node.uri,"purple");
            }
            highlightPuts(isVariableOfMapping[node.uri]);
        } else if (node.type == 'output') {
            getExecutionArtifactValues(addVariableInfo, node.uri, isVariableOfMapping[node.uri], outputByMapping[node.uri], 'output');
            highlightPuts(outputByMapping[node.uri]);
            highlightNode(node.uri,"purple");
        }
    });
}

var loadSummary = function (svg, vis) {
    summaryList = [];
    svg.selectAll('g.node').each(function(id)  {
        var node = vis.node(id);
        if(node.type == 'input') {
            if($($(this).find("ellipse")[0]).css("fill")=="rgb(253, 219, 154)") {
                $(".parameter_row ul").append("<li>" + node.label + "</li>");
                summaryList.push(node.uri);
                $(".parameter_row").show();
            }
            else if (typeof outputByMapping[node.uri] == 'undefined') {
                $(".input_row ul").append("<li>" + node.label + "</li>");
                summaryList.push(node.uri);
            }
        }
        else if(node.type == 'output') {
            $(".output_row ul").append("<li>" + node.label + "</li>");
            summaryList.push(node.uri);
        }
    });
}

/*
    @params: string URI
    @return: a parsed, human-readable substring of the URI
*/
var stripNameFromURI = function(uri) {
    if (uri.indexOf('CE_') <= -1) {
        return uri.substring(uri.lastIndexOf('/')+1, uri.length-13).toLowerCase();
    }
    return uri.substring(uri.lastIndexOf('CE_')+3, uri.length).toLowerCase();
}

var stripTypeFromURI = function(uri)  {
    if (uri.indexOf('ontology.owl') > -1)  {
        return uri.substring(uri.lastIndexOf('ontology.owl')+13, uri.length).replace(/_/g, " ");
    }
    return -1;
}


/*
    @params: d3 render object
    - adds dimensions look for nodes which have dimensionality
*/
var addDimensions = function(render) {
    render.shapes().customEllipse = function(parent, bbox, node) {
        var rx = bbox.width/2,
            ry = bbox.height/2,
            shapeSvg = parent.insert("ellipse", ":first-child")
            .attr('x', -bbox.width/2)
            .attr('y', -bbox.height/2)
            .attr('rx', rx)
            .attr('ry', ry)
            .attr('style', 'fill: #003366'); 
        if (node.dimensions > 1) {
            for (var i = 1; i < node.dimensions; i++) {
                shapeSvg = parent.insert("ellipse", ":first-child")
                .attr('x', -bbox.width/2)
                .attr('y', -bbox.height/2)
                .attr('rx', rx)
                .attr('ry', ry)
                .attr('style', "fill: #FFF; stroke: #003366")
                .attr('transform', 'translate(' + 0 + ',' + (bbox.height/10)*i + ')' + 'scale(' + '1.0' + ')');
            }
            
            shapeSvg = parent.insert("ellipse", ":first-child")
                .attr('x', -bbox.width/2)
                .attr('y', -bbox.height/2)
                .attr('rx', rx)
                .attr('ry', ry)
                .attr('transform', 'translate(' + 0 + ',' + (bbox.height/10)*(node.dimensions) + ')' + 'scale(' + '1.0' + ')');
                node.style = "fill: #FFF; stroke: #003366";
        }

        node.intersect = function(point) {
            return dagreD3.intersect.ellipse(node, rx, ry, point);
        };
        return shapeSvg;
    };
    
    render.shapes().customInputEllipse = function(parent, bbox, node) {
        var rx = bbox.width/2,
            ry = bbox.height/2,
            shapeSvg = parent.insert("ellipse", ":first-child")
            .attr('x', -bbox.width/2)
            .attr('y', -bbox.height/2)
            .attr('rx', rx)
            .attr('ry', ry)
            .attr('style', 'fill: #336633'); 
        
        if (node.dimensions > 1) {
            for (var i = 1; i < node.dimensions; i++) {
                shapeSvg = parent.insert("ellipse", ":first-child")
                .attr('x', -bbox.width/2)
                .attr('y', -bbox.height/2)
                .attr('rx', rx)
                .attr('ry', ry)
                .attr('style', "fill: #FFF; stroke: #336633")
                .attr('transform', 'translate(' + 3*i + ',' + (bbox.height/10)*i + ')' + 'scale(' + '1.0' + ')');
            }
            shapeSvg = parent.insert("ellipse", ":first-child")
                .attr('x', -bbox.width/2)
                .attr('y', -bbox.height/2)
                .attr('rx', rx)
                .attr('ry', ry)
                .attr('transform', 'translate(' + 3*(node.dimensions) + ',' + (bbox.height/10)*(node.dimensions) + ')' + 'scale(' + '1.0' + ')');
                node.style = "fill: #FFF; stroke: #336633";
        }

        node.intersect = function(point) {
            return dagreD3.intersect.ellipse(node, rx, ry, point);
        };
        return shapeSvg;
    };
    
    render.shapes().process = function(parent, bbox, node) {
        var width = bbox.width,
            height = bbox.height,
            shapeSvg = parent.insert('rect', ':first-child')
                .attr('rx', node.rx)
                .attr('ry', node.ry)
                .attr('x', -width/2)
                .attr('y', -height/2)
                .attr('width', width)
                .attr('height', height)
                .attr('style', 'fill: #FFCC99;');

        for (var i = 1; i < node.dimensions; i++) {
            shapeSvg = parent.insert('rect', ':first-child')
                .attr('rx', node.rx)
                .attr('ry', node.ry)
                .attr('x', -width/2 + (5*i))
                .attr('y', -height/2 + (5*i))
                .attr('width', width)
                .attr('height', height)
                .attr('style', 'fill: #FFCC99;');
        }
        node.intersect = function(point) {
            return dagreD3.intersect.rect(node, point);
        }
        return shapeSvg;
    }
}


