package com.smart.dto;

import javax.validation.constraints.NotNull;


public class OTPDto {

	@NotNull
	private Integer otp;
	
	private String username;

	public Integer getOtp() {
		return otp;
	}

	public void setOtp(Integer otp) {
		this.otp = otp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "OTPDto [otp=" + otp + ", username=" + username + "]";
	}
	

}
