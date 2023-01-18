package com.reficulx.tms.controllers;


import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.User;
import com.reficulx.tms.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * for bootstrapping the loggedin status of the user
   *
   * @return
   */
  @GetMapping("/me")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<User> getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    try {
      User user = userService.getUserByUsername(authentication.getName());
      if (Objects.isNull(user)) {
        logger.error("Error: the username is not found!");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

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
