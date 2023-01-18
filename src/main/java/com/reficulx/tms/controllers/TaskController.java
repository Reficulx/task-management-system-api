package com.reficulx.tms.controllers;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.services.TaskService;
import com.reficulx.tms.utils.Utils;
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

  @GetMapping("/username={username}&title={title}")
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
    if (!isOperationAllowed(taskRequest.getUsername())) {
      // NOT_ACCEPTABLE response for operation access/authority issues
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
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
    if (!isOperationAllowed(updatedTask.getUsername())) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
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

  @PostMapping("/update/username={username}&title={title}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Task> updateTaskByUsernameAndTitle(@PathVariable("username") String username, @PathVariable("title") String title, @RequestBody Task updatedTask) {
    if (!areUsernameAndTitleValid(username, title)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (isOperationAllowed(username)) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    try {
      Task task = taskService.updateTask(username, title, updatedTask);
      if (!Objects.isNull(task)) {
        return new ResponseEntity<>(task, HttpStatus.OK);
      } else {
        logger.error("Error: task to be updated is not found!");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/delete/username={username}&title={title}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> deleteTasksByUsernameAndTitle(@PathVariable("username") String username, @PathVariable("title") String title) {
    if (!areUsernameAndTitleValid(username, title)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (isOperationAllowed(username)) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    return deleteByUsernameAndTitle(username, title);
  }

  @DeleteMapping("/delete/id={id}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Task> deleteTaskById(@PathVariable("id") String id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    try {
      Task task = taskService.deleteTask(id, authentication.getName(), hasUserAccessOnly(authentication));
      if (!Objects.isNull(task)) {
        return new ResponseEntity<>(task, HttpStatus.OK);
      } else {
        logger.error("Error: The task to be deleted is not found!");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
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

  /**
   * only ADMIN/MODERATOR roles could create/modify/delete other users' tasks
   *
   * @param username the owner of the task on which the request is operated
   * @return whether the user of the request has the access to do the operation
   */
  private boolean isOperationAllowed(String username) {
    // obtain the user information through the authentication context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (hasUserAccessOnly(authentication) && !Objects.equals(authentication.getName(), username)) {
      logger.error("Error: user group members do not have access to modify other users' tasks!");
      return false;
    }
    return true;
  }

  private boolean areUsernameAndTitleValid(String username, String title) {
    boolean hasUsername = Utils.isValidString(username);
    boolean hasTitle = Utils.isValidString(title);
    if (!(hasUsername && hasTitle)) {
      logger.error("Error: both username and title are required for the requested operation!");
      return false;
    }
    return true;
  }

  private boolean hasUserAccessOnly(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    return (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || authorities.contains(new SimpleGrantedAuthority("ROLE_MODERATOR")));
  }

}
