package ru.igorit.andrk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseRequest;

public interface OpenCloseRequestRepository extends JpaRepository<OpenCloseRequest,Long> {
}
