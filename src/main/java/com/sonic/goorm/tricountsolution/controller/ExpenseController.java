package com.sonic.goorm.tricountsolution.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.goorm.tricountsolution.dto.ExpenseRequest;
import com.sonic.goorm.tricountsolution.dto.ExpenseResult;
import com.sonic.goorm.tricountsolution.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
  private final ExpenseService expenseService;

  @PostMapping("/expenses/add")
  public ResponseEntity<ExpenseResult> addExpenseToSettlement(
    @Valid @RequestBody ExpenseRequest expenseRequest
  ){
    return new ResponseEntity<>(expenseService.addExpense(expenseRequest), HttpStatus.OK);
  }

}
