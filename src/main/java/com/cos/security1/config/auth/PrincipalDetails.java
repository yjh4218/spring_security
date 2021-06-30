package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.security1.model.User;

import lombok.Data;

/*********
 * 
 * @author ji_hyo
 *
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인을 진행이 완료가 되면 시큐리티 session을 만들어줌.
 * Security ContextHolder 라는 키 값에 세션 정보를 전달함
 * Security Session에 들어갈 수 있는 오브젝트 타입은 Authentication 타입 객체만 들어갈 수 있음
 * Authentication 안에 User 정보가 있어야 함
 * User 오브젝트 타입은 UserDetails 타입 객체로 들어와야 함
 * 
 * Security Session 영역에는 Authentication 객체만 들어갈 수 있으며, Authentication에는 UserDeatails 객체가 들어감
 * 
 * UserDetails에 PrincipalDetails를 넣음으로서 PrincipalDetails을 Authentication에 전달할 수 있음
 */

@Data
public class PrincipalDetails implements UserDetails{

	private User user; //콤포지션
	
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	
	// 해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		//ArrayList는 Collection의 자식임
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				// TODO Auto-generated method stub
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {

		return user.getPassword();
	}

	@Override
	public String getUsername() {

		return user.getUsername();
	}

	//계정 만료 true는 아니오
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	//계정 잠김 true는 아니오
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	//비밀번호 오래 사용 true는 아니오
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	// 계정이 활성화되어 있니 true는 아니오
	@Override
	public boolean isEnabled() {

		// 우리 사이트에서 1년 동안 회원이 로그인을 안하면 휴면 계정으로 하기로 함.
		// User 모델에서 로그인 시간를 넣은 후 해당 날짜를 가져와서
		// 현재시간-로그인시간으로 계산해서 1년 초과하면 return false로 설정
		
		return true;
	}
	
}
