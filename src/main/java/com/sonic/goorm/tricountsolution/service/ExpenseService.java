package com.sonic.goorm.tricountsolution.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonic.goorm.tricountsolution.dto.ExpenseRequest;
import com.sonic.goorm.tricountsolution.dto.ExpenseResult;
import com.sonic.goorm.tricountsolution.model.Expense;
import com.sonic.goorm.tricountsolution.model.Member;
import com.sonic.goorm.tricountsolution.model.Settlement;
import com.sonic.goorm.tricountsolution.repository.ExpenseRepository;
import com.sonic.goorm.tricountsolution.repository.MemberRepository;
import com.sonic.goorm.tricountsolution.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
  private final ExpenseRepository expenseRepository;
  private final MemberRepository memberRepository;
  private final SettlementRepository settlementRepository;

  @Transactional
  public ExpenseResult addExpense(ExpenseRequest expenseRequest) {
    // 예외 처리
    Optional<Member> payer = memberRepository.findById(expenseRequest.getPayerMemberId());
    if(!payer.isPresent()) {
      throw new RuntimeException("INVALID MEMBER ID! (Payer)");
    }

    Optional<Settlement> settlement = settlementRepository.findById(expenseRequest.getSettlementId());
    if(!settlement.isPresent()) {
      throw new RuntimeException("INVALID SETTLEMNET ID");
    }

    Expense expense = Expense.builder()
      .name(expenseRequest.getName())
      .settlementId(expenseRequest.getSettlementId())
      .payerMemberId(expenseRequest.getPayerMemberId())
      .amount(expenseRequest.getAmount())
      .expenseDateTime(Objects.nonNull(expenseRequest.getExpenseDateTime()) ? expenseRequest.getExpenseDateTime() : LocalDateTime.now() )
      .build();

    expenseRepository.save(expense);

    return new ExpenseResult(expense, payer.get());

  }

}
