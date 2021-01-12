package org.rygn.kanban.controler;

import org.rygn.kanban.domain.Developer;
import org.rygn.kanban.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeveloperRestControler{
    @Autowired
    private DeveloperService developerService;

    @GetMapping(value = "/developers",produces = "application/json")
    public List<Developer> developers(){
        return this.developerService.findAllDevelopers();
    }

}
