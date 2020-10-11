package com.ngtr.forum.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ngtr.forum.exception.ForumException;

@RestControllerAdvice
public class GlobalExceptionHander {
	@ExceptionHandler(ForumException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<GlobalExceptionHander.ErrorResponse> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		ErrorResponse errorResponsePayload = new ErrorResponse();
		errorResponsePayload.setMessage(e.getMessage());
		errorResponsePayload.setError("Internal Server Error");
		errorResponsePayload.setTimestamp(System.currentTimeMillis());
		errorResponsePayload.setPath(req.getRequestURI());
		errorResponsePayload.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<GlobalExceptionHander.ErrorResponse>(errorResponsePayload, HttpStatus.INTERNAL_SERVER_ERROR);
	  }
	
	
	public class ErrorResponse {
		private String message;
		private String error;
		private String path;
		private int status;
		private long timestamp;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	}
}
