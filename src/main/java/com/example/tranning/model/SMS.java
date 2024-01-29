package com.example.tranning.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SMS_TEST")
public class SMS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "message_id", nullable = false, unique = true)
    @NotEmpty
    private String messageId;
    @Column(nullable = false)
    @NotEmpty
    @Size(max = 100)
    private String keyword;
    @Column(nullable = false)
    @NotEmpty
    @Size(max = 50)
    private String sender;
    @Column(nullable = false)
    @NotEmpty
    @Pattern(regexp = "\\d{10,12}")
    private String destination;
    @Column(name = "short_message", nullable = false)
    @NotEmpty
    @Size(max = 500)
    private String shortMessage;
    @Column(name = "partner_code", nullable = false)
    private String partnerCode;

    public void setStatus(String sent) {
    }

    public void setDescription(String result) {
    }

    public void setSentTime(Instant now) {
    }
}
