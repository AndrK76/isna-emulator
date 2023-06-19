package ru.igorit.andrk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseResponse;

public interface OpenCloseResponseRepository extends JpaRepository<OpenCloseResponse,Long> {
}
