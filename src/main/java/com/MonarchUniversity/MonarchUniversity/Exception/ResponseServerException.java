package com.MonarchUniversity.MonarchUniversity.Exception;

public class ResponseServerException extends RuntimeException {
	public ResponseServerException() {
	}
	public ResponseServerException(String message) {
        super(message);
    }
	 public ResponseServerException(String message, Throwable cause) {
	        super(message, cause);
	    }
	 public ResponseServerException(Throwable cause) {
	        super(cause);
	    }
	  protected ResponseServerException(String message, Throwable cause,
              boolean enableSuppression,
              boolean writableStackTrace) {
super(message, cause, enableSuppression, writableStackTrace);
}
}
