package com.quynhtadinh.finalexample.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.quynhtadinh.finalexample.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long>{
   Role findByName(String name);

}