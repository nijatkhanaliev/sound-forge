package com.company.controller;

import com.company.models.dto.ApiResponseDto;
import com.company.models.dto.PageResponseDto;
import com.company.models.dto.request.MusicRequest;
import com.company.models.dto.response.MusicResponse;
import com.company.service.MusicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/musics")
@Tag(name = "Musics")
public class MusicController {
    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<MusicResponse>> createMusic(
            @RequestPart(name = "file") MultipartFile file,
            @Valid @RequestPart(name = "metadata") MusicRequest musicRequest) {

        return ResponseEntity
                .status(CREATED)
                .body(
                        new ApiResponseDto<>(
                                "Music created successfully",
                                musicService.createMusic(file,musicRequest)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<MusicResponse>> findAllPublicMusic(
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "size",required = false,defaultValue = "10") int size
    ){

        return ResponseEntity.ok(musicService.findAllPublicMusics(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MusicResponse> findPublicMusicById(
            @PathVariable Long id
    ){

        return ResponseEntity.ok(musicService.findPublicMusicById(id));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponseDto<MusicResponse>> findAllMusicByOwner(
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "size",required = false,defaultValue = "10") int size
    ){
        return ResponseEntity.ok(musicService.findAllMusicByOwner(page,size));
    }

    @PatchMapping("/{musicId}/status/private")
    public ResponseEntity<ApiResponseDto<Long>> updateMusicStatusToPrivate(
            @PathVariable(name = "musicId") Long id
    ){
        return ResponseEntity.ok(new ApiResponseDto<>(
                "Music status changed successfully",
                musicService.updateStatusToPrivate(id)
        ));
    }

    @PatchMapping("/{musicId}/status/public")
    public ResponseEntity<ApiResponseDto<Long>> updateMusicStatusToPublic(
            @PathVariable(name = "musicId") Long id
    ){
        return ResponseEntity.ok(new ApiResponseDto<>(
                "Music status changed successfully",
                musicService.updateStatusToPublic(id)
        ));
    }

    @PatchMapping("/{musicId}/status/archived")
    public ResponseEntity<ApiResponseDto<Long>> updateMusicStatusToArchived(
            @PathVariable(name = "musicId") Long id
    ){
        return ResponseEntity.ok(new ApiResponseDto<>(
                "Music status changed successfully",
                musicService.updateArchiveMusic(id)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable Long id){
        musicService.deleteMusic(id);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<MusicResponse>> searchMusic(
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "size",required = false,defaultValue = "10") int size,
            @RequestParam(name = "firstName",required = false) String creatorFirstName,
            @RequestParam(name = "lastName",required = false) String creatorLastName,
            @RequestParam(name = "title",required = false) String musicTitle
    ){
        return ResponseEntity.ok(musicService.searchMusic(
                page,
                size,
                creatorFirstName,
                creatorLastName,
                musicTitle
        ));
    }


}
