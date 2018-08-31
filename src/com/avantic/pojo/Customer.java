package com.avantic.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Customer {
	private String id;
	private String name;
	private String lastname;
	private String email;
	private String phone;
	private String comment;
	
	private LocalDate birthday;
	private LocalDateTime createAt;
	
	public Customer(String id, String name, String lastname, LocalDateTime createAt) {
		super();
		
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.email= "";
		this.phone= "";
		this.comment= "";
		this.birthday= null;
		this.createAt = createAt;
	}
	
	public Customer(String id, String name, String lastname) {
		this(id, name, lastname, LocalDateTime.now());
	}

	public Customer() {
		this("", "", "");
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		else if (obj instanceof Customer)
			return ((Customer) obj).id.equals(this.id);
		
		return false;
	}
	
	@Override
	public int hashCode() {
		var hashCode= 11;
		
		hashCode= 33 * hashCode + Objects.hash(this.id);
		
		return hashCode;
	}
}