package com.shopping.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration  // 나는 설정파일이야
@EnableJpaAuditing  // jpa감사기능할 수 있게끔 설정해놓음
public class AuditCoinfig {

    @Bean // AuditorAwareImpl의 객체를 생성하겠다. 리턴값의 객체를 생성하겠다.
    public AuditorAware<String> auditorProvider(){
        return new AuditorAwareImpl();
    }
}
