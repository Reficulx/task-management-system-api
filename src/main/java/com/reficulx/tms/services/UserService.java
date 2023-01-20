package com.reficulx.tms.services;

import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.User;
import com.reficulx.tms.payload.response.UserResponse;

import java.util.List;

public interface UserService {
  User createUser(User user) throws Exception;

  User getUserByUsername(String username) throws Exception;

  List<UserResponse> getUsers() throws Exception;

  List<UserResponse> getUsers(ERole role) throws Exception;

  void deleteUser(String id) throws Exception;

  User updateUser(User user) throws Exception;

}
