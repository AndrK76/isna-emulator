package ru.igorit.andrk.repository.main;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByIdLessThan(Long id, Pageable pageable);

}
