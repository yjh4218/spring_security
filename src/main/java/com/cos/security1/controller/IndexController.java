package com.cos.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // view를 리턴하겠다.
public class IndexController {

	@GetMapping({"","/"})
	public String index() {
		//머스테치 기본폴더 src/main/resources
		//뷰리졸버 설정 : templates (prefix), mustache (surffix)
		//application.yml에 설정으로 잡혀있음(삭제해도 됨) -> 머스테치를 사용하겠다고 의존성 설정을 하면 생략 가능함
		return "index";
	}
}
