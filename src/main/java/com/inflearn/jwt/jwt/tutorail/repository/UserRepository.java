package com.inflearn.jwt.jwt.tutorail.repository;

import com.inflearn.jwt.jwt.tutorail.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /*
    * username을 기준으로 User정보를 가져올때 권한정보(Authorities)도 같이 가져오게 됨
    * @EntityGraph은 쿼리가 수행이 될때 Lazy조회가 아니고 Eager조회로 authorities정보를
    *  같이 가져오게 된다.*/
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
