package com.cjbarker.wb.ws;

import org.apache.http.Header;

/**
 * Class holds HTTP response data affiliated w/ <code>ClientRequest.java</code> 
 * 
 * @author CJ Barker
 */
public class ClientResponse {
	
	protected final int respCode;			// HTTP response code
	protected final byte[] respMesssage;	// HTTP response msg
	protected final Header[] headers;		// HTTP response hdrs
	
	private ClientResponse() {
		this.respCode = 0;
		this.respMesssage = null;
		this.headers = null;
	}
	
	public ClientResponse(int code, byte[] msg, Header[] hdrs) {
		this.respCode = code;
		this.respMesssage = msg;
		this.headers = hdrs;
	}
	
	public String toString() {
    	int bLen = (this.respMesssage != null) ? this.respMesssage.length : 0;
    	return ("Response Code: "+this.respCode+"\nResponse Msg Byte Len: "+bLen);
    }

    /* getters */
    public final byte[] getResponseMessage() {
        return this.respMesssage;
    }
    public int getResponseCode() { 
        return this.respCode;
    }
    public final Header[] getHeaders() {
    	return this.headers;
    }
}
