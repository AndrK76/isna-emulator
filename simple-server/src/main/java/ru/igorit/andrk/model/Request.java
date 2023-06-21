package ru.igorit.andrk.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.lang.NonNull;


import javax.annotation.Generated;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "service_id")
    private String serviceId;

    @NonNull
    @Column(name = "message_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime messageDate;

    @Column(name = "data",length = 32000)
    private String data;
}
