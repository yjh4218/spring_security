package com.cos.security1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;



@Entity
@Data
public class User {

	@Id //primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role; //ROLE_USER, ROLE_ADMIN
	
	private String provider; //oAuth로 들어온 주소(구글 페이스북)
	private String providerId; // oAuth로 들어온 아이디
	@CreationTimestamp
	private Timestamp creatDate;
}
