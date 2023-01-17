package com.reficulx.tms.controllers;

import com.reficulx.tms.models.Task;
import com.reficulx.tms.payload.request.TaskRequest;
import com.reficulx.tms.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<String> createTask(@Valid @RequestBody TaskRequest taskRequest) {
    try {
      Task task = taskService.createTask(taskRequest);
      return new ResponseEntity<>(task.toString(), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }


}
