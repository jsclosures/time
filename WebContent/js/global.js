var API = {
	appContext : '/time', 
        messageContext : ':9990/time', 
	baseURL : '/time/', 
    imageURL : '/time/images/', 
    profileURL : 'conf/uiprofile.json',
    configurationPath: 'conf/'
};

function getAPI() {
    return (API);
}

var mojo = {
};

function loadScreenDimensions(padX, padY) {
    var windowDimension;
    
    if( dojo.isIE ){
        //windowDimension = getIEScreenSize();
        windowDimension = dojo.window.getBox();
        //windowDimension.h = windowDimension.h -10;
    }
    else {
        windowDimension = dojo.window.getBox();
    }
    
    if( dojo.isIE == 8 ){
        getCurrentContext().screenWidth = windowDimension.w - padX;
        getCurrentContext().screenHeight = windowDimension.h - padY;
        getCurrentContext().windowWidth = windowDimension.w;
        getCurrentContext().windowHeight = windowDimension.h;
    }
    else {
        getCurrentContext().screenWidth = windowDimension.w - padX;
        getCurrentContext().screenHeight = windowDimension.h - padY;
        getCurrentContext().windowWidth = windowDimension.w;
        getCurrentContext().windowHeight = windowDimension.h;
    }
}

function getIEScreenSize() {
    var winW = 630, winH = 460;
    if (document.body && document.body.offsetWidth) {
     winW = document.body.offsetWidth;
     winH = document.body.offsetHeight;
    }
    if (document.compatMode=='CSS1Compat' &&
        document.documentElement &&
        document.documentElement.offsetWidth ) {
     winW = document.documentElement.offsetWidth;
     winH = document.documentElement.offsetHeight;
    }
    if (window.innerWidth && window.innerHeight) {
     winW = window.innerWidth;
     winH = window.innerHeight;
    }

    return( {w: winW, h: winH});
}

function getDefaultRoundedTime() {
 var result = new Date().getTime();
 
 var minutes = Math.floor(result/60000);
 
 var remaining = minutes%60;
 
 return( minutes - remaining );
}

function getDefaultTime() {
 return( Math.floor(new Date().getTime()/60000) );
}

function setDocumentTheme(theme){
    if( theme ){
        document.body.className = theme;
    }
}

var audioContainer = false;

function playSystemSound(soundFileName) {
  try{
    if ( !audioContainer ) {
        var newDialog = document.createElement('span');
        newDialog.setAttribute('id', 'audioContainer');
        newDialog.style.visibility = 'hidden';
        newDialog.style.display = 'none';
        document.body.appendChild(newDialog);
        
        audioContainer = newDialog;
    }
    var soundContent = "<embed src='/queryui/images/sounds/beep.wav' autostart='true' height='0' id='systemsoundbeep' enablejavascript='true' hidden='true'>";
    
    //dojo.attr(audioContainer,"innerHTML",soundContent);
    dojo.byId("audioContainer").innerHTML = soundContent;
  }
  catch(exp){
      //console.log("sound file exception: " + soundFileName);
  }
}

function collapseFrame(orderList, frameData) {
    var result = '';
    var t;
    var counter = 0;
    var olArray = orderList.split(",");

    if (orderList.length > 0 && olArray.length > 0) {

        var p;
        for (var i = 0;i < olArray.length;i++) {
            p = olArray[i];

            if (frameData.hasOwnProperty(p)) {
                t = frameData[p];

                if (counter > 0)
                    result = result + ';';

                result = result + p + ':' + t;

                counter++;
            }

        }
    }

    for (var p in frameData) {
        if (frameData.hasOwnProperty(p) && olArray.indexOf(p) < 0) {
            t = frameData[p];

            if (counter > 0)
                result = result + ';';

            result = result + p + ':' + t;

            counter++;
        }

    }

    return result;
}

function globalResizeWidget(args){  
    //console.log("resize widget id: " + args.widget.id + " resizing: " + args.resizing);
    if( dojo.isIE && getCurrentContext().playerContainerName ) {
        var tObj = dijit.byId(getCurrentContext().playerContainerName);
        
        if( tObj ){
            /*var childrenList = tObj.getChildren();
            
            for(var i = 0;i < childrenList.length;i++){
                if( args.resizing ){
                    tObj._hideChild(childrenList[i]);
                }
                else {
                    tObj._showChild(childrenList[i]);
                }
            }*/
            
            if( args.resizing ){
                dojo.style(tObj.domNode,"visibility","hidden");
            }
            else {
                dojo.style(tObj.domNode,"visibility","visible");
            }
        }
    }
}
function setCurrentView(viewName,doLaters){
    getCurrentContext().nextActions = doLaters;
    dojo.byId(getCurrentContext().mainContainerName).selectTab(viewName);
}

function getCurrentView(){
    return( dojo.byId(getCurrentContext().mainContainerName).getSelectedTabId() );
}

function purgeNextActions(){
    var doLater = function(){
        if( getCurrentContext().nextActions && getCurrentContext().nextActions.length > 0 ){
            var doLaters = getCurrentContext().nextActions;
            getCurrentContext().nextActions = false;
            
            for(var i = 0;i < doLaters.length;i++){
                try {
                    doLaters[i]();
                }
                catch(exp){
                    console.log("exp: " + exp);
                }
            }
       } 
    }
    
    setTimeout(doLater,1000);
}

var CURRENTCONTEXT = {
    resizeWidget: globalResizeWidget,
    setCurrentView: setCurrentView,
    getCurrentView: getCurrentView,
    purgeNextActions: purgeNextActions,
    rtmenabled: true
};

function setCurrentContext(ctx) {
    CURRENTCONTEXT = ctx;
}

function getCurrentContext() {
    return (CURRENTCONTEXT);
}

function resetContext() {
    CURRENTCONTEXT = {
        resizeWidget: globalResizeWidget,
        setCurrentView: setCurrentView,
        getCurrentView: getCurrentView,
        purgeNextActions: purgeNextActions,
        rtmenabled: true
    };
}

var AUTHPARAMS = {
    target : getAPI().authURL, parameters : []
};

var busyDialog;
var runningTransport = false;
var isBusy = false;

function setBusy(mode, message) {
    /*if( mode )
	{
          if( busyDialog == null )
          {
               busyDialog = new dojox.widget.Dialog({
                         xtitle:"Loading...",
                         dimensions: [300,200],
                         draggable:true,
                         closable: false,
                         modal: true,
                         easing: dojo.fx.easing.elasticOut
                    },"busydialog");
          }
          
          busyDialog.setContent(message + '...');
          runningTransport = true;
          
          setTimeout(showDialog,500);
	}
	else
	{
          runningTransport = false;
		busyDialog.hide();
	}*/
    var cContext = getCurrentContext();
    var statusContainer = cContext.statusContainerName ? anyWidgetById(cContext.statusContainerName) : false;

    
    if (mode) {
        if (busyDialog == null) {
            busyDialog = document.createElement('div');
            busyDialog.setAttribute('id', 'busydialog');
            busyDialog.style.visibility = 'hidden';
            busyDialog.style.display = 'none';
            document.body.appendChild(busyDialog);
        }
        var tContent = '<table width="100%" height="100%" cellspacing=0 cellpadding=0><tr>';
        tContent += '<td width="100%" align="center" style="background: #C8C8C8;color: #000;">' + message;
        tContent += '...</td></tr>';
        tContent += '<td width="100%" align="center" style="background: #C8C8C8;"><img src=\"images/progress.gif\" width=180 height=20></td></tr>';
        
        tContent += '</table>';
        
        busyDialog.innerHTML = tContent;
        runningTransport = true;

        setTimeout(showDialog, 500);
        
        if( statusContainer && statusContainer.lifecycle && statusContainer.lifecycle.setData ){
            statusContainer.lifecycle.setData({message: message});
        }
    }
    else {
        runningTransport = false;
        busyDialog.style.visibility = 'hidden';
        busyDialog.style.display = 'none';
        if( statusContainer && statusContainer.lifecycle && statusContainer.lifecycle.setData ){
            statusContainer.lifecycle.setData({message: ""});
        }
    }

    isBusy = mode;
}

function setImmediateBusy(mode, message) {
    if (mode) {
        if (busyDialog == null) {
            busyDialog = document.createElement('div');
            busyDialog.setAttribute('id', 'busydialog');
            busyDialog.style.visibility = 'hidden';
            busyDialog.style.display = 'none';
            document.body.appendChild(busyDialog);
        }

        busyDialog.innerHTML = '<table width="100%" height="100%"><tr><td width="100%" height="100%" align="center">' + message + '...' + '</td></tr></table>';
        runningTransport = true;
        //console.log("show dialog now");
        showDialog();
    }
    else {
        runningTransport = false;
        busyDialog.style.visibility = 'hidden';
        busyDialog.style.display = 'none';

    }

    isBusy = mode;

    return (isBusy);
}

function showDialog() {
    if (runningTransport) {
        //busyDialog.show();
        var top = (getCurrentContext().screenHeight - 100) / 2;
        var left = (getCurrentContext().screenWidth - 200) / 2;

        busyDialog.style.width = '200px';
        busyDialog.style.height = '100px';
        busyDialog.style.left = left + 'px';
        busyDialog.style.top = top + 'px';
        busyDialog.style.background = 'gray';
        busyDialog.style.border = '3px outset gray';
        busyDialog.style.position = 'absolute';
        busyDialog.style.visibility = 'visible';
        busyDialog.style.display = 'inline';
        busyDialog.style.color = 'white';
        busyDialog.style.zindex = '100';
    }
}

var helpDialog;

function showHelpDialog(message) {
    if (helpDialog == null) {
        
        var tDiv = document.createElement('div');
            tDiv.setAttribute('id', 'helpdialog');
            //tDiv.style.visibility = 'hidden';
            //tDiv.style.display = 'none';
            document.body.appendChild(tDiv);
        
        helpDialog = new dojox.mobile.SimpleDialog( {
            title : getCurrentContext().UIProfileManager.getString("help"),
            closeButton: true,
            modal: true
        },tDiv);
        //dojo.body().appendChild(helpDialog.domNode);
        
        var msgBox = dojo.create("div",
                                     {id: helpDialog.id + "inner",
                                     "class": "mblSimpleDialogText",
                                      innerHTML: "Processing..."},
                                      helpDialog.domNode);
        
    var cancelBtn = new dojox.mobile.Button({class: "mblSimpleDialogButton mblRedButton",
                                innerHTML: "Cancel"});
    cancelBtn.connect(cancelBtn.domNode, "click",
                      function(e){hideHelpDialog();});
    cancelBtn.placeAt(helpDialog.domNode);
    
        helpDialog.startup();
    }

    var doLater = function(){
        dojo.byId(helpDialog.id + "inner").innerHTML = message;
    
        helpDialog.show();
    }
    
    setTimeout(doLater,100);
}

function hideHelpDialog() {
    if (helpDialog)
        helpDialog.hide();
}


var modalDialog;

function showModalDialog(title,message,callback,args) {
    if (modalDialog == null) {
        var newDialog = document.createElement('div');
        newDialog.setAttribute('id', 'modaldialog');
        newDialog.style.visibility = 'hidden';
        newDialog.style.display = 'none';
        document.body.appendChild(newDialog);
        
        modalDialog = new dojox.widget.Dialog( {
            title : title, baseClass: "modaldialog",draggable : true, dimensions : [200, 100], easing : dojo.fx.easing.elasticOut, overlay :  {
                backgroundColor : 'gray', opacity : .9
            },
            buttons :  {
                'Close' : function () {
                    $(this).dialog('destroy');
                    if( callback )
                        callback(false);
                }
            }
        },
        "modaldialog");

        modalDialog.startup();
        
        //dojo.style(modalDialog.domNode,"background","white");
        
        //$(".ui-dialog-titlebar-close").hide();
    }
    
    modalDialog.lifecycle= {callback: function(mode) { dijit.byId("modaldialog").hide(); callback(mode,args); } };
    
    var tContent = "<table class=modaldialogcontainer>";
    
    tContent += "<tr><td colspan=2>" + message + "</td></tr>";
    var uiManager = getCurrentContext().UIProfileManager;
    
    tContent += "<tr><td align=right><a href='JavaScript:void(0)' class=modaldialogok onclick='getModalDialog().lifecycle.callback(true);'>" + uiManager.getString("ok") + "</a></td>";
    tContent += "<td align=left><a href='JavaScript:void(0)' class=modaldialogcancel onclick='getModalDialog().lifecycle.callback(false);'>" + uiManager.getString("cancel") + "</a></td></tr>";
    
    tContent += "</table>";
    
    modalDialog.setContent(tContent);

    modalDialog.show();
}

function getModalDialog() {
    return( modalDialog );
}

function hideModalDialog() {
    if (modalDialog)
        modalDialog.hide();
}

function getConfirm(title,message){
    
    var callback = function(mode){
    
    }

    showModalDialog(title,message,callback);
    
    
    return( result );
}

var toolTipDialog;

function showToolTipDialog(title,message,callback,args,position) {
    if (toolTipDialog == null) {
        var newDialog = document.createElement('div');
        newDialog.setAttribute('id', 'tooltipdialog');
        newDialog.style.visibility = 'hidden';
        newDialog.style.display = 'none';
        
        document.body.appendChild(newDialog);
        
        newDialog.onclick = function(evt){ 
                                            dojo.byId('tooltipdialog').lifecycle.callback(true);
                                    }
                                    
        newDialog.onmouseout = function(evt){ 
                                            dojo.byId('tooltipdialog').lifecycle.callback(false);
                                    }
        
        /*toolTipDialog = new dojox.widget.Dialog( {
            title : title, 
            baseClass: "tooltipdialog",
            draggable : true,
            dimensions : [200, 200],
            easing : dojo.fx.easing.elasticOut, 
            xoverlay :  {backgroundColor : 'gray', opacity : .9}
        },
        "tooltipdialog");*/
        
        toolTipDialog = dojo.byId("tooltipdialog");
    }
    
    toolTipDialog.lifecycle= {callback: function(mode) { if( mode ) hideToolTipDialog(); callback(mode,args); } };
    
    var uiManager = getCurrentContext().UIProfileManager;
    
    var tContent = "<table class=tooltipcontainer>";
    
    // tContent += "<tr><td align=left><a href='JavaScript:void(0)' onclick='dojo.byId('toolTipDialog').lifecycle.callback(false);'><img src='images/dialogCloseButton.png'></a></td></tr>";
    
    tContent += "<tr><td colspan=1>" + message + "<\/td><\/tr>";
    
    tContent += "<\/table>";
    
    toolTipDialog.innerHTML = tContent;

    if( position.width && position.height )
        dojo.style(dojo.byId("tooltipdialog"),{display: "block",color: "white",background: "gray",left: position.x + "px",top: position.y + "px",width: position.width + "px",height: position.height + "px",visibility: "visible",position: "absolute","z-index": 100});
    else
        dojo.style(dojo.byId("tooltipdialog"),{display: "block",color: "white",background: "gray",width: "174px",height: "140px",left: position.x + "px",top: position.y + "px",visibility: "visible",position: "absolute","z-index": 100});
}

function getToolTipDialog() {
    return( toolTipDialog );
}

function hideToolTipDialog() {
    if (toolTipDialog)
        dojo.style(dojo.byId("tooltipdialog"),"display","none");
}

function handleToolTipEvent(which,evt,title,message,dataArgs,parentName,width,height,popUpCallback){
    
    if( which ){
        var parentPosition = dojo.position(dojo.byId(parentName));
        
        var position = {x: evt.pageX ? evt.pageX : evt.clientX,y: evt.pageY ? evt.pageY : evt.clientY,width: width,height: height};
        
        if( position.x + width > parentPosition.x + parentPosition.w )
            position.x = parentPosition.x + parentPosition.w - width;
        
        position.y = position.y - 40;
        
        var uiManager = getCurrentContext().UIProfileManager;
        
        showToolTipDialog(title,message,popUpCallback,dataArgs,position);
    }
    else {
        hideToolTipDialog();
    }
}

if (false && !document.console) {
    document.console = {
        log : function (message) {
        }
    };
    console = document.console;
}

function mergeObjects(a,b){
  return( dojo.mixin(a,b) );
}
function indexOf(aList, fieldName, matchValue) {
    var result =  - 1;

    if (aList) {
        var matchStr = typeof matchValue != 'string' ? "" + matchValue : matchValue;
        for (var i = 0;i < aList.length;i++) {
            if ( matchStr == aList[i][fieldName] ) {
                result = i;
                break;
            }
        }
    }
    return (result);
}
function indexOfString(aList, matchValue) {
    var result =  - 1;

    if (aList) {
        var matchStr = typeof matchValue != 'string' ? "" + matchValue : matchValue;
        for (var i = 0;i < aList.length;i++) {
            if ( matchStr == aList[i] ) {
                result = i;
                break;
            }
        }
    }
    return (result);
}

function indexInString(str,find) {
  return new RegExp(find, 'g').test(str);
}

function replaceAll(str,find, replace) {
  return str ? str.replace(new RegExp(find, 'g'), replace) : "";
}

function openSimpleWindow(windowURL, windowName, width, height) {
    var openwindow = open(windowURL, windowName, 'menubars=1,resizable=1,scrollbars=1,toolbar=1,width=' + width + ',height=' + height);
    openwindow.focus();
    return openwindow;
}


var preferenceDialog;

function showPreferenceDialog(title,message,callback,args) {
    if (preferenceDialog == null) {
        var newDialog = document.createElement('div');
        newDialog.setAttribute('id', 'preferenceDialog');
        newDialog.style.visibility = 'hidden';
        newDialog.style.display = 'none';
        document.body.appendChild(newDialog);
        
        preferenceDialog = new dojox.widget.Dialog( {
            title : title, 
            draggable : true, 
            dimensions : [420, 580], 
            easing : dojo.fx.easing.elasticOut, 
            overlay :  { opacity : 1.0 }
        },
        "preferenceDialog");
        preferenceDialog.startup();

        var tContent = "<div id=modaldialogpreferences class='preferenceDialog'></div>";

        
        preferenceDialog.setContent(tContent);
        
        var mainContext = {
        };
    
        mainContext.paddingX = globalAppPadX;
        mainContext.paddingY = globalAppPadY;
        mainContext.useDojo = false;
        mainContext.tabHeight = 0;
        mainContext.footerHeight = 20;
    
        var cContext = getCurrentContext();
    
        mainContext.style = 'height: ' + cContext.screenHeight + 'px; width: ' + cContext.screenWidth + 'px;';
        mainContext.height = cContext.screenHeight;
        mainContext.width = cContext.screenWidth;
        mainContext.tabWidth = cContext.screenWidth;
        mainContext.footerWidth = cContext.screenWidth;
        
        buildSettingsPage(mainContext, "modaldialogpreferences",{});
    }
    else {
        var fObj = anyWidgetById("modaldialogpreferences");
        //console.log("main obj " + fObj);
        fObj.loadTarget( getCurrentContext().UIProfileManager.getUserPreferences());
    }
    
    preferenceDialog.lifecycle= {callback: function(mode) { getPreferenceDialog().hide(); callback(mode,args); } };
    
    

    preferenceDialog.show();
}

function getPreferenceDialog() {
    return( preferenceDialog );
}

function hidePreferenceDialog() {
    if (preferenceDialog)
        preferenceDialog.hide();
}



function connectToGeoLocation(){
    if( navigator.geolocation ){
        getCurrentContext().GeoLocation = {enabled: true,
                                        successCallback: function(data){
                                            //console.log("gps data callback: " + data);
                                            getCurrentContext().GeoLocation.position = data;
                                            doGeoLocation(getCurrentContext().UIProfileManager.getSetting("mapGeoLocation").successDelay);
                                        },
                                        failureCallback: function(data){
                                            //console.log("gps failure callback: " + data);
                                            doGeoLocation(getCurrentContext().UIProfileManager.getSetting("mapGeoLocation").failureDelay);
                                        }
                                        };
                                        
        doGeoLocation(getCurrentContext().UIProfileManager.getSetting("mapGeoLocation").startDelay);
    }
}

function doGeoLocation(delay){
    if( navigator.geolocation ){
        var doLater = function(){
            var options = {maximumAge:getCurrentContext().UIProfileManager.getSetting("mapGeoLocation").maximumAge,timeout: getCurrentContext().UIProfileManager.getSetting("mapGeoLocation").timeout};
            
            navigator.geolocation.getCurrentPosition(getCurrentContext().GeoLocation.successCallback,
                                                     getCurrentContext().GeoLocation.failureCallback,
                                                     options);
        }
        
        setTimeout(doLater,delay);
    }
}

//real time messaging
function publishToRTM(message){
    //console.log("sending to server");
    
    var cometd = getCurrentContext().cometd;
    cometd.publish('/zen/chat',{
        "text": message,
        "user": getCurrentContext().alias,
        "channel": "/zen/chat"
    });
}
function connectToRTM(){
    try{
        _connectToRTM();
    }
    catch(exp){
        console.log("rtm error: " + exp);
    }
}

function _connectToRTM(){

    if( getCurrentContext().rtmenabled ){
        var cometd = getCurrentContext().cometd;
        
        var origin = '';
        
        if( window.location.origin && window.location.origin.indexOf("ws:") == -1 )
            origin = window.location.origin;
        else {
            origin = window.location.protocol + "//" + window.location.host;
        }
        
        cometd.configure({
            url: origin + getAPI().messageContext + '/cometd',
            logLevel: 'info'
        });
        
        //cometd.init("http://neoc.vh.vzwnet.com/lte/cometd");
        
        var cometdListener = cometd.addListener('/meta/handshake', function(message)
        {
            if (message.successful)
            {
                //console.log("success handshake");
               
                if( !getCurrentContext().RTMIsConnected ){
                    
                    
                    var cometdSubscription = cometd.subscribe('/zen/app', function(subMsg) {
                        //console.log("/zen/app subscription callback");
                        getCurrentContext().lastMessage = subMsg;
                        if(subMsg){
                            notifyMessageInbound(subMsg);
                        } 
                        else {
                            console.log("Response contained no message");
                        }
                     
                        return( true );
                    });
                    
                    getCurrentContext().RTMIsConnected = true;
                }
            }
            else
            {
                //dom.byId('status').innerHTML += '<div>CometD handshake failed</div>';
                //console.log("failed handshake");
                getCurrentContext().RTMIsConnected = false;

            }
        });
        //need to tear down listern
        //cometd.removeListener(cometdListener);
        //cometd.unsubscribe(cometdSubscription);
        
        getCurrentContext().cometdListener = cometdListener;
    
    
        cometd.handshake();
    }
}

function connectToPrivateChannel(channelName,callback){
    if( getCurrentContext().rtmenabled && getCurrentContext().RTMIsConnected  ){
        var cometdSubscription = getCurrentContext().cometd.subscribe('/zen/app/' + channelName,callback );
        if( !getCurrentContext().RTMPrivateChannelList ){
            getCurrentContext().RTMPrivateChannelList = {};
        }
        if( !getCurrentContext().RTMPrivateChannelList.hasOwnProperty(channelName) )
            getCurrentContext().RTMPrivateChannelList[channelName] = cometdSubscription;
    }
}

function disconnectFromPrivateChannel(channelName){
    getCurrentContext().cometd.unsubscribe(getCurrentContext().RTMPrivateChannelList[channelName]);
    delete getCurrentContext().RTMPrivateChannelList[channelName];
}

function publishToPrivateRTM(channelName,message){
    //console.log("sending to server");
    
    var cometd = getCurrentContext().cometd;
    cometd.publish('/zen/app/' + channelName,{
        "text": message,
        "user": getCurrentContext().alias,
        "channel": '/zen/app/' + channelName
    });
}


function connectToChannelSimple(args){
    var result = false;
    if( getCurrentContext().rtmenabled ){
        var cometd = getCurrentContext().cometd;
        
        result = cometd.subscribe(args.channel, function(subMsg) {
            //console.log(args.channel + " subscription callback");
        
            if(subMsg && !subMsg.data.membership){
                //console.log("JSON: "+dojo.toJson(subMsg)); 
                var message = subMsg.data ? subMsg.data.chat : subMsg.text;

                respondeToMessage("MESSAGE",message);
                
                
            }
            else {
                //console.log("Response contained no message");
            }

            /**@todo: Single Instance really needs to mean single session.
              * We need to alter the messaging framework so that the application broacasting
              * messages can tell the UI when it's done broadcasting.
              * At that point, the UI will unsubscribe.  */
            //cometd.unsubscribe(result);
             
            return( true );
        });
    }
  
    return( result );
}


function respondeToMessage(messageType,message){
    if( messageType && messageType == "RELOAD_DATA" ){

          //console.log("reset navigation in response to message change");
                    
    }
    else {
        if( message ){
        	//console.log("simple message " + messageType + " " + message);
        }
    }
}

function registerRTMessageListener(listener){
    var uiManager = getCurrentContext().UIProfileManager;
    var listenerCache = uiManager.getCachedObject("messageListener");
    
    if( !listenerCache ){
      listenerCache = new Array();
      uiManager.cacheObject("messageListener",listenerCache);
    }
    
    listenerCache.push(listener);
}


function notifyMessageInbound(msg){
//console.log("message inbound " + msg);
    var uiManager = getCurrentContext().UIProfileManager;
    var listenerCache = uiManager.getCachedObject("messageListener");
    var alias = getCurrentContext().SessionManager.getAttribute("alias");
    
    if( msg.data && msg.data.alias == alias ){
        if( listenerCache ){
          for(var i = 0;i < listenerCache.length;i++){
            var listener = anyWidgetById(listenerCache[i].id);
            
            if( listener && listener.lifecycle && listener.lifecycle.messageNotify ){
              listener.lifecycle.messageNotify(msg);
            }
          }
        }
    }
}

var objectDialog;
var objectDialogConnect;

function showObjectDialog(title,message,callback,args) {
    var pageId = 'objectDialog';
        
    if ( !objectDialog ) {
        var newDialog = document.createElement('div');
        newDialog.setAttribute('id', pageId);
        newDialog.style.visibility = 'hidden';
        newDialog.style.display = 'none';
        document.body.appendChild(newDialog);
        
        objectDialog = new dojox.widget.DialogSimple( {
            title : title,
            closeButton: true,
            modal: true,
            executeScripts: true
        },pageId);
        objectDialog.startup();
        
        /*objectDialog = new dojox.mobile.SimpleDialog( {
            title : title,
            closeButton: true,
            modal: true,
            executeScripts: true
        },newDialog);
        
        var msgBox = dojo.create("div",
                                     {id: objectDialog.id + "inner",
                                     "class": "mblSimpleDialogText",
                                      innerHTML: "Processing..."},
                                      objectDialog.domNode);*/
    }

     
    if( objectDialogConnect ){
        deregisterEventHandler(objectDialogConnect);
    }
    
    anyWidgetById(pageId).set("href",args.href);
    
    
    var cb = function(cArgs){
        getObjectDialog().hide(); 
        try {
            callback(true,args);
        }
        catch(exp) {
            console.log(exp);
        }
    }
    objectDialogConnect = registerEventHandler(anyWidgetById(pageId),"onDownloadEnd",cb);

    objectDialog.show();
}

function getObjectDialog() {
    return( objectDialog );
}

function hideObjectDialog() {
    if (objectDialog)
        objectDialog.hide();
}

getCurrentContext().registerDataChangeListener = function(listener){
    var listenerCache = getCurrentContext().listenerCache;
    
    if( !listenerCache ){
      listenerCache = new Array();
      getCurrentContext().listenerCache = listenerCache;
    }
    
    listenerCache.push(listener);
}

getCurrentContext().notifyDataChange = function(changedData){
   var listenerCache = getCurrentContext().listenerCache;
    
    if( listenerCache ){
      for(var i = 0;i < listenerCache.length;i++){
        var listener = listenerCache[i];
        
        if( listener && listener.lifecycle && listener.lifecycle.dataChange && changedData.sourceId != listener.id ){
          listener.lifecycle.dataChange(changedData);
        }
      }
    }
}

