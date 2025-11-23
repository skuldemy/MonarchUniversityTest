package com.MonarchUniversity.MonarchUniversity.Exception;

public class ResponseNotFoundException extends RuntimeException {
	public ResponseNotFoundException() {
        super();
    }
	
	 public ResponseNotFoundException(String message) {
	        super(message);
	    }
	 
	 public ResponseNotFoundException(String message, Throwable cause) {
	        super(message, cause);
	    }
	 public ResponseNotFoundException(Throwable cause) {
	        super(cause);
	    }
	   protected ResponseNotFoundException(String message, Throwable cause,
               boolean enableSuppression,
               boolean writableStackTrace) {
super(message, cause, enableSuppression, writableStackTrace);
}
}

