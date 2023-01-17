package com.reficulx.tms.controllers;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
  TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/role=user&username={username}&title={title}")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<Task>> getTasks(@PathVariable("username") String username, @PathVariable("title") String title) {
    try {
      List<Task> tasks = taskService.getTasks(username, title);
      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/create")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
    try {
      Task task = taskService.createTask(taskRequest);
      return new ResponseEntity<>(task, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/update/id={id}")
  @PreAuthorize("hasRole('USER') and not hasRole('MODERATOR') and not hasRole('ADMIN')")
  public ResponseEntity<Task> updateTask(@PathVariable("id") String id, @RequestBody Task updatedTask) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (hasUserAccessOnly(authentication) && !authentication.getName().equals(updatedTask.getUsername())) {
      logger.error("Error: user does not have access to modify other users' tasks!");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      Task task = taskService.updateTask(id, updatedTask);
      if (!Objects.isNull(task)) {
        return new ResponseEntity<>(task, HttpStatus.OK);
      } else {
        logger.error("Error: The task to be updated is not found!");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/delete/role=user&username={username}&title={title}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> deleteTasks(@PathVariable("username") String username, @PathVariable("title") String title) {
    // obtain the user information through the authentication
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // only admin/moderator could delete others' tasks
    boolean hasUsername = !Objects.isNull(username) && (username.length() > 0);
    boolean hasTitle = !Objects.isNull(title) && (title.length() > 0);
    if (!(hasUsername && hasTitle)) {
      String message = "Error: username and title are required to delete a task!";
      logger.error(message);
      return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    if (!authentication.getName().equals(username)) {
      String message = "Error: users could not delete others' tasks or wrong username!";
      logger.error(message);
      return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    return deleteByUsernameAndTitle(username, title);
  }

  @DeleteMapping("/delete/username={username}&title={title}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> adminDeleteTasks(@PathVariable("username") String username, @PathVariable("title") String title) {
    return deleteByUsernameAndTitle(username, title);
  }

  private ResponseEntity<String> deleteByUsernameAndTitle(String username, String title) {
    try {
      taskService.deleteTasks(username, title);
      return new ResponseEntity<>("Task is successfully deleted!", HttpStatus.OK);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private boolean hasUserAccessOnly(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    return (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authorities.contains(new SimpleGrantedAuthority("ROLE_MODERATOR")));
  }
}
