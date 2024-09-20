package com.sonic.goorm.tricountsolution.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.sonic.goorm.tricountsolution.model.Member;
import com.sonic.goorm.tricountsolution.model.Settlement;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SettlementRepository {
  private final JdbcTemplate jdbcTemplate;

  public Optional<Settlement> findById(Long id) {
    List<Settlement> result = jdbcTemplate.query("select * from settlement "
      + "join settlement_participant on settlement.id = settlement_participant.settlement_id "
      + "join member on settlement_participant.member_id = member.id "
      + "where settlement.id = ?", settlementParticipantsRowMapper(), id);
    return result.stream().findAny();
  }

  private RowMapper<Settlement> settlementParticipantsRowMapper() {
    return ((rs, rowNum) -> {
      Settlement settlement = Settlement.builder()
        .id(rs.getLong("settlement.id"))
        .name(rs.getString("settlement.name"))
        .build();

      List<Member> participants = new ArrayList<>();
      do {
        Member participant = new Member(
          rs.getLong("member.id"),
          rs.getString("member.login_id"),
          rs.getString("member.name"),
          rs.getString("member.password")
        );
        participants.add(participant);
      } while (rs.next());

      settlement.setParticipants(participants);
      return settlement;
    });
  }

  public Settlement create(String name) {
    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
      .withTableName("settlement").usingGeneratedKeyColumns("id");

    Map<String, Object> parmas = new HashMap<>();
    parmas.put("name", name);

    Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parmas));

    Settlement settlement = new Settlement();
    settlement.setId(key.longValue());
    settlement.setName(name);

    return settlement;
  }

  public void addParticipantToSettlement(Long settlementId, Long memberId) {
    jdbcTemplate.update("INSERT INTO settlement_participant (settlement_id, member_id) VALUES (?, ?)",
      settlementId, memberId);
  }
}
