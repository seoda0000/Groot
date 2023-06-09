package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;
import com.groot.backend.entity.QNotificationEntity;
import com.groot.backend.entity.QPlanEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlanRepositoryImpl implements PlanRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByCodeAndPotId(Integer code, Long potId) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
        queryFactory.delete(qPlan)
                .where(qPlan.code.eq(code), qPlan.potId.eq(potId), qPlan.done.eq(false))
                .execute();
    }

    @Override
    public List<PlanEntity> findAllByDateTimeAndUserPK(LocalDateTime start, LocalDateTime end, Long userPK) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.userPK.eq(userPK))
                .fetch();
        return plans;
    }

    @Override
    public long updateByCodeAndPotId(Long potId, Integer code, LocalDateTime time) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
        LocalDateTime start = LocalDateTime.of(LocalDate.from(time), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.from(time), LocalTime.of(23, 59, 59));
        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, false)
                .where(qPlan.code.eq(code), qPlan.done.eq(true),  qPlan.potId.eq(potId), qPlan.dateTime.between(start, end))
                .execute();
        return updateCnt;
    }

    @Override
    public long updateByCodeAndDiaryId(Integer code, Long diaryId) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, false)
                .where(qPlan.code.eq(code), qPlan.diaryId.eq(diaryId))
                .execute();
        return updateCnt;
    }

    @Override
    public long updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, true)
                .set(qPlan.dateTime, LocalDateTime.now())
                .where(qPlan.code.eq(code), qPlan.potId.eq(potId))
                .execute();
        return updateCnt;
    }

    @Override
    public LocalDateTime findLastDateTimeByDoneAndPotIdAndCode(boolean done, Long potId, Integer code) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        LocalDateTime date = queryFactory.select(qPlan.dateTime.max())
                .from(qPlan)
                .where(qPlan.done.eq(done), qPlan.potId.eq(potId), qPlan.code.eq(code))
                .fetchOne();
        return date;
    }

    @Override
    public long updateDoneById(Long planId, boolean done) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, done)
                .where(qPlan.id.eq(planId))
                .execute();
        return updateCnt;
    }

    @Override
    public PlanEntity existsByCodeAndPotIdAndDateTimeBetween(Integer code, Long potId, LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        PlanEntity plan = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.code.eq(code), qPlan.potId.eq(potId), qPlan.done.eq(false))
                .fetchOne();
        return plan;
    }

    @Override
    public List<PlanEntity> findAllByDoneAndDateTimeBetween(boolean done, LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.done.eq(done))
                .fetch();
        return plans;
    }

    @Override
    public List<PlanEntity> findByDoneAndDateTimeBetween(boolean done, LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.done.eq(done), qPlan.dateTime.between(start, end))
                .fetch();
        return plans;
    }

    @Override
    public long updateDateTimeByPlanId(Long planId, LocalDateTime now) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.dateTime, now)
                .where(qPlan.id.eq(planId))
                .execute();
        return updateCnt;
    }
}
