package com.reficulx.tms.controllers;


import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.User;
import com.reficulx.tms.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/all")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<User>> getUsers() {
    try {
      return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/role={role}")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<User>> getUsers(@PathVariable("role") String role) {
    ERole eRole;
    switch (role.toUpperCase()) {
      case "ADMIN":
        eRole = ERole.ROLE_ADMIN;
        break;
      case "MOD":
        eRole = ERole.ROLE_MODERATOR;
        break;
      default:
        eRole = ERole.ROLE_USER;
    }
    try {
      return new ResponseEntity<>(userService.getUsers(eRole), HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/delete/id={id}")
  @PreAuthorize(value = "hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
    try {
      userService.deleteUser(id);
      return new ResponseEntity<>("User deletion succeeds!", HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/update")
  @PreAuthorize(value = "hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<User> updateUser(@RequestBody User user) {
    try {
      return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
