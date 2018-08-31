package com.avantic.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.avantic.pojo.Customer;
import com.avantic.service.CustomerService;
import com.avantic.service.DataBaseService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@WebServlet(asyncSupported = true, urlPatterns= {"/customer"})
public class RESTCustomerServlet extends HttpServlet {
	private static final long serialVersionUID = -7944771699430189605L;
	
	private final String DATE_PATTERN= "yyyy-MM-dd";
	private final String CONTENT_TYPE= "application/json";
	
	private DataBaseService<Customer> service;
	private Logger logger;
	
	private enum QueryParam {
		FIND_TYPE, NAME, LASTNAME, EMAIL, PHONE, BIRTHDAY,
		COMMENT, ID;

		@Override
		public String toString() {
			return name().replaceAll("_", "-").toLowerCase();
		}
	}
	
	private enum TypeFind {
		ALL, ID, UNKNOWN;
		
		public static TypeFind fromName(String name) {
			return Stream.of(values())
					.filter(t -> t.name().equalsIgnoreCase(name))
					.findFirst()
					.orElse(UNKNOWN);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.runAsync(req, resp, () ->  asyncFind(req, resp));
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.runAsync(req, resp, () ->  asyncDelete(req, resp));
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.runAsync(req, resp, () -> asyncAdd(req, resp));
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.runAsync(req, resp, () -> asyncUpdate(req, resp));
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		this.service= new CustomerService();
		this.logger= Logger.getLogger(getClass().getSimpleName());
	}
	
	// section of private methods
	
	private void runAsync(HttpServletRequest req, HttpServletResponse resp, Runnable run) {
		var asyncCxt= req.getAsyncContext();
		
		asyncCxt.addListener(new AsyncListenerImpl(), req, resp);
		asyncCxt.setTimeout(TimeUnit.SECONDS.toMillis(3));
		asyncCxt.start(run);
	}
	
	private void asyncFind(HttpServletRequest req, HttpServletResponse resp) {
		var text= "";
		var gson= new GsonBuilder().
						generateNonExecutableJson().
						create();
		
		resp.setContentType(CONTENT_TYPE);
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		
		switch (TypeFind.fromName(req.getParameter(QueryParam.FIND_TYPE.toString()))) {
			case ALL:
				text= gson.toJson(service.findAll());
				
				break;
				
			case ID:
				var result= service.findById(req.getParameter(QueryParam.ID.name()));
				
				if (result.isPresent())
					text= gson.toJson(result.get());
				
				break;
				
			case UNKNOWN:
				var jsonErr= new JsonObject();
				jsonErr.addProperty("message", "Bad find type");
				
				text= jsonErr.toString();
				resp.setStatus(406);
				
				break;
		}
		
		try {
			resp.setContentLength(text.length());
			resp.getWriter().print(text);
			
		} catch (IOException ex) {
			this.logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private void asyncDelete(HttpServletRequest req, HttpServletResponse resp) {
		var jsonObj= new JsonObject();
		var idCustomer= req.getParameter(QueryParam.ID.name());
		var result= this.service.remove(idCustomer);
		
		if (result)
			jsonObj.addProperty("message", String.format("The customer: %s was removed successful", idCustomer));
		
		else
			jsonObj.addProperty("error", "The customer could not remove, please try again");
		
		jsonObj.addProperty("result", result);
		
		resp.setContentType(CONTENT_TYPE);
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resp.setContentLength(jsonObj.toString().length());
		
		try {
			var out= resp.getWriter();
			
			out.print(jsonObj.toString());
			out.flush();
		} catch (IOException ex) {
			this.logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private void asyncAdd(HttpServletRequest req, HttpServletResponse resp) {
		var jsonObj= new JsonObject();
		var customer= this.getCustomer(req);
		var result= this.service.add(customer);
		
		if (result)
			jsonObj.addProperty("message", String.format("The customer %s - %s %s was added successful", 
					customer.getId(), customer.getName(), customer.getLastname()));
		
		else
			jsonObj.addProperty("error", "The customer could not add, please try again");
		
		jsonObj.addProperty("result", result);
		
		resp.setContentType(CONTENT_TYPE);
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resp.setContentLength(jsonObj.toString().length());
		
		try {
			var out= resp.getWriter();
			
			out.print(jsonObj.toString());
			out.flush();
		} catch (IOException ex) {
			this.logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private void asyncUpdate(HttpServletRequest req, HttpServletResponse resp) {
		var jsonObj= new JsonObject();
		var customer= this.getCustomer(req);
		var result= this.service.update(customer);
		
		if (result)
			jsonObj.addProperty("message", String.format("The customer %s - %s %s was added successful", 
					customer.getId(), customer.getName(), customer.getLastname()));
		
		else
			jsonObj.addProperty("error", "The customer could not add, please try again");
		
		jsonObj.addProperty("result", result);
		
		resp.setContentType(CONTENT_TYPE);
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resp.setContentLength(jsonObj.toString().length());
		
		try {
			var out= resp.getWriter();
			
			out.print(jsonObj.toString());
			out.flush();
		} catch (IOException ex) {
			this.logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private Customer getCustomer(HttpServletRequest req) {
		var id= req.getParameter(QueryParam.ID.name());
		var name= req.getParameter(QueryParam.NAME.name());
		var lastname= req.getParameter(QueryParam.LASTNAME.name());
		var email= req.getParameter(QueryParam.EMAIL.name());
		var phone= req.getParameter(QueryParam.PHONE.name());
		var comment= req.getParameter(QueryParam.COMMENT.name());
		var dateString= req.getParameter(QueryParam.BIRTHDAY.name());
		var customer= new Customer(id, name, lastname);
		
		customer.setEmail(email);
		customer.setPhone(phone);
		customer.setComment(comment);
		customer.setBirthday(LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_PATTERN)));
		
		return customer;
	}
}