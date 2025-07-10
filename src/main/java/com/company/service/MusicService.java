package com.company.service;

import com.company.models.dto.PageResponseDto;
import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MusicService {
    MusicResponse createMusic(MultipartFile music, MusicRequest musicRequest);

    PageResponseDto<MusicResponse> findAllPublicMusics(int page, int size);

    PageResponseDto<MusicResponse> findAllMusicByOwner(int page, int size);

    MusicResponse findPublicMusicById(Long id);

    Long updateStatusToPrivate(Long musicId);

    Long updateStatusToPublic(Long musicId);

    Long updateArchiveMusic(Long musicId);

    void deleteMusic(Long musicId);

    PageResponseDto<MusicResponse> searchMusic(int page,
                                               int size,
                                               String creatorFirstName,
                                               String creatorLastName,
                                               String musicTitle);
}
