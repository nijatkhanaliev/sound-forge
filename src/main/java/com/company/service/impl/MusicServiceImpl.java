package com.company.service.impl;

import com.company.models.dto.PageResponseDto;
import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import com.company.models.entity.Category;
import com.company.models.entity.Music;
import com.company.models.entity.User;
import com.company.models.mapper.MusicMapper;
import com.company.repository.CategoryRepository;
import com.company.repository.MusicRepository;
import com.company.repository.UserRepository;
import com.company.service.FileService;
import com.company.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.company.util.SecurityUtils.getCurrentUser;


@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {
    private final UserRepository userRepository;
    private final MusicMapper musicMapper;
    private final MusicRepository musicRepository;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;

    @Override
    public MusicResponse createMusic(MultipartFile file, MusicRequest musicRequest) {

        User user = getCurrentUser(userRepository);

        log.info("Getting all category");
        Set<Long> allCategoryId = musicRequest.getAllCategoryId();
        Set<Category> allCategory = new HashSet<>(categoryRepository.
                findAllById(allCategoryId));

        String url = fileService.saveFile(file,user.getId());

        log.info("Creating music, userId: {}",user.getId());
        Music music = musicMapper.toMusic(musicRequest);
        music.setCategories(allCategory);
        music.setUser(user);
        music.setUrl(url);
        musicRepository.save(music);

        return musicMapper.toMusicResponse(music);
    }

    @Override
    public PageResponseDto<MusicResponse> findAllMusics(int page, int size) {
        User user = getCurrentUser(userRepository);

        log.info("Getting all musics");
        Pageable pageable = PageRequest.of(page, size);
        Page<Music> musics = musicRepository.findAll(pageable, user.getId());
        List<MusicResponse> musicResponses = musics.stream()
                .map(musicMapper::toMusicResponse)
                .toList();

        return new PageResponseDto<>(
                musicResponses,
                musics.getNumber(),
                musics.getSize(),
                musics.getTotalElements(),
                musics.getTotalPages(),
                musics.isFirst(),
                musics.isLast()
                );
    }


}
