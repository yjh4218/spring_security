package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

/************************
 * 
 * @author ji_hyo
 * SecurityConfig Class는 Spring Security의 필터 경로를 설정함
 * 이로 인해 권한이 있는 사람과 권한이 없는 사람들의 경로를 설정함
 * 현재 권한이 없는 사람이 접근할 경우 로그인 페이지로 접근하도록 함
 * 그 이외의 페이지는 접근 가능하게 함
 */


@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) 
//securedEnabled은 secured 어노테이션 활성화
//prePostEnabled은 @PreAuthorize, postAuthorize 활성화
//seured은 단일 권한 부여 가능 어노테이션임, PreAuthorize은 다중 권한 부여 어노테이션임 -> 메서드 하나에만 사용할 때 유용
//글로벌하게 메서드들에 권한을 부여한다면 아래처럼 글로벌하게 부여
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IOC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //user로 들어오면 인증 진행
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") //manager로 들어오면 관리자나 매니저 권한이 있는 사람만 들어오게 함
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") //manager로 들어오면 관리자나 매니저 권한이 있는 사람만 들어오게 함
			.anyRequest().permitAll() // 그 이외의 주소는 권한 허용함.
			.and()
			.formLogin()
			.loginPage("/loginForm") //formLogin이 있기 때문에 인증이 필요한 경우 무조건 loginForm으로 이동함
			.loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행함. 이럴 경우 controller에 login을 만들지 않아도 됨
			.defaultSuccessUrl("/") //로그인 후 메인 화면으로 이동
			.and()
			.oauth2Login() //oauth를 이용해 로그인 진행(구글, 페이스북)
			.loginPage("/loginForm") // 구글로그인이 완료된 뒤의 후처리 필요. principalOauth2UserService에서 후처리 진행
		//-> 1.코드받기(인증), 2.엑세스토큰 받기(권한받기), 3.사용자 프로필 정보를 가져오고, 4. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.(이메일, 전화번호, 이름, 아이디 등의 기본정보)
		//또는 이메일, 전화번호, 이름, 아이디가 필요한 쇼핑몰, 백화점몰, vip등급 등 추가적인 정보가 필요한 경우 화면을 별도로 구성
		// 구글 로그인이 완료되면 코드가 아닌 엑세스토큰+사용자 프로필정보를 한 번에 받음
			.userInfoEndpoint()
			.userService(principalOauth2UserService); //oAuth2 타입의 서비스를 넣어야 함.
	}
}
