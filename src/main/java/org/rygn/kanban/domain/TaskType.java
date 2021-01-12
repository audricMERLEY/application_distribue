package org.rygn.kanban.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class TaskType {

	private @Id Long id;
	@NotNull(message = "Label cannot be null.")
	@NotEmpty(message = "Label cannot be empty.")
	private String label;
	
	public TaskType(Long id, String label) {
		this.id = id;
		this.label = label;
	}
	
	public TaskType() {
	}
}
