package com.sonic.goorm.tricountsolution.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonic.goorm.tricountsolution.model.Member;
import com.sonic.goorm.tricountsolution.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  //회원가입

  @Transactional
  public Member signup(Member member){
    //중복 회원 체크
    memberRepository.findByLoginId(member.getLoginId())
      .ifPresent((member1) -> {
        throw new RuntimeException("login id duplicated");
      });
    return memberRepository.save(member);
  }

  //로그인
  public Member login(String loginId, String password) {
    Member loginMember = memberRepository.findByLoginId(loginId)
      .filter(m -> m.getPassword().equals(password))
      .orElseThrow(() -> new RuntimeException("Member info is not found!"));
    return loginMember;
  }

  public Member findMemberById(Long memberId) {
    Optional<Member> loginMember = memberRepository.findById(memberId);
    if (!loginMember.isPresent()) {
      throw new RuntimeException("Member info is not found");
    }
    return loginMember.get();
  }


}
