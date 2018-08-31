package com.avantic.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.avantic.pojo.Customer;

public final class CustomerService extends AbstractDataBaseService<Customer>  {

	@Override
	public boolean add(Customer t) {
		var result= false;
		var option= this.createConnection();
		
		if (option.isPresent()) {
			var conn= option.get();
			
			try (conn;
					var pstmt= conn.prepareStatement(this.generateSQLQuery(Action.ADD))) {
				
				conn.setAutoCommit(false);
				
				pstmt.setString(1, t.getId());
				pstmt.setString(2, t.getName());
				pstmt.setString(3, t.getLastname());
				pstmt.setString(4, t.getEmail());
				pstmt.setString(5, t.getPhone());
				pstmt.setDate(6, Date.valueOf(t.getBirthday()));
				pstmt.setString(7, t.getComment());
				
				result= pstmt.executeUpdate() > 0;
				
				if (result)
					conn.commit();
				
			} catch (SQLException ex) {
				this.logError(ex);
				this.rollback(conn);
			}
		}
		
		return result;
	}

	@Override
	public boolean update(Customer t) {
		var result= false;
		var option= this.createConnection();
		
		if (option.isPresent()) {
			var conn= option.get();
			
			try (conn;
					var pstmt= conn.prepareStatement(this.generateSQLQuery(Action.UPDATE))) {
				conn.setAutoCommit(false);
				
				pstmt.setString(1, t.getName());
				pstmt.setString(2, t.getLastname());
				pstmt.setString(3, t.getEmail());
				pstmt.setString(4, t.getPhone());
				pstmt.setDate(5, Date.valueOf(t.getBirthday()));
				pstmt.setString(6, t.getComment());
				pstmt.setString(7, t.getId());
				
				result= pstmt.executeUpdate() > 0;
				
				if (result)
					conn.commit();
				
			} catch (SQLException ex) {
				this.logError(ex);
				this.rollback(conn);
			}
		}
		
		return result;
	}

	@Override
	public <E> boolean remove(E id) {
		var result= false;
		var option= this.createConnection();
		
		if (option.isPresent() && id != null) {
			var conn= option.get();
			
			try (conn; 
					var pstmt= conn.prepareStatement(this.generateSQLQuery(Action.DELETE))) {
				conn.setAutoCommit(false);
				
				pstmt.setString(1, id.toString());
				
				result= pstmt.executeUpdate() > 0;
				
				if (result)
					conn.commit();
			} catch (SQLException ex) {
				this.logError(ex);
				this.rollback(conn);
			}
		}
		
		return result;
	}
	
	@Override
	protected List<String> getTuplesName() {
		return List.of(getIdTupleName(), "cus_name", "cus_lastname", 
				"cus_email", "cus_phone", "cus_birthday", "cus_comment");
	}
	
	@Override
	protected String getTableName() {
		return "tb_custumer";
	}
	
	@Override
	protected String getIdTupleName() {
		return "cus_id";
	}

	@Override
	public List<Customer> findAll() {
		var sql= String.format("SELECT * FROM %s;", this.getTableName());
		var result= List.<Customer>of();
		var option= this.createConnection();
		
		if (option.isPresent()) {
			var conn= option.get();
			
			try (conn;
					var pstmt= conn.prepareStatement(sql)) {
				result= this.mapResulSet(pstmt.executeQuery());
				
			} catch (SQLException ex) {
				this.logError(ex);
			}
		}
		
		return result;
	}

	@Override
	public <E> Optional<Customer> findById(E id) {
		var customer= Optional.<Customer>empty();
		var option= this.createConnection();
		var sql= String.format("SELECT * FROM %s WHERE %s= ?", getTableName(), getIdTupleName());
		
		if (option.isPresent() && id != null) {
			var conn= option.get();
			
			try (conn;
					var pstmt= conn.prepareStatement(sql)) {
				pstmt.setString(1, id.toString());
				
				customer= this.mapResulSet(pstmt.executeQuery()).
						stream().
						findFirst();
			} catch (SQLException ex) {
				this.logError(ex);
			}
		}
		
		return customer;
	}
	
	@Override
	protected List<Customer> mapResulSet(ResultSet rs) throws SQLException {
		var list= new ArrayList<Customer>();
		
		if (rs != null) {
			while (rs.next()) {
				var customer= new Customer();
				
				customer.setId(rs.getString(getIdTupleName()));
				customer.setName(rs.getString("cus_name"));
				customer.setLastname(rs.getString("cus_lastname"));
				customer.setEmail(rs.getString("cus_email"));
				customer.setPhone(rs.getString("cus_phone"));
				customer.setBirthday(rs.getDate("cus_birthday").toLocalDate());
				customer.setComment(rs.getString("cus_comment"));
				customer.setCreateAt(LocalDateTime.ofInstant(rs.getDate("cus_createAt").toInstant(), 
						ZoneId.systemDefault()));
				
				list.add(customer);
			}
		}
		
		return list;
	}
}