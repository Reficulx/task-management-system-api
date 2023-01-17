package com.reficulx.tms.services;

import com.reficulx.tms.models.ETaskStatus;
import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

  private TaskRepository taskRepository;
  private SimpleDateFormat dateFormatter;

  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    this.dateFormatter = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss", Locale.ENGLISH);
    dateFormatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  }

  @Override
  public Task createTask(TaskRequest taskRequest) throws Exception {
    if (taskRepository.existsByUsernameAndTitle(taskRequest.getUsername(), taskRequest.getTitle())) {
      throw new IllegalArgumentException("Error: Task already exists!");
    }
    if (!(Objects.isNull(taskRequest.getStatus()) || taskRequest.getStatus().equals("unpublished"))) {
      throw new IllegalArgumentException("Error: Illegal task status argument from client when creating a task!");
    }
    // convert corresponding string inputs to Date object
    // 1. creationTime and startTime are set to current time if not specified
    // 2. deadline is set to one day after the start time if not specified
    Date creationTime = Objects.isNull(taskRequest.getCreationTime()) ? new Date() : dateFormatter.parse(taskRequest.getCreationTime());
    Date startTime = Objects.isNull(taskRequest.getStartTime()) ? new Date() : dateFormatter.parse(taskRequest.getStartTime());
    Date deadline = Objects.isNull(taskRequest.getDeadline()) ? new Date(startTime.getTime() + (1000 * 60 * 60 * 24)) : dateFormatter.parse(taskRequest.getCreationTime());

    ETaskStatus status = getEnumStatus(taskRequest.getStatus());
    return taskRepository.save(new Task(taskRequest.getUsername(), taskRequest.getTitle(), taskRequest.getDescription(),
            creationTime, startTime, deadline, status));
  }

  @Override
  public List<Task> getAllTasks() {
    return null;
  }

  @Override
  public boolean deleteTask() {
    return false;
  }

  @Override
  public List<Task> getTasksByTitleContaining(String title) {
    return null;
  }

  @Override
  public List<Task> getTasksByUsername(String username) {
    return null;
  }

  @Override
  public Task getTaskById(String id) {
    return null;
  }

  @Override
  public Task updateTask(String id, Task task) {
    return null;
  }

  private ETaskStatus getEnumStatus(String status) {
    if (Objects.isNull(status)) {
      return ETaskStatus.UNPUBLISHED;
    }
    switch (status) {
      case "unpublished":
        return ETaskStatus.UNPUBLISHED;
      case "inprogress":
        return ETaskStatus.INPROGRESS;
      case "overdue":
        return ETaskStatus.OVERDUE;
      case "completed":
        return ETaskStatus.COMPLETED;
      case "archived":
        return ETaskStatus.ARCHIVED;
      default:
        return ETaskStatus.UNPUBLISHED;
    }
  }

}
