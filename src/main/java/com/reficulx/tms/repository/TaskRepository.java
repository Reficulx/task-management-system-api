package com.reficulx.tms.repository;

import com.reficulx.tms.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
  List<Task> findTasksByTitleContaining(String title);

  List<Task> findTasksByUsername(String username);

  List<Task> findTasksByUsernameAndTitle(String username, String title);

  Boolean existsByUsernameAndTitle(String username, String title);

  void deleteTasksByUsername(String username);

  void deleteTasksByTitle(String title);

  void deleteTasksByUsernameAndTitle(String username, String title);

}
