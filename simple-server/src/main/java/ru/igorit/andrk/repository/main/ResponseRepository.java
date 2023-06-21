package ru.igorit.andrk.repository.main;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.Response;

public interface ResponseRepository extends JpaRepository<Response,Long> {
}
