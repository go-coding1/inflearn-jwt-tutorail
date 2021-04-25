package com.inflearn.jwt.jwt.tutorail.config;

import com.inflearn.jwt.jwt.tutorail.jwt.JwtAccessDeniedHandler;
import com.inflearn.jwt.jwt.tutorail.jwt.JwtAuthenticationEntryPoint;
import com.inflearn.jwt.jwt.tutorail.jwt.JwtSecurityConfig;
import com.inflearn.jwt.jwt.tutorail.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
* @EnableWebSecurity 어노테이션은 기본적인 Web보안을 활성화 하겠다는 의미입니다.
* 추가적인 설정을 위해서 WebSecurityConfigurer를 implements하거나
* WebSecurityConfigurerAdapter를 extedns 하는 방법이 있습니다.
*
* .authorizeRequests()는 HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다는 의미이고
* .antMatchers("/api/hello").permitAll() > /api/hello에 대한 요청은 인증없이 접근을 허용하겠다는 의미
* .anyRequest().authenticated(); > 나머지 요청에 대해서는 인증을 받아야 한다는 의미
* */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//@PreAuthorize어노테이션을 메소드 단위로 추가,사용하기 위해서 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //SecurityConfig는 위 3개 값을 주입받음
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ){
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()//토큰을 사용하기 때문에 csrf설정은 disable 해주고

                .exceptionHandling()    //예외처리할때 우리가 만들었던 클래스들로 추가해준다
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()  //h2-console을 위한 설정을 추가
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()  //세연을 사용하지 않기 때문에 세션 설정을 STATELESS로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()   //토큰을 받기위한 로그인 api는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll
                .antMatchers("/api/signup").permitAll() //회원가입API 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll
                .anyRequest().authenticated()

                .and()  //마지막으로 JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig클래스도 적용
                .apply(new JwtSecurityConfig(tokenProvider));


    }
/*
*h2-console하위 모든 요청들과 파비콘 관련 요청은 Spring Security로직을
* 수행하지 않도록 configure 메소드를 오버라이드하여 내용을 추가해줍니다.
* */
    @Override
    public void configure(WebSecurity web){
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }
}
