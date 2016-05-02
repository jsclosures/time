function buildContentPage(mainContext, mainId,currentChild) {

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
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildContentPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildContentPage(mainContext, mainId) {

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
                
                buildMainPage({id: mainForm});
        }

        context.resizeDisplay = function () {
            //console.log("resize Content page: ");
		  
         var cContext = getCurrentContext();
		  
		  var tObj = dojo.byId(mainForm);

        }

        context.startChild = function () {
		//console.log("start content page");
		
        }

        context.stopChild = function () {
		//console.log("stop content page");
		
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
           
        context.setTarget = function (target) {
		//console.log("start content page");
		console.log(target);
                anyWidgetById(mainForm + "title").set("value",target.contenttitle);
                anyWidgetById(mainForm + "body").set("value",target.contentbody);
        }

        //console.log("added lifecycle handlers to content page context");
    }

    function buildMainPage(context){
            var mainId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.Container({id: mainId},dojo.byId(mainId));
                    registeredWidgetList.push(outerContainer.id);
                    //outerContainer.startup();
                        
                    var formContainer = new dojox.mobile.RoundRect({id: mainId + "form"});
                    registeredWidgetList.push(formContainer.id);
                    
                    outerContainer.addChild(formContainer);
                    
                    var label = new dojox.mobile.ContentPane({id: mainId + "titlelabel",content: profileManager.getString("contentTitle")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
    
              var titleField = new dojox.mobile.TextBox(
                          {
                              id: mainId + "title",
                              name: mainId + "title",
                              style: "width: 100%",
                              rows: 10
                          }
                      );
                   registeredWidgetList.push(titleField.id);   
              
            formContainer.addChild(titleField);
            
            var label2 = new dojox.mobile.ContentPane({id: mainId + "bodylabel",content: profileManager.getString("contentBody")});
                    registeredWidgetList.push(label2.id);
                    formContainer.addChild(label2);
    
              var bodyField = new dojox.mobile.TextArea(
                          {
                              id: mainId + "body",
                              name: mainId + "body",
                              style: "width: 100%"
                          }
                      );
                   registeredWidgetList.push(bodyField.id);   
              
            formContainer.addChild(bodyField);
        
            
            
            /*var controlContainer = new dojox.mobile.RoundRect({id: mainId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var loginButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "action",
                    innerHTML: profileManager.getString("return"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "loginIcon",
                    onClick: function(){
                        doAction();
                    }
            });
            
            registeredWidgetList.push(loginButton.id);
            controlContainer.addChild(loginButton); */
            
               formContainer.startup();
               //controlContainer.startup();
        
                outerContainer.startup();
    
            function doAction() {
                     getCurrentContext().setCurrentView("main");           
            }
            
            
            return( outerContainer );
    }
}
