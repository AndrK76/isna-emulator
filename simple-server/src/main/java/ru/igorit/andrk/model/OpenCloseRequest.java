package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "open_close_requests")
public class OpenCloseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "message_id")
    private UUID messageId;

    @NonNull
    private String reference;

    @NonNull
    @Column(name = "code_form")
    private String codeForm;

    @NonNull
    @Column(name = "notify_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime notifyDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", cascade = CascadeType.ALL)
    private List<OpenCloseRequestAccount> accounts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "raw_request_id")
    private Request rawRequest;

    public OpenCloseRequest(Request request){
        this();
        this.rawRequest = request;
    }

}
