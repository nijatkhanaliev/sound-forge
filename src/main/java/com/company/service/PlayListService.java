package com.company.service;

import com.company.models.dto.request.PlaylistRequest;
import com.company.models.dto.response.PlayListResponse;

import java.util.List;

public interface PlayListService {
    PlayListResponse createPlayList(PlaylistRequest request);

    List<PlayListResponse> getAllPlayListByOwner();

    PlayListResponse getPlayListById(Long id);

    PlayListResponse removeMusicFromPlaylist(Long musicId, Long playListId);

    void deletePlayList(Long playListId);
}
