var zen = {};

zen.brains = false;

zen.replaceAll = function(str,find, replace) {
  return str ? str.replace(new RegExp(find, 'g'), replace) : "";
}
zen.speakMessage = function(message){
    try{
        if( window.speechSynthesis ){
            window.speechSynthesis.speak(
                new SpeechSynthesisUtterance(message)
            );
        }
        else if( meSpeak ){
            meSpeak.speak(message)
        }
    }
    catch(exp){
        console.log("no speech engine");
    }
}

zen.buildRegularExpression = function(args,slotDefinition){
    var result = slotDefinition.text;
    var slotDef = slotDefinition.condition.toLowerCase();
    
    if( slotDef == 'startswith' ){
        result = "^" + result;
    }
    else if( slotDef == 'endswith' ){
        result = result + "$";
    }
    else if( slotDef == 'contains' ){
        result = result;
    }
    
    return( result );
}

zen.extractKeyWords = function(args,input,grammar){
    var result = {keywords: "",useAsInput: true};
    var condition = grammar.condition.toLowerCase();
    
    if( condition == "startswith" ) {
        result.keywords = zen.replaceAll(input,grammar.text,"");
    }
    else if( condition == "endswith" ) {
        result.keywords = zen.replaceAll(input,grammar.text,"");
    }
    else if( condition == "contains" ) {
        var regularStr = zen.buildRegularExpression(args,grammar);          
        var regEx = new RegExp(regularStr,"i");
        var execResult = regEx.exec(input);
        
        if( execResult.length > 1 ){
            result.keywords = execResult[1];
        }
        else {
            result.keywords = zen.replaceAll(input.toLowerCase(),grammar.text,"");
        }
        /*
        var idx = grammar.text.indexOf(".*");
        
        var newGrammar = grammar.text;
        if( idx > -1 ){
            var startGrammar = idx > 0 ? newGrammar.substring(0,idx) : newGrammar.substring(idx+2);
            var endGrammar = newGrammar.substring(idx+2);
            
            result.keywords = zen.replaceAll(input,startGrammar,"");
            result.keywords = zen.replaceAll(result.keywords,endGrammar,"");
        }
        else {
            result.keywords = zen.replaceAll(input,newGrammar,"");
        }*/
        
    }
    else if( condition == "analytical" ) {
        var keyWordsInfo = {keywords: [zen.replaceAll(input,grammar.text,"")],confidence: 1};//anaylitical
        result.info = keyWordsInfo;
        result.keywords = keyWordsInfo.keywords;
        result.discarded = "";
        result.useAsInput = keyWordsInfo.confidence > 0;
    }
    else if( condition == "any" ) {
        result.keywords = zen.replaceAll(input,grammar.text,"");
    }
    
    return( result );
}

zen.replaceTokens = function(args,responseText){
    var result = responseText;
    
    if( result.indexOf("@TIME") > -1 ){
        result = zen.replaceAll(result,"@TIME","" + new Date());
    }
    
    if( result.indexOf("@DAY") > -1 ){
        result = zen.replaceAll(result,"@DAY",dojo.date.locale.format(new Date(),{datePattern: "EEEE",selector: "date"}));
    }
    
    if( result.indexOf("@SYSTEMNAME") > -1 ){
        result = zen.replaceAll(result,"@SYSTEMNAME","Benjamin");
    }
    
    if( result.indexOf("@KEYWORDS") > -1 ){
        result = zen.replaceAll(result,"@KEYWORDS",args.currentState.keywords ? args.currentState.keywords : args.keywords);
    }
    
    if( result.indexOf("@CREATORNAME") > -1 ){
        result = zen.replaceAll(result,"@CREATORNAME","God the Father");
    }
    
    if( result.indexOf("@ALIAS") > -1 ){
        result = zen.replaceAll(result,"@ALIAS",getCurrentContext().SessionManager.getAttribute("alias"));
    }
    
    for(;;){
        var sIdx = result.indexOf("@SLOT");
        
        if( typeof(sIdx) == 'number' && sIdx > -1 && args.currentState ){
            var fStr = result.substring(sIdx + "@SLOT".length + 1);
            sIdx = fStr.indexOf("'");
            if( sIdx < 0 ){
                sIdx = fStr.indexOf(" ");
            }
            var fieldName = sIdx > -1 ? fStr.substring(0,sIdx).trim() : fStr;
            //console.log(fieldName);
            result = zen.replaceAll(result,"@SLOT." + fieldName,args.currentState[fieldName]);
        }
        else {
            break;
        }
    }
    
    if( result.indexOf("@QUERY") > -1 ){
        //post a message to results view to do a query
        var idx = result.indexOf("@QUERY");
        var endIndex = result.indexOf(" ",idx+1);
        if( endIndex < 1 )
            endIndex = result.length;
        
        var query = result.substring(idx + "@QUERY:".length,endIndex);
        
        var data = {id: "zen",sourceId: "dialog",context: args,query: query};
        
        getCurrentContext().notifyDataChange(data);
        
        result = zen.replaceAll(result,"@QUERY:" + query,"searching for " + query);
    }
    
    if( result.indexOf("@ACTION") > -1 ){
        //post a message to results view to do a query
        var idx = result.indexOf("@ACTION");
        var endIndex = result.indexOf(" ",idx+1);
        if( endIndex < 1 )
            endIndex = result.length;
        
        var action = result.substring(idx + "@ACTION:".length,endIndex);
        
        var doLater = function(){
            console.log("action: " + action);
            var i = action.indexOf(":");
            
            var a = i > 0 ? action.substring(0,i) : action;
            
            if( i < action.length ){
                var p = action.substring(i+1);
                var pi = p.indexOf(":");
                
                if( pi > 0 ){
                    var viewName = p.substring(0,pi);
                    var t = p.substring(pi+1);
                    var target = {};
                    target[t] = args.currentState ? args.currentState[t] : "";
                    var doLater = function(){
                        anyWidgetById(viewName).setTarget(target);
                    }
                    getCurrentContext().setCurrentView(viewName,[doLater]);
                }
                else
                    getCurrentContext()[a](p);
            }
            else
                getCurrentContext()[a]();
        }
        setTimeout(doLater,2000);
        
        result = zen.replaceAll(result,"@ACTION:" + action,"launching...");
    }
    
    return( result );
}

zen.buildSlot = function(args,currentState){
    var result = {};
    var slotValue = false;
    if( currentState ){
        //add check to abort and help grammars on input to see if there is a match
        args.currentState = currentState;
        slotValue = args.currentState["_slotValue"];
        var grammarIndex = args.currentState["_grammarIndex"];
        //args.currentState.used = slotValue.grammar[grammarIndex];
        args.currentState.used = slotValue.grammars;
        
        var keyWordsInfo = {keywords: slotValue.keywords,useAsInput: true};//zen.extractKeyWords(args,args.input,slotValue.grammar[grammarIndex]);
        args.currentState.keyWordsInfo = keyWordsInfo;
        
        args.currentState.keywords = keyWordsInfo.keywords;
        args.currentState.action = slotValue.action;
        args.currentState.input = args.input;
    }
    else {
        var tCurrentState = args.currentState;
        result.keywords = args.input;
        result.action = tCurrentState.action;
        result.used = tCurrentState.used;
        var currentField = tCurrentState.currentField;

        args.currentState[currentField.name] = args.input;
        slotValue = args.currentState["_slotValue"];
       
    }
    
    var isValid = true;
    
    if( slotValue.detail && slotValue.detail.fields ){
        for(var i = 0;i < slotValue.detail.fields.length;i++){
            var currentField = slotValue.detail.fields[i];
            if( currentField.required && !args.currentState[currentField.name] && args.currentState.keywords ){
                if( args.currentState.keyWordsInfo.useAsInput ) {
                    args.currentState[currentField.name] = args.currentState.keywords;
                    args.currentState.keywords = "";
                }
            }
            if( currentField.required && !args.currentState[currentField.name] ){
                
                result.response = zen.replaceTokens(args,currentField.directive[zen.random(currentField.directive.length)]);
                result.currentField = currentField;
                
                isValid = false;
                break;
            }
        } 
    }

    if( isValid ) {
        //result.response = zen.replaceTokens(args,slotValue.response[zen.random(slotValue.response.length)]);
        if( slotValue.detail ) {
            result.response = zen.replaceTokens(args,slotValue.detail.responses[zen.random(slotValue.detail.responses.length)]);
        }
        else {
            result.response = getCurrentContext().UIProfileManager.getString("unknowInput");
        }
        result.isComplete = true;
        result.previousState = args.currentState;
    }
    else {
        args.currentState.currentField = result.currentField;
    }
    
    return( result );
}

zen.parse = function(args){
	var result = new Array();
        
        if( args.hasOwnProperty("currentState") ){
            var newResult = zen.buildSlot(args);
                            
            result.push(newResult);
            
            args.parseCallback(result);
        }
        else {
            zen.requestGrammarMatch(result,args);
        }
}

zen.requestGrammarMatch = function(result,args){
    var doFinally = function(data){
        var slotValue = data && data.items && data.items.length > 0 ? data.items[0] : false;
            
        if( !args.currentState ){
            var currentState = {frameType: slotValue.sentencetype,"_grammarIndex": 0,"_input": args.input, "_slotValue": slotValue };
            
            var newResult = zen.buildSlot(args,currentState);
            
            result.push(newResult);
        }
        else {
            var newResult = {};
            
            if( slotValue ){
                newResult.used = slotValue.grammars[0];
                 var keyWordInfo = {keywords: slotValue.keywords,useAsInput: true};
                newResult.keyWordInfo = keyWordInfo;
                
                newResult.keywords = slotValue.keywords;
                newResult.action = slotValue.action;
            }
            else {
                
            }
            
            result.push(newResult);
        }
        
        zen.processResult(result,args.parseCallback);
    }
    
    var theURL = getCurrentContext().UIProfileManager.getSetting("mojoBaseUrl") + "/restservice?contenttype=AGENT&query=" + replaceAll(args.input," ","%20");
		
    dojo.xhrGet(
     {
          url : theURL, 
          handleAs : "json", 
          headers : {
               "Content-Type" : "application/json"
          },
          preventCache: true,
          load: doFinally,
          error: doFinally
     });
}

zen.processResult = function(result,callback){
    if( result.length == 0 || (result[0].previousState && result[0].previousState.action == "query") ){
            var doFinally = function(data){
               if( data.items && data.items.length > 0 ){
                //result.push({"action": "GET","response": data.items[0].body});
                result[0].response = data.items[0].body;
                }
                else {
                    ///result.push({"action": "PUT","response": "NOIDEA"}); 
                    result[0].response = "NOIDEA";
                }
                
                callback(result);
            }
            
            var searchFor = result[0].previousState.input;
            
            var theURL = getCurrentContext().UIProfileManager.getSetting("mojoBaseUrl") + "/restservice?contenttype=ZEN&question=" + replaceAll(searchFor," ","%20");
		
		dojo.xhrGet(
                 {
                      url : theURL, 
                      handleAs : "json", 
                      headers : {
                           "Content-Type" : "application/json"
                      },
                      preventCache: true,
                      load: doFinally,
                      error: doFinally
                 });
            
             
        }
        else {
            result = result.sort(function(a,b){ return( a.score < b.score)});
            
             callback(result);
        }
}

zen.random = function(max){
	return( (Math.random() * max | 0) );	
}

zen.respond = function(args){
	var state = {"action": "PUT","response": "NOIDEA"};
	setBusy(true,getCurrentContext().UIProfileManager.getString("pleaseWait"));
        
        var parseCallback = function(data){
            if( args.callback ){
                    args.callback(data);
            }
            
            setBusy(false);
        }
        args.parseCallback = parseCallback;
        
	if( zen.brains ){
		zen.parse(args);
	}
	else {
		var doLater = function(data){
			
			zen.brains = data;
			
			zen.parse(args);
		}
		var theURL = getCurrentContext().UIProfileManager.getSetting("mojoBaseUrl") + "/conf/grammar.json";
		
		dojo.xhrGet(
                 {
                      url : theURL, 
                      handleAs : "json", 
                      headers : {
                           "Content-Type" : "application/json"
                      },
                      preventCache: true,
                      load: doLater,
                      error: doLater
                 });
	}	
}