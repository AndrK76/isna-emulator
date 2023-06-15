package ru.igorit.andrk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "open_close_request_accounts")
public class OpenCloseRequestAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private OpenCloseRequest request;

    @NonNull
    private Integer sort;

    private String account;

    @Column(name = "oper_type")
    private Integer operType;

    @Column(name = "account_type")
    private Integer accountType;

    private String bic;

    @Column(name = "oper_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime operDate;

    private String rnn;

    private String dog;



}
