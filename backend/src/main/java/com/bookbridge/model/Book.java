package com.bookbridge.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "books") @Data
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String subject;
    private String edition;

    @Enumerated(EnumType.STRING)
    private Condition condition = Condition.GOOD;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Condition  { NEW, GOOD, FAIR, WORN }
    public enum BookStatus { AVAILABLE, REQUESTED, LENT }
}
