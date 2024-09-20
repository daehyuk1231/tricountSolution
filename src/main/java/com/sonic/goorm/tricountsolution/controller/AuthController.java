package com.sonic.goorm.tricountsolution.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.goorm.tricountsolution.dto.LoginRequest;
import com.sonic.goorm.tricountsolution.dto.SignupRequest;
import com.sonic.goorm.tricountsolution.model.Member;
import com.sonic.goorm.tricountsolution.service.MemberService;
import com.sonic.goorm.tricountsolution.util.TricountApiConst;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
  private final MemberService memberService;

  //회원가입
  @PostMapping("/signup")
  public ResponseEntity<Member> signup(@Valid @RequestBody SignupRequest request) {
    Member member = Member.builder()
      .loginId(request.getLoginId())
      .password(request.getPassword())
      .name(request.getName())
      .build();

    return new ResponseEntity<>(memberService.signup(member), HttpStatus.OK);
  }

  //로그인
  @PostMapping("login")
  public ResponseEntity<Void> login(
    @Valid @RequestBody LoginRequest loginRequest,
    HttpServletResponse response
  ){
    Member loginMember = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());

    Cookie idCookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, String.valueOf(loginMember.getId()));
    response.addCookie(idCookie);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  //로그아웃
  @PostMapping("logout")
  public ResponseEntity<Void> logout(
    HttpServletResponse response
  ) {
    Cookie cookie = new Cookie(TricountApiConst.LOGIN_MEMBER_COOKIE, null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
