package com.company.models.mapper;

import com.company.exception.NotFoundException;
import com.company.models.dto.request.PlaylistRequest;
import com.company.models.dto.response.PlayListResponse;
import com.company.models.entity.Music;
import com.company.models.entity.PlayList;
import com.company.repository.MusicRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = MusicMapper.class)
public interface PlayListMapper {

    @Mapping(target = "musics", source = "musicIds", qualifiedByName = "getMusicsById")
    PlayList toPlayList(PlaylistRequest request, @Context MusicRepository musicRepository);

    PlayListResponse toPlayListResponse(PlayList playList);

    List<PlayListResponse> toPlayListResponses(List<PlayList> playLists);

    @Named("getMusicsById")
    default Set<Music> setMusic(Set<Long> musicIds, @Context MusicRepository musicRepository) {
        return musicIds.stream()
                .map((id) -> musicRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Music not found with id: " + id)))
                .collect(Collectors.toSet());
    }

}
