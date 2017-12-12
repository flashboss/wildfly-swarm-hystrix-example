package org.wildfly.swarm.payroll.model;

import java.io.Serializable;

public class Employee implements Serializable {

	private static final long serialVersionUID = -3864318158923814437L;

	private Long id;

    private String name;
    
	public Employee() {
		super();
	}

	public Employee(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
