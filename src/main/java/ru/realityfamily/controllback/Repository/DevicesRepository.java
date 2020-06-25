package ru.realityfamily.controllback.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.realityfamily.controllback.Models.Devices;

@Repository
public interface DevicesRepository extends JpaRepository<Devices, Long> {
}
