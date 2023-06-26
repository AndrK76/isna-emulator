package ru.igorit.andrk.repository.main;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorit.andrk.model.OpenCloseRequest;
import ru.igorit.andrk.model.OpenCloseResponse;
import ru.igorit.andrk.model.Request;
import ru.igorit.andrk.model.Response;

import java.util.List;

public interface OpenCloseResponseRepository extends JpaRepository<OpenCloseResponse, Long> {

    Page<OpenCloseResponse> findAllByIdLessThan(Long id, Pageable pageable);

    List<OpenCloseResponse> findAllByRequestBetween(OpenCloseRequest first, OpenCloseRequest last);
}
