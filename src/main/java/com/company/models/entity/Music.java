package com.company.models.entity;

import com.company.models.enums.MusicStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "musics")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "musics_seq")
    @SequenceGenerator(name = "musics_seq",sequenceName = "musics_seq",allocationSize = 1)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private MusicStatus status; // TODO ADD THIS FIELD TO LIQUIBASE

    @ManyToMany
    @JoinTable(
            name = "music_category",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    @ManyToMany(mappedBy = "musics")
    private Set<PlayList> playLists;
}
