package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

/*******************
 * 
 * @author ji_hyo
 *
 * 구글로부터 받은 userRequest 데이터에 대한 후처리 함수
 * 구글로부터 전달받은 데이터를 바탕으로 로그인 진행 또는 회원가입 진행함
 * 
 *  PrincipalDetails을 반환하기 위해 사용(PrincipalDetails을 이용해 일반, OAuth 로그인의 세션을 편히 관리하기 위함)
 * 
 * 오버라이딩 한 이유는 1. PrincipalDetails로 반환을 위함
 * 2. 강제 회원가입 진행을 위함
 */

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리 함수
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		System.out.println("getClientRegistration : " + userRequest.getClientRegistration()); // registrationId로 어떤
																								// oAuth로 로그인했는지 확인 가능
		System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());

		OAuth2User oauth2User = super.loadUser(userRequest);

		// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(oAuth-Client 라이브러리) ->
		// AccessToken 요청 = userRequest 정보
		// userRequest 정보 -> loadUser 함수호출 -> 구글로부터 회원 프로필을 받아줌
		System.out.println("getAttributes : " + oauth2User.getAttributes());

		//회원가입 진행
		OAuth2UserInfo oAuth2UserInfo = null;
		
		//업체별로 보내주는 값이 다르기 때문에 인터페이스를 통해서 클레스들 구현
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
			
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
			
		} else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("naver 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map<String, Object>)oauth2User.getAttributes().get("response"));
			
		} else {
			System.out.println("구글과 페이스북만 지원함");
		}

		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider + "_" + providerId;
		String email = oAuth2UserInfo.getEmail();
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String role = "ROLE_USER";
		
		
//		String provider = userRequest.getClientRegistration().getRegistrationId(); //google ID
//		String providerId = oauth2User.getAttribute("sub");//구글일때는 sub, 페이스북은 id
//		String username = provider + "_" + providerId;
//		String email = oauth2User.getAttribute("email");
//		String password = bCryptPasswordEncoder.encode("겟인데어");
//		String role = "ROLE_USER";
		
		//기존에 회원가입 되어 있는지 확인
		User userEntity = userRepository.findByUsername(username);
		
		//없는 회원이면 회원가입 강제 진행
		if(userEntity == null) {
			userEntity = User.builder()
			.username(username)
			.password(password)
			.email(email)
			.role(role)
			.provider(provider)
			.providerId(providerId)
			.build();
			userRepository.save(userEntity);
		}
		
		// 회원가입을 강제로 진행해볼 예정
		// 로그인 진행함. 세션 생성
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
