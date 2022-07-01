package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smart.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("Select u from User u where u.username =?1")
	User findByUsername(String username);


}
//``