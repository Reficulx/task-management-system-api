package com.reficulx.tms.controllers;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.services.TaskService;
import com.reficulx.tms.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/create")
  @PreAuthorize(value = "hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> createTask(@Valid @RequestBody TaskRequest taskRequest) {
    try {
      Task task = taskService.createTask(taskRequest);
      return new ResponseEntity<>(task.toString(), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
      return new ResponseEntity<>("Error: username and title are required to delete a task!", HttpStatus.BAD_REQUEST);
    }
    if (!authentication.getName().equals(username)) {
      return new ResponseEntity<>("Error: users could not delete others' tasks or wrong username!", HttpStatus.BAD_REQUEST);
    }
    return deleteByUsernameAndTitle(username, title);
  }

  @DeleteMapping("/delete/username={username}&title={title}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> adminDeleteTasks(@PathVariable("username") String username, @PathVariable("title") String title) {
    System.out.println("Here!");
    return deleteByUsernameAndTitle(username, title);
  }

  private ResponseEntity<String> deleteByUsernameAndTitle(String username, String title) {
    try {
      taskService.deleteTasks(username, title);
      return new ResponseEntity<>("Task is successfully deleted!", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }


}
