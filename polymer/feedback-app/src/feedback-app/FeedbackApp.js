/**
    * @customElement
    * @polymer
    */
class FeedbackApp extends Polymer.Element {
    static get is() { return 'feedback-app'; }
    static get properties(){
        return {
            /*
            The 'hideEditor' property is used to hide or reveal the <div> containing narrative editor
            */
            hideEditor: {
                type: Boolean,
                value: true
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
            The 'hideTextEditor' property will be used to hide or reveal the <paper-card> containing the editor for links.
            */
            hideTextEditor: {
                type: Boolean,
                value: true
            },
            workflowURL: {
                type: String,
                value: ""
            },
            /*
            The narrative property is used to bind the contents of the <paper-card> iteratively to elements of an Array
            property (narratives) by dom-repeat. An element in narratives represents the JSON data correspondong to 
            narrative of a single view.
            */
            narratives: {
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
                    4: "edit text",
                    5: "save edits"
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
            } 
        };
    }
    /*
    The 'supermethod' which is the observer on editState property. It includes all the code
    for styling changes and the actual manipulation of DOM and data model.
    It implicitly receives the old and new values of the observed property. Based on these values,
    appropriate action is taken.
    */
   editStateChange(newValue, oldValue){
    // Logging to console for debugging purposes
    console.log(newValue+" : "+this.editStateDictionary[newValue]);
    if(oldValue == 4){
        this.hideTextEditor = true;
    }
    if(oldValue == 2 && newValue != 3){
        var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
        $(editorArea).find("a").css("color", "");
        $(editorArea).find("a").css("text-decoration", "");
        $(editorArea).find("a").off("click");
    }
    // editState = 0 or 'editor closed' state. 
    // Clear the contents of the editor and make it hidden
    if(newValue == 0){
        Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = "";
        Polymer.dom(this.root).querySelector("#editor #textarea").innerHTML = "";
        this.narrativeToEdit = -1;
        Polymer.dom(this.root).querySelector("#view").style.width = "";
        Polymer.dom(this.root).querySelector("#editor").style.width = "";
        this.hideEditor = true;
    }

    // editState = 1 or 'editor opened' state.
    // Open the editor panel and populate it with the contents of the card which caused
    // editor to open
    else if(newValue == 1){
        if(oldValue == 5){
        if(this.editedLinks.length == 0){
            Polymer.dom(this.root).querySelector("#warning").innerHTML = "You haven't made any edits";
        }
        else{
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
            var URL = "/src/feedback-app/data_store.json";

            var dataObject = { 'newWeekEntry': "aaa", 'oldWeekEntry': "zdfdfc" };

            // alert(JSON.stringify(dataObject));

            $.ajax({
                url: URL,
                type: 'PUT',    
                data: JSON.stringify(dataObject),
                contentType: 'application/json',
                success: function(result) {
                    alert("success?");
                }
            });
            var date = new Date(metadata.timeStamp);
            this.hideSaveEditsBox = true;
        }
        
        }
        this.hideEditor = false;
        Polymer.dom(this.root).querySelector("#editor").style.width = "50%";
        Polymer.dom(this.root).querySelector("#view").style.width = "50%";
        Polymer.dom(this.root).querySelector("#editorarea #display").innerHTML = Polymer.dom(this.root).querySelector(".card-content[identity='"+this.narrativeToEdit+"']").innerHTML; 
        // var editor = Polymer.dom(this.root).querySelector("#editor");
        // $(editor).show("slide", { direction: "right"}, 1000);
        // $()
    }
    
    // editState = 2 or 'edit links' satate.
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
        $(editorArea).find("a").on("click", this.editLinkHandler);
    }

    // editState = 3 or 'edit llnk label' state.
    // Just un-hide the link-editor <paper-card> 
    else if(newValue == 3){
        this.hideLinkEditor = false;
    }

    // editState = 4 or 'edit text' state.
    // Need to manipulate <paper-textarea> to fill it with HTML corresponding to the text of the view
    else if(newValue == 4){
        var editorArea = Polymer.dom(this.root).querySelector("#editorarea #display");
        this.hideLinkEditor = true;
        var textEditor = Polymer.dom(this.root).querySelector("#editorarea #textarea");
        textEditor.value = editorArea.innerHTML;
        this.hideTextEditor = false;
    }

    // editState = 5 or 'record edits' state.
    // Currently, prints the Array editedLinks to the console.
    else if(newValue == 5){
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
    openEditor(e){
    var identity = e.target.getAttribute("identity");
    this.narrativeToEdit = identity;
    this.editState = 1;
    }
    /*
    Close the editor by setting editState to 0.
    */
    closeEditor(){
    this.editState = 0;
    }
    /*
    Enter into 'edit links' state by setting editState to 2.
    */
    editLinkMode() {
    this.editState = 2;
    }
    /*
    Enter into 'edit text' state by setting editState to 4.
    */
    editTextMode() {
    this.editState = 4;
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
    Clicking on the 'Save' button after editing the text should transition to
    the 'editor opened' state by setting editState to 1.
    */
    saveEditedText(e){
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
        this.editState = 5;
    }
    }
    /*
    This event handling method called editLinkHandler method is to be associated with
    all the <a> elements in the text of the narrative when the state is 'edit links'.
    Thus, this handler is imperatively added while tranisitioning into 'edit links'
    and removed while transitioning out.
    */
    editLinkHandler(e){
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
    ready(){
    super.ready();
    var _self = this;
    $.get("/src/feedback-app/data_narratives.json", function(data){
        _self.workflowURL = data.workflowURL;
        _self.narratives = data.narratives;
    });
    }
}