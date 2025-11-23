package com.MonarchUniversity.MonarchUniversity.Exception;

public class ResponseForbiddenException extends RuntimeException {
	public ResponseForbiddenException() {
		
	}
	public ResponseForbiddenException(String message) {
        super(message);
    }
	 public ResponseForbiddenException(String message, Throwable cause) {
	        super(message, cause);
	    }
	 public ResponseForbiddenException(Throwable cause) {
	        super(cause);
	    }
	  protected ResponseForbiddenException(String message, Throwable cause,
              boolean enableSuppression,
              boolean writableStackTrace) {
super(message, cause, enableSuppression, writableStackTrace);
}
}

