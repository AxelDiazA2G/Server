package com.module_7.server;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "items") // Ensures Hibernate uses the correct table name
public class ItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ItemID") // This must match the column name for the ID in the database
    private Long id;

    @Column(name = "Description") // Ensure this matches the case exactly ("Description" vs "description")
    private String description;

    @Column(name = "CreatedAt") // Change this to match the column name in the database ("CreatedAd")
    private Timestamp createdAt;

    // Standard getters and setters for the id field
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Standard getters and setters for the description field
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Standard getters and setters for the createdAt field
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
