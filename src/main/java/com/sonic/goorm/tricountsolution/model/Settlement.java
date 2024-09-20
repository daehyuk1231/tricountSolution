package com.sonic.goorm.tricountsolution.model;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 정산은 여러개의 지출을 가지고 있다 (1:N) */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Settlement {
  private Long id;
  private String name;
  private List<Member> participants = Collections.emptyList();// 특정 정산에 참여한 유저들만 정산 내역을 열람 가능
}
