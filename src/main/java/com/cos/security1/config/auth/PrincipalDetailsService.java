package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

/***********
 * 
 * @author ji_hyo
 *
 * 시큐리티 설정에서 SecurityConfig의 loginProcessingUrl("/login")으로 걸어굼
 *  /login 요청이 오면 자동으로 UserDetailsService 타입으로 IOC 되어 있는 loadUserByUsername 함수가 실행(규칙)
 *  @Service를 하면 자동으로 PrincipalDetailsServicedl IoC에 등록됨
 *  로그인을 진행할 경우 아이디를 찾으면 UserDetails로 찾은 값을 반환함.
 *  이 때 PrincipalDetails 생성자를 통해 찾은 값을 저장 후 세션으로 등록함
 */

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	
	// 시큐리티 Session = Authtication = UserDetails
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userEntity = userRepository.findByUsername(username);
		if(userEntity != null) {
			return new PrincipalDetails(userEntity);
			// 리턴하게 되면 Authentication 내부에 들어가고, 시큐리티 session 내부에 Authentication 내부에 들어감.
		}
		return null;
	}

}
