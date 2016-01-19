package com.jsclosures.handlers;

import com.jsclosures.Configuration;
import com.jsclosures.DataBean;
import com.jsclosures.DoThread;
import com.jsclosures.DoThreadListener;
import com.jsclosures.Helper;
import com.jsclosures.MessageClient;
import com.jsclosures.RestService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *


 */
public class MessageHandler implements DoThreadListener,RestService,MessageClient.MessageHandler  {
    public static String SECRETCODE = "esbrules";
    public static String START = "start";
    public static String SHUTDOWN = "shutdown";
    public static String RESTART = "restart";
    public static String STATUS = "status";
    public static String INIT = "init";
    public static String ACTION = "action";
    
    private Configuration configuration = new Configuration();
    public Configuration getConfiguration(){
        return( configuration );
    }
    
    public String getConfiguration(String which){
        return( getConfiguration().getString(which) );
    }
    
    public void writeLog(int level, String message) {
        if (level <= getConfiguration().getInt("maximumloglevel") ) {
            Helper.writeLog(1,message);
        }
    }
    
    public void setConfiguration(String name,String value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(String name,int value){
        configuration.setValue(name,value);
    }
    
    public void setConfiguration(Configuration conf){
        this.configuration = conf;
    }
    
    private DataBean parseMessage(String json){
        DataBean result = Helper.parseJSON(json);

        return( result );
    }

    private String readFromInput(BufferedReader input){
        StringBuilder result = new StringBuilder();
        
        try {
            int buffer;
            
            while( (buffer = input.read()) != -1 ){
                result.append((char)buffer);
            }
        }
        catch(Exception e){
            result.append(e.toString());
        }
        
        return( result.toString() );
    }
    

    public void server(int port) throws BindException, IOException
    {

      ServerSocket serv = new ServerSocket( port );
      boolean isExit = false;

      while (true) {
              
            Socket sock = serv.accept();
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
          
            String jsonInput = readFromInput(r);
            
            PrintWriter w = new PrintWriter(sock.getOutputStream(), false); // no autoFlush
            Helper.writeLog(1,"Server reading input");
            Helper.writeLog(1,"Server input: " + jsonInput);
          
            DataBean tCmd = parseMessage(jsonInput);
            String istr = "";
          
            if( tCmd.isValid("command") && tCmd.getString("secret").equals(SECRETCODE) )
                istr = tCmd.getString("command");
            
            if (istr.equals(SHUTDOWN)) {
                Helper.writeLog(1,"received: " + SHUTDOWN);
                w.println("server is shutting down.  Please wait.  This may take several minutes.  See the log for details ...\n");
                w.flush();
                processShutdown();
                isExit = true;
                w.println("application shut down...\n");
            } else if (istr.equals(RESTART)) {
                Helper.writeLog(1,"received: " + RESTART);
                w.println("Application is restarting...\n");
                processRestart();
            } else if (istr.equals(INIT)) {
                Helper.writeLog(1,"received: " + RESTART);
                w.println("Application is initializing...\n");
                processStart();
            } else if(istr.equals(STATUS)) {
                Helper.writeLog(1,"received: " + STATUS);
                w.println( "OK");
            }  else {
                Helper.writeLog(1,"received: " + tCmd.getString("command"));
                w.println("Application is processing " + tCmd.toString() + "\n");
                processAction(tCmd);
            } 

            w.flush();
            r.close();
            w.close();
            //sock.close();
            
            if(isExit)
                break;
        }

      serv.close();
      System.exit(0);
    }
    
    public void client(String host,int port,String cmd) throws IOException
    {
       Helper.writeLog(1,"openning client socket host: " + host + " port: " + port + " cmd: " + cmd);
       
       Socket sock = new Socket(host, port);
       PrintWriter w = new PrintWriter(sock.getOutputStream(), true); // autoFlush
       BufferedReader r = new BufferedReader(new InputStreamReader(sock.getInputStream()));
       DataBean cRec = new DataBean();
       cRec.copyFrom(getConfiguration());
       
       cRec.setValue("command",cmd);
       cRec.setValue("secret",SECRETCODE);
       String json = Helper.toJson(cRec);
       Helper.writeLog(1,"json: " + json);
       w.println(json);
       w.close();
        
        while (true)
        {
            String clientCommand = r.readLine();

            if (clientCommand != null)
                Helper.writeLog(1,clientCommand);
            else
                break;
        }

        r.close();
        //sock.close();
    }

    
    public void processArgs(String[] args) {
        setConfiguration("maximumloglevel",2);
        setConfiguration("command",START);
        setConfiguration("host","localhost");
        setConfiguration("port","8777");
        setConfiguration("messageserver","http://localhost/zen/cometd");
        setConfiguration("includemessages","true");
        

        for (int i = 0; i < args.length; i++) {
            int idx = args[i].indexOf("=");
            
            if( idx > -1 ){
                String name = args[i].substring(0,idx);
                String value = args[i].substring(idx+1);
                setConfiguration(name,value);
            }
        }

        writeLog(1,getConfiguration().toString());
    }
    public void handleMessage(DataBean data){
        Helper.writeLog(1,"handle Message " + data);
        DoThread worker = getNextWorker();
        worker.setTarget(data);
        worker.setRunning(true);
    }
    /**
     *   BatchProcess start localhost 8888
     * @param args
     */
    public static void main(String[] args)
    {
        MessageHandler runner = new MessageHandler();
        runner.processArgs(args);
        
        String command = runner.getConfiguration("command");
        String host = runner.getConfiguration("host");
        int port = runner.getConfiguration().getInt("port");
        
        boolean isStart = false;
        boolean isRestart = false;
        boolean isStatus = false;
        
        if ( true ) {
            if (command.equalsIgnoreCase("shutdown")){
                command = SHUTDOWN;
            }
            else if(command.equalsIgnoreCase("status")) {
                command = STATUS;
                isStatus = true;
            } else if(command.equalsIgnoreCase("restart")) {
                command = RESTART;
            }
            else if( command.equalsIgnoreCase("start") ){
                command = START;
                isStart = true;
            }
        }
        
        if( port > -1 ) {
            if( !isStart ){
                boolean needsStarted =  false;
                
                try {
                    runner.client(host, port,command);
                } catch(IOException ex) {
                  String showCommand = command;
                  if ( showCommand != null)
                    showCommand = command;
                    Helper.writeLog(1,"unable to send command: '" + showCommand + "' stack: " + ex.toString());
                    if( isStatus ){
                        needsStarted = true;
                    }
                }
                
                if( isRestart ){
                    try {
                        command = START + "@" + SECRETCODE;
                        
                        runner.client(host, port,command);
                    } catch(IOException ex) {
                      String showCommand = command;
                      if ( showCommand != null)
                        showCommand = command;
                        Helper.writeLog(1,"unable to send command: '" + showCommand + "' stack: " + ex.toString());
                    }
                }

                if( needsStarted ){
                    runner.startServer(port);
                }
                else {
                    System.exit(0);
                }
            }
            else {
                runner.startServer(port);
            }
        }
    }
    
    public void startServer(int port){
        try {
            Helper.writeLog(1,"starting server");
            this.server(port);
        }
        catch(Exception e){
            Helper.writeLog(1,"Server start exception");
            System.exit(0);
        }
    }

    public void processShutdown(){
        //when shutdown is called
        destroyWorkerQueue();
        
        if( messageClient != null ){
            messageClient.destroy();
        }
    }
    
    public void processRestart(){
        //when restart is called
        destroyWorkerQueue();
        initWorkerQueue();
    }
    private MessageClient messageClient;
    
    public void processStart(){
        //when start is called
        initWorkerQueue();
        DataBean serverArgs = new DataBean();
        serverArgs.setValue("mode","server");
        serverArgs.setValue("messagekey","72999");
        serverArgs.setValue("user","auth");
        serverArgs.setValue("messagechannel","/esb/system");
        messageClient = new MessageClient(this,serverArgs);
        Helper.writeLog(1,"starting message server");
        messageClient.addHandler(this);
        
    }
    
    public void processAction(DataBean args){
        String cmd = args.getString("command");
        
        if( cmd.equalsIgnoreCase("auth") ){
        
        }
    }
    
    public void doThreadComplete(DoThread t) {
        try {
            if( t.getType().equalsIgnoreCase("worker") ){
                workerQueue.offer(t);
            }
           
            //writeLog(1,"adding back worker: " + t);
        } catch (Exception e) {
            writeLog(-1, "thread reloading error: " + e.toString());
        }
    }
    
    private BlockingQueue workerQueue;
    private static int MAXWORKERQUEUESIZE = 1;
    private void initWorkerQueue() {
        workerQueue = new ArrayBlockingQueue(MAXWORKERQUEUESIZE);

        try {
            for (int i = 0; i < MAXWORKERQUEUESIZE; i++) {
                DoThread newWorker = new WorkerThread();
                newWorker.setContext(this);
                newWorker.setType("worker");
                newWorker.addDoThreadListener(this);
                newWorker.setSleepInterval(500);
                newWorker.start();
                writeLog(1, "loading worker: " + i);
                workerQueue.offer(newWorker);
            }
        } catch (Exception e) {
            writeLog(-1, "thread loading error: " + e.toString());
        }
    }
    
    private void destroyWorkerQueue() {
        writeLog(1,"start destroy worker queue");
        
        try {
            for (;;) {
                if (workerQueue.size() == MAXWORKERQUEUESIZE) {
                    break;
                } else {
                    Thread.currentThread().sleep(1000);
                }
            }
        } catch (Exception e) {
            writeLog(-1, "thread destroy error: " + e.toString());
        }
        
        writeLog(1,"after flush destroy worker queue");
    }
    
    public DoThread getNextWorker() {
        DoThread result = null;
        try {

            result = (DoThread) workerQueue.take();
            //System.out.println("getting worker: " + result);
        } catch (Exception e) {
            writeLog(-1, "thread getting next error: " + e.toString());
        }
        return (result);
    }
    
    public void queueWork(DataBean args){
        DoThread newWorker = getNextWorker();
        newWorker.setTarget(args);
        newWorker.setRunning(true);
    }
    
    public void launchTimer(DataBean args){
        java.util.Timer tTimer = new java.util.Timer();
        tTimer.schedule(new DoTimerTask(this,args), 1000);
    }
    
    private class DoTimerTask extends java.util.TimerTask {
        private MessageHandler context;
        private DataBean queryArgs;
        
        public DoTimerTask(MessageHandler context, DataBean queryArgs){
            super();
            
            this.context = context;
            this.queryArgs = queryArgs;
            
        }
        
        public void run() {
            Helper.writeLog(1,"Timer Fired: " + queryArgs.toString());
            //processMessage(queryArgs);
        }
    }
    
    
    public class WorkerThread extends DoThread {
        public WorkerThread() {
            super();
        }
        public boolean doOperation() {
            boolean result = true;

            DataBean target = Helper.parseJSON(getTarget().getString("text"));
            
            Helper.writeLog(1,"process target: " + target);
            ArrayList<DataBean> params = target.getCollection("params");
            
            DataBean wrapper = new DataBean();
            if( params != null ){
                for(int i = 0,size = params.size();i < size;i++){
                    DataBean tEntry = params.get(i);
                    Enumeration innerColumNameList = tEntry.getColumnNames();
                    String tKey;
                    while( innerColumNameList.hasMoreElements() )
                    {
                        tKey = innerColumNameList.nextElement().toString();
                        wrapper.setValue(tKey,tEntry.getString(tKey));
                    }                    
                }
            }
            
            wrapper.setValue("getkey","true");
            wrapper.setValue("contenttype","AUTH");
            writeLog(1,wrapper.toString());
            
            String pResult = Helper.postContent("http://localhost/zen/authservice","pulse",wrapper);
            DataBean tResult = Helper.parseJSON(pResult);
            
            writeLog(1,tResult.toString());
            ArrayList<DataBean>itemList = tResult.getCollection("items");
            String currentUser = wrapper.getString("username");
            DataBean messageArgs = new DataBean();
            messageArgs.setValue("username",currentUser);
            messageArgs.setValue("status",tResult.getValue("status"));
            
            if( itemList != null && itemList.size() > 0 ){
                messageArgs.setValue("authkey",itemList.get(0).getValue("authkey"));
            }
            else {
                
            }
            messageClient.sendUserMessage(currentUser,"auth","user",messageArgs,tResult.getString("message"));
            
            return (result);
        }
        
        public void cleanUp() {
            //noop default
            setRunning(false);
            
            notifyThreadListeners();
        }
    }
    
    
}
