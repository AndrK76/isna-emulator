package ru.igorit.andrk.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseRequest;

public interface OpenCloseRequestRepository extends JpaRepository<OpenCloseRequest,Long> {
}
