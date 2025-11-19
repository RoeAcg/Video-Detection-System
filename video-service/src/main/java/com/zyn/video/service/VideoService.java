package com.zyn.video.service;

import com.zyn.common.dto.response.PageResponse;
import com.zyn.common.dto.response.VideoUploadResponse;
import com.zyn.common.entity.Video;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频服务接口
 */
public interface VideoService {

    /**
     * 上传视频（小文件）
     */
    VideoUploadResponse uploadVideo(MultipartFile file, String description, Long userId);

    /**
     * 初始化分块上传
     */
    String initChunkUpload(String fileName, Long fileSize, Integer totalChunks, Long userId);

    /**
     * 上传分块
     */
    void uploadChunk(String fileId, Integer chunkIndex, MultipartFile chunk);

    /**
     * 完成分块上传
     */
    VideoUploadResponse completeChunkUpload(String fileId, String description, Long userId);

    /**
     * 获取视频详情
     */
    Video getVideo(Long videoId, Long userId);

    /**
     * 获取用户视频列表
     */
    PageResponse<Video> getUserVideos(Long userId, int page, int size);

    /**
     * 删除视频
     */
    void deleteVideo(Long videoId, Long userId);
}
