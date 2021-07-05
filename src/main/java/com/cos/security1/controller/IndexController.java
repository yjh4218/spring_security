package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;


/***********
 * 
 * @author ji_hyo
 *
 * index main Controller
 * html로부터 주소 요청되면 해당 주소를 낚아채서 view로 리턴해줌
 */


@Controller // view를 리턴하겠다.
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	/************
	 * 
	 * @param authentication
	 * @param userDetails
	 * @return
	 * 
	 * 유저 오브젝트를 찾는 방법은 2가지
	 * 1.Authentication 을 DI(의존성 주입) 후 다운캐스팅 하여 User 오브젝트를 찾을 수 있음
	 * authentication 안에 principal이 있고, principal은 리턴하는 타입이 오브젝트임
	 * 다운캐스팅하여(PrincipalDetails)의 getUser로 하면 User 정보를 받을 수 있음
	 * 
	 * 2.@AuthenticationPrincipal 이라는 어노테이션을 통해 User 오브젝트를 찾을 수 있음
	 * 세션 정보를 받을 수 있음
	 * @AuthenticationPrincipal UserDetails userDetails 
	 * -> @AuthenticationPrincipal PrincipalDetails userDetails 로 해도 됨
	 * UserDetails에 PrincipalDetails가 implement 되어 있기 때문
	 * 
	 * 
	 * 시큐리티 세션에는 Authentication의 객체만 들어갈 수 있음
	 * Authentication에는 UserDetails 타입과 OAuth2User 타입만 들어갈 수 있음
	 * UserDetail은 일반 로그인 OAuth2User는 구글, 페이스북 등의 로그인에 사용
	 * 유저가 어떤 로그인을 할지 모르기 때문에 복잡해짐.
	 * 따라서 PrincipalDetails를 부모로 설정하여 UserDetails와 OAuth2User을 implement 하면 됨(자식) 
	 * 이렇게 되면 언제 어디서나 PrincipalDetails로 로그인하면 문제 없음.
	 */
	@GetMapping("/test/login")
	public @ResponseBody String loginTest(Authentication authentication,
			@AuthenticationPrincipal UserDetails userDetails) { 
		System.out.println("/test/login =====================");
		
		// 1번 방법
		// 원래 UserDetails로 다운캐스팅 해야하나, PrincipalDetails을 UserDetails 를 갖고 있음
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		
		System.out.println("authentication : " + principalDetails.getUser());
		System.out.println("userDetails : " + userDetails.getUsername());
		return "세션정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String loginOauthTest(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { 
		System.out.println("/test/login =====================");
		
		OAuth2User oauthUser = (OAuth2User)authentication.getPrincipal();
		
		System.out.println("authentication : " + oauthUser.getAttributes());
		System.out.println("oAuth2User : " + oauth.getAttributes());
		return "OAuth 세션정보 확인하기";
	}
	
	
	@GetMapping({ "", "/" })
	public String index() {
		// 머스테치 기본폴더 src/main/resources
		// 뷰리졸버 설정 : templates (prefix), mustache (surffix)
		// application.yml에 설정으로 잡혀있음(삭제해도 됨) -> 머스테치를 사용하겠다고 의존성 설정을 하면 생략 가능함
		return "index";
	}

	//OAuth 로그인을 해도 PrincipalDetails로 받을 수 있음
	//일반 로그인을 해도 PrincipalDetails로 받을 수 있음
	//@AuthenticationPrincipal은 
	@GetMapping("/user")
	public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public String manager() {
		return "manager";
	}

	// 스프링시큐리티가 login 주소를 낚아챔 -> SecurityConfig 파일 설정 후 스프링 시큐리티가 작동 안 함
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	// 회원가입 페이지 이동
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	// 회원가입 진행
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user); //회원가입은 되나, 비밀번호가 암호화되지 않았기 때문에 시큐리티로 로그인은 안됨
		
		return "redirect:/loginForm"; // redirect를 붙이면 /loginForm 함수 호출. 위의 loginForm 호출됨
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //함수가 시작하기 전에 동작함
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
}
