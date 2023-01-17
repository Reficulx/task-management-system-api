package com.reficulx.tms.services;

import com.reficulx.tms.models.ETaskStatus;
import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.repository.TaskRepository;
import com.reficulx.tms.utils.Utils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final SimpleDateFormat dateFormatter;

  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    this.dateFormatter = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss", Locale.ENGLISH);
    dateFormatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
  }

  @Override
  public Task createTask(TaskRequest taskRequest) throws Exception {
    if (Objects.isNull(taskRequest.getUsername()) || Objects.isNull(taskRequest.getTitle()) || Objects.isNull(taskRequest.getDescription())) {
      throw new IllegalArgumentException("Error: missing information for username, title, or description!");
    }
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
  public void deleteTasks(String username, String title) throws Exception {
    boolean hasUsername = Utils.isValidString(username);
    boolean hasTitle = Utils.isValidString(title);
    if (!(hasUsername || hasTitle)) {
      throw new IllegalArgumentException("Error: username and title cannot both be null at the same time!");
    }
    if (hasUsername && hasTitle) {
      taskRepository.deleteTasksByUsernameAndTitle(username, title);
    }
    if (hasUsername) {
      taskRepository.deleteTasksByUsername(username);
    }
    taskRepository.deleteTasksByTitle(title);
  }

  @Override
  public List<Task> getTasks(String username, String title) throws Exception {
    boolean hasUsername = Utils.isValidString(username);
    boolean hasTitle = Utils.isValidString(title);

    // TODO: check overdue status for all get task operations
    if (!(hasUsername || hasTitle)) {
      return new ArrayList<>(taskRepository.findAll());
    }
    if (hasUsername && hasTitle) {
      return new ArrayList<>(taskRepository.findTasksByUsernameAndTitle(username, title));
    }
    if (hasUsername) {
      return new ArrayList<>(taskRepository.findTasksByUsername(username));
    }
    return new ArrayList<>(taskRepository.findTasksByTitleContaining(title));
  }

  @Override
  public Task updateTask(String id, Task updatedTask) {
    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isPresent()) {
      // creationTime is not changed
      Task task = taskData.get();
      task.setTitle(updatedTask.getTitle());
      task.setDescription(updatedTask.getDescription());
      task.setStartTime(updatedTask.getStartTime());
      task.setDeadline(updatedTask.getDeadline());
      // completionTime is dependent on the task status
      updateTaskStatus(task, updatedTask);
      return taskRepository.save(task);
    }
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

  private void updateTaskStatus(Task task, Task updatedTask) {
    if (Objects.isNull(updatedTask.getStatus())) {
      updatedTask.setStatus(ETaskStatus.UNPUBLISHED);
    }
    if (updatedTask.getStatus().equals(ETaskStatus.COMPLETED) || updatedTask.getStatus().equals(ETaskStatus.ARCHIVED)) {
      if (Objects.isNull(updatedTask.getCompletionTime())) {
        task.setCompletionTime(new Date());
      } else {
        task.setCompletionTime(updatedTask.getCompletionTime());
      }
    } else {
      task.setCompletionTime(null);
    }
    task.setStatus(updatedTask.getStatus());
  }

}
