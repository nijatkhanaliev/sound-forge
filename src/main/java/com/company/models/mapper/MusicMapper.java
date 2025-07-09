package com.company.models.mapper;

import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import com.company.models.entity.Music;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MusicMapper {

    Music toMusic(MusicRequest musicRequest);

    MusicResponse toMusicResponse(Music music);

}
