package com.sonic.goorm.tricountsolution.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
  private Long id;
  private String name;
  private Long settlementId; //정산은 여러개의 지출을 가질 수 있다
  private Long payerMemberId; //어떤 유저의 정산인지
  private BigDecimal amount;
  private LocalDateTime expenseDateTime;
}
