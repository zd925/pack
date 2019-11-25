package pack.util;

import pack.dao.PackInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PacketUtil {

	/**
	 * 生成日志
	 *
	 * @param packInfoList
	 * @return
	 */
	public static String packNewPatchLog(List<PackInfo> packInfoList, String content) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("# 发布时间\n");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		stringBuffer.append("release_time: " + sdf.format(new Date()) + "\n");
		stringBuffer.append("\n");
		stringBuffer.append("# 模块包描述信息\n");
		stringBuffer.append("description:\n");
		String str = Arrays.asList(packInfoList.get(0).getJarName().split("-")).get(0);
		// 首字母大写
		str = str.substring(0, 1).toUpperCase() + str.substring(1);
		stringBuffer.append("  - ")
				.append(str + " " + "v" + packInfoList.get(0).getNewVersion() + "补丁包" + "," + content + "\n");
		stringBuffer.append("  - ").append(str + " " + "v" + packInfoList.get(0).getNewVersion() + "patch " + "\n");
		stringBuffer.append("\n");
		stringBuffer.append("target_modules: \n");
		for (PackInfo packInfo : packInfoList) {
			stringBuffer.append("  - ").append("name: " + packInfo.getJarName() + "\n");
			stringBuffer.append("    ").append("current_module_version: " + packInfo.getOldVersion().replace("R", "") + "\n");
			stringBuffer.append("    ").append("patched_module_version: " + packInfo.getNewVersion().replace("R", "") + "\n");
		}
		return stringBuffer.toString();

	}

	/**
	 * 打包
	 *
	 * @param packInfoList 打包的文件集合
	 * @param patchName    打包文件名
	 * @param patchPath    打包路径
	 * @param patchLog     日志str
	 */
	public static Boolean packTarGz(List<PackInfo> packInfoList, String patchName, String patchPath, String patchLog)
			throws Exception {
		String relaysFilePath = patchPath + File.separator + "5dce13949ae8ec82d0ab5d8f";  // 中转文件路径
		boolean flag = false;
		for (PackInfo packInfo : packInfoList) { // 获取项目信息
			String relaysSunPath = relaysFilePath + File.separator + packInfo.getJarName(); // 转移文件夹路径
			String uploadFilesPath = packInfo.getUploadFilePath(); // 多文件路径
			List<String> uploadFilesList = Arrays.asList(uploadFilesPath.split(",")); // 分离转化为list
			for (String uploadFilePath : uploadFilesList) { // 获取文件路径
				if (!"".equals(uploadFilePath)) {
					File file = new File(uploadFilePath);
					String fileName = file.isDirectory() ? file.getName() : ""; // 判断上传文件是否是文件夹 获取目录名
					File dir = null; // 转移路径
					if ("".equals(fileName)) { // 文件
						dir = new File(relaysSunPath);
					} else { // 文件夹
						dir = new File(relaysSunPath + File.separator + fileName);
					}
					dir.mkdirs(); // 创建文件夹(包含父目录)
					String absolutePath = dir.getAbsolutePath(); // 获取复制后路径
					copyDir(uploadFilePath, absolutePath);
				}
			}
		}
		writeFileContent(relaysFilePath, patchLog); // 生成log文件

		// 打包文件路径tar
		String tarPackname = patchPath + File.separator + patchName + ".tar";
		FileUtils.packFile2Tar(relaysFilePath, tarPackname); // 打包 tar

		// 打包文件路径名tar,gz
		String packname = tarPackname + ".gz";
		File file = FileGzUtils.compress(new File(tarPackname), packname);// tar 压缩成 tar.gz
		if (file != null) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 生成yaml文件
	 *
	 * @param patchPath
	 * @param patchLog
	 * @throws IOException
	 */
	private static void writeFileContent(String patchPath, String patchLog) throws IOException {
		String pathPatch = patchPath + "\\patch" + ".yaml";
		File file = new File(pathPatch);
		file.createNewFile();
		FileWriter fileWritter = new FileWriter(file, true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(patchLog);
		bufferWritter.close();
	}

	/**
	 * 复制文件夹
	 *
	 * @param oldPath srcPath 复制路径
	 * @param newPath targetPath 复制后的路径
	 * @throws IOException
	 */
	private static void copyDir(String oldPath, String newPath) throws Exception {
		//判断转移路径是否存在,不存在创建
		if (!(new File(newPath)).exists()) {
			(new File(newPath)).mkdir();
		}

		//文件名称列表
		File file = new File(oldPath);
		String[] filePath = null;
		if (file.isDirectory()) {
			filePath = file.list();
		} else {
			copyFile(oldPath, newPath + file.separator + file.getName());
			return;
		}
		if (filePath != null) {
			for (int i = 0; i < filePath.length; i++) {
				if ((new File(oldPath + file.separator + filePath[i])).isDirectory()) {
					copyDir(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
				}
				if (new File(oldPath + file.separator + filePath[i]).isFile()) {
					copyFile(oldPath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
				}
			}
		}
	}

	/**
	 * 复制文件
	 *
	 * @param oldPath
	 * @param newPath
	 * @throws IOException
	 */
	private static void copyFile(String oldPath, String newPath) throws Exception {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(new File(oldPath));
			out = new FileOutputStream(new File(newPath));
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				out.flush();
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
