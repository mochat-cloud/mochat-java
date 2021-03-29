package com.mochat.mochat.service.sidebar;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * @author: yangpengwei
 * @time: 2021/1/27 2:11 下午
 * @description 文件存储服务
 */
public interface StorageService {

	/**
	 * 初始化服务, 创建存储目录
	 */
	void init();

	/**
	 * 存储文件
	 */
	void store(MultipartFile file);

	/**
	 * 获取文件路径
	 */
	Path load(String filename);

	/**
	 * 将文件路径转化成链接
	 */
	Resource loadAsResource(String filename);

	/**
	 * 清空文件
	 */
	void deleteAll();

}
