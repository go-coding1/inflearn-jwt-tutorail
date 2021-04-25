package com.inflearn.jwt.jwt.tutorail.controller;

import com.inflearn.jwt.jwt.tutorail.dto.LoginDto;
import com.inflearn.jwt.jwt.tutorail.dto.TokenDto;
import com.inflearn.jwt.jwt.tutorail.jwt.JwtFilter;
import com.inflearn.jwt.jwt.tutorail.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    //로그인 api경로, post 요청
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto){
        //username과 password를 이용해서 UsernamePasswordAuthenticationToken을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        //authenticationToken을 이용해서authenticate메소드가 실행될때
        //CostomUserDetailService에서 만들었던 loadUserByUsername메소드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);//받은 객체를 securityContext에 저장

        String jwt = tokenProvider.createToken(authentication); //인증 정보를 이용해서 토큰 생성

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);   //jwt를 Response헤더에 넣고

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK); //new TokenDto(jwt)로 Respponse바디에도 넣어서 리턴
    }
}
