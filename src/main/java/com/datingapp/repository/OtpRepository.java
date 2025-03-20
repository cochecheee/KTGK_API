package com.datingapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datingapp.entity.UserOtp;

@Repository
public interface OtpRepository extends JpaRepository<UserOtp, Integer> {
    Optional<UserOtp> findTopByEmailOrderByExpiryTimeDesc(String email);
}