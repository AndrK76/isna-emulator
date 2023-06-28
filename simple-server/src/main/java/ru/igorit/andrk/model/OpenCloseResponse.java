package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "open_close_responses")
public class OpenCloseResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private OpenCloseRequest request;

    @NonNull
    @Column(name = "message_id")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID messageId;

    @NonNull
    private String reference;

    @NonNull
    @Column(name = "code_form")
    private String codeForm;

    @NonNull
    @Column(name = "notify_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime notifyDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "response", cascade = CascadeType.ALL)
    //@Fetch(FetchMode.SUBSELECT)
    private List<OpenCloseResponseAccount> accounts = new ArrayList<>();

    public OpenCloseResponse(OpenCloseRequest request){
        this();
        this.request = request;
        this.messageId = request.getMessageId();
        this.reference = request.getReference();
    }

}
