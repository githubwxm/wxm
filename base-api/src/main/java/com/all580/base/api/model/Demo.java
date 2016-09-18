package com.all580.base.api.model;

import java.io.Serializable;

public class Demo implements Serializable {
	
	public Demo () {}
	
	public Demo (int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	private int id;
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
