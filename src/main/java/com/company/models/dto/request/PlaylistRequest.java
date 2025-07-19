package com.company.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PlaylistRequest {
    @NotBlank(message = "Playlist name cannot be blank")
    private String name;

    @NotNull(message = "Musics cannot be null")
    private Set<Long> musicIds;
}
