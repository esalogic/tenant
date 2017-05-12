package entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_roles")
public class User_roles implements Serializable {
	private static final long serialVersionUID = -6789000881844490448L;
	@Id
	private String username;
	private String role_name;

	public User_roles() {
	}
	
	public User_roles(String username, String role_name) {
		setUsername(username);
		setRole_name(role_name);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

}
