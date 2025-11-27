package com.smartagri.alert.repository;

import com.smartagri.alert.model.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    List<AlertHistory> findByAlertIdOrderByActionTimeDesc(Long alertId);

    List<AlertHistory> findByPerformedBy(String performedBy);
}