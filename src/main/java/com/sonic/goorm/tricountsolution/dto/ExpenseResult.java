package com.sonic.goorm.tricountsolution.dto;

import java.math.BigDecimal;

import com.sonic.goorm.tricountsolution.model.Expense;
import com.sonic.goorm.tricountsolution.model.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpenseResult {
  private Long settlementId;
  private Member payerMember;
  private BigDecimal amount;

  public ExpenseResult(Expense expense, Member member) {
    this.settlementId = expense.getSettlementId();
    this.payerMember = member;
    this.amount = expense.getAmount();
  }

}
