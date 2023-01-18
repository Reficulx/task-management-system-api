package com.reficulx.tms.services;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;

import java.util.List;

public interface TaskService {

  Task createTask(TaskRequest taskReqest) throws Exception;

  List<Task> getTasks(String username, String title) throws Exception;

  void deleteTasks(String username, String title) throws Exception;

  Task deleteTask(String id, String username, boolean hasUserAccessOnly) throws Exception;

  Task updateTask(String username, String title, Task updatedTask) throws Exception;

  Task updateTask(String id, Task updatedTask) throws Exception;
}
