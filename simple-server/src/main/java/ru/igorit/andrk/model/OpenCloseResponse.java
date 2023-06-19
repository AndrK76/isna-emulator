package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "response", cascade = CascadeType.ALL)
    private List<OpenCloseResponseAccount> accounts = new ArrayList<>();

    public OpenCloseResponse(OpenCloseRequest request){
        this();
        this.messageId = request.getMessageId();
        this.reference = request.getReference();
    }

}
