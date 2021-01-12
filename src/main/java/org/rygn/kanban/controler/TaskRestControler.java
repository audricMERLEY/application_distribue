package org.rygn.kanban.controler;

import org.rygn.kanban.domain.Task;
import org.rygn.kanban.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
public class TaskRestControler {
    @Autowired
    private TaskService taskService;

    private String createErrorMsg(Set<ConstraintViolation<Task>> constraintViolations){
        String error_answers = "";
        if (constraintViolations.size() > 0 ) {
            error_answers = "Error because of :\n";
            for(ConstraintViolation<Task> constraintViolation : constraintViolations){
                error_answers = error_answers + "\n"+constraintViolation.getRootBeanClass().getSimpleName()+
                        "." + constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage();
            }
        }
        return error_answers;
    }

    @GetMapping(value = "/tasks",produces = "application/json")
    public Collection<Task> tasks(){
        return this.taskService.findAllTasks();
    }

    @PostMapping(value = "/task")
    public ResponseEntity<String> addTask(@RequestBody Task task){
        task = this.taskService.createTask(task);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Task>> constraintViolations = validator.validate(task);

        if (constraintViolations.size() > 0 ) {
            String error_answers = this.createErrorMsg(constraintViolations);
            return new ResponseEntity<String>(error_answers, HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<String>("Task created", HttpStatus.CREATED);
        }
    }

    @PatchMapping(value = "/moveTask")
    public ResponseEntity modifyTask(@RequestParam(name = "id") Long id,@RequestParam(name="toRight") Boolean toRight){
        Task task = this.taskService.findTask(id);
        if(task != null){
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            if(toRight){
                task = this.taskService.moveRightTask(task);
            }else {
                this.taskService.moveLeftTask(task);
            }

            Set<ConstraintViolation<Task>> constraintViolations =validator.validate(task);
            if (constraintViolations.size() > 0 ) {
                return new ResponseEntity(this.createErrorMsg(constraintViolations),HttpStatus.BAD_REQUEST);
            }
            return  new ResponseEntity("Update Successfully!", HttpStatus.OK);
        }
        return new ResponseEntity("Task id not found!", HttpStatus.NOT_FOUND);
    }
}
