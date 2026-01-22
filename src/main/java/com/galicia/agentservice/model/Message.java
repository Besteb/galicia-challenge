package com.galicia.agentservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
@Builder
@Getter
@Setter
@Table(name = "chatHistory")
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long chatId;
    @Lob
    String message;
    String prompt;
    @Lob
    String immediateContext;
    MessageSenderType messageSenderType;

    @CreationTimestamp
    Date creationDate;
}
