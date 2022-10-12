package com.mega.book.springboot.config.auth;

import com.mega.book.springboot.config.auth.dto.OAuthAttributes;
import com.mega.book.springboot.config.auth.dto.SessionUser;
import com.mega.book.springboot.domain.user.User;
import com.mega.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    //웹에서 따오는 거라 httpSession 필수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        //기본 속성
        //기본 유저서비스에 대한 객체를 delegate에 담음
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        //구글로그인시 이메일정보를 구글에 전달
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        //구글에 저장된 유저의 id 따오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //구글에 저장된 유저의 이름 따오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();


        //등록된 유저의 아이디 이름 속성들 담기
        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        //유저엔티티에 정보 저장
        User user = saveOrUpdate(attributes);

        //session에 값 담아서 넘김(view에서 사용 가능) 성능이슈 때문에 사용
        httpSession.setAttribute("user", new SessionUser(user));

        //싱글톤 객체 하나 만들어서 스프링에서 관리할 수 있게 리턴함(로그인시 스프링에 전달)
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        //이메일로 db에 저장된 유저 객체 찾기
        User user = userRepository.findByEmail(attributes.getEmail())
                //user 객체가 있으면 업데이트
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                //user 객체가 없으면 새로 만들어 저장
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }

}
