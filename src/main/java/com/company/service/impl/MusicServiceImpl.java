package com.company.service.impl;

import com.company.exception.NotFoundException;
import com.company.exception.OperationNotPermittedException;
import com.company.models.dto.PageResponseDto;
import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import com.company.models.entity.Category;
import com.company.models.entity.Music;
import com.company.models.entity.User;
import com.company.models.mapper.MusicMapper;
import com.company.models.specification.MusicSpecification;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.company.models.enums.MusicStatus.ARCHIVED;
import static com.company.models.enums.MusicStatus.DELETED;
import static com.company.models.enums.MusicStatus.PRIVATE;
import static com.company.models.enums.MusicStatus.PUBLIC;
import static com.company.models.specification.MusicSpecification.hasMusicianLastName;
import static com.company.models.specification.MusicSpecification.hasStatus;
import static com.company.models.specification.MusicSpecification.hasTitle;
import static com.company.models.specification.MusicSpecification.notDeleted;
import static com.company.util.SecurityUtils.checkUser;
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

        String url = fileService.saveFile(file, user.getId());

        log.info("Creating music, userId: {}", user.getId());
        Music music = musicMapper.toMusic(musicRequest);
        music.setCategories(allCategory);
        music.setUser(user);
        music.setUrl(url);
        music.setStatus(PUBLIC);
        musicRepository.save(music);

        return musicMapper.toMusicResponse(music);
    }

    @Override
    public PageResponseDto<MusicResponse> findAllPublicMusics(int page, int size) {
        User user = getCurrentUser(userRepository);

        log.info("Getting all public musics");
        Pageable pageable = PageRequest.of(page, size);
        Page<Music> musics = musicRepository.findAllPublicMusics(pageable,
                user.getId(), PUBLIC);
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

    public MusicResponse findPublicMusicById(Long id) {
        log.info("Getting public music by id: {}", id);
        Specification<Music> specification = MusicSpecification
                .hasId(id)
                .and(hasStatus(PUBLIC));

        Music music = musicRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Music not found with"));

        return musicMapper.toMusicResponse(music);
    }

    public PageResponseDto<MusicResponse> findAllMusicByOwner(int page, int size) {
        User user = getCurrentUser(userRepository);

        Specification<Music> specification = MusicSpecification
                .withOwnerId(user.getId())
                .and(notDeleted());

        log.info("Getting all musics by owner, ownerId: {}", user.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Music> musics = musicRepository.findAll(specification, pageable);
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

    public Long updateStatusToPrivate(Long musicId) {
        User currentUser = getCurrentUser(userRepository);

        Specification<Music> specification = MusicSpecification
                .hasId(musicId)
                .and(notDeleted());

        Music music = musicRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Music not found with"));

        if (music.getStatus() == PRIVATE) {
            throw new OperationNotPermittedException("Music is already private");
        }

        checkUser(currentUser, music, "You cannot change music status");

        log.info("Updating music status to private, musicId: {} by owner," +
                " userId: {}", musicId, currentUser.getId());
        music.setStatus(PRIVATE);
        musicRepository.save(music);

        return music.getId();
    }

    @Override
    public Long updateStatusToPublic(Long musicId) {
        User currentUser = getCurrentUser(userRepository);

        Specification<Music> specification = MusicSpecification
                .hasId(musicId)
                .and(notDeleted());

        Music music = musicRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Music not found with"));

        if (music.getStatus() == PUBLIC) {
            throw new OperationNotPermittedException("Music is already public");
        }

        checkUser(currentUser, music, "You cannot change music status");

        log.info("Updating music status to public, musicId: {} by owner," +
                " userId: {}", musicId, currentUser.getId());
        music.setStatus(PUBLIC);
        musicRepository.save(music);

        return music.getId();
    }

    @Override
    public Long updateArchiveMusic(Long musicId) {
        User currentUser = getCurrentUser(userRepository);

        Specification<Music> specification = MusicSpecification
                .hasId(musicId)
                .and(notDeleted());

        Music music = musicRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Music not found with"));

        if (music.getStatus() == ARCHIVED) {
            throw new OperationNotPermittedException("Music is already archived");
        }

        checkUser(currentUser, music, "You cannot change music status");

        log.info("Archiving music, musicId: {} by owner, userId: {}",
                musicId, currentUser.getId());
        music.setStatus(ARCHIVED);
        musicRepository.save(music);

        return music.getId();
    }

    @Override
    public void deleteMusic(Long musicId) {
        User currentUser = getCurrentUser(userRepository);

        Specification<Music> specification = MusicSpecification
                .hasId(musicId)
                .and(notDeleted());

        Music music = musicRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Music not found with"));

        checkUser(currentUser, music, "You cannot delete music");

        log.info("Deleting music, musicId: {} by owner, userId: {}",
                musicId, currentUser.getId());
        music.setStatus(DELETED);
        musicRepository.save(music);
    }

    @Override
    public PageResponseDto<MusicResponse> searchMusic(int page,
                                                      int size,
                                                      String creatorFirstName,
                                                      String creatorLastName,
                                                      String musicTitle) {
        log.info("Searching music by creatorFirsName:{},creatorLastName:{},musicTitle:{}",
                creatorFirstName, creatorLastName, musicTitle);

        Specification<Music> specification = MusicSpecification
                .hasMusicianFirstName(creatorFirstName)
                .or(hasMusicianLastName(creatorLastName))
                .or(hasTitle(musicTitle));

        Pageable pageable = PageRequest.of(page, size);
        Page<Music> musics = musicRepository.findAll(specification, pageable);

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
