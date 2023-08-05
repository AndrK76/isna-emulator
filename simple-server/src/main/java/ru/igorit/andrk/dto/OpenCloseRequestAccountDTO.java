package ru.igorit.andrk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.igorit.andrk.model.OpenCloseRequestAccount;

import java.time.LocalDateTime;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class OpenCloseRequestAccountDTO {
    private Long id;
    private Integer sort;
    private String bic;
    private String account;
    private String accountType;
    private Integer operType;
    private LocalDateTime operDate;
    private String rnn;
    private String dog;
    private LocalDateTime dogDate;
    private String bicOld;
    private String accountOld;
    private LocalDateTime dateModify;
    public static OpenCloseRequestAccountDTO create(OpenCloseRequestAccount account){
        return OpenCloseRequestAccountDTO.builder()
                .id(account.getId())
                .sort(account.getSort())
                .bic(account.getBic())
                .account(account.getAccount())
                .accountType(account.getAccountType())
                .operType(account.getOperType())
                .operDate(account.getOperDate())
                .rnn(account.getRnn())
                .dog(account.getDog())
                .dogDate(account.getDogDate())
                .bicOld(account.getBicOld())
                .accountOld(account.getAccountOld())
                .dateModify(account.getDateModify())
                .build();
    }
}
