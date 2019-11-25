package pack.view;

import pack.dao.PackInfo;
import pack.util.FileUtils;
import pack.util.PacketUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Super_DH
 * The ideal is to be authentic but larger than life.
 *
 * @version java.11
 * @package autozip
 * @date: 2019-10-26    17:29
 */
public class PacthFrame extends JFrame {
	/**
	 * 添加组件
	 */
	private JTextArea pacthLog;
	private JPanel panel, jarsPanel;
	private JScrollPane jarsJScrollPane;
	private JTextField patchPath, updateVersion, patchName, content;
	private JButton pathButton, createLogButton, createPackNanmeJButton, removeJarButton, addJarButton;

	public JPanel getJarsPanel() {
		return jarsPanel;
	}

	public void setJarsPanel(JPanel jarsPanel) {
		this.jarsPanel = jarsPanel;
	}

	public JScrollPane getJarsJScrollPane() {
		return jarsJScrollPane;
	}

	public void setJarsJScrollPane(JScrollPane jarsJScrollPane) {
		this.jarsJScrollPane = jarsJScrollPane;
	}

	// 开始的字符
	int index = 1;

	public PacthFrame() {
		/**
		 * 主
		 */
		panel = new JPanel();
		panel.setBackground(Color.white);
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		/**
		 * 项目框
		 */
		jarsPanel = new JPanel();
		jarsPanel.setLayout(new BoxLayout(jarsPanel, BoxLayout.Y_AXIS));//盒子布局.从上到下
		jarsJScrollPane = new JScrollPane(jarsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jarsJScrollPane.getViewport().getView().setPreferredSize(new Dimension(100, 220));
		panel.add(jarsJScrollPane);
		jarsPanel.add(new JarsPanel(1));

		/**
		 * 第二行
		 */
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		addJarButton = new JButton("添加项目");
		panel_2.add(addJarButton);
		removeJarButton = new JButton("删除项目");
		panel_2.add(removeJarButton);
		removeJarButton.setEnabled(false);

		// 添加项目
		addJarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (index < 6) {
					index++;//自加1
					jarsPanel.add(new JarsPanel(index));//添加1个自己定义的面板组件
					myUpdateUI();//刷新界面
				}
				if (index == 6) {
					addJarButton.setEnabled(false);
				}
				if (index == 2) {
					removeJarButton.setEnabled(true);
				}
			}
		});
		// 删除项目
		removeJarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jarsPanel.getComponentCount() > 0) { // 得到jpc里的MyJPanel的组件数量
					jarsPanel.remove(jarsPanel.getComponentCount() - 1);//删除末尾的一个组件 ,
					index -= 1;
					myUpdateUI();
				}
				if (jarsPanel.getComponentCount() <= 5) {
					addJarButton.setEnabled(true);
				}
				if (jarsPanel.getComponentCount() == 1) {
					removeJarButton.setEnabled(false);
				}
			}
		});

		/**
		 *  第三行
		 */
		JPanel panel_3 = new JPanel();
		panel.add(panel_3);

		JLabel uploadVersionLabe = new JLabel("迭代版本");
		panel_3.add(uploadVersionLabe);
		updateVersion = new JTextField();
		panel_3.add(updateVersion);
		updateVersion.setText("本次迭代版本格式为: 2.0.R16.21");
		updateVersion.setForeground(Color.GRAY);
		updateVersion.setColumns(17);
		updateVersion.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//获取焦点时，清空提示内容
				String temp = updateVersion.getText();
				if (temp.equals("本次迭代版本格式为: 2.0.R16.21")) {
					updateVersion.setText("");
					updateVersion.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时，没有输入内容，显示提示内容
				String temp = updateVersion.getText();
				if (temp.equals("")) {
					updateVersion.setForeground(Color.GRAY);
					updateVersion.setText("本次迭代版本格式为: 2.0.R16.21");
				}
				JarsPanel jarPanel = (JarsPanel) jarsPanel.getComponent(0);
				String str = Arrays.asList(jarPanel.getPackInfo().getJarName().split("-")).get(0);
				// 首字母大写
				str = str.substring(0, 1).toUpperCase() + str.substring(1);
				patchName.setText("UYUN-" + str + "-v" + updateVersion.getText() + "-patch");
				patchName.setForeground(Color.BLACK);
			}
		});

		JLabel contentlabel = new JLabel("更改内容:");
		panel_3.add(contentlabel);
		content = new JTextField();
		panel_3.add(content);
		content.setText("说明本次版本修复了什么问题");
		content.setForeground(Color.GRAY);
		content.setColumns(30);
		content.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//获取焦点时，清空提示内容
				String temp = content.getText();
				if (temp.equals("说明本次版本修复了什么问题")) {
					content.setText("");
					content.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时，没有输入内容，显示提示内容
				String temp = content.getText();
				if (temp.equals("")) {
					content.setForeground(Color.GRAY);
					content.setText("说明本次版本修复了什么问题");
				}
			}
		});

		createLogButton = new JButton("生成记录patch");
		panel_3.add(createLogButton);

		// 生成日志
		createLogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<PackInfo> packInfoArrayList = new ArrayList<>(jarsPanel.getComponentCount());
				for (int i = 0; i < jarsPanel.getComponentCount(); i++) {
					JarsPanel jarPanel = (JarsPanel) jarsPanel.getComponent(i);
					PackInfo packInfo = jarPanel.getPackInfo();
					packInfo.setNewVersion(updateVersion.getText());
					packInfoArrayList.add(packInfo);
				}
				String contentText = content.getText();
				String log = PacketUtil.packNewPatchLog(packInfoArrayList, contentText);
				pacthLog.setText(log == null ? null : log);
			}
		});

		/**
		 *  log框
		 */
		JPanel panel2 = new JPanel();
		JPanel pane2_1 = new JPanel();
		panel2.add(pane2_1);
		panel2.setLayout(null);
		panel2.setBackground(Color.CYAN);
		getContentPane().add(panel2, BorderLayout.CENTER);
		panel2.setLayout(new GridLayout(1, 2, 1, 0));
		pacthLog = new JTextArea(10, 3);
		pacthLog.setColumns(90);
		pacthLog.setTabSize(2);
		pacthLog.setFont(new Font("标楷体", Font.BOLD, 12));
		// 激活断行不断字功能
		pacthLog.setWrapStyleWord(true);
		pacthLog.setBackground(Color.white);
		JScrollPane LogJScrollPane = new JScrollPane(pacthLog);
		pane2_1.add(LogJScrollPane);

		/**
		 * 打包
		 */
		JPanel panel3 = new JPanel();
		JPanel pane3_1 = new JPanel();
		panel3.add(pane3_1);
		panel3.setBackground(Color.CYAN);
		getContentPane().add(panel3, BorderLayout.SOUTH);
		panel3.setLayout(new GridLayout(1, 1, 1, 0));

		JLabel patchNameLabe2 = new JLabel("打包文件名");
		pane3_1.add(patchNameLabe2);
		patchName = new JTextField();
		pane3_1.add(patchName);
		patchName.setColumns(25);
		patchName.setText("输入迭代版本后自动生成");
		patchName.setForeground(Color.GRAY);
		patchName.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				//获取焦点时，清空提示内容
				String temp = patchName.getText();
				if (temp.equals("输入迭代版本后自动生成")) {
					patchName.setText("");
					patchName.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时，没有输入内容，显示提示内容
				String temp = patchName.getText();
				if (temp.equals("")) {
					patchName.setForeground(Color.GRAY);
					patchName.setText("输入迭代版本后自动生成");
				}
			}
		});

		JLabel pacthPathLabe = new JLabel("压缩包输出路径:");
		pane3_1.add(pacthPathLabe);
		patchPath = new JTextField();
		patchPath.setText("E:\\autoFileZip");
		pane3_1.add(patchPath);
		patchPath.setColumns(10);
		pathButton = new JButton("打包");
		pane3_1.add(pathButton);
		// 打包
		pathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pathButton.setEnabled(false);
				String patchNameText = patchName.getText();
				String patchPathText = patchPath.getText();
				String pacthLogText = pacthLog.getText();
				if (!(patchNameText.equals("") || pacthLogText.equals("") || patchPathText.equals(""))) {
					ArrayList<PackInfo> packInfoArrayList = new ArrayList<>(jarsPanel.getComponentCount());
					for (int i = 0; i < jarsPanel.getComponentCount(); i++) {
						JarsPanel jarPanel = (JarsPanel) jarsPanel.getComponent(i);
						PackInfo packInfo = jarPanel.getPackInfo();
						packInfo.setNewVersion(updateVersion.getText());
						packInfoArrayList.add(packInfo);
					}
					boolean flag = false;
					try {
						flag = PacketUtil.packTarGz(packInfoArrayList, patchNameText, patchPathText, pacthLogText);
					} catch (Exception e1) {
						FileUtils.deleteDir(new File(patchPathText+ File.separator+"5dce13949ae8ec82d0ab5d8f"));
						FileUtils.deleteDir(new File(patchPathText+ File.separator+patchNameText+".tar"));
						JOptionPane.showMessageDialog(panel, "打包失败" +e1);
					}
					// 打包成功弹窗
					getAlert(flag);
				}else{
				JOptionPane.showMessageDialog(panel, "打包失败,打包路径未填或者未生成patch等原因照成");
				}
				pathButton.setEnabled(true);
			}
		});
		// 窗口大小
		setSize(1050, 550);
		// 退出
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// 标题
		setTitle("打包");
		//居中
		setLocationRelativeTo(null);

	}

	//刷新界面函数
	private void myUpdateUI() {
		SwingUtilities.updateComponentTreeUI(PacthFrame.this);//添加或删除组件后,更新窗口
		JScrollBar jsb = jarsJScrollPane.getVerticalScrollBar();//得到垂直滚动条
		jsb.setValue(jsb.getMaximum());//把滚动条位置设置到最下面
	}

	// 打包提醒弹窗
	private void getAlert(boolean flag) {
		if (flag) {
			JOptionPane.showMessageDialog(panel, "打包成功");
		}
	}
}


