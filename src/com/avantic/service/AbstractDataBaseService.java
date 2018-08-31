package com.avantic.service;

import static java.util.stream.Collectors.joining;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public abstract class AbstractDataBaseService<T> implements DataBaseService<T> {
	protected DataSource dataSource;
	
	private final Logger logger;
	
	protected enum Action {
		ADD, UPDATE, DELETE;
	}
	
	public AbstractDataBaseService() {
		this.logger= Logger.getLogger(getClass().getSimpleName());
		
		init();
	}
	
	// section of protected methods
	
	protected void init() {
		try {
			var initialCtx= new InitialContext();
			var ctx= (Context) initialCtx.lookup("java:comp/env");
			
			dataSource= (DataSource) ctx.lookup("jdbc/db_avantic");
		} catch (NamingException ex) {
			this.logError(ex);
		}
	}
	
	protected Optional<Connection> createConnection() {
		Connection conn= null;
		
		try {
			conn= this.dataSource.getConnection();
		} catch (SQLException ex) {
			this.logError(ex);
		}
		
		return Optional.ofNullable(conn);
	}
	
	protected void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				this.logError(ex);
			}
		}
	}
	
	protected void logError(Exception ex) {
		this.logError(ex, ex.getMessage());
	}
	
	protected void logError(Exception ex, String message) {
		if (this.logger != null)
			this.logger.log(Level.SEVERE, message, ex);
	}
	
	protected String generateSQLQuery(Action action) {
		var sqlQuery= new StringBuilder(100);
		
		switch (action) {
			case ADD:
				sqlQuery.append("INSERT INTO ")
					.append(getTableName())
					.append(" (")
					.append(getTuplesName().
							stream().
							collect(joining(", ")))
					.append(")")
					.append(" VALUES (")
					.append(getTuplesName()
							.stream()
							.map(s -> "?")
							.collect(joining(", ")))
					.append(");");
				
				break;
				
			case UPDATE:
				sqlQuery.append("UPDATE ")
					.append(getTableName())
					.append("SET ")
					.append(getTuplesName()
							.stream()
							.filter(s -> !s.equals(getIdTupleName()))
							.map(s -> s + "= ?")
							.collect(joining(", ")))
					.append(" WHERE ")
					.append(getIdTupleName())
					.append("= ?;");
				
				break;
				
			case DELETE:
				sqlQuery.append("DELETE FROM ")
					.append(getTableName())
					.append(" WHERE ")
					.append(getIdTupleName())
					.append("= ?");
				
				break;
		}
		
		return sqlQuery.toString();
	}
	
	protected abstract List<String> getTuplesName();
	
	protected abstract String getIdTupleName();
	
	protected abstract String getTableName();
	
	protected abstract List<T> mapResulSet(ResultSet rs) throws SQLException;
}