package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smart.entity.OTP;
import com.smart.entity.User;

public interface OTPRepository extends JpaRepository<OTP, Long> {

	@Query("Select o from OTP o where o.user=?1") 
	OTP findByUser(User user);

	


}
