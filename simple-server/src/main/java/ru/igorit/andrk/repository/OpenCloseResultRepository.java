package ru.igorit.andrk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseResult;

public interface OpenCloseResultRepository extends JpaRepository<OpenCloseResult, Long> {
}
