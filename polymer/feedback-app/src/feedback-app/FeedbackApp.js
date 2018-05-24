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
                The 'hideEditHistory' property is used to hide or reveal the <paper-card> containing edit history
            */
            hideEditHistory: {
                type: Boolean,
                value: true
            },
            /* 
                The 'workflowURL' property acts as a unique identifier of a particular narrative
            */
            workflowURL: {
                type: String,
                value: ""
            },
            /*
                The 'narratives' property is used to bind the contents of the <paper-card> iteratively to elements of an Array
                property (i.e. narratives) by dom-repeat. An element in narratives represents the JSON data corresponding to the
                narrative of a single view.
            */
            narratives: {
                type: Array,
                value: []
            },
            /*
                The 'editNarrativeHistory' property is used to bind the contents of <paper-card> iteratively
                using <dom-repeat>
            */
            editNarrativeHistory: {
                type: Array,
                value: []
            },
            /*
                The property 'narrativeToEdit' is one of the state variables - it keeps a record of which narrative view is being
                edited currently and by consequence which edited view will be saved
            */
            narrativeToEdit: {
                type: Number,
                value: -1
            },
            /*
                The 'editStateDictionary' property exists mainly to assist programmers and debuggers to keep a handy reference for the 
                state of the narrative editor system
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
                The 'editState' property is another state variable which keeps track of the current state of the editing process
                The event handling has been restructured to follow the observer pattern. The event handlers relevant to
                editing change a single property - editState, on changing which an observer method - editStateChange is notifed
                This method is passed both the old value and new value of the observed property as arguments. It can examine these 
                values to take the appropriate action
                The values taken by editState can be used as a key to editStateDictionary to get a human understandable string 
                description of the state
                Its default value on loading the page is 0 - corresponding to 'editor closed' state
            */
            editState: {
                type: Number,
                value: 0,
                observer: "editStateChange"
            },
            /*
                The 'linkToEdit' property encapsulates information about the particular link that is being edited - its link label.
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
                The links edited so far are pushed on to the Array 'editedLinks'
            */
            editedLinks: {
                type: Array,
                value: []
            },
            /*
                The 'discardLinkLabelChange' property is used if users want to 
                cancel making an edit to the link label
            */
            discardLinkLabelChange: {
                type: Boolean,
                value: false
            },
            /*
                The 'historiedNarrative' property is also a state variable which keeps track of
                which narrative view's history is displayed in the edit history
                It has an observer method 'historiedNarrativeChange'
            */
            historiedNarrative: {
                type: Number,
                value: -1,
                observer: "historiedNarrativeChange"
            },
            /*
                The 'historyState' property is a state variable that keeps track of whether the <paper-card>
                containing edit history is opened or not
            */
            historyState: {
                type: Number,
                value: 0,
                observer: "historyStateChange"
            },
            /*
                The 'historyStateDictionary' provides human readable string values for the 'historyState'
            */
            historyStateDictionary: {
                value: {
                    0: "history closed",
                    1: "history opened" 
                }
            } 
        };
    }
    
    /*
        The event handler 'closePanel' closes the entire right panel - by writing to the state variables
    */
    closePanel() {
        this.editState = 0;
        this.historyState = 0;
    }

    /*
        The event handler 'hidePanel' closes the entire right panel - as a callback from historyStateChange() or editStateChange().
        It unsets any width styles applied to the left and right panels.
    */
    hidePanel() {
        this.hideRightPanel = true;
        Polymer.dom(this.root).querySelector("#editor").style.width = "";
        Polymer.dom(this.root).querySelector("#view").style.width = "";
    }

    /*
        The event handler 'openPanel' opens the entire right panel.
        It sets the width style of the left and right panels
    */
    openPanel() {
        this.hideRightPanel = false;
        Polymer.dom(this.root).querySelector("#editor").style.width = "50%";
        Polymer.dom(this.root).querySelector("#view").style.width = "50%";
    }

    /*
        The 'historyStateChange' is the observer method for the property 'historyState'.
        The old and new values of the observed property are implicitly passed to the observer.
        The observer then defines the actions associated with the observed property changing values in a particular way.
        Here, it involves setting/unsetting various properties.
    */
    historyStateChange(newValue, oldValue) {
        /*
            If 'historyState' is 0 i.e., the box needs to be closed, clear associated properties.
            If narrative editor is also closed, then close the entire right panel.
        */
        if(newValue == 0){
            this.hideEditHistory = true;
            this.historiedNarrative = -1;
            this.editNarrativeHistory.length = 0;
            if(this.hideNarrativeEditor == true){
                this.hidePanel();
            }
        }
        /*
            If 'historyState' is 1 i.e., the box needs to be opened, unhide it.
            If right panel is closed, then open it.  
        */
        if(newValue == 1){
            if(this.hideRightPanel == true){
                this.openPanel();
            }
            this.hideEditHistory = false;
        }
    }

    /*
        The 'closeHistory' method is the event handler that closes the edit history box.
    */
    closeHistory() {
        this.historyState = 0;
    }

    /*
        The 'openHistory' method is the event handler that opens the edit history box.
        It sets the 'historiedNarrative' property based on the 'identity' of the button which caused it to open.
        It also sets the 'historyState' property, if it was unset.
    */
    openHistory(e) {
        var identity = e.target.getAttribute("identity");
        this.historiedNarrative = identity;
        if(this.historyState == 0){
            this.historyState = 1;
        }
    }

    /*
        The 'historiedNarrativeChange' observes the property 'historiedNarrative'.
        Setting it to -1 carries the meaning of clearing the property.
        For any other value, it needs to fetch from the server, the edit history of 
        the narrative identified by that value.    
    */
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
                /*
                    Make the property 'editNarrativeHistory' contain as an array,
                    the edit history of the narratives.
                */
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

        /*
            If an empty array was fetched from the server, set a warning message.
            Else, clear the message.
        */
        if(this.editNarrativeHistory.length == 0){
            Polymer.dom(this.root).querySelector("#warning-history").innerHTML = "You haven't made any edits to this narrative";
        }
        else{
            Polymer.dom(this.root).querySelector("#warning-history").innerHTML = "";
        }

    }

    /*
        The 'toggleCard' method expands the <paper-card> containing
        a past edited history. Additionally, it highlights the edited links.
    */
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
    
    /*
        The 'editStateChange' method is the observer on 'editState' property. It includes all the code
        for styling changes and the actual manipulation of DOM and data model.
        It implicitly receives the old and new values of the observed property. Based on these values,
        appropriate action is taken.
    */
    editStateChange(newValue, oldValue){
        console.log(newValue+" : "+this.editStateDictionary[newValue]);

        /*
            Unsetting the CSS changes applied for 'edit links' state
        */
        if(oldValue == 2 && newValue != 3){
            var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
            $(editorArea).find("a").css("color", "");
            $(editorArea).find("a").css("text-decoration", "");
            $(editorArea).css("user-select", "");
            $(editorArea).find("a").off("click");
        }

        /*
            editState = 0 or 'editor closed' state. 
            Clear the contents of the editor and make it.
        */
        if(newValue == 0){
            Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = "";
            this.narrativeToEdit = -1;
            this.hideNarrativeEditor = true;
            if(this.hideEditHistory == true){
                this.hidePanel();
            }
        }

        /*
            editState = 1 or 'editor opened' state.
            Open the editor panel and populate it with the contents of the card
            which caused editor to open.
        */
        else if(newValue == 1){
            /*
                If, entering 'editor opened' state from 'save edits'
                state, send the edited narrative to the server for
                permanent storage. 
            */
            if(oldValue == 4){
                var _self = this;
                /*
                    Pack all the details needed to be saved into the metadata object.
                    Then use an Ajax POST request to send it to the server.
                */
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
                        console.log('POST complete');
                    },

                    success: function(data) {
                        console.log(data);
                        console.log('Data posted successfully');
                        /*
                            If the edit history box was open, push recently edited
                            narrative on to the queue.
                        */
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
            this.hideNarrativeEditor = false;
            this.openPanel();
            Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = Polymer.dom(this.root).querySelector(".card-content[identity='"+this.narrativeToEdit+"']").innerHTML; 
        }
        
        /*
            editState = 2 or 'edit links' state.
        */
        else if(newValue == 2){
            /*
                If coming from 'edit link label' state, save the link label, if edited
                by pushing it on to the Array editedLinks
            */
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
            /*
                Change the styling of <a> elements in the editor,
                make the text selectable, and
                associate the handler editLinkHandler with the click event on these <a> elements.
            */
            var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
            $(editorArea).find("a").css("color", "red");
            $(editorArea).find("a").css("text-decoration", "none");
            $(editorArea).css("user-select", "none");
            $(editorArea).find("a").on("click", this.editLinkHandler);
        }

        /*
            editState = 3 or 'edit link label' state.
            Just un-hide the link-editor <paper-card> 
        */
        else if(newValue == 3){
            this.hideLinkEditor = false;
        }

        /*
            editState = 4 or 'save edits' state.
        */
        else if(newValue == 4){
            this.hideSaveEditsBox = false;
        }  
    }

    /*
        The method 'seeLess' - Hide all <span> tags with class "see". This is of course applied to the relevant <div>
        by selecting on the 'identity' attribute of the 'See More' button which triggered this event. 
    */
    seeLess(e){
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).find(".see").hide();
    }

    /*
        The method 'seeMore' - Show all <span> tags with class "see"
    */
    seeMore(e){
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).find(".see").show();
    }

    /*
        The method 'toggleCollapse' - Toggle the card to expand or collapse
    */
    toggleCollapse(e) {
        var identity = e.target.getAttribute("identity");
        var div = Polymer.dom(this.root).querySelector(".card-content[identity='"+identity+"']");
        $(div).html(this.narratives[identity].text);
        Polymer.dom(this.root).querySelector("iron-collapse[identity='"+identity+"']").toggle();
    }

    /*
        The method 'openEditor' - Has the effect of opening the <div> containing the narrative editor and populate it with the 
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
    }
    
    /*
        The method 'closeEditor' - Close the editor by setting editState to 0.
    */
    closeEditor() {
        this.editState = 0;
    }

    /*
        The method 'editLinkMode' - Enter into 'edit links' state by setting editState to 2.
    */
    editLinkMode() {
        this.editState = 2;
    }

    /*
        The method 'saveLinkLabel' - Clicking on the 'Save' button after editing a link label will transition
        to the 'edit links' state by setting editState to 2.
    */
    saveLinkLabel(e) {
        this.editState = 2;
        this.discardLinkLabelChange = false;
    }

    /*
        The method 'cancelEditLink' - Prevents saving edited link label if users click on 'Cancel' button
        and reverts to 'edit links' state.
    */
    cancelEditLink(e) {
        this.discardLinkLabelChange = true;
        console.info('In cancelEditLink, discardLinkLabelChange = '+this.discardLinkLabelChange);
        this.editState = 2;
    }

    /*
        The method 'writeSavedEdit' - Take the action of saving edits, by moving to 'editor opened' state
        where the observer will actually handle the task of sending data to the server for storage.
    */
    writeSavedEdit(e) {
        this.editState = 1;
    }

    /*
        The method 'cancelSavingEdit' - Discard editing process.
    */
    cancelSavingEdit(e) {
        this.editState = 1;
    }

    /*
        The method 'saveEdits' - Clicking on the 'Save Edits' button after making several changes and 'OK's
        should transition to the 'save edits' state by setting editState to 5.
    */
    saveEdits() {
        if(this.editedLinks.length == 0){
            var warning = Polymer.dom(this.root).querySelector("#warning");
            warning.innerHTML = "You haven't made any edits so far";
        }
        else{
            this.editState = 4;
        }
    }

    /*
        This event handling method called 'editLinkHandler' method is to be associated with
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
        The method called on 'ready' event. It makes an Ajax call to retrieve narratives from a JSON file
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
