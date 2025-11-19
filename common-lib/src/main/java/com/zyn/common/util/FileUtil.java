package com.zyn.common.util;

import com.zyn.common.enums.FileType;
import com.zyn.common.exception.FileUploadException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件操作工具类
 */
public final class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final List<String> ALLOWED_VIDEO_EXTENSIONS =
            Arrays.asList("mp4", "avi", "mov", "mkv", "webm");

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        return FilenameUtils.getExtension(filename).toLowerCase();
    }

    /**
     * 验证视频文件格式
     */
    public static boolean isValidVideoFormat(String filename) {
        String extension = getExtension(filename);
        return ALLOWED_VIDEO_EXTENSIONS.contains(extension);
    }

    /**
     * 验证文件大小
     */
    public static boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }

    /**
     * 生成唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * 创建目录
     */
    public static void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 删除目录及其内容
     */
    public static boolean deleteDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        return dir.delete();
    }

    /**
     * 复制文件
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 移动文件
     */
    public static void moveFile(String sourcePath, String destPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(destPath);
        Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.size(path);
    }

    /**
     * 检查文件是否存在
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
