function buildJobPage(mainContext, mainId,currentChild) {

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
                    "js/jobedit.js",
                    "js/jobview.js"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildJobPage(mainContext, mainId);
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildJobPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
            
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
        function onListClick(){
            var item = this;
            console.log(item.actualRecord.jobtitle);
            getCurrentContext().setCurrentView("jobview");
            
            var doLater = function(){
                anyWidgetById("jobview").setTarget(item.actualRecord);
            }
            setTimeout(doLater,1000);
        }
        var started = false;
        
        context.startChild = function () {
		console.log("start Main page");
		if( !started ){
                    started = true;
                    buildMainPage({id: mainForm});
                }

		console.log("start job page");
                var doLater = function(response){
                    /*var storeData = [
                        { "id":1,"jobtitle": "Job 1", "icon": "images/i-icon-3.png", "rightText": "Join", "moveTo": "bar" ,"onClick": onListClick},
                        { "id":2,"jobtitle": "Job 2", "icon": "images/i-icon-4.png", "rightText": "Join", "moveTo": "bar" ,"onClick": onListClick}
                    ];*/
                    var storeData = new Array();
                    
                    if( response.items ){
                        var uiManager = getCurrentContext().UIProfileManager;
                        
                        for(var i = 0;i < response.items.length;i++){
                            var item = response.items[i];
                            var newItem = {id: item.id,
                            label: item.jobtitle,
                            actualRecord: item,
                            "icon": "images/job.png", 
                            "rightText": uiManager.getString("join"), 
                            "moveTo": "bar" ,
                            "onClick": onListClick};
                            
                            storeData.push(newItem);
                        }
                    }
                    var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "label"});
                    
                    dijit.byId(mainForm + "itemlist").setStore(newStore);
                }
                getCurrentContext().CacheManager.getData({contenttype:"JOB",callback: doLater});
		
        }

        context.stopChild = function () {
            console.log("stop job page");
		
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
            var mainId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.ScrollableView({id: mainId},dojo.byId(mainId));
                    registeredWidgetList.push(outerContainer.id);
                    
                    
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    var label = new dojox.mobile.ContentPane({id: mainId + "titlelabel",content: profileManager.getString("jobTitle")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
                    
                                var controlContainer = new dojox.mobile.RoundRect({id: mainId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var loginButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "action",
                    innerHTML: profileManager.getString("newJob"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "loginIcon",
                    onClick: function(){
                        doAction();
                    }
            });
            
            registeredWidgetList.push(loginButton.id);
            controlContainer.addChild(loginButton); 
                    
                    
                    
    var storeData = [
                    
                ];
                var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "jobtitle"});
              var itemList = new dojox.mobile.EdgeToEdgeStoreList(
                          {
                              id: mainId + "itemlist",
                              name: mainId + "itemlist",
                              store: newStore
                          }
                      );
                   registeredWidgetList.push(itemList.id);   
              
            formContainer.addChild(itemList);
            itemList.startup();

        
            
            

            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doAction() {
                     getCurrentContext().setCurrentView("jobedit"); 
                     
                     
                     var doLater = function(){
                         anyWidgetById("jobedit").setTarget({});
                     }
                     
                     setTimeout(doLater,1000);
            }
            
            
            return( outerContainer );
    }
}