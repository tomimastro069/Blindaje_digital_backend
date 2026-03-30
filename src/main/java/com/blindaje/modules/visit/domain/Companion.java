package com.blindaje.modules.visit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "companions")
public class Companion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String document;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    @JsonIgnore
    private Visit visit;

    public Companion() {}

    public Companion(String name, String document, Visit visit) {
        this.name = name;
        this.document = document;
        this.visit = visit;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public Visit getVisit() { return visit; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDocument(String document) { this.document = document; }
    public void setVisit(Visit visit) { this.visit = visit; }
}