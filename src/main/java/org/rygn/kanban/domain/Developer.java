package org.rygn.kanban.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Developer {

	private @Id @GeneratedValue Long id;

	@NotNull(message = "Firstname cannot be null.")
	@NotEmpty(message = "Firstname cannot be empty.")
	private String firstname;
	@NotNull(message = "Lastname cannot be null.")
	@NotEmpty(message = "Lastname cannot be empty.")
	private String lastname;
	
	private String email;
	@NotNull(message = "Password cannot be null.")
	@NotEmpty(message = "Password cannot be empty.")
	private String password;
	
	private LocalDate startContract;
	 
	@ManyToMany(mappedBy="developers", fetch=FetchType.EAGER)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
    private Set<Task> tasks;
	
	public Developer() {
		
		this.tasks = new HashSet<>();
	}
}
