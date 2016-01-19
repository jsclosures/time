package com.jsclosures;

import java.util.ArrayList;

/**
  A robust thread wrapper for j2me an other platforms.  Use this class to do threaded operations.
 */
 public abstract class DoThread extends Thread {
     private boolean running;

     public DoThread() {
         running = false;
     }
     
     private String type;
     
     public void setType(String type){
         this.type = type;
     }
     
     public String getType(){
         return( this.type );
     }

     public void run() {
         for (;;) {
             try {
                 if (running) {
                     if (doOperation()) {
                         setRunning(false);
                         notifyThreadListeners();
                     }
                 }
                 //else
                 Thread.currentThread().sleep(getSleepInterval());
             } catch (InterruptedException e) {
                 closeDown();
             }
         }
     }

     public void setRunning(boolean b) {
         running = b;
         if( running ){
             //Thread.currentThread().notify();
         }
     }

     public void closeDown() {
         setRunning(false);
     }
     private Object context;

     public void setContext(Object ctx) {
         context = ctx;
     }

     public Object getContext() {
         return (context);
     }

     private DataBean target;

     public void setTarget(DataBean t) {
         target = t;
     }

     public DataBean getTarget() {
         return (target);
     }

     private String message;

     public void setMessage(String t) {
         message = t;
     }

     public String getMessage() {
         return (message);
     }

     private String name;

     public void setThreadName(String t) {
         name = t;
     }

     public String getThreadName() {
         return (name);
     }

     private int sleepInterval = 500;

     public void setSleepInterval(int t) {
         sleepInterval = t;
     }

     public int getSleepInterval() {
         return (sleepInterval);
     }

     public void cleanUp() {
         //noop default
     }

     public abstract boolean doOperation();

     private ArrayList<DoThreadListener> listeners;

     public void addDoThreadListener(DoThreadListener tl) {
         if (listeners == null) {
             listeners = new ArrayList<DoThreadListener>();

         }
         listeners.add(tl);
     }

     public void notifyThreadListeners() {
         if (listeners != null) {
             for (int i = 0; i < listeners.size(); i++) {
                 listeners.get(i).doThreadComplete(this);
             }
         }
     }
 }

