package ru.realityfamily.controllback.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.realityfamily.controllback.Models.States;

public interface StateRepository extends JpaRepository<States, Long> {
}
