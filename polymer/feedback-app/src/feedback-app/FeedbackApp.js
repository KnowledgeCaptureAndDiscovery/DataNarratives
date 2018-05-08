/**
    * @customElement
    * @polymer
    */
class FeedbackApp extends Polymer.Element {
    static get is() { return 'feedback-app'; }
    static get properties(){
        return {
            /*
            The 'hideRightPanel' property is used to hide or reveal the right panel
            */
            hideRightPanel: {
                type: Boolean,
                value: true
            },
            /*
            The 'hideNarrativeEditor' property is used to hide or reveal the <paper-card> containing narrative editor
            */
            hideNarrativeEditor: {
                type: Boolean,
                value: false
            },
            /*
            The 'hideLinkEditor' property is used to hide or reveal the <paper-card> containing the editor for links
            */
            hideLinkEditor: {
                type: Boolean,
                value: true
            },
            /*
            The 'hideSaveEditsBox' property is used to hide or reveal the <paper-card> containing the box to save the edits
            */
            hideSaveEditsBox: {
                type: Boolean,
                value: true
            },
            /*
            The 'hideEditHistory' property is used to hide or reveal the <paper-card> containing edit historys
            */
            hideEditHistory: {
                type: Boolean,
                value: true
            },
            
            /* 
            The 'workflowURL' property acts as a uniquely identifying property of a particular narrative
            */
            workflowURL: {
                type: String,
                value: ""
            },
            /*
            The 'narrative' property is used to bind the contents of the <paper-card> iteratively to elements of an Array
            property (narratives) by dom-repeat. An element in narratives represents the JSON data correspondong to 
            narrative of a single view.
            */
            narratives: {
                type: Array,
                value: []
            },
            /*
            The 'editNarrativeHistory' property is used to bind the contents of <paper-collapse-item> iteratively
            using <dom-repeat>
            */
            editNarrativeHistory: {
                type: Array,
                value: []
            },
            /*
            The property narrativeToEdit is one of two state variables - it keeps a record of which narrative view is being
            edited currently and by consequence which edited view will be recorded. 
            */
            narrativeToEdit: {
                type: Number,
                value: -1
                // observer: "narrativeToEditChange"
            },
            /*
            The editStateDictionary property exists mainly to assist programmers and debuggers to keep a handy reference for the 
            state of the system.
            */
            editStateDictionary: {
                value: {
                        0: "editor closed",
                        1: "editor opened",
                        2: "edit links",
                        3: "edit link label",
                        4: "save edits"
                    }
            },
            /*
            The editState property is the other state variable which keeps track of the current state of the editing process.
            The event handling has been restructured to be similar to the observer pattern. The event handlers relevant to
            editing change a single property - editState, on changing which an observer method - editStateChange is notifed.
            This method is passed both the old value and new value of the observed property as arguments. It can examine these 
            values to take the appropriate action.
            The values taken by editState can be used as a key in editStateDictionary to get a human understandale string 
            description of the state.
            Its default value on loading the page is 0 - corresponding to 'editor closed' state.
            */
            editState: {
                type: Number,
                value: 0,
                observer: "editStateChange"
            },
            /*
            The linkToEdit property encapsulates information about the particular link that is being edited - its link label.
            It has three subproperties - the href attribute of the link, the value of the label before editing and the 
            value of the label after editing.
            */
            linkToEdit: {
            value: {
                    href: {
                        type: String,
                        value: ""
                    },
                    valueBeforeEdit: {
                        type: String,
                        value: ""
                    },
                    valueAfterEdit: {
                        type: String,
                        value: ""
                    }
                }   
            },
            /*
            The links edited so far are pushed on to the Array - editedLinks
            */
            editedLinks: {
                type: Array,
                value: []
            },
            discardLinkLabelChange: {
                type: Boolean,
                value: false
            },
            historiedNarrative: {
                type: Number,
                value: -1,
                observer: "historiedNarrativeChange"
            },
            historyState: {
                type: Number,
                value: 0,
                observer: "historyStateChange"
            },
            historyStateDictionary: {
                value: {
                    0: "history closed",
                    1: "history opened" 
                }
            } 
        };
    }
    /*
    The 'supermethod' which is the observer on editState property. It includes all the code
    for styling changes and the actual manipulation of DOM and data model.
    It implicitly receives the old and new values of the observed property. Based on these values,
    appropriate action is taken.
    */
    closePanel() {
        this.editState = 0;
        this.historyState = 0;
    }
    hidePanel() {
        this.hideRightPanel = true;
        Polymer.dom(this.root).querySelector("#editor").style.width = "";
        Polymer.dom(this.root).querySelector("#view").style.width = "";
    }
    openPanel() {
        this.hideRightPanel = false;
        Polymer.dom(this.root).querySelector("#editor").style.width = "50%";
        Polymer.dom(this.root).querySelector("#view").style.width = "50%";
    }
    historyStateChange(newValue, oldValue) {
        if(newValue == 0){
            this.hideEditHistory = true;
            this.historiedNarrative = -1;
            this.editNarrativeHistory.length = 0;
            // dom-repeat render?
            if(this.hideNarrativeEditor == true){
                this.hidePanel();
                // this.hideRightPanel = true;
            }
        }
        if(newValue == 1){
            if(this.hideRightPanel == true){
                this.openPanel();
                // this.hideRightPanel = false;
            }
            this.hideEditHistory = false;
            // Polymer.dom(this.root).querySelector("#editor").style.width = "50%";
            // Polymer.dom(this.root).querySelector("#view").style.width = "50%";
        }
    }
    closeHistory() {
        this.historyState = 0;
    }
    openHistory(e) {
        var identity = e.target.getAttribute("identity");
        this.historiedNarrative = identity;
        if(this.historyState == 0){
            this.historyState = 1;
        }
    }
    historiedNarrativeChange(newValue, oldValue) {
        if(newValue == -1){
            return;
        }
        var _self = this;
        $.ajax({
            url: "http://localhost:8080/",
            type: "GET",
            dataType: "json",
            data: {originNarrativeIndex: this.historiedNarrative},
            contentType: "application/json",
            cache: false,
            timeout: 5000,
            async: false,
            complete: function() {
                console.log("GET request sent");
            },

            success: function(data) {
                console.log("GET success");
                _self.editNarrativeHistory.length = 0;
                var array = [];
                for(var i=0; i<data.length; ++i){
                    var obj = {};
                    obj.dateString = new Date(data[i].timeStamp).toDateString();
                    if(typeof data[i].editName !== "undefined"){
                        obj.editName = "Name : "+data[i].editName;
                    }
                    if(typeof data[i].editDescription !== "undefined"){
                        obj.editDescription = "Description : "+data[i].editDescription;
                    }
                    if(typeof data[i].editReason !== "undefined"){
                        obj.editReason = "Reason : "+data[i].editReason;
                    }
                    obj.editedNarrativeText = data[i].editedNarrativeText;
                    obj.editedLinks = data[i].editedLinks;
                    array.push(JSON.parse(JSON.stringify(obj)));
                }
                _self.editNarrativeHistory = array;
                // _self.notifyPath('editNarrativeHistory');
            },

            error: function(jqXHR, exception) {
                var msg = '';
                if (jqXHR.status === 0) {
                    msg = 'Not connected.\n Verify Network.';
                } 
                else if (jqXHR.status == 404) {
                    msg = 'Requested page not found. [404]';
                } 
                else if (jqXHR.status == 500) {
                    msg = 'Internal Server Error [500].';
                } 
                else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } 
                else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } 
                else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } 
                else {
                    msg = 'Uncaught Error.\n' + jqXHR.responseText;
                }
                console.log(msg);
            }
        });

        if(this.editNarrativeHistory.length == 0){
            Polymer.dom(this.root).querySelector("#warning-history").innerHTML = "You haven't made any edits to this narrative";
        }
        else{
            Polymer.dom(this.root).querySelector("#warning-history").innerHTML = "";
        }

    }
    toggleCard(e) {
        var identifier = e.target.getAttribute('identifier');
        var div = Polymer.dom(this.root).querySelector("iron-collapse[identifier='"+identifier+"']");
        var textDiv = Polymer.dom(div).querySelector(".card-content");
        $(textDiv).html(this.editNarrativeHistory[identifier].editedNarrativeText);
        for(var i=0; i<this.editNarrativeHistory[identifier].editedLinks.length; ++i){
            $(textDiv).find("a[href='"+this.editNarrativeHistory[identifier].editedLinks[i].href+"']").css("color","red");
        }
        div.toggle();
    }

    narrativeToEditChange(newValue, oldValue){
        this.editState = 9;
    } 
    editStateChange(newValue, oldValue){
        // Logging to console for debugging purposes
        console.log(newValue+" : "+this.editStateDictionary[newValue]);
        if(newValue == 9){
            this.editState = 1;
        }
        if(oldValue == 2 && newValue != 3){
            var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
            $(editorArea).find("a").css("color", "");
            $(editorArea).find("a").css("text-decoration", "");
            $(editorArea).css("user-select", "");
            $(editorArea).find("a").off("click");
        }
        // editState = 0 or 'editor closed' state. 
        // Clear the contents of the editor and make it hidden
        if(newValue == 0){
            Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = "";
            // Polymer.dom(this.root).querySelector("#editor #textarea").innerHTML = "";
            this.narrativeToEdit = -1;
            this.hideNarrativeEditor = true;
            if(this.hideEditHistory == true){
                this.hidePanel();
            }
            // this.restoreDefaults();
            // Polymer.dom(this.root).querySelector("#view").style.width = "";
            // Polymer.dom(this.root).querySelector("#editor").style.width = "";
            // this.hideRightPanel = true;
        }

        // editState = 1 or 'editor opened' state.
        // Open the editor panel and populate it with the contents of the card which caused
        // editor to open
        else if(newValue == 1){
            // var div = Polymer.dom(this.root).querySelector("#editor");
            // $(div).toggleClass('show')
            if(oldValue == 4){
                if(this.editedLinks.length == 0){
                    Polymer.dom(this.root).querySelector("#warning").innerHTML = "You haven't made any edits";
                }
                else{
                    var _self = this;
                    var metadata = {};
                    metadata.timeStamp = Date.now();
                    metadata.workflowURL = this.workflowURL;
                    metadata.originNarrativeIndex = this.narrativeToEdit;
                    metadata.originNarrativeTitle = this.narratives[this.narrativeToEdit].title;
                    metadata.originNarrativeText = this.narratives[this.narrativeToEdit].text;
                    metadata.editedNarrativeText = Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML;
                    metadata.editName = Polymer.dom(this.root).querySelector("#edit-name").value;
                    metadata.editDescription = Polymer.dom(this.root).querySelector("#edit-description").value;
                    metadata.editReason = Polymer.dom(this.root).querySelector("#edit-reason").value;
                    metadata.editedLinks = this.editedLinks;
                    console.info(metadata);
                    $.ajax({
                        url: "http://localhost:8080/",
                        type: "POST",
                        dataType: "json",
                        data: JSON.stringify(metadata),
                        contentType: "application/json",
                        cache: false,
                        timeout: 5000,
                        complete: function() {
                        //called when complete
                            console.log('POST complete');
                        },

                        success: function(data) {
                            console.log(data);
                            console.log('Data posted successfully');
                            if(_self.narrativeToEdit == _self.historiedNarrative){
                                var obj = {};
                                obj.dateString = new Date(metadata.timeStamp).toDateString();
                                if(typeof metadata.editName !== "undefined"){
                                    obj.editName = "Name : "+metadata.editName;
                                }
                                if(typeof metadata.editDescription !== "undefined"){
                                    obj.editDescription = "Description : "+metadata.editDescription;
                                }
                                if(typeof metadata.editReason !== "undefined"){
                                    obj.editReason = "Reason : "+metadata.editReason;
                                }
                                obj.editedNarrativeText = metadata.editedNarrativeText;
                                obj.editedLinks = metadata.editedLinks;
                                _self.editNarrativeHistory.unshift(obj);
                                Polymer.dom(_self.root).querySelector("#history-cards").render();
                                if(_self.editNarrativeHistory.length == 0){
                                    Polymer.dom(_self.root).querySelector("#warning-history").innerHTML = "You haven't made any edits to this narrative";
                                }
                                else{
                                    Polymer.dom(_self.root).querySelector("#warning-history").innerHTML = "";
                                }
                                // _self.notifyPath('editNarrativeHistory');
                                console.log(_self.editNarrativeHistory);
                            }
                        },

                        error: function(jqXHR, exception) {
                            var msg = '';
                            if (jqXHR.status === 0) {
                                msg = 'Not connected.\n Verify Network.';
                            } else if (jqXHR.status == 404) {
                                msg = 'Requested page not found. [404]';
                            } else if (jqXHR.status == 500) {
                                msg = 'Internal Server Error [500].';
                            } else if (exception === 'parsererror') {
                                msg = 'Requested JSON parse failed.';
                            } else if (exception === 'timeout') {
                                msg = 'Time out error.';
                            } else if (exception === 'abort') {
                                msg = 'Ajax request aborted.';
                            } else {
                                msg = 'Uncaught Error.\n' + jqXHR.responseText;
                            }
                            console.log(msg);
                        },
                    });
                    Polymer.dom(this.root).querySelector("#edit-name").value = "";
                    Polymer.dom(this.root).querySelector("#edit-description").value = "";
                    Polymer.dom(this.root).querySelector("#edit-reason").value = "";
                    this.editedLinks.length = 0;
                    this.hideSaveEditsBox = true;
                }
            
            }
            this.hideNarrativeEditor = false;
            this.openPanel();
            Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = Polymer.dom(this.root).querySelector(".card-content[identity='"+this.narrativeToEdit+"']").innerHTML; 

        }
        
        // editState = 2 or 'edit links' state.
        // There are two changes - change the styling of <a> elements in the editor and
        // associate the handler editLinkHandler with the click event on these <a> elements.
        else if(newValue == 2){
            // If coming from 'edit link label' state, save the link label, if edited
            // by pushing it on to the Array editedLinks
            if(oldValue == 3){
            if(this.discardLinkLabelChange == true){
                this.discardLinkLabelChange = false;
                console.info('In editStateChange, discardLinkLabelChange = '+this.discardLinkLabelChange);
            }
            else{
                var inputTextElement = Polymer.dom(this.root).querySelector("#input");
                var linkElement = Polymer.dom(this.root).querySelector("#editorarea #display a[href='"+this.linkToEdit.href+"']");
                var valueAfterSave = inputTextElement.value;
                if(valueAfterSave != this.linkToEdit.valueBeforeEdit){
                this.linkToEdit.valueAfterEdit = valueAfterSave;
                linkElement.innerHTML = this.linkToEdit.valueAfterEdit;
                this.editedLinks.push(JSON.parse(JSON.stringify(this.linkToEdit)));
                var warning = Polymer.dom(this.root).querySelector("#warning");
                warning.innerHTML = "";
                }
            }
            console.log(this.editedLinks);
            this.hideLinkEditor = true;
            }
            
            var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
            $(editorArea).find("a").css("color", "red");
            $(editorArea).find("a").css("text-decoration", "none");
            $(editorArea).css("user-select", "none");
            $(editorArea).find("a").on("click", this.editLinkHandler);
        }

        // editState = 3 or 'edit link label' state.
        // Just un-hide the link-editor <paper-card> 
        else if(newValue == 3){
            this.hideLinkEditor = false;
        }

        // editState = 4 or 'save edits' state.
        else if(newValue == 4){
            this.hideSaveEditsBox = false;
        }  
    }
    /*
    Hide all <span> tags with class "see". This is of course applied to the relevant <div>
    by selecting on the 'identity' attribute of the 'See More' button which triggered this event. 
    */
    seeLess(e){
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).find(".see").hide();
    }
    /*
    Show all <span> tags with class "see"
    */
    seeMore(e){
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).find(".see").show();
    }
    /*
    Toggle the card to expand or collapse
    */
    toggleCollapse(e) {
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).html(this.narratives[identity].text);
        Polymer.dom(this.root).querySelector("iron-collapse[identity='"+identity+"']").toggle();
    }
    /*
    Has the effect of opening the <div> containing the narrative editor and populate it with the 
    contents of the view that caused the editor to open.
    As a matter of implementation, it sets the narrativeToEdit property with the 'identity'
    attribute of the 'Edit' button which caused the editor to open. Additionally, it sets the 
    editState property to 1 - corresponding to the 'editor opened' state. The observer on editState
    which is editStateChange makes the changes.
    */ 
    openEditor(e) {
        var identity = e.target.getAttribute("identity");
        if(this.editState == 0){
            this.narrativeToEdit = identity;
            console.log(this.narrativeToEdit)
            this.editState = 1;
        }
        // if(this.editState == 0 || this.editState == 1){
        //      this.narrativeToEdit = identity;
        // }
        // else{
        //     return;
        // }
    }
    
    /*
    Close the editor by setting editState to 0.
    */
    closeEditor() {
        this.editState = 0;
    }
    /*
    Enter into 'edit links' state by setting editState to 2.
    */
    editLinkMode() {
        this.editState = 2;
    }
    /*
    Clicking on the 'Save' button after editing a link label will transition
    to the 'edit links' state by setting editState to 2.
    */
    saveLinkLabel(e) {
        this.editState = 2;
        this.discardLinkLabelChange = false;
    }
    cancelEditLink(e) {
        this.discardLinkLabelChange = true;
        console.info('In cancelEditLink, discardLinkLabelChange = '+this.discardLinkLabelChange);
        this.editState = 2;
    }
    writeSavedEdit(e) {
        this.editState = 1;
    }
    cancelSavingEdit(e) {
        this.editState = 1;
    }
    /*
    Clicking on the 'Save Edits' button after making several changes and 'OK's
    should transition to the 'save edits' state by setting editState to 5.
    */
    saveEdits() {
        if(this.editedLinks.length == 0){
            var warning = Polymer.dom(this.root).querySelector("#warning");
            // console.info(warning.hidden);
            warning.innerHTML = "You haven't made any edits so far";
            // console.info(warning.display)
        }
        else{
            this.editState = 4;
        }
    }
    /*
    This event handling method called editLinkHandler method is to be associated with
    all the <a> elements in the text of the narrative when the state is 'edit links'.
    Thus, this handler is imperatively added while tranisitioning into 'edit links'
    and removed while transitioning out.
    */
    editLinkHandler(e)  {
        console.log("In the editLinkHandler method");
        var _self = document.querySelector("feedback-app");
        _self.linkToEdit.href = e.target.href;
        _self.linkToEdit.valueBeforeEdit = e.target.innerHTML;
        Polymer.dom(_self.root).querySelector("#header").innerHTML = _self.linkToEdit.href;
        Polymer.dom(_self.root).querySelector("#header").href = _self.linkToEdit.href;
        Polymer.dom(_self.root).querySelector("#input").value = _self.linkToEdit.valueBeforeEdit;
        _self.editState = 3;
        return false;
    }
    
    
    /*
    ready event. It makes an ajax call to retrieve narratives from a JSON file
    Then it manipulates the shadow DOM to insert the data
    */
    ready() {
        super.ready();
        var _self = this;
        $.get("http://127.0.0.1:8080", {fileName: "data_narratives.json"}, function(data){
            _self.workflowURL = data.workflowURL;
            _self.narratives = data.narratives;
        });
    }
}
