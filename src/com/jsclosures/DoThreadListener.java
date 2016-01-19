package com.jsclosures;


/**
An interface for a callback for state change notifications.
 */
public interface DoThreadListener {
    /**
     * callback for state change in a do thread
     */
    public void doThreadComplete(DoThread t);
}

