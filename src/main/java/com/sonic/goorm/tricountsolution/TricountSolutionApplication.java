package com.sonic.goorm.tricountsolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TricountSolutionApplication {

  public static void main(String[] args) {
    SpringApplication.run(TricountSolutionApplication.class, args);
  }

  //핵심
  //1. 기획 -> API 명세대로 직접 어느정도 기능이 들어간 어플리케이션 구현
  //2. 스프링 시큐리티 없이 로그인 기능 (쿠키 사용) 구현해보기
  //3. jdbc template 사용해서 jpa같은 orm이 아니라 mapper를 사용 -> 장단점

}
