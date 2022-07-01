package com.smart.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class OTP {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private Integer OTP;
	
	@CreationTimestamp
	private Date createdDate;
	
	@OneToOne
	private User user;

	
	public OTP() {
		super();
	}


	public OTP(User user, Integer oTP) {
		super();
		this.user = user;
		OTP = oTP;
	}

	
	public Long getId() {
		return id;
	}


	protected void setId(Long id) {
		this.id = id;
	}

	

	public Date getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getOTP() {
		return OTP;
	}

	public void setOTP(Integer oTP) {
		OTP = oTP;
	}


	@Override
	public String toString() {
		return "OTP [id=" + id + ", user=" + user + ", OTP=" + OTP + "]";
	}


}
