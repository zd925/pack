package pack.view;

import pack.dao.PackInfo;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JarsPanel extends JPanel {
	public JTextField oldVersion;
	public JTextField uploadFilePath;
	public JComboBox fileNameComboBox;

	public JarsPanel(int index) {
		JLabel fileNameLabe1 = new JLabel("项目名:");
		add(fileNameLabe1);
		String[] nians = { "automation-job", "automation-mgr", "automation-local", "automation-nginx", "ufs-server",
				"ufs-nginx" };
		fileNameComboBox = new JComboBox(nians);
		add(fileNameComboBox);

		JLabel oldVersionLabe = new JLabel("上一版本:");
		add(oldVersionLabe);
		oldVersion = new JTextField();
		oldVersion.setText("版本格式为: 2.0.R16.21");
		oldVersion.setColumns(20);
		oldVersion.setForeground(Color.GRAY);
		add(oldVersion);
		oldVersion.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//获取焦点时，清空提示内容
				String temp = oldVersion.getText();
				if (temp.equals("版本格式为: 2.0.R16.21")) {
					oldVersion.setText("");
					oldVersion.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时，没有输入内容，显示提示内容
				String temp = oldVersion.getText();
				if (temp.equals("")) {
					oldVersion.setForeground(Color.GRAY);
					oldVersion.setText("版本格式为: 2.0.R16.21");
				}
			}
		});

		JButton openFileJButton = new JButton("项目路径");
		add(openFileJButton);
		uploadFilePath = new JTextField();
		uploadFilePath.setColumns(40);
		uploadFilePath.setText("可添加多文件");
		uploadFilePath.setForeground(Color.GRAY);
		add(uploadFilePath);
		uploadFilePath.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//获取焦点时，清空提示内容
				String temp = uploadFilePath.getText();
				if (temp.equals("可添加多文件")) {
					uploadFilePath.setText("");
					uploadFilePath.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时，没有输入内容，显示提示内容
				String temp = uploadFilePath.getText();
				if (temp.equals("")) {
					uploadFilePath.setForeground(Color.GRAY);
					uploadFilePath.setText("可添加多文件");
				}
			}
		});

		openFileJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setMultiSelectionEnabled(true);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.showDialog(new JLabel(), "选择");
				File[] files = fileChooser.getSelectedFiles();
				List<String> FileNameList = Arrays.stream(files).map(File::getAbsolutePath).collect(Collectors.toList());
				String fileNames = String.join(",", FileNameList);
				uploadFilePath.setText(uploadFilePath.getText().equals("可添加多文件") ? "" : uploadFilePath.getText());
				uploadFilePath.setText(uploadFilePath.getText() + (
						uploadFilePath.getText().equals("可添加多文件") || uploadFilePath.getText().equals("") ?
								fileNames :
								"," + fileNames));
				uploadFilePath.setForeground(Color.BLACK);
			}
		});

	}

	// 获取项目信息
	public PackInfo getPackInfo() {
		PackInfo packInfo = new PackInfo();
		packInfo.setJarName(fileNameComboBox.getSelectedItem().toString());
		packInfo.setOldVersion(oldVersion.getText());
		packInfo.setUploadFilePath(uploadFilePath.getText());
		return packInfo;
	}
}
