package com.manuelmaly.hn.server;

/**
 * Interface for HTTP request commands (command pattern).
 * 
 * @author manuelmaly
 * 
 * @param <T>
 *            Return type of request.
 */
public interface IAPICommand<T> extends Runnable {
    static final String JSON_MIME = "application/json";
    static final String HTML_MIME = "text/html";
    static final String MULTIPART_MIME = "multipart/form-data";
    static final String ACCEPT_HEADER = "Accept";
    static final String RESPONSE_SC_ERROR_MSG = "Response status code does not match the expected: ";
    
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNKNOWN = 1;
    public static final int ERROR_GENERIC_COMMUNICATION_ERROR = 10;
    public static final int ERROR_DEVICE_OFFLINE = 20;
    public static final int ERROR_RESPONSE_PARSE_ERROR = 30;
    public static final int ERROR_SERVER_RETURNED_ERROR = 40;
    public static final int ERROR_CANCELLED_BY_USER = 1000;
    
    public static final String BROADCAST_INTENT_EXTRA_ERROR = "error";
    public static final String BROADCAST_INTENT_EXTRA_RESPONSE = "response";
    
    public static final String DEFAULT_BROADCAST_INTENT_ID = "APICommandFinished";

    /**
     * Returns the result of this command AFTER it has been run.
     * 
     * @return T return type of request.
     */
    public T getResponseContent();

    /**
     * Returns the error code which occurred. If a high-level error occurred
     * which has nothing to do with technical issues (e.g. validation error),
     * ServerErrorCodes.UNDEFINED is returned here.
     * 
     * @see ServerErrorCodes
     * 
     */
    public int getErrorCode();
    
    /**
     * Returns the HTTP status code from the response.
     * @return int http status code.
     */
    public int getActualStatusCode();

    public void responseHandlingFinished(T parsedResponse, int responseHttpStatus);
    
    public enum RequestType {
        GET, PUT, POST, DELETE
    }
    
}
