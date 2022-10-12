package com.mega.book.springboot.config.auth;

import com.mega.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/","/css/**","/images/**","/js/**","h2-console/**","/profile").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                //로그인 시작 //로그인 성공 이후 사용자 정보 가져오기 //소셜로그인시 유저서비스를 customOAuth2UserService로 진행
                .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
    }

}
