function buildTrackerViewPage(mainContext, mainId,currentChild) {

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
                    "dojo/data/ItemFileReadStore"
         ], 
         function(){
        	 //console.log("building routingedit page");
        	 
        	 internalBuildTrackerViewPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildTrackerViewPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var formFields = new Array();
    formFields.push({label: "name",name: "name"});
    formFields.push({label: "action",name: "action"});
    formFields.push({label: "target",name: "target"});
            
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

        }
        var started = false; 
        context.startChild = function () {
		//console.log("start content page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                    if( target ){
                        context.setTarget(target);
                    }
                }
                
        }

        context.stopChild = function () {
		//console.log("stop content page");
		
        }
        
        function onListClick(){
            var item = this;
            console.log(item.boardtitle);
            getCurrentContext().setCurrentView("trackeredit");
            
            var doLater = function(){
                anyWidgetById("trackeredit").setParent(anyWidgetById(mainId).getTarget());
                anyWidgetById("trackeredit").setTarget(item.actualRecord);
            }
            setTimeout(doLater,1000);
        }
        
        var target = false;
        context.reloadTarget = function () {
            context.setTarget(target);
        }
        
        context.setTarget = function (t) {
		//console.log("start content page");
                target = t;
		console.log(target);
                
                var doLater = function(response){
                   
                    var storeData = new Array();
                    
                    if( response.items ){
                        var uiManager = getCurrentContext().UIProfileManager;
                        
                        for(var i = 0;i < response.items.length;i++){
                            var item = response.items[i];
                            var newItem = {id: item.id,
                                            label: item.id,
                                            mode: item.mode,
                                            action: item.action,
                                            target: item.target,
                                            required: item.required,
                                            xsequence: item.sequence,
                                            xclassName: item.className,
                                            actualRecord: item,
                                            "icon": "images/tracker.png", 
                                            "rightText": uiManager.getString("edit"), 
                                            "moveTo": "bar" ,
                                            "onClick": onListClick};
                            
                            storeData.push(newItem);
                        }
                    }
                    var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "label"});
                    
                    dijit.byId(mainForm + "itemlist").setStore(newStore);
                }
                getCurrentContext().CacheManager.getData({contenttype:"TRACKER",nocache: true,parentid: target.id,callback: doLater});
                
                for(var i = 0;i < formFields.length;i++){
                    var tField = formFields[i];
                       
                    var tObj = anyWidgetById(mainForm + tField.name);
                    
                    if( tObj ){
                        tObj.set("value",target[tField.name]);
                    }
                }
                
                
        }
        
        context.getTarget = function () {
            return( target );
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
            
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: mainForm},dojo.byId(mainForm));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainForm + "form"});
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
        
            
            
            var controlContainer = new dojox.mobile.RoundRect({id: mainForm + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var addButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "add",
                    innerHTML: profileManager.getString("add"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "addIcon",
                    onClick: function(){
                        doAddAction();
                    }
            });
            
            registeredWidgetList.push(addButton.id);
            controlContainer.addChild(addButton); 
            
            var deleteButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "delete",
                    innerHTML: profileManager.getString("delete"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "deleteIcon",
                    onClick: function(){
                        doDeleteAction();
                    }
            });
            
            registeredWidgetList.push(deleteButton.id);
            controlContainer.addChild(deleteButton); 
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "cancel",
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
            
            var storeData = [
                    
                ];
                var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "label"});
              var itemList = new dojox.mobile.EdgeToEdgeStoreList(
                          {
                              id: mainForm + "itemlist",
                              name: mainForm + "itemlist",
                              store: newStore
                          }
                      );
                   registeredWidgetList.push(itemList.id);   
              
            formContainer.addChild(itemList);
            itemList.startup();

            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doAddAction() {
                     getCurrentContext().setCurrentView("trackeredit");
                     var doLater = function(){
                        var t = anyWidgetById(mainId).getTarget();
                        
                         anyWidgetById("trackeredit").setParent(t);
                         anyWidgetById("trackeredit").setTarget({});
                     }
                     
                     setTimeout(doLater,1000);
            }
            
            function doDeleteAction() {
                     var requestData = {contenttype: "TRACKER",id: anyWidgetById(mainId).getTarget().id};
                     
                     console.log(requestData);
                     
                     var doLater = function(data){
                        for(var i = 0;i < formFields.length;i++){
                            var tField = formFields[i];
                             anyWidgetById(mainForm + tField.name).set("value","");
                         }
                         getCurrentContext().CacheManager.purgeType({contenttype: "TRACKER"});
                         getCurrentContext().setCurrentView("tracker"); 
                     }
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater)["delete"](false,requestData); 
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("tracker");           
            }
            
            
            return( outerContainer );
    }
}
