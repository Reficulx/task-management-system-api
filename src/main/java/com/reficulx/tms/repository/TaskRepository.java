package com.reficulx.tms.repository;

import com.reficulx.tms.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
  List<Task> findTasksByTitleContaining(String title);

  List<Task> findTasksByUsername(String username);

  Boolean existsByUsernameAndTitle(String username, String title);

}
