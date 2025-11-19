package com.zyn.common.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 哈希计算工具类
 */
public final class HashUtil {

    private HashUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 计算字符串的SHA-256哈希
     */
    public static String sha256(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return DigestUtils.sha256Hex(input);
    }

    /**
     * 计算字节数组的SHA-256哈希
     */
    public static String sha256(byte[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return DigestUtils.sha256Hex(input);
    }

    /**
     * 计算文件的SHA-256哈希
     */
    public static String sha256(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        try (InputStream is = new FileInputStream(file)) {
            return DigestUtils.sha256Hex(is);
        }
    }

    /**
     * 计算输入流的SHA-256哈希
     */
    public static String sha256(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        return DigestUtils.sha256Hex(inputStream);
    }

    /**
     * 计算字符串的MD5哈希
     */
    public static String md5(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return DigestUtils.md5Hex(input);
    }

    /**
     * 计算文件的MD5哈希
     */
    public static String md5(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        try (InputStream is = new FileInputStream(file)) {
            return DigestUtils.md5Hex(is);
        }
    }

    /**
     * 验证哈希值是否匹配
     */
    public static boolean verifyHash(String input, String expectedHash, HashAlgorithm algorithm) {
        if (input == null || expectedHash == null) {
            return false;
        }
        String actualHash = algorithm == HashAlgorithm.SHA256 ? sha256(input) : md5(input);
        return actualHash.equalsIgnoreCase(expectedHash);
    }

    public enum HashAlgorithm {
        SHA256, MD5
    }
}
