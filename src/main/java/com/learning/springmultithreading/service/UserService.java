package com.learning.springmultithreading.service;

import com.learning.springmultithreading.entity.User;
import com.learning.springmultithreading.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Async
    public void saveUsers(MultipartFile files) {
        List<User> users = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(files.getInputStream()))) {
                String line;
                while ((line=br.readLine()) != null) {
                    final String[] data=line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setCountry(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
            } catch(final IOException e) {
            log.error("Failed to parse CSV file {}", e);
        }
        if(!users.isEmpty()){
            log.info("Current Thread: " + Thread.currentThread().getName());
            repository.saveAll(users);
        }
    }

    @Async
    public CompletableFuture<List<User>> retrieveUsers() {
        log.info("Current Thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(repository.findAll());
    }
}
