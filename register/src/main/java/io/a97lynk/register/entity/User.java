package io.a97lynk.register.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(
		name = "user",
		uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	@Column(name = "enabled")
	private boolean enabled = false;

	//	@ManyToMany
//	@JoinColumn(columnDefinition = "", referencedColumnName = "")
	private String roles = "";

	public List<String> getRoles() {
		return Arrays.asList(roles.split("\\|"));
	}

	public void setRoles(List<String> roles) {
		this.roles = String.join("|", roles);
	}

}



