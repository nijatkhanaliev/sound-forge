package com.company.models.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PlayListResponse {
    private String name;
    private Set<MusicResponse> musics;
}
