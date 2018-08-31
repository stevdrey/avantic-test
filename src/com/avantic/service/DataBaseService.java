package com.avantic.service;

import java.util.List;
import java.util.Optional;

public interface DataBaseService<T> {
	
	public boolean add(T t);
	
	public boolean update(T t);
	
	public <E> boolean remove(E id);
	
	public List<T> findAll();
	
	public <E> Optional<T> findById(E id);
}