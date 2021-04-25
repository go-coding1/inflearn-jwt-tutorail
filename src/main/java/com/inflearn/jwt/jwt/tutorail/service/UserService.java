package com.inflearn.jwt.jwt.tutorail.service;


import com.inflearn.jwt.jwt.tutorail.dto.UserDto;
import com.inflearn.jwt.jwt.tutorail.entity.Authority;
import com.inflearn.jwt.jwt.tutorail.entity.User;
import com.inflearn.jwt.jwt.tutorail.repository.UserRepository;
import com.inflearn.jwt.jwt.tutorail.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

/*회원가입을 위한 클래스*/
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        //여기서 회원가입한 회원은 권한이 ROLE_USER뿐임
        //data.sql에서 만든 유저는 권한이 ROLE_USER, ROLE_ADMIN2개임
        //이 차이를 통해 권한검증 부분을 테스트가능
        //빌더 패턴의 장점
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    //username을 기준으로 username과 권한을 가져옴
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    //현재SecurityContext에 저장된 username의 username과 권한 정보를 가져옴
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}