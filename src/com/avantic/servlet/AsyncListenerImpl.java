package com.avantic.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

@WebListener
public class AsyncListenerImpl implements AsyncListener {
	private Logger logger;
	private enum StatusCode {
		TIMEOUT(504),  ERROR(500);
		
		private final int code;
		
		private StatusCode(int code) {
			this.code= code;
		}
	}
	
	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		this.logger.info("The event was completed!");
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		this.logger.log(Level.SEVERE, "The event couldn't complete because reach the timeout");
		
		var json= new JsonObject();
		var res= (HttpServletResponse) event.getSuppliedResponse();
		
		if (res != null) {
			var out= res.getWriter();
			
			json.addProperty("message", "The action couldn't complete because reach the timeout");
			
			res.setContentType("application/json");
			res.setContentLength(json.toString().length());
			res.setCharacterEncoding(StandardCharsets.UTF_8.name());
			res.setStatus(StatusCode.TIMEOUT.code);
			
			out.print(json.toString());
			out.flush();
		}
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		var ex= event.getThrowable();
		var jsonObj= new JsonObject();
		var res= (HttpServletResponse) event.getSuppliedResponse();
		
		this.logger.log(Level.SEVERE, "Occurs an error when process the event", ex);
		
		if (res != null) {
			var out= res.getWriter();
			
			jsonObj.addProperty("message", ex.getMessage());
			
			res.setContentType("application/json");
			res.setContentLength(jsonObj.toString().length());
			res.setCharacterEncoding(StandardCharsets.UTF_8.name());
			res.setStatus(StatusCode.ERROR.code);
			
			out.print(jsonObj.toString());
			out.flush();
		}
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		this.logger= Logger.getLogger(getClass().getSimpleName());
		this.logger.info("Starting event");
	}

}
