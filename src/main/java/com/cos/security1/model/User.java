package com.cos.security1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User {

	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role; // ROLE_USER, ROLE_ADMIN

	private String provider; // oAuth로 들어온 주소(구글 페이스북)
	private String providerId; // oAuth로 들어온 아이디
	@CreationTimestamp
	private Timestamp creatDate;

	@Builder
	public User(String username, String password, String email, String role, String provider, String providerId,
			Timestamp creatDate) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.creatDate = creatDate;
	}

}
