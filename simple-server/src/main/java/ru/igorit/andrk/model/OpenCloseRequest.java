package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns=@JoinColumn(name="request_id"))
    private Set<OpenCloseRequestAccount> accounts = new HashSet<>();

}
