package com.reficulx.tms.services;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;

import java.util.List;

public interface TaskService {

  Task createTask(TaskRequest taskReqest) throws Exception;

  List<Task> getAllTasks() throws Exception;

  void deleteTasks(String username, String title) throws Exception;

  List<Task> getTasksByTitleContaining(String title) throws Exception;

  List<Task> getTasksByUsername(String username) throws Exception;

  Task getTaskById(String id) throws Exception;

  Task updateTask(String id, Task task) throws Exception;


}
