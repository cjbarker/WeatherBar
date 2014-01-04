package com.cjbarker.wb.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

/**
 * Class provides HTTP request/response handling for weather API clients.
 * 
 * @author CJ Barker
 */
public class ClientRequest {
	
	// static vars
	private static final String TAG = "HttpClient";
	private static final String DEFAULT_USER_AGENT = "WB/1.0";
	private static final String DEFAULT_ACCEPT_TYPES = "application/json,application/xml";
	public static enum Method { GET };
	
	// predefined headers
	private static final BasicHeader HEADER_CONTENT_TYPE = 
			new BasicHeader("Content-Type", "application/xml");
	private static final BasicHeader HEADER_ACCEPT = 
			new BasicHeader("Accept", "application/xml");
	private static final BasicHeader HEADER_CONNECTION = 
			new BasicHeader("Connection", "Keep-Alive");
	private static final BasicHeader HEADER_CACHE_CONTROL = 
			new BasicHeader("Cache-Control", "no-cache");
	private static final BasicHeader HEADER_PRAGMA = 
			new BasicHeader("Pragma", "no-cache");

	// member vars
	private String baseUri;
	private String userAgent;		 
	private String acceptTypes;
	
	private ClientRequest() {}
	
	/**
	 * Default constructor
	 * @param uri	URI to perform HTTP request on
	 */
	public ClientRequest(String uri) {
		this(uri, DEFAULT_USER_AGENT);
	}
	
	/**
	 * Constructor
	 * @param uri		URI to perform HTTP request on
	 * @param userAgent HTTP user-agent header
	 */
	public ClientRequest(String uri, String userAgent) {
		this(uri, DEFAULT_USER_AGENT, DEFAULT_ACCEPT_TYPES);
	}
	
	/**
	 * Constructor
	 * @param uri			URI to perform HTTP request on
	 * @param userAgent 	HTTP user-agent header
	 * @param acceptTypes	HTTP accept-type header
	 */
	public ClientRequest(String uri, String userAgent, String acceptTypes) {
		this.baseUri = uri;
		this.userAgent = userAgent;
		this.acceptTypes = acceptTypes;
	}
	
	public ClientResponse sendRequest(Method method) throws IOException {
		return sendRequest(method, null, null);
	}
	
	public ClientResponse sendRequest(Method method, String resource) throws IOException {
		return sendRequest(method, resource, null);
	}
	
	/**
	 * Performs HTTP request and returns the associated response.
	 * 
	 * @param method		HTTP method to execute
	 * @param resource		Resource parameter(s) to append to baseUri
	 * @param payload		Optional request payload to send in HTTP body
	 * @return				Parsed client response
	 * @throws IOException	Occurs if HTTP IOException encountered
	 */
	public ClientResponse sendRequest(Method method, String resource, byte[] payload) throws IOException {
		ClientResponse clientResp = null;
		
		if (resource == null || resource.length() <= 0) {
			resource ="";
		}
	    else {
	    	if (resource.charAt(0) == '/' && resource.length() >= 2) {
	    		resource = resource.substring(1);
	        }
	    	resource = (resource.charAt(resource.length()-1) == '/') ? resource.substring(0, resource.length()-1) : resource;
	    }

	    final String url = this.baseUri + "/" + resource;
	        
	    long beginMS = System.currentTimeMillis();
	    Log.d(TAG, "HTTP ["+ method +"] to "+ url + " at time (ms): " + beginMS);
	    
	    URI uri = URI.create(url);
        
		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		HttpClient client = getHttpClient();
        HttpRequestBase request = getRequest(method, url);       
        
        request.setHeaders(getHeaders());
        
        if (payload != null && payload.length > 0) {
        	HttpEntity ent = new ByteArrayEntity(payload);
			((HttpEntityEnclosingRequest) request).setEntity(ent);
        }
        
        HttpResponse response = null;
        
		try {
			response = client.execute(host, request);
			
			if (response == null) {
				Log.e(TAG, "Failed to get HTTP response");
				throw new IOException("Failed to get HTTP response");
			}
			
			StatusLine status = response.getStatusLine();
			
			if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_CREATED || status.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) 
	        {
				Header[] headers = response.getAllHeaders();	
				byte[] data = getResponseEntity(response);
	        	if (data != null && data.length > 0) {
	        		Log.d(TAG, "Also read data of length = " + data.length);
				}
				else {
					Log.d(TAG, "Data is null?");
				}
	        	clientResp = new ClientResponse(status.getStatusCode(), data, headers);
	        }
	        else {
	        	Log.e(TAG, "Error connecting to "+ uri +" with response code: " + status.getStatusCode());
	        }

    	}
        catch (IOException ioe) {
        	Log.e(TAG, "Encountered IOException for HTTP ["+ method +"] to "+ uri);
            throw ioe;
        }
        finally {
    		long endMS = System.currentTimeMillis();
    		Log.d(TAG, "End request to "+uri+" at time (ms): " + endMS + ". It took about " + (endMS - beginMS) + " ms.");
        }
        
        return clientResp;
	}
	
	/**
	 * Builds HTTP Client w/ associated parameters
	 * @return	HttpClient
	 */
	private HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpParams params = httpClient.getParams();
		
		// Set timeout
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
		
		// Set basic data
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setUserAgent(params, this.userAgent);

		// Make connection pool
		ConnPerRoute connPerRoute = new ConnPerRouteBean(30);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		ConnManagerParams.setMaxTotalConnections(params, 30);

		return httpClient;
	}
	
	/**
	 * Create appropriate request based on the method
	 * @param method	HTTP Method for request
	 * @param uri		URI to perform request on
	 * @return
	 */
	private HttpRequestBase getRequest(Method method, String uri) {
    	HttpRequestBase request = null;
		if (method == Method.GET) {
    		request = new HttpGet(uri);
        }
        else {
			throw new IllegalArgumentException("Not support such type!");
		}
		return request;
	}
	
	/**
	 * Reads the response payload (entity) and returns it
	 * 
	 * @param response	HttpResponse to read message body for payload
	 * @return	Parsed response's payload
	 */
	private byte[] getResponseEntity(HttpResponse response) {
		try {
			// Pull content stream from response
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				ByteArrayOutputStream content = new ByteArrayOutputStream();

				// Read response into a buffered stream
				int readBytes;
				byte [] buffer = new byte[512];
				while ((readBytes = inputStream.read(buffer)) != -1) {
					content.write(buffer, 0, readBytes);
				}
				
				inputStream.close();
				return content.toByteArray();
			}
		} catch (IOException e) {
			Log.e(TAG, "Get response entity failed!");
		}
		return null;
	}

	/**
	 * Builds HTTP headers
	 * @return	Http headers
	 */
	private Header[] getHeaders() {
		List<Header> l = new ArrayList<Header>();
		l.add(HEADER_CONTENT_TYPE);
		l.add(HEADER_ACCEPT);
		l.add(HEADER_CONNECTION);
		l.add(HEADER_CACHE_CONTROL);
		l.add(HEADER_PRAGMA);
		return l.toArray(new Header[] {});
	}
	
	public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("BaseURI: ").append(baseUri).append("\n");
        buf.append("UserAgent: ").append(userAgent).append("\n");
        buf.append("AcceptType: ").append(acceptTypes).append("\n");
        return buf.toString();
    }

    /* getters */
    public String getBaseURI() {
        return this.baseUri;
    }
    public String getUserAgent() {
        return this.userAgent;
    }
    
    /* setters */
    public void setBaseURI(String uri) {
        this.baseUri = uri;
    }
    public void setUserAgent(String agent) {
        this.userAgent = agent;
    }
    public void setAcceptType(String type) {
    	this.acceptTypes = type;
    }
}
