function buildEquipmentViewPage(mainContext, mainId,currentChild) {

	require([
                    "dojox/mobile/ComboBox",
                    "dojox/mobile/Button",
                    "dojox/mobile/SimpleDialog",
                    "dojox/mobile/View",
                    "dojox/mobile/TextBox",
                    "dojox/mobile/TextArea",
                    "dojox/mobile/RoundRect",
                    "dojox/mobile/Container",
                    "dojox/mobile/ContentPane",
                    "dojox/mobile/ScrollableView",
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore",
                    "dojox/gfx"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildEquipmentViewPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildEquipmentViewPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var maxBounds = false;
    var formFields = new Array();
    formFields.push({label: "title",name: "title"});
    formFields.push({label: "comments",name: "comments"});
   
    //console.log("content page context " + context + " in : " + mainId);

    if (context) {
        context.initChild = function () {
		  //console.log("init Content page");
		  var iHtml = '<div id="' + mainForm + '"></div>';
		  
                if( !context.useDojo ) 
                {
                        dojo.byId(mainId).innerHTML = iHtml;
                }
                else
                {
                        dijit.byId(mainId).attr("content",iHtml);
                }
        }

        context.resizeDisplay = function () {
            //console.log("resize Content page: ");
		  
         var cContext = getCurrentContext();
		  
		  var tObj = dojo.byId(mainForm);

        }

        var surface = false;
        var started = false; 

        context.startChild = function () {
                if( !started ){
                    started = true;
                    buildMainPage({id: mainForm});
                    
                }
                
		//console.log("start content page");
                if( !surface ){
                    var canvas = anyWidgetById(mainForm + "canvas");
                    var dim = {width: 300,height: 300};
                
                    dojox.gfx.createSurface(mainForm + "canvas",dim.width,dim.height).whenLoaded(this, function(newSurface){
                        setSurface(newSurface);
                    });
                }
                
        }
        
        
        function setSurface(s){
            surface = s;
            var container = anyWidgetById(mainForm + "canvas");
            
            var sPos = dojo.position(container.domNode);
            maxBounds = {x: sPos.w,y: sPos.h};
            
        }
        

        function loadEquipmentData(){
            surface.clear();
            
            if( target && target.background ){
                surface.createImage({x:0,y:0, width:360, height:240, src:target.background})
            }
            
            
        }
        
        context.stopChild = function () {
		console.log("stop equipmentview page");
        }
        var target = false;
        context.getTarget = function(){
            return( target );
        }
        context.setTarget = function (rec) {
		console.log("start equipmentview page");
                target = rec;
		console.log("target: " + target);
                
                anyWidgetById(mainForm + "title").set("value",target.title);
                anyWidgetById(mainForm + "comments").set("value",target.comments);
                loadEquipmentData();
        }

        context.destroyChild = function () {
		//console.log("destroy content page");
                for(var i = 0;i < connectorList.length;i++)
                {
                    deregisterEventHandler(connectorList[i]);
                }
                
                for(var i = 0;i < registeredWidgetList.length;i++)
                {
                    deregisterDijitWidget(registeredWidgetList[i]);
                }
	   }

        //console.log("added lifecycle handlers to content page context");
    }

    function buildMainPage(context){
            var formId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: formId},dojo.byId(formId));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: formId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                        var label = new dojox.mobile.ContentPane({id: mainForm + tField.name + "label",content: profileManager.getString(tField.label)});
                        registeredWidgetList.push(label.id);
                        formContainer.addChild(label);
    
                          var newField = new dojox.mobile.TextBox(
                                      {
                                          id: mainForm + tField.name,
                                          name: mainForm + tField.name,
                                          disabled: true
                                      }
                                  );
                               registeredWidgetList.push(newField.id);   
                          
                        formContainer.addChild(newField);
                    }

            var controlContainer = new dojox.mobile.RoundRect({id: formId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "delete",
                    innerHTML: profileManager.getString("delete"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "deleteIcon",
                    onClick: function(){
                        doDeleteAction();
                    }
            });
            
            registeredWidgetList.push(saveButton.id);
            controlContainer.addChild(saveButton); 
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    id: formId + "cancel",
                    innerHTML: profileManager.getString("cancel"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "cancelIcon",
                    onClick: function(){
                        doCancelAction();
                    }
            });
            
            registeredWidgetList.push(cancelButton.id);
            controlContainer.addChild(cancelButton); 
            
            
            
            var canvas = new dojox.mobile.ContentPane({id: formId + "canvas"});
                    registeredWidgetList.push(canvas.id);
                    formContainer.addChild(canvas);
            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doDeleteAction() {
                    var rec = anyWidgetById(mainId).getTarget();
                    
                    var requestData = {contenttype: "EQUIPMENT"};
                     requestData.id = rec.id;
                     
                     console.log(requestData);
                     
                     var doLater = function(data){
                         getCurrentContext().CacheManager.purgeType({contenttype: "EQUIPMENT"});
                         getCurrentContext().setCurrentView("equipment"); 
                     }
                     
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater)['delete'](false,requestData);         
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("equipment");           
            }
            
        
            
            
            return( outerContainer );
    }
}




