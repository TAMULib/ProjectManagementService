/* 
 * ThemePropertyName.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.Id;

/**
 * 
 * 
 * @author 
 *
 */
@Entity
public class ThemePropertyName {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column
	private String name;
	
	public ThemePropertyName() { }
	
	public ThemePropertyName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.getName();
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
