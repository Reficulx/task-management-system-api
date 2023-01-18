package com.reficulx.tms.services;

import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.User;

import java.util.List;

public interface UserService {
  User createUser(User user) throws Exception;

  List<User> getUsers() throws Exception;

  List<User> getUsers(ERole role) throws Exception;

  void deleteUser(String id) throws Exception;

  User updateUser(User user) throws Exception;

}
