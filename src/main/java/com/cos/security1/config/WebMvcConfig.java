package com.cos.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**************************
 * 
 * 
 * @author ji_hyo
 *
 * WebMvcConfig class는 MustacheViewResolver의 경로, 확장자등을 설정하여, 자동으로 view를 연결할 수 있게 함
 */


@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	// 해당 뷰를 머스테치로 재설정. encoder와 기본적으로 던지는 파일의 확장자(html), 경로 설정함 
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		// TODO Auto-generated method stub
		MustacheViewResolver resolver = new MustacheViewResolver();
		resolver.setCharset("UTF-8");
		resolver.setContentType("text/html; charset=UTF-8");
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		
		registry.viewResolver(resolver);
	}
}
