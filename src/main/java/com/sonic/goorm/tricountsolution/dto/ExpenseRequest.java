package com.sonic.goorm.tricountsolution.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sonic.goorm.tricountsolution.util.MemberContext;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpenseRequest {
  @NotNull
  private String name;
  @NotNull
  private Long settlementId;
  private Long payerMemberId = MemberContext.getCurrentMember().getId();
  @NotNull
  private BigDecimal amount;
  private LocalDateTime expenseDateTime;
}
