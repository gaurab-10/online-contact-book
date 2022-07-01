package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smart.entity.Contact;
import com.smart.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	// @Query("Select c from Contact c where c.user.id=?1")
	// List<Contact> findAllWithUserId(Long id);

	@Query("Select c from Contact c where c.user=?1")
	Page<Contact> findAllByUser(User user, Pageable pageable);

	// This method returns the contact details containing the given pattern and
	// containing the specific user
	// (i.e. user must be logged user so this method doesn't the load the contact
	// which doesn't belong to the particular user).
	// like findByNameContaining(Pattern)AndUser(givnUser)
	// @Query("SELECT m FROM Movie m WHERE m.title LIKE %:title%")
	List<Contact> findByNameContainingAndUser(String pattern, User user);
}
