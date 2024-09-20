package com.sonic.goorm.tricountsolution.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.goorm.tricountsolution.dto.BalanceResult;
import com.sonic.goorm.tricountsolution.model.Settlement;
import com.sonic.goorm.tricountsolution.util.MemberContext;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SettlementController {
  private final SettlementService settlementService;

  @PostMapping("/settles/create")
  public ResponseEntity<Settlement> createSettlement(@RequestParam String settlementName) {
    return new ResponseEntity<>(settlementService.createAndJoinSettlement(settlementName, MemberContext.getCurrentMember()), HttpStatus.OK);
  }

  @PostMapping("/settles/{id}/join")
  public ResponseEntity<Void> joinSettlement(@PathVariable("id") Long settlementId) {
    settlementService.joinSettlement(settlementId, MemberContext.getCurrentMember().getId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/settles/{id}/balance")
  public ResponseEntity<List<BalanceResult>> getSettlementBalanceResult(@PathVariable("id") Long settlementId) {
    return new ResponseEntity<>(settlementService.getBalanceResult(settlementId), HttpStatus.OK);
  }


}
