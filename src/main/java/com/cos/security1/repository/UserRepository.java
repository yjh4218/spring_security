package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

// CRUD 함수를 JPARepository가 들고 있음
// @Repository라는 어노테이션이 없어도 IOC가 됨. 이유는 JPARepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer>{

	// findBy 까지는 문법 규칙. Username은 문법
	// select * from user where username = 1? 호출됨
	// 1?에는 username이 들어감
	// JPA Query method
	public User findByUsername(String username);
	
}
