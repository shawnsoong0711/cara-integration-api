package com.slinfo.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slinfo.util.log.SLogger;

/**
 * Standard http call class. Supports GET, POST, PUT.
 * 
 * @author
 *
 */
public class HttpClient {
	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	// hk.chang 12/1/2020 Add in new function to handle https and http calls.
	// Request
	private String encoding = "UTF-8";
	private final List<String> validMethods = Arrays.asList("GET", "POST", "PUT", "DELETE");
	private boolean httpsFlag = false;
	private String methodType;
	private String host;
	private Integer port;
	private String path;
	private String urlPath;
	private String body;
	private Map<String, String> pathParams = new HashMap<String, String>();
	private Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
	private Map<String, String> headers = new HashMap<String, String>();

	// Response
	private Integer responseStatus;
	private String responseBody;
	Map<String, List<String>> headerMap;
	private String responseHeader;

	/**
	 * Constructor which sets the connection to the given urlPath.
	 * 
	 * @param urlPath
	 */
	public HttpClient(String urlPath) {
		setUp();
		this.urlPath = urlPath;
	}

	/**
	 * Used to set up needed values.
	 */
	private void setUp() {
		this.headers.put("Content-Type", "application/json");
	}

	/**
	 * Constructor which sets the connection to the given host, port and path.
	 * 
	 * @param host
	 * @param port
	 * @param path
	 */
	public HttpClient(String host, Integer port, String path) {
		setUp();
		this.host = host;
		this.port = port;
		this.path = path;
	}

	/**
	 * Constructor which sets the connection to the given host, port and path. If
	 * https is set to true, the url will be called with "https" instead of "http"
	 * 
	 * @param https
	 * @param host
	 * @param port
	 * @param path
	 */
	public HttpClient(boolean https, String host, Integer port, String path) {
		setUp();
		this.httpsFlag = https;
		this.host = host;
		this.port = port;
		this.path = path;
	}

	/**
	 * Sets the Method type with the given String. Input will be set to Upper Case
	 * 
	 * @param type
	 * @return
	 */
	public HttpClient setMethodType(String type) {
		this.methodType = type.toUpperCase();
		return this;
	}

	/**
	 * Sets the https flag to the given boolean. If true, the url called will have
	 * "https" in it's url. If false, the url called will have "http" in it's url.
	 * 
	 * @param flag
	 * @return
	 */
	public HttpClient setHttps(boolean flag) {
		this.httpsFlag = flag;
		return this;
	}

	/**
	 * Sets the path parameter by replacing a path parameter
	 * placeholder with an actual value.
	 *
	 * @param name 
	 * @param value the value of path parameter to use
	 * @return
	 */
	// TODO: decide on a proper name for this
	public HttpClient setPathParam(String name, String value) {
		pathParams.put(name, com.slinfo.helper.URLEncoder.encodeUsingUTF8(value));
		return this;
	}

	/**
	 * Sets the QueryParams with the given Map. This resets any previously set
	 * QueryParams.
	 * 
	 * @see {@link #addQueryParam(String, String)}
	 * @param queryParams
	 * @return This Instance.
	 */
	public HttpClient setQueryParams(Map<String, List<String>> queryParams) {
		this.queryParams = queryParams;
		return this;
	}

	/**
	 * Adds a QueryParam to the current Map.
	 *
	 * @param key   name of query parameter, cannot be null
	 * @param value the value of the query parameter. If value is null,
	 *               nothing is added.
	 * @return This Instance.
	 */
	public HttpClient addQueryParam(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}
		// get the list of values associated with the key
		List<String> values = queryParams.get(key);

		/*
		 * if there is no such list associated with the key, associate the key with a
		 * new ArrayList
		 */
		if (values == null) {
			values = new ArrayList<>();
			queryParams.put(key, values);
		}

		// add value to the list of values
		if (value != null) {
			values.add(value);
		}
		return this;
	}

	/**
	 * Sets the Header with the given Map. This resets any previously set Headers.
	 * 
	 * @param headers
	 * @return
	 */
	public HttpClient setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	/**
	 * Adds a Header property into the HttpCall.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public HttpClient addHeader(String key, String value) {
		this.headers.put(key, value);
		return this;
	}

	/**
	 * Sets the body as the given String.
	 * 
	 * @param body
	 * @return
	 */
	public HttpClient setBody(String body) {
		this.body = body;
		return this;
	}

	/**
	 * Sets the path to the given String. This method resets the urlPath.
	 * 
	 * @param path
	 * @return
	 */
	public HttpClient setPath(String path) {
		resetConnection(true);
		this.path = path;

		return this;
	}

	/**
	 * Sets the port to the given Integer. This method resets the urlPath.
	 * 
	 * @param port
	 * @return
	 */
	public HttpClient setPort(Integer port) {
		resetConnection(true);
		this.port = port;

		return this;
	}

	/**
	 * Sets the host to the given String. This method resets the urlPath.
	 * 
	 * @param host
	 * @return
	 */
	public HttpClient setHost(String host) {
		resetConnection(true);
		this.host = host;
		return this;
	}

	/**
	 * Sets the urlPath to the given String. This method resets the host, port and
	 * path.
	 * 
	 * @param urlPath
	 * @return
	 */
	public HttpClient setUrlPath(String urlPath) {
		resetConnection(false);
		this.urlPath = urlPath;
		return this;
	}

	/**
	 * Triggers the connection based on the given properties. Sets the response
	 * results (status and body) into private properties.
	 * 
	 * @see {@link #responseBody()}, {@link #responseStatus()}
	 */
	public void connect() {
		// HttpURLConnection con = new URL(this.urlPath);
		try {
			// Verify
			String errors = "";
			for (String s : verify()) {
				errors += s;
			}
			if (errors.length() > 0) {
				throw new Exception("Setup Errors -> " + errors);
			}

			if (this.urlPath == null) {
				this.urlPath = (this.httpsFlag ? "https" : "http") + "://" + this.host + ":" + this.port + "/"
						+ this.path;
			}

			// replaces path parameters in URL with actual values
			replacePathParametersWithValues();

			switch (this.methodType) {
			case "GET":
				this.urlPath += "?" + queryParamToString();
				break;
			case "POST":
				break;
			case "PUT":
				break;
			case "DELETE":
				break;
			}

			// Set properties
			HttpURLConnection con = (HttpURLConnection) new URL(this.urlPath).openConnection();
			con.setRequestMethod(this.methodType);

			for (String key : this.headers.keySet()) {
				con.setRequestProperty(key, this.headers.get(key));
			}

			if (this.methodType.equals("PUT") || this.methodType.equals("POST")) {
				con.setDoOutput(true);
				OutputStream op = con.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(op, this.encoding);
				writer.write(this.body);
				// writer.write(queryParamToString());

				// End Writing
				writer.flush();
				writer.close();
				op.close();
			}

			// Trigger call
			logger.info("{} {} - Send request", con.getRequestMethod(), this.urlPath);
			con.connect();
			logger.debug("{} {} - Response received", con.getRequestMethod(), this.urlPath);

			// Get Results
			this.responseStatus = con.getResponseCode();
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			if (this.responseStatus == 200 || this.responseStatus == 201) {
				BufferedInputStream in = new BufferedInputStream(con.getInputStream());
				int nextByte = in.read();
				while (nextByte != -1) {
					byteStream.write(nextByte);
					nextByte = in.read();
				}
				this.responseBody = byteStream.toString();
			} else {
				BufferedInputStream in = new BufferedInputStream(con.getErrorStream());
				int nextByte = in.read();
				while (nextByte != -1) {
					byteStream.write(nextByte);
					nextByte = in.read();
				}
				this.responseBody = byteStream.toString();
			}
			this.headerMap = con.getHeaderFields();

			// BufferedInputStream in = new
			// BufferedInputStream(con.getErrorStream());

			con.disconnect();
		} catch (Exception e) {
			SLogger.error(HttpClient.class.getCanonicalName(), "", e);
		}

	}

	/**
	 * Returns the responseStatus.
	 * 
	 * @return
	 */
	public Integer responseStatus() {
		return this.responseStatus;
	}

	/**
	 * Returns the responseBody.
	 * 
	 * @return
	 */
	public String responseBody() {
		return this.responseBody;
	}

	/**
	 * Returns the corresponding field values, given the response-header field name.
	 *
	 * @param headerName response-header field name
	 * @return an unmodifiable list of response-header field values
	 */
	public List<String> responseHeader(String headerName) {
		return headerMap.get(headerName);
	}

	private String queryParamToString() throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (String key : this.queryParams.keySet()) {
			List<String> values = this.queryParams.get(key);
			for (String value : values) {
				builder.append(URLEncoder.encode(key, this.encoding));
				builder.append("=");
				builder.append(URLEncoder.encode(value, this.encoding));
				builder.append("&");
			}
		}
		String result = builder.toString();
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * If true, resets the urlPath. If false, resets the path, host and port.
	 * 
	 * @param bool
	 */
	private void resetConnection(boolean bool) {
		if (bool) {
			this.urlPath = null;
		} else {
			this.path = null;
			this.host = null;
			this.port = null;
		}
	}

	/**
	 * Used to check required detail before HTTP calling.
	 * 
	 * @return List of errors.
	 */
	private List<String> verify() {
		List<String> errors = new ArrayList<String>();
		if (this.urlPath == null) {
			if (this.host == null) {
				errors.add("Host is Null.");
			}
			if (this.port == null) {
				errors.add("Port is Null.");
			}
			if (this.path == null) {
				errors.add("Path is Null.");
			}
		}

		if (this.methodType == null) {
			errors.add("Method Type is Null.");
		} else {
			if (!this.validMethods.contains(this.methodType)) {
				errors.add("Invalid Method Type");
			}
		}

		return errors;
	}

	/**
	 * 
	 */
	private void replacePathParametersWithValues() {
		pathParams.keySet();
		for (String parameterName : pathParams.keySet()) {
			String parameterValue = pathParams.get(parameterName);

			// replace path parameters in the middle of the line
			urlPath.replaceAll("(?<=/):" + parameterName + "(?=(/\\?)", parameterValue);
			// replace path parameters at the end of the line
			urlPath.replaceAll("(?<=/):" + parameterName + "$", parameterValue);
		}
	}
}
