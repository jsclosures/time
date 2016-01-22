function buildEquipmentEditPage(mainContext, mainId,currentChild) {

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
                    "dojox/mobile/Video"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildEquipmentEditPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildEquipmentEditPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var formFields = new Array();
    formFields.push({label: "title",name: "title","type": "TEXTFIELD"});
    formFields.push({label: "comments",name: "comments","type": "TEXTFIELD"});
            
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
        var currentPhoto = false;
        context.startChild = function () {
		//console.log("start content page");
		if( !started ){
                    buildMainPage({id: mainForm});
                    started = true;
                }
        }

        context.stopChild = function () {
		//console.log("stop content page");
		var video = dojo.byId(mainForm + "video");
                
                if( video && video.pause ){
                    video.pause();
                }
        }
        
        context.setTarget = function (target) {
		//console.log("start content page");
		console.log(target);
                for(var i = 0;i < formFields.length;i++){
                    var tField = formFields[i];
                       
                    var tObj = anyWidgetById(mainForm + tField.name);
                    
                    if( tObj ){
                        tObj.set("value",target[tField.name] ? target[tField.name] : "");
                    }
                }
                
                currentPhoto = false;
                
                navigator.getUserMedia = ( navigator.getUserMedia ||
                       navigator.webkitGetUserMedia ||
                       navigator.mozGetUserMedia ||
                       navigator.msGetUserMedia);

                if (navigator.getUserMedia) {
                   navigator.getUserMedia (
                
                      // constraints
                      {
                         video: {
                            mandatory: {
                              maxWidth: 320,
                              maxHeight: 240
                            }
                          },
                         audio: false
                      },
                
                      // successCallback
                      function(localMediaStream) {
                         var video = dojo.byId(mainForm + "video");
                         video.src = window.URL.createObjectURL(localMediaStream);
                         // Do something with the video here, e.g. video.play()
                         video.play();
                      },
                
                      // errorCallback
                      function(err) {
                         console.log("The following error occured: " + err);
                      }
                   );
                } else {
                   console.log("getUserMedia not supported");
                }
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
                    
                    var label = new dojox.mobile.ContentPane({id: mainForm + "titlelabel",content: profileManager.getString("equipmentTitle")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
    
              var titleField = new dojox.mobile.TextBox(
                          {
                              id: mainForm + "title",
                              name: mainForm + "title"
                          }
                      );
                   registeredWidgetList.push(titleField.id);   
              
            formContainer.addChild(titleField);
            
            
            var label = new dojox.mobile.ContentPane({id: mainForm + "commentslabel",content: profileManager.getString("comments")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
    
              var titleField = new dojox.mobile.TextBox(
                          {
                              id: mainForm + "comments",
                              name: mainForm + "comments"
                          }
                      );
                   registeredWidgetList.push(titleField.id);   
              
            formContainer.addChild(titleField);
        
            
            
            var controlContainer = new dojox.mobile.RoundRect({id: mainForm + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var saveButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "save",
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
            
            var photoButton = new dojox.mobile.Button({
                    label: "",
                    name: mainForm + "photo",
                    innerHTML: profileManager.getString("photo"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "photoIcon",
                    onClick: function(){
                        doPhotoAction();
                    }
            });
            
            registeredWidgetList.push(photoButton.id);
            controlContainer.addChild(photoButton); 
            
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
            
            
            var videoWrapper = new dojox.mobile.ContentPane({id: mainForm + "videowrapper",content: "<video id=\"" + mainForm + "video" + "\" style=\"border: 0px solid black;width: 320px;height: 240px;\"></video>"});
                    registeredWidgetList.push(videoWrapper.id);
                    formContainer.addChild(videoWrapper);
            
            var viewWrapper = new dojox.mobile.ContentPane({id: mainForm + "viewwrapper",content: "<canvas id=\"" + mainForm + "view" + "\" style=\"border: 0px solid black;width: 320px;height: 240px;\"></canvas>"});
                    registeredWidgetList.push(viewWrapper.id);
                    formContainer.addChild(viewWrapper);
            
            //var video = new dojox.mobile.video({id: mainForm + "video",src: [{src:"video/sample.mp4", type:"video/mp4"}]},dojo.byId(mainForm + "video"));
            
               formContainer.startup();
               controlContainer.startup();
        
                outerContainer.startup();
    
            function doSaveAction() {
                     var requestData = {contenttype: "EQUIPMENT"};
                     requestData.title = anyWidgetById(mainForm + "title").get("value");
                     requestData.comments = anyWidgetById(mainForm + "comments").get("value");
                     var bg = currentPhoto ? currentPhoto : doPhotoAction();
                     if( bg ){
                         requestData.background = bg;
                     }
                     console.log(requestData);
                     
                     var doLater = function(data){
                        anyWidgetById(mainForm + "title").set("value","");
                         getCurrentContext().CacheManager.purgeType({contenttype: "EQUIPMENT"});
                         getCurrentContext().setCurrentView("equipment"); 
                     }
                     var tURL = getCurrentContext().UIProfileManager.getSetting("mojoStoreUrl");
                
                     getDataService(tURL, doLater).post(false,requestData);             
            }
            
            function doPhotoAction() {
                var video = anyWidgetById(mainForm + "video");
                var result = false;
                if( video ){
                    var canvas = dojo.byId(mainForm + "view");
                    var ctx = canvas.getContext('2d');
                    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
                     var imageData = canvas.toDataURL('image/png', 1);
                     console.log(imageData);
                     result = imageData;
                     currentPhoto = result;
                }
                
                return( result );
            }
            
            function doCancelAction() {
                     getCurrentContext().setCurrentView("equipment");
                     
            }
            
            
            return( outerContainer );
    }
}
