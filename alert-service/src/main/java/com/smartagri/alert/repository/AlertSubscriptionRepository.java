package com.smartagri.alert.repository;

import com.smartagri.alert.model.AlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {

    List<AlertSubscription> findByUserId(String userId);

    Optional<AlertSubscription> findByUserIdAndParcelId(String userId, Long parcelId);

    List<AlertSubscription> findByParcelIdAndIsEnabledTrue(Long parcelId);

    @Query("SELECT s FROM AlertSubscription s WHERE s.isEnabled = true " +
            "AND (s.parcelId = :parcelId OR s.parcelId IS NULL)")
    List<AlertSubscription> findEnabledSubscriptionsForParcel(@Param("parcelId") Long parcelId);

    List<AlertSubscription> findByIsEnabledTrue();
}