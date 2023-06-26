package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @NonNull
    @Setter
    private Request request;

    @NonNull
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "service_id")
    private String serviceId;

    @NonNull
    @Column(name = "is_success")
    @Setter
    private Boolean isSuccess;

    @NonNull
    @Column(name = "response_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime responseDate;

    @Column(name = "status_code")
    @Setter
    private String statusCode;

    @Column(name = "status_message")
    @Setter
    private String statusMessage;

    @Column(name = "data",length = 32000)
    @Setter
    private String data;
    public Response(Request request){
        this();
        this.request = request;
        this.messageId = request.getMessageId();
        this.serviceId = request.getServiceId();
        this.responseDate = OffsetDateTime.now();
    }
}
