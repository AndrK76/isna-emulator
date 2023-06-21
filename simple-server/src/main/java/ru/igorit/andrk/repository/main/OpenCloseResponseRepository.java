package ru.igorit.andrk.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseResponse;

public interface OpenCloseResponseRepository extends JpaRepository<OpenCloseResponse,Long> {
}
