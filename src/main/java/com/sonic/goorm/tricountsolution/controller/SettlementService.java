package com.sonic.goorm.tricountsolution.controller;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sonic.goorm.tricountsolution.dto.BalanceResult;
import com.sonic.goorm.tricountsolution.dto.ExpenseResult;
import com.sonic.goorm.tricountsolution.model.Member;
import com.sonic.goorm.tricountsolution.model.Settlement;
import com.sonic.goorm.tricountsolution.repository.ExpenseRepository;
import com.sonic.goorm.tricountsolution.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {
  private final SettlementRepository settlementRepository;
  private final ExpenseRepository expenseRepository;

  @Transactional
  public Settlement createAndJoinSettlement(String settlementName, Member member) {
    Settlement settlement = settlementRepository.create(settlementName);
    settlementRepository.addParticipantToSettlement(settlement.getId(), member.getId());
    settlement.getParticipants().add(member);
    return settlement;
  }

  public void joinSettlement(Long settlementId, Long memberId) {
    //TODO 없는 아이디, 정산id 요청했을 경우 예외처리
    settlementRepository.addParticipantToSettlement(settlementId, memberId);
  }

  public List<BalanceResult> getBalanceResult(Long settlementId) {


    //1. 지출정보를 가져와서 지출한 사람 별로 grouping
    Map<Member, List<ExpenseResult>> collected = expenseRepository.findExpensesWithMemberBySettlementId(settlementId)
      .stream()
      .collect(groupingBy(ExpenseResult::getPayerMember));

    if(CollectionUtils.isEmpty(collected)) {
      throw new RuntimeException("정산 할 정보가 없습니다.");
    }

    //2. 위를 바탕으로 각 멤버별로 얼만큼 지출했는지 총합 나타내는 맵 구현

    Map<Member, BigDecimal> memberAmountSumMap = collected.entrySet().stream()
      .collect(toMap(Map.Entry::getKey, memberListEntry ->
        memberListEntry.getValue().stream()
          .map(ExpenseResult::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
      ));

    //3. 정산 그룹의 총 지출을 구해야함
    //소숫점 처리는 정책에 맞김. 현재는 구현 편의를 위해 버림처리 합니다.
    BigDecimal sumAmount = memberAmountSumMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    //4. 정산그룹 총 지출에 대한 평균을 구함
    BigDecimal averageAmount = sumAmount.divide(BigDecimal.valueOf(memberAmountSumMap.size()), RoundingMode.DOWN);

    //5. 각 멤버별로 지출한 돈에서 총 지출 평균값을 뺀다. (각 멤버 지출 값 - 총지출 평균값)
    //받을돈 줄 돈 계산해서 나누기
    Map<Member, BigDecimal> calculatedAmountMap = memberAmountSumMap.entrySet().stream()
      .collect(toMap(Map.Entry::getKey, memberBigDecimalEntry ->
        memberBigDecimalEntry.getValue().subtract(averageAmount)));

    //6. 5번의 계산값에서 양수가 나오면, 평균값 대비 많이 지출한것이라 -> 받아야하는 사람 -> receiver
    List<Map.Entry<Member, BigDecimal>> receiver = calculatedAmountMap.entrySet().stream()
      .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() > 0)
      .sorted((o1, o2) -> o2.getValue().subtract(o1.getValue()).signum())
      .collect(toList());

    //7. 5번의 계산값에서 음수가 나오면, 평균값 대비 적게 지출한것이라 -> 내야하는 사람 -> sender
    List<Map.Entry<Member, BigDecimal>> sender = calculatedAmountMap.entrySet().stream()
      .filter(memberBigDecimalEntry -> memberBigDecimalEntry.getValue().signum() < 0)
      .sorted((o1, o2) -> o1.getValue().subtract(o2.getValue()).signum())
      .collect(toList());


    //8. 돈을 내야하는 사람과 받아야하는 사람이 들어있는 데이터구조에서 반복문을 돌면서 result에 넣는다.
    List<BalanceResult> balanceResults = new ArrayList<>();
    int receiverIndex = 0;
    int senderIndex = 0;
    while (receiverIndex < receiver.size() && senderIndex < sender.size()) {
      BigDecimal amountToTransfer = receiver.get(receiverIndex).getValue()
        .add(sender.get(senderIndex).getValue());

      if(amountToTransfer.signum() < 0) {
        balanceResults.add(new BalanceResult(
          sender.get(senderIndex).getKey().getId(),
          sender.get(senderIndex).getKey().getName(),
          receiver.get(receiverIndex).getValue().abs(),
          receiver.get(receiverIndex).getKey().getId(),
          receiver.get(receiverIndex).getKey().getName()
        ));
        receiver.get(receiverIndex).setValue(BigDecimal.ZERO);
        sender.get(senderIndex).setValue(amountToTransfer);
        receiverIndex++;
      } else if(amountToTransfer.signum() > 0) {
        balanceResults.add(new BalanceResult(
          sender.get(senderIndex).getKey().getId(),
          sender.get(senderIndex).getKey().getName(),
          sender.get(senderIndex).getValue().abs(),
          receiver.get(receiverIndex).getKey().getId(),
          receiver.get(receiverIndex).getKey().getName()
        ));
        receiver.get(receiverIndex).setValue(amountToTransfer);
        sender.get(senderIndex).setValue(BigDecimal.ZERO);
        senderIndex++;
      } else {//평균값만큼 낸 경우
        balanceResults.add(new BalanceResult(
          sender.get(senderIndex).getKey().getId(),
          sender.get(senderIndex).getKey().getName(),
          sender.get(senderIndex).getValue().abs(),
          receiver.get(receiverIndex).getKey().getId(),
          receiver.get(receiverIndex).getKey().getName()
        ));
        receiver.get(receiverIndex).setValue(BigDecimal.ZERO);
        sender.get(senderIndex).setValue(BigDecimal.ZERO);
        receiverIndex++;
        senderIndex++;
      }
    }

    return balanceResults;
  }
}
