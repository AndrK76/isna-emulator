package ru.igorit.andrk.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "open_close_results")
@Getter
@EqualsAndHashCode(of={"id"})
public class OpenCloseResult {

    @Id
    private Long id;

    @NonNull
    private String text;

    @Override
    public String toString() {
        return "OpenCloseResult{" +
                "id=" + id +
                ", Text='" + text + '\'' +
                '}';
    }
}
