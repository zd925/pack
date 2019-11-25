package pack.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Date;

public class FileUtils {

	/**
	 * 压缩文件tar
	 *
	 * @param sourceFilePath 源文件路径
	 * @param tarFilePath    压缩后文件存储路径tar
	 */
	public static boolean packFile2Tar(String sourceFilePath, String tarFilePath) throws Exception {
		File tarFile = new File(tarFilePath); // tar压缩包
		File sourceFile = new File(sourceFilePath); // 压缩的文件夹
		boolean flag = false;

		try (TarArchiveOutputStream taros = new TarArchiveOutputStream(new FileOutputStream(tarFile))) {
			File[] files = sourceFile.listFiles();
			for (File file : files) {
				writeTar(file, "", taros);
			}
		}
		//文件压缩完成后，删除被压缩文件夹temp
		flag = deleteDir(sourceFile);
		System.out.println(new Date() + "删除被压缩文件[" + sourceFile + "]标志：" + flag);

		return flag;
	}

	/**
	 * 遍历所有文件，压缩
	 *
	 * @param sourceFile       源文件目录
	 * @param parentPath 压缩文件目录
	 * @param taros      文件流
	 */
	private static void writeTar(File sourceFile, String parentPath, TarArchiveOutputStream taros) throws Exception {
		if (sourceFile.isDirectory()) {
			//目录
			parentPath += sourceFile.getName() + File.separator;
			File[] files = sourceFile.listFiles();
			for (File file : files) {
				writeTar(file, parentPath, taros);
			}
		} else {
			//文件
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
				TarArchiveEntry tarEntry = null;
				if ("".equals(parentPath)) {
					tarEntry = new TarArchiveEntry(sourceFile, sourceFile.getName());//打包的时候只是包含文件，不带路径
				} else {
					tarEntry = new TarArchiveEntry(parentPath + sourceFile.getName()); // 打包带路径
				}
				tarEntry.setSize(sourceFile.length()); //
				taros.putArchiveEntry(tarEntry);  //打包的时候带路径
				IOUtils.copy(bis, taros); // 前者是需要复制的文件路径, 则是压缩包文件路径
				taros.closeArchiveEntry();
			}
		}
	}

	/**
	 * 删除文件夹
	 *
	 * @param file
	 * @return
	 */
	public static boolean deleteDir(File file) {
		if (!file.exists()) {
			return false;
		}
		if (file.isFile()) {
			return file.delete();
		} else {
			File[] files = file.listFiles();
			for (File target : files) {
				deleteDir(target);
			}
			return file.delete();
		}
	}
}