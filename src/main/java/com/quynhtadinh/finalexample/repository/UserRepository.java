package com.quynhtadinh.finalexample.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.quynhtadinh.finalexample.entity.User;



public interface UserRepository extends JpaRepository<User, Long>,PagingAndSortingRepository<User, Long> {
    User findByUsername(String username);

	@Query("SELECT e FROM User e WHERE e.username LIKE %:keyword% ")
	Page<User> findAllByUsername(Optional<String> keyword,Pageable pageable);

}