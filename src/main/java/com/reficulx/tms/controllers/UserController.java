package com.reficulx.tms.controllers;


import com.reficulx.tms.services.UserService;

public class UserController {

  UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }


}
