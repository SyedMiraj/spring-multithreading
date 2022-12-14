package com.learning.springmultithreading.controller;

import com.learning.springmultithreading.entity.User;
import com.learning.springmultithreading.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveUsers(@RequestParam(name = "file") MultipartFile files){
        service.saveUsers(files);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<User>>> retrieveUsers(){
        CompletableFuture<List<User>> userSet1 = service.retrieveUsers();
        return userSet1.thenApply(ResponseEntity::ok);
    }
}





