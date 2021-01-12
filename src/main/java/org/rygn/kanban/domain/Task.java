package org.rygn.kanban.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
public class Task {

	private @Id @GeneratedValue Long id;
	@NotNull(message = "Title cannot be null.")
	@NotEmpty(message = "Title cannot be empty.")
	private String title;
	
	private Integer nbHoursForecast;
	
	private Integer nbHoursReal;
	
	private LocalDate created;
	
	@ManyToOne
	private TaskType type;
	
	@ManyToOne
	private TaskStatus status;	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
    private Set<Developer> developers;
	
	@OneToMany(mappedBy="task", cascade={CascadeType.ALL}, orphanRemoval=true,fetch=FetchType.EAGER)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Set<ChangeLog> changeLogs;
	
	public Task() {
		
		this.developers = new HashSet<>();
		
		this.changeLogs = new HashSet<>();
	}
	
	public void addDeveloper(Developer developer) {
		
		developer.getTasks().add(this);
		
		this.developers.add(developer);
	}
	
	public void addChangeLog(ChangeLog changeLog) {
		
		changeLog.setTask(this);
		
		this.changeLogs.add(changeLog);
	}

	public void clearChangeLogs() {
		
		for (ChangeLog changeLog :  this.changeLogs) {
			
			changeLog.setTask(null);
		}
		
		this.changeLogs.clear();
	}
}
