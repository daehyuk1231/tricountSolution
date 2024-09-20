package com.sonic.goorm.tricountsolution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.goorm.tricountsolution.service.MemberService;
import com.sonic.goorm.tricountsolution.util.MemberContext;
import com.sonic.goorm.tricountsolution.util.TricountApiConst;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
  @Autowired
  private MemberService memberService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws Exception {

    Cookie[] cookies = request.getCookies();

    //쿠키에 사용자 정보가 X -> return false
    if (!this.constainsUserCookie(cookies)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return false;
    }

    //쿠키 사용자 정보 O -> 값을 세팅해줌
    for (Cookie cookie : cookies) {
      if(TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
        try {
          //cookie에서 id(member entity의 pk) 를 꺼내고, DB에서 이 아이디에 해당하는 member 조회
          MemberContext.setCurrentMember(memberService.findMemberById(Long.parseLong(cookie.getValue())));
          break;
        } catch (Exception e) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN, "MEMBER INFO SET ERROR" + e.getMessage());
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
    @Nullable ModelAndView modelAndView) throws Exception {
    MemberContext.clear();
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }


  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
    @Nullable Exception ex) throws Exception {
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

  }


  private boolean constainsUserCookie(Cookie[] cookies){
    if (cookies!=null){
      for (Cookie cookie: cookies){
        if (TricountApiConst.LOGIN_MEMBER_COOKIE.equals(cookie.getName())) {
          return true;
        }
      }
    }
    return false;
  }

}
