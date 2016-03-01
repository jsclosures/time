function buildTrackerEditPage(mainContext, mainId,currentChild) {

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
                    "dojox/mobile/DatePicker",
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore",
                    "dojox/mobile/DatePicker",
                    "dojox/mobile/TimePicker"
         ], 
         function(){
        	 //console.log("building routingedit page");
        	 
        	 internalBuildTrackerEditPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildTrackerEditPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var formFields = new Array();
    formFields.push({label: "name",name: "name","type": "TEXTFIELD"});
    formFields.push({label: "job",name: "job","type": "COMBOFIELD",contentType: "JOBFACET"});
    formFields.push({label: "equipment",name: "equipment","type": "COMBOFIELD",contentType: "EQUIPMENTFACET"});
    formFields.push({label: "location",name: "location","type": "TEXTFIELD"});
    formFields.push({label: "comments",name: "comments","type": "TEXTFIELD"});
    formFields.push({label: "starttime",name: "starttime","type": "TEXTFIELD"});
    formFields.push({label: "endtime",name: "endtime","type": "TEXTFIELD"});
            
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
        var started = false; 
        context.startChild = function () {
		//console.log("start content page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                }
                getCurrentContext().purgeNextActions();
        }

        context.stopChild = function () {
		//console.log("stop content page");
		
        }
        function onListClick(){
            var item = this;
            console.log(item.actualRecord.name);
            target = item.actualRecord;
            for(var i = 0;i < formFields.length;i++){
                var tField = formFields[i];
                   
                var tObj = anyWidgetById(mainForm + tField.name);
                
                if( tObj ){
                    tObj.set("value",target[tField.name] ? target[tField.name] : "");
                }
            }
            
            if( target.hasOwnProperty("id") ){
                hideMobileWidget(false,mainForm + "copy");    
            }
            else {
                hideMobileWidget(true,mainForm + "copy");    
            }
        }
        var target = false;
        context.setTarget = function (t) {
                target = t;
		//console.log("start content page");
		console.log(target);
                
                if( !target.location ){
                    var pos = getCurrentContext().GeoLocation.position.coords;
                    
                    target.location =  pos.latitude + "," + pos.longitude;
                    target.latitude = pos.latitude;
                    target.longitude = pos.longitude;
                }
                var currentTime = getCurrentContext().UIProfileManager.getCurrentTime(target.hours ? target.hours : 8);
                
                if( !target.starttime ){
                    target.starttime = currentTime.starttime;
                }
                
                if( !target.endtime ){
                    target.endtime = currentTime.endtime;
                }
                
                if( target.hasOwnProperty("id") ){
                    hideMobileWidget(false,mainForm + "copy");    
                }
                else {
                    hideMobileWidget(true,mainForm + "copy");    
                }
                
                for(var i = 0;i < formFields.length;i++){
                    var tField = formFields[i];
                       
                    var tObj = anyWidgetById(mainForm + tField.name);
                    
                    if( tObj ){
                        tObj.set("value",target[tField.name] ? target[tField.name] : "");
                        
                        if( tField.type != 'DATEFIELD' && tField.contentType ){
                        
                            var sContext = {widgetId: tObj.id};
                            
                            var doLater = function(sData){
                                var tsData = new dojo.store.Memory({ idProperty: "name", data: sData.items});
                                                                                            
                                dijit.byId(this.widgetId).set("store",tsData);
                            }
                            
                            var sCallback = dojo.hitch(sContext,doLater);
                            
                            getCurrentContext().CacheManager.getData({contenttype:tField.contentType,nocache:true,callback: sCallback});
                        }
                    }
                }
                
                var doLater = function(response){
                    var storeData = new Array();
                    
                    if( response.items ){
                        for(var i = 0;i < response.items.length;i++){
                            var item = response.items[i];
                            var newItem = {id: item.id,label: item.name,actualRecord: item,"icon": "images/tracker.png", "rightText": "Select", "moveTo": "bar" ,"onClick": onListClick};
                            
                            storeData.push(newItem);
                        }
                    }
                    var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "label"});
                    
                    dijit.byId(mainForm + "itemlist").setStore(newStore);
                }
                getCurrentContext().CacheManager.getData({contenttype:"TRACKER",nocache:true, distance: 5, location: target.location,callback: doLater});
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
    
                          var newField; 
                          if( tField.type == 'DATEFIELD' ){
                                newField = new dojox.mobile.DatePicker(
                                      {
                                          id: mainForm + tField.name,
                                          name: mainForm + tField.name
                                      }
                                  );
                          }
                          else {
                            newField = new dojox.mobile.ComboBox(
                                      {
                                          id: mainForm + tField.name,
                                          name: mainForm + tField.name
                                      }
                                  );
                              
                          }
                           newField.domNode.setAttribute("x-webkit-speech",true);
                      newField.domNode.setAttribute("speech",true);       
                                  
                               registeredWidgetList.push(newField.id);   
                          
                        formContainer.addChild(newField);
                    }
                    
        
            
            
            var controlContainer = new dojox.mobile.RoundRect({id: mainForm + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    id: mainForm + "save",
                    innerHTML: profileManager.getString("save"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "saveIcon",
                    onClick: function(){
                        doSaveAction();
                    }
            });
            
            registeredWidgetList.push(saveButton.id);
            controlContainer.addChild(saveButton); 
            
            var copyButton = new dojox.mobile.Button({
                    label: "",
                    id: mainForm + "copy",
                    innerHTML: profileManager.getString("copy"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "copyIcon",
                    onClick: function(){
                        doCopyAction();
                    }
            });
            
            registeredWidgetList.push(copyButton.id);
            controlContainer.addChild(copyButton); 
            
            var cancelButton = new dojox.mobile.Button({
                    label: "",
                    id: mainForm + "cancel",
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
                var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "name"});
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
    
            function doSaveAction() {
                     var requestData = {contenttype: "TRACKER"};
                     for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                         requestData[tField.name] = anyWidgetById(mainForm + tField.name).get("value");
                     }
                     if( anyWidgetById(mainId).getTarget().id ){
                         requestData.id = anyWidgetById(mainId).getTarget().id;
                     }
                     
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
                
                     if( requestData.id )
                        getDataService(tURL, doLater).put(false,requestData);     
                     else
                        getDataService(tURL, doLater).post(false,requestData);                
            }
            
            function doCopyAction() {
                     var requestData = {contenttype: "TRACKER"};
                     for(var i = 0;i < formFields.length;i++){
                        var tField = formFields[i];
                         requestData[tField.name] = anyWidgetById(mainForm + tField.name).get("value");
                     }
  
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
                
                     getDataService(tURL, doLater).post(false,requestData);                
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("tracker");           
            }
            
            
            return( outerContainer );
    }
}
