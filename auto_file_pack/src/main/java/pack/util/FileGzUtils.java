package pack.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class FileGzUtils {
	/**
	 * @param sources 要打包的原文件数组
	 * @param target  打包后的文件 tar
	 * @return File 返回打包后的文件
	 * @throws
	 * @Title: pack
	 * @Description: 将一组文件打成tar包
	 */
	public static File pack(List<File> sources, File target) throws IOException {
		TarArchiveOutputStream os = new TarArchiveOutputStream(new FileOutputStream(target)); //  转化为tar文件输出流
		for (File file : sources) {
			os.putArchiveEntry(new TarArchiveEntry(file)); //打包的时候带路径
			InputStream	inputStream = new FileInputStream(file);
			IOUtils.copy(inputStream, os);// 前者是需要复制的文件路径, 则是tar内部路径

			os.closeArchiveEntry();
			if (inputStream != null) {
				inputStream.close();
			}
		}
		if (os != null) {
				os.flush();
				os.close();

		}

		return target;
	}

	/**
	 * tar打包压缩
	 *
	 * @param source   需要压缩的文件(.tar)
	 * @param FilePath 压缩后的文件全文件名(.tar.gz)
	 * @return 返回压缩后的文件
	 */
	public static File compress(File source, String FilePath) throws Exception {
		File target = new File(FilePath);
		FileInputStream in = null;
		GZIPOutputStream out = null;
			in = new FileInputStream(source);
			out = new GZIPOutputStream(new FileOutputStream(target));
			byte[] array = new byte[1024];
			int number;
			while ((number = in.read(array, 0, array.length)) != -1) {
				out.write(array, 0, number);
			}
			if (in != null) {
					in.close();
					source.delete();//解压成功后，删除tar文件
			}
			if (out != null) {
					out.close();
					System.out.println("打包后文件为：" + target);
			}
		return target;
	}
}
