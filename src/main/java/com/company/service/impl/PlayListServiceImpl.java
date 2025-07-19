package com.company.service.impl;

import com.company.exception.NotFoundException;
import com.company.exception.OperationNotPermittedException;
import com.company.models.dto.request.PlaylistRequest;
import com.company.models.dto.response.PlayListResponse;
import com.company.models.entity.Music;
import com.company.models.entity.PlayList;
import com.company.models.entity.User;
import com.company.models.mapper.PlayListMapper;
import com.company.models.specification.MusicSpecification;
import com.company.repository.MusicRepository;
import com.company.repository.PlayListRepository;
import com.company.repository.UserRepository;
import com.company.service.PlayListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.company.models.enums.MusicStatus.PUBLIC;
import static com.company.util.SecurityUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlayListService {

    private final UserRepository userRepository;
    private final PlayListRepository playListRepository;
    private final PlayListMapper playListMapper;
    private final MusicRepository musicRepository;
    private final GeminiAIService geminiAIService;

    @Override
    public PlayListResponse createPlayList(PlaylistRequest request) {
        User currentUser = getCurrentUser(userRepository);
        if (playListRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
            log.info("Playlist already exists with name: {}, userId: {}",
                    request.getName(), currentUser.getId());
            return addMusicToPlaylist(request, currentUser.getId());
        }

        log.info("Creating new Playlist, userId: {}", currentUser.getId());
        PlayList playList = playListMapper.toPlayList(request, musicRepository);
        playListRepository.save(playList);

        return playListMapper.toPlayListResponse(playList);
    }

    @Override
    public List<PlayListResponse> getAllPlayListByOwner() {
        User currentUser = getCurrentUser(userRepository);
        log.info("Getting all playlist by user, userId: {}", currentUser.getId());

        List<PlayList> playLists = playListRepository.findAllByUserId(currentUser.getId());

        return playListMapper.toPlayListResponses(playLists);
    }

    @Override
    public PlayListResponse getPlayListById(Long id) {
        User currentUser = getCurrentUser(userRepository);
        log.info("Getting playlist, playListId: {}, userId: {}", id, currentUser.getId());

        PlayList playList = playListRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Playlist not found with id: " + id));

        return playListMapper.toPlayListResponse(playList);
    }


    @Override
    public PlayListResponse removeMusicFromPlaylist(Long musicId, Long playListId) {
        User currentUser = getCurrentUser(userRepository);
        log.warn("Deleting music from playlist, musicId: {}, playListId: {}, userId: {}",
                musicId, playListId, currentUser.getId());

        PlayList playList = playListRepository.findById(playListId)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " +
                        playListId));

        if (playList.getUser().getId() != currentUser.getId()) {
            throw new OperationNotPermittedException("You cannot remove music from playlist");
        }

        Set<Music> musics = playList.getMusics();
        Set<Music> musicsResult = musics.stream()
                .filter((m) -> m.getId() != musicId)
                .collect(Collectors.toSet());

        playList.setMusics(musicsResult);
        playListRepository.save(playList);

        return playListMapper.toPlayListResponse(playList);
    }

    @Override
    public void deletePlayList(Long id) {
        User currentUser = getCurrentUser(userRepository);

        PlayList playList = playListRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with id: " + id));

        if (playList.getUser().getId() != currentUser.getId()) {
            throw new OperationNotPermittedException("You cannot delete playlist");
        }

        playListRepository.delete(playList);
    }

    private PlayListResponse addMusicToPlaylist(PlaylistRequest request, Long userId) {
        log.info("Adding new music to playlist, playlistName: {}, userId: {}",
                request.getName(), userId);
        PlayList playList = playListRepository.findByName(request.getName())
                .orElseThrow(() -> new NotFoundException("Playlist not found with name: " +
                        request.getName()));

        Set<Long> musicIds = request.getMusicIds();
        Set<Music> musics = musicIds.stream()
                .map((id) -> musicRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Music not found with id: " + id)))
                .collect(Collectors.toSet());

        playList.getMusics().addAll(musics);
        playListRepository.save(playList);

        return playListMapper.toPlayListResponse(playList);
    }

    @Scheduled(cron = "0 0 10 ? * MON,FRI")
    public void createPlayListForUserHasOnePlayList() {
        log.info("Creating automatically playlist working");
        LocalDateTime cutOff = LocalDateTime.now().minusDays(1);
        List<Long> userIds = userRepository.findUserWithOnePlaylistAndCreatedBefore(cutOff);

        Specification<Music> specification = MusicSpecification.hasStatus(PUBLIC);
        Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        log.info("Getting all music with page: {}, and size: {}", 1, 10);
        Page<Music> musicPage = musicRepository.findAll(specification, pageable);
        Set<Music> musics = musicPage.stream()
                .collect(Collectors.toSet());
        List<String> titles = musics.stream()
                .map(Music::getTitle)
                .toList();
        String promptList = "Suggest a creative playlist name based on these songs: " + String.join(",", titles);

        try {
            String playListName = geminiAIService.generatePlayListName(promptList);
            userIds.forEach((id) -> createAutomaticPlaylist(id, musics, playListName));
        } catch (IOException e) {
            log.error("IOException happened in geminiAIService, message: {}", e.getMessage());
        }
    }

    private void createAutomaticPlaylist(Long id, Set<Music> musics, String playListName) {
        log.info("Create automatic playlist called, userId: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " +
                        id));
        PlayList playList = new PlayList();
        playList.setUser(user);
        playList.setAutoGenerated(true);
        playList.setMusics(musics);
        playList.setName(playListName);
        playListRepository.save(playList);

    }

}
