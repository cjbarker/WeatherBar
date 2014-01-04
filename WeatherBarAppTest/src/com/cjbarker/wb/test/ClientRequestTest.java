package com.cjbarker.wb.test;

import java.io.IOException;

import org.apache.http.HttpStatus;

import com.cjbarker.wb.ws.ClientRequest;
import com.cjbarker.wb.ws.ClientResponse;

import junit.framework.TestCase;

public class ClientRequestTest extends TestCase {
	
	private static final String URI = "http://www.google.com";
	
	private ClientRequest request;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        this.request = new ClientRequest(URI);
    }
    
    public void testGetters() {
    	assertEquals(URI, request.getBaseURI());
    	assertNotNull(request.getUserAgent());
    	assertTrue(request.toString().length() > 0);
    }
    
    public void testSendRequestGet() {
    	ClientResponse response = null;
    	try {
    		response = request.sendRequest(ClientRequest.Method.GET);
    		assertNotNull(response);
    		assertTrue(HttpStatus.SC_OK == response.getResponseCode());
    		assertNotNull(response.getResponseMessage());
    		assertNotNull(response.getHeaders());
    		
    		String entity = new String(response.getResponseMessage());
    		assertTrue(entity.length() > 0);
    		assertTrue(entity.startsWith("<!doctype html>"));
    	}
    	catch (IOException ioe) {
    		fail(ioe.getMessage());
    	}
    }

}
