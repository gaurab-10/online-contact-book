package com.smart.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OTPGeneration {

	public Integer generateOTP(int min, int max) {
		Random rand = new Random();
		return rand.nextInt(max - min) + min;
	}
}
