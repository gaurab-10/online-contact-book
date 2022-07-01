package com.smart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String secondName;
	private String work;
	private String email;
	private Long phone;
	private String img;
	@Column(length = 500)
	private String description;

	@ManyToOne
	//If it is not used then there will be circular searialization error i.e. first it will searalize the contact then user
	// again goes to user class to searalize it's property then from that user class it's sees teh contact then again comes to contact class
	// and again goes to user class 
	// which caues circular searialization
	@JsonIgnore
	private User user;
//``````

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

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", email="
				+ email + ", phone=" + phone + ", img=" + img + ", description=" + description + ", user=" + user + "]";
	}


}
