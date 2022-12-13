package com.learning.springmultithreading.repo;

import com.learning.springmultithreading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
