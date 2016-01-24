function buildAuthPage(mainContext, mainId,currentChild) {

	require([
                    "dojox/mobile/ComboBox",
                    "dojox/mobile/Button",
                    "dojox/mobile/SimpleDialog",
                    "dojox/mobile/View",
                    "dojox/mobile/TextBox",
                    "dojox/mobile/RoundRect",
                    "dojox/mobile/Container",
                    "dojox/mobile/ContentPane",
                    "dojox/mobile/TabBar",
                    "dojox/mobile/TabBarButton",
                    "dojo/data/ItemFileReadStore",
                    "js/global.js"
         ], 
         function(){
        	 console.log("building Login page");
        	 
        	 internalBuildAuthPage(mainContext, mainId);
			 
                anyWidgetById(mainId).initChild();
    
                console.log("end build Login page");
         }
);
}

function internalBuildAuthPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var loginForm = mainId + "form";
    
    console.log("login page context " + context + " in : " + mainId + " " + loginForm);

    if (context) {
        context.initChild = function () {
		  console.log("init login page");
		  var iHtml = '<div id="' + loginForm + '"></div>';
		  
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
            console.log("resize login page: ");

        }
        var started = false;
        
        context.startChild = function () {
		console.log("start login page");
		if( !started ){
                    buildLoginPage(context,{id: loginForm});
                    started = true;
                }
                var cUser = localStorage ? localStorage.username : "";
                anyWidgetById(loginForm + "login").set("value",cUser);
        }

        context.stopChild = function () {
		console.log("stop login page");
		
        }

        context.destroyChild = function () {
		console.log("destroy login page");
	   }

        console.log("added lifecycle handlers to login page context");
    }
}

function buildLoginPage(context){
	var mainId = context.id;
        var profileManager = getCurrentContext().UIProfileManager;
        
        var connectorList = new Array();
	var registeredWidgetList = new Array();
        var loginInfo = {};
        
        var destroyChild = function () {
                //console.log("destroy login page");
        
                for(var i = 0;i < connectorList.length;i++)
                {
                    deregisterEventHandler(connectorList[i]);
                }
                
                for(var i = 0;i < registeredWidgetList.length;i++)
                {
                    deregisterDijitWidget(registeredWidgetList[i]);
                }
        }
        
        var startChild = function () {
		console.log("start login page");
                var cUser = loginInfo.login;
                if( !cUser ){
                    cUser = localStorage ? localStorage.username : "";
                }
                
                if( cUser ){
                    loginInfo.login = cUser;
                    var tObj = dojo.byId(mainId + "login");
                    tObj.innerHTML = "<input type='password'/>";
                    tObj = dojo.byId("login_label");
                    tObj.innerHTML = profileManager.getString("userPassword");
                    tObj = dojo.byId(mainId + "loginbutton");
                    tObj.innerHTML = profileManager.getString("login");
                }
                else {
                    var tObj = anyWidgetById(mainId + "login");
                    tObj.set("value","");
                    tObj.set("type","text");
                    tObj = anyWidgetById("login_label");
                    tObj.innerHTML = profileManager.getString("userName");
                    tObj = dojo.byId(mainId + "loginbutton");
                    tObj.innerHTML = profileManager.getString("next");
                }
        }
                
        var result = {destroyChild: destroyChild};

		var outerContainer = new dojox.mobile.Container({id: mainId},dojo.byId(mainId));
		registeredWidgetList.push(outerContainer.id);
                result.container = outerContainer;
                
    
		var logo = new dojox.mobile.ContentPane({id: "login_logo",baseClass: profileManager.getSetting("largeLogo")});
		registeredWidgetList.push(logo.id);
		outerContainer.addChild(logo);
		
		var formContainer = new dojox.mobile.RoundRect({id: "login_form"});
		registeredWidgetList.push(formContainer.id);
                
		outerContainer.addChild(formContainer);
	   
                var titleMessage = new dojox.mobile.ContentPane({id: "login_message",content: profileManager.getString("welcomeMessage")});
		registeredWidgetList.push(titleMessage.id);
                formContainer.addChild(titleMessage);
                
                
                var label = new dojox.mobile.ContentPane({id: "login_label",content: profileManager.getString("userName")});
		registeredWidgetList.push(label.id);
		formContainer.addChild(label);
                
                var userField = new dojox.mobile.TextBox(
                      {
                          id: mainId + "login",
                          name: mainId + "login",
                          value: ""
                      }
                  );
               registeredWidgetList.push(userField.id);   
          
                formContainer.addChild(userField);
                
                var controlContainer = new dojox.mobile.RoundRect({id: "loginapp-innercontrol"});
        registeredWidgetList.push(controlContainer.id);
        formContainer.addChild(controlContainer);
        

        var loginButton = new dojox.mobile.Button({
                label: "",
                id: mainId + "loginbutton",
                name: mainId + "loginbutton",
                innerHTML: profileManager.getString("login"),
                colspan: 1,
                showLabel: false,
                iconClass: "loginIcon",
                onClick: function(){
                    doLogin();
                }
        });
        
        registeredWidgetList.push(loginButton.id);
        controlContainer.addChild(loginButton); 
           
           var footerWrapper = new dojox.mobile.RoundRect({id: "login_footer_wrapper"});
	   registeredWidgetList.push(footerWrapper.id);
	   outerContainer.addChild(footerWrapper);
 
	   var footer = new dojox.mobile.ContentPane({id: "login_footer",content: "<a href=\"JavaScript:void(0)\" onClick=\"showHelpDialog(getCurrentContext().UIProfileManager.getHelp('login'));\">" + profileManager.getString("help") + "</a> | " + profileManager.getString("copyright")});
	   registeredWidgetList.push(footer.id);
	   footerWrapper.addChild(footer);
	   footerWrapper.startup();
           
           formContainer.startup();
	   controlContainer.startup();
	   outerContainer.startup();

        function doLogin() {
			  var queryFrame = {};
			   var user = loginInfo.login;
                           
			   var passwd = null;
			   
                           if( user && !passwd ){
                               passwd = anyWidgetById(mainId + "login").get("value");
                           }
                           else {
                               loginInfo.login = anyWidgetById(mainId + "login").get("value");
                               anyWidgetById(mainId + "login").set("value","");
                           }
           
			   if( user != null && user.length > 0 
						&& passwd != null && passwd.length > 0)
			  {
                                if( localStorage ){
                                    localStorage.username = user;
                                }
				   queryFrame.user = user;
				   queryFrame.password = passwd;
                                   
                                   var sm = mojo.data.SessionManager.getInstance(queryFrame);
                                   
                                   
                                   sm.createSession({query: queryFrame,callback: context.callback});
			  }
                          else {
                              startChild();
                          }
        }
        
        startChild();
        
        return( result );
}
