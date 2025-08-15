package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {

	@Id
	private String username;
	private String password;
	private String email;
	@Enumerated(EnumType.STRING)
	private RoleType role;
	
}
