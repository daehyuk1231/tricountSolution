package com.sonic.goorm.tricountsolution.util;

import com.sonic.goorm.tricountsolution.model.Member;

public class MemberContext {
  private static final ThreadLocal<Member> memberThreadLocal = new ThreadLocal<>();

  public static void setCurrentMember(Member member) {
    memberThreadLocal.set(member);
  }
  public static Member getCurrentMember() {return memberThreadLocal.get();}
  public static void clear() { memberThreadLocal.remove(); }

}
