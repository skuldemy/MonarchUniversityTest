package com.MonarchUniversity.MonarchUniversity.Exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdviceClass extends ResponseEntityExceptionHandler{
	@ExceptionHandler(ResponseNotFoundException.class)
	private ResponseEntity<Response> handleExeption (ResponseNotFoundException ex, WebRequest req){
		Response response = new Response(HttpStatus.NOT_FOUND,req.getDescription(false),
				ex.getMessage(),LocalDate.now());
		
		return new ResponseEntity<Response>(response,HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(ResponseForbiddenException.class)
	private ResponseEntity<Response> handleException(ResponseForbiddenException e, WebRequest req){
		Response response = new Response(HttpStatus.FORBIDDEN,req.getDescription(false),e.getMessage(),LocalDate.now());
		
		return new ResponseEntity<Response>(response,HttpStatus.FORBIDDEN);
	}
	@ExceptionHandler(ResponseServerException.class)
	private ResponseEntity<Response> handleException(ResponseServerException ex,WebRequest req){
		Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR,req.getDescription(false),ex.getMessage(),LocalDate.now());
		
		return new ResponseEntity<Response>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
	        MethodArgumentNotValidException ex,
	        org.springframework.http.HttpHeaders headers,
	        HttpStatusCode status,
	        WebRequest request) {

	    Map<String, Object> body = new HashMap<>();
	    body.put("timestamp", LocalDate.now());
	    body.put("Status", HttpStatus.BAD_REQUEST.value());

	    Map<String, Object> errors = new HashMap<>();
	    ex.getBindingResult().getFieldErrors().forEach(error ->
	        errors.put(error.getField(), error.getDefaultMessage())
	    );
	    body.put("errors", errors);
	    body.put("message", "Validation failed for one or more fields");

	    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	
	
}

