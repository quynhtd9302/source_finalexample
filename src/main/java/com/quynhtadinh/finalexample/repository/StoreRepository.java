package com.quynhtadinh.finalexample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quynhtadinh.finalexample.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByActiveTrue();
}
