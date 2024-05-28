package com.isp.backend.domain.schedule.repository;

import com.isp.backend.domain.member.entity.Member;
import com.isp.backend.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByIdAndActivatedIsTrue(Long scheduleId);
    @Query("SELECT s FROM Schedule s WHERE s.member = :member AND s.activated = true ORDER BY s.updatedAt DESC")
    List<Schedule> findSchedulesByMember(@Param("member") Member member);
    List<Schedule> findTop5ByMemberOrderByIdDesc(Member member);

}
