function buildTrackerPage(mainContext, mainId,currentChild) {

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
                    "js/trackeredit.js"
         ], 
         function(){
        	 //console.log("building Content page");
        	 
        	 internalBuildTrackerPage(mainContext, mainId);
			 	 
                anyWidgetById(mainId).initChild();
                anyWidgetById(mainId).startChild();
                 
                getCurrentContext().setBusy(false);
        
         }
);
}

function internalBuildTrackerPage(mainContext, mainId) {

    var context = anyWidgetById(mainId);
    var mainForm = mainId + "form";
    var connectorList = new Array();
    var registeredWidgetList = new Array();
    var mapName = mainForm + "map";
    
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
            console.log("resize tracker page: ");
            setMapContainerSize();
        }
        function onListClick(){
            var item = this;
            console.log(item.boardtitle);
            getCurrentContext().setCurrentView("trackerview");
            
            var doLater = function(){
                anyWidgetById("trackerview").setTarget(item);
            }
            setTimeout(doLater,1000);
        }
        var map = false;
        var started = false;
        var currentPoints = false;
        context.startChild = function () {
		console.log("start tracker page");
                if( !started ){
                    started = true;
                    buildMainPage({id: mainForm});
                }
                else {
                    setMapContainerSize();
                }
                

                var pos = getCurrentContext().GeoLocation.position.coords;
                
                if( !map ){
                    map = buildGMap(mapName);
                }
                else {
                    
                    handleGMapCenter(map,pos.latitude,pos.longitude);
                }
                
                buildAutoMarker(map,pos);
                
                var doLater = function(response){
                    
                    if( currentPoints ){
                        for(var i = 0;i < currentPoints.length;i++){
                            if( currentPoints[i].marker ){
                                currentPoints[i].marker.setMap(null);
                            }
                        }
                    }
                    currentPoints = new Array();
                    
                    if( response.items ){
                        for(var i = 0;i < response.items.length;i++){
                            var item = response.items[i];
                            var newItem = {id: item.id,name: item.name,equipment: item.equipment,location: item.location,comments: item.comments,starttime: item.starttime,endtime: item.endtime};
                            if( newItem.location ){
                                var tloc = newItem.location.split(",");
                                if( tloc.length > 1 ){
                                    newItem.latitude = tloc[0];
                                    newItem.longitude = tloc[1];
                                    
                                }
                            }
                            
                            currentPoints.push(newItem);
                        }
                        
                        buildMapPoints(map,currentPoints);
                    }
                    
                }
                getCurrentContext().CacheManager.getData({contenttype:"TRACKER",nocache: true,callback: doLater});
                
        }

        context.stopChild = function () {
            console.log("stop board page");
		
        }
        
        context.openPoint = function (pointId) {
            console.log("open point");
            if( pointId ){
                if( currentPoints ){
                    for(var i = 0;i < currentPoints.length;i++){
                        if( currentPoints[i].id == pointId ){
                            
                             var doLater = function(){
                                anyWidgetById("trackeredit").setTarget(currentPoints[i]);
                             }
                             
                             getCurrentContext().setCurrentView("trackeredit",[doLater]);  
                         
                            break;
                        }
                    }
                }
            }
            else {
                doAction(); 
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
    var mapPadX = 0;
    var mapPadY = 0;
    function setMapContainerSize(){
        var cContext = getCurrentContext();
        var cMap = dojo.byId(mapName);
        dojo.style(cMap,"width",(cContext.screenWidth-mapPadX) + "px");     
        dojo.style(cMap,"height",(cContext.screenHeight-mapPadY) + "px");   
        
           mapHandleResize();         

    }
    
    function doAction() {
             getCurrentContext().setCurrentView("trackeredit");  
             
             var doLater = function(){

                if( getCurrentContext().GeoLocation && getCurrentContext().GeoLocation.position ){
                    var pos = getCurrentContext().GeoLocation.position.coords;
                    
                    anyWidgetById("trackeredit").setTarget({location: pos.latitude + "," + pos.longitude,latitude: pos.latitude,longitude: pos.longitude});
                }
             }
             
             setTimeout(doLater,1000);
    }

    function buildAutoMarker(map,pos){
        var cPoint =   new google.maps.LatLng(pos.latitude,pos.longitude);
        var rec = {name: getCurrentContext().SessionManager.getAttribute("user")};
        
        createGMapMarker(map,cPoint,rec,"current");
    }
    
    function buildMapPoints(map,items){
        for(var i = 0;i < items.length;i++){
            var pos = items[i];
            var cPoint =   new google.maps.LatLng(pos.latitude,pos.longitude);
            var rec = {id: pos.id,name: pos.name,equipment: pos.equipment,comments: pos.comments,location: pos.location};
            
             items[i].marker = createGMapMarker(map,cPoint,rec,"past");
        }
    }

    function buildMainPage(context){
            var mainId = context.id;
            var profileManager = getCurrentContext().UIProfileManager;
            
    
                    var outerContainer = new dojox.mobile.Container({id: mainId},dojo.byId(mainId));
                    registeredWidgetList.push(outerContainer.id);
                    
                    
                    //outerContainer.startup();
                        
                   // var formContainer = new dojox.mobile.RoundRect({id: mainId + "form"});
                    //registeredWidgetList.push(formContainer.id);
                    
                    //outerContainer.addChild(formContainer);
                    
                    /*var label = new dojox.mobile.ContentPane({id: mainId + "titlelabel",content: profileManager.getString("trackerTitle")});
                    registeredWidgetList.push(label.id);
                    formContainer.addChild(label);
                    
                                var controlContainer = new dojox.mobile.RoundRect({id: mainId + "innercontrol"});
            registeredWidgetList.push(controlContainer.id);
            formContainer.addChild(controlContainer);
            
    
            var newButton = new dojox.mobile.Button({
                    label: "",
                    name: mainId + "action",
                    innerHTML: profileManager.getString("newTrack"),
                    colspan: 1,
                    showLabel: false,
                    iconClass: "loginIcon",
                    onClick: function(){
                        doAction();
                    }
            });
            
            registeredWidgetList.push(newButton.id);
            controlContainer.addChild(newButton); */
                    
                    
                    
    var storeData = [
                    
                ];
                var newStore = new dojo.store.Memory({data:storeData, idProperty:"id",labelProperty: "tracktitle"});
                var cContext = getCurrentContext();
              
              var mapContainer = new dojox.mobile.ContentPane(
                          {
                              id: mainId + "mapcontainer",
                              name: mainId + "mapcontainer",
                              content: "<div id=\"" + mapName + "\" style=\"padding: 0px;width: " + (cContext.screenWidth-mapPadX) + "px;height:  " + (cContext.screenHeight-mapPadY) + "px;\"></div>"
                          }
                      );
                   registeredWidgetList.push(mapContainer.id);   
              
            outerContainer.addChild(mapContainer);
            mapContainer.startup();

        
            
            

            
               //formContainer.startup();
               //controlContainer.startup();
        
                outerContainer.startup();
    
            
            
            
            return( outerContainer );
    }
    function buildGMap(mapName){
          //console.log("build map with name " + mapName);
          var uiManager = getCurrentContext().UIProfileManager;
            
          var pos = getCurrentContext().GeoLocation ? getCurrentContext().GeoLocation.position.coords : {latitude: uiManager.getUserPreference("mapCenterLatitude"),longitude: uiManager.getUserPreference("mapCenterLongitude")};
                    
          var cPoint =   new google.maps.LatLng(pos.latitude,pos.longitude);
          
          var map = new google.maps.Map(dojo.byId(mapName),{
              zoom: uiManager.getUserPreference("mapZoomLevel"),
              mapTypeId: uiManager.getUserPreference("mapType"),
              xxmapTypeControlOptions: {style: google.maps.MapTypeControlStyle.DROPDOWN_MENU}
          });
          
          getCurrentContext().mapInfo = { map: map};
        
          
          map.setCenter(cPoint);
          
          return( map );
      }
      
      function mapHandleResize() {
           handleGMapResize(map);
      }
        
    function createGMapMarker(map,cPoint,rec,type){
    console.log("rec: " + rec);
      //var cPoint = new google.maps.LatLng(rec.latitude,rec.longitude);
      var result = _createGMapMarker(map,cPoint,rec,type);
    
      return( result );
    }
    
    function handleGMapResize(map){
      google.maps.event.trigger(map, 'resize');
    }
    
    function handleGMapCenter(map,lat,long){
      var doLater = function(){
          var cPoint =   new google.maps.LatLng(lat,long);
          map.setCenter(cPoint);
           
      }
      
      setTimeout( doLater, 200);
    }
    
    function _createGMapMarker(map,point, rec,type) {
        if( map ) {
            var uiManager = getCurrentContext().UIProfileManager;
            
            var html = "<div style=\"width: 120px;height: 80px;color: #000;\"><span>" + uiManager.getString("name") + ": " + rec.name + "</span><br/>";
            html += "<span>" + uiManager.getString("location") + ": " + rec.location + "</span><br/>";
            if( type == 'past' )
                html += "<span><a href=\"JavaScript:void(0)\" onClick=\"anyWidgetById('" + mainId + "').openPoint('" + rec.id + "');\">" + uiManager.getString("openTo") + "</a></span><br/>";
            else
                html += "<span><a href=\"JavaScript:void(0)\" onClick=\"anyWidgetById('" + mainId + "').openPoint('');\">" + uiManager.getString("newTrack") + "</a></span><br/>";
            html += "</div>";
            
            console.log("point: " + point);
            var shape = {
                              coords: [1, 1, 1, 20, 18, 20, 18 , 1],
                              type: 'poly'
                          };
            var image = {
                            url: uiManager.getSetting("mojoBaseUrl") + (type == 'past' ? "/images/icon_summary.png" : "/images/icon_map.png"),
                            // This marker is 20 pixels wide by 32 pixels tall.
                            size: new google.maps.Size(32, 32),
                            // The origin for this image is 0,0.
                            origin: new google.maps.Point(0,0),
                            // The anchor for this image is the base of the flagpole at 0,32.
                            anchor: new google.maps.Point(0, 32)
                          };
                          
            var markerOptions = {   position: point,
                                    title: rec.name,
                                    xxanimation: google.maps.Animation.DROP,
                                    xxicon: image,
                                    clickable: true
                                };
             
            var marker = new google.maps.Marker(markerOptions);
            
            var infowindow = new google.maps.InfoWindow({
                content: html
            });
            
            google.maps.event.addListener(infowindow, 'closeclick', function() {
                                                                            marker.isOpen = false;
                                                                        });

            
            google.maps.event.addListener(marker, 'click', function() {
                                                                            infowindow.open(map,marker);
                                                                            marker.isOpen = true;
                                                                        });
            
            marker.setMap(map);
            
            return( marker );
        }
        else
            return( false );
    }
}
