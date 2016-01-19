package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.RestService;

/**
 *
 * implement a get, post (create), put (update) and delete
 *
 */
public interface RestImplService {
    public DataBean getData(RestService context, DataBean args);
    public DataBean postData(RestService context,DataBean args);
    public DataBean putData(RestService context, DataBean args);
    public DataBean deleteData(RestService context, DataBean args);
}
