package org.example.demoboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {

    public enum CategoryName {
        ALL,
        MOVIE, // ID : 1
        BOOK,
        RECIPE,
        GAME,
        MUSIC,
        SECRET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CategoryName name;

    public String getName() {
        return String.valueOf(name);
    }

    public void setName(String name) {
        this.name = CategoryName.valueOf(name);
    }
}
