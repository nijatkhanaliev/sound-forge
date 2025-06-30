package com.company.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Music> musics;

}
