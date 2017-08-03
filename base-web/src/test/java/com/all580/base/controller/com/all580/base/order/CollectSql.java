package com.all580.base.controller.com.all580.base.order;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectSql {
	/**
	 * 日志对象
	 */
	private static final CollectLogger _log = new CollectLogger("logs/collect_"
			+ new SimpleDateFormat("yyyy-MM-dd").format(new Date(System
					.currentTimeMillis())) + ".sql");

	static {
		try {
			_log.open();
		} catch (Exception ex) {
			throw new UnsupportedOperationException(ex);
		}
	} // End static

	/**
	 * 默认构造函数
	 */
	private CollectSql() {
	} // End CollectLog

	/**
	 * 输出错误消息
	 * 
	 * @param message
	 *            错误消息
	 */
	public static void addSql(String sql) {
		_log.addSql(sql);
	} // End logError

	/**
	 * 记录错误信息
	 * 
	 * @author LiZhengGuang
	 * @date 2013-8-20
	 * @project COM.STEEL.MV.VIDEO.COLLECT
	 * @package com.steel.mv.video.collect.logs
	 * @package CollectLog.java
	 * @version 1.0
	 */
	static class CollectLogger {
		/**
		 * 日志文件名称
		 */
		private final File _file;

		/**
		 * 文件写数据流
		 */
		private FileOutputStream _fos;

		/**
		 * 文件缓存数据流
		 */
		private BufferedOutputStream _bos;

		/**
		 * 默认构造函数
		 */
		public CollectLogger(String fileName) {
			_file = new File(fileName);
			if (!_file.getParentFile().isDirectory()
					&& !_file.getParentFile().mkdirs()) {
				throw new UnsupportedOperationException(
						"Create File Path Failed. File Path:'"
								+ _file.getParent() + "'");
			}
		}

		/**
		 * 打开数据流
		 */
		public synchronized void open() throws Exception {
			_fos = new FileOutputStream(_file, true);
			_bos = new BufferedOutputStream(_fos);
		} // End open

		private synchronized void addSql(String sql) {
			try {
				_bos.write((sql + "\r\n").getBytes(Charset.forName("UTF-8")));
				_bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} // End class CollectLogger
} // End class CollectLog