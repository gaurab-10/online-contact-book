package com.smart.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USER")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	@Size(min = 2, max = 20)
	private String name;
	
	@Column(unique = true)
	@Email
	@NotBlank
	private String username;
	
	@NotBlank
	@Size(min = 6)
	private String password;
	
	private String role;
	
	private boolean enabled;
	
	private String imageUrl;
	
	@Column(length = 500)
	@Size(max = 100)
	private String about;

	@OneToMany(mappedBy = "user")
	private List<Contact> contact = new ArrayList<Contact>();
		
	@OneToOne(mappedBy="user")
	private OTP otp;

	public Long getId() {
		return id;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub`
	}

	public User( String name, String username, String password, String role, boolean enabled, String imageUrl,
			String about) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
		this.about = about;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Contact> getContacts() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact.add(contact);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", username=" + username + ", password=" + password + ", role="
				+ role + ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + "]";
	}

}
