package ru.igorit.andrk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
