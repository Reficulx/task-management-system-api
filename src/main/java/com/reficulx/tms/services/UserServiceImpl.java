package com.reficulx.tms.services;

import com.reficulx.tms.models.ERole;
import com.reficulx.tms.models.Role;
import com.reficulx.tms.models.User;
import com.reficulx.tms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User createUser(User user) throws Exception {
    userRepository.save(user);
    return user;
  }

  @Override
  public User getUserByUsername(String username) throws Exception {
    return userRepository.findByUsername(username).orElse(null);
  }

  @Override
  public List<User> getUsers() throws Exception {
    return userRepository.findAll();
  }

  @Override
  public List<User> getUsers(ERole eRole) throws Exception {
    return userRepository.findAll().stream()
            .filter(user -> user.getRoles().stream().map(Role::getName)
                    .collect(Collectors.toList()).contains(eRole))
            .collect(Collectors.toList());
  }

  @Override
  public void deleteUser(String id) throws Exception {
    userRepository.deleteById(id);
  }

  @Override
  public User updateUser(User user) throws Exception {
    Optional<User> data = userRepository.findById(user.getId());
    if (data.isPresent()) {
      User old = data.get();
      old.setUsername(user.getUsername());
      old.setUsername(user.getPassword());
      old.setEmail(user.getEmail());
      old.setRoles(user.getRoles());
      userRepository.save(old);
      return old;
    }
    return null;
  }
}
