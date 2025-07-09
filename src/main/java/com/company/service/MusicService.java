package com.company.service;

import com.company.models.dto.PageResponseDto;
import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MusicService {
    MusicResponse createMusic(MultipartFile music, MusicRequest musicRequest);

    PageResponseDto<MusicResponse> findAllMusics(int page,int size);


}
