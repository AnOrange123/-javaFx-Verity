package service.sjtb;

import beans.ProjectVariableManager;
import beans.SJKFTabManager;
import beans.sjtb.JobFieldMapperBean;
import com.google.gson.Gson;
import com.jcraft.jsch.*;
import constants.SomeConstant;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import mapper.SJKFMapper;
import mapper.SJTBMapper;
import service.common.Loading;
import service.sjkf.SJKFController;
import utils.PropertiesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:DataxScriptPreviewController
 * Package:service.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/27 - 20:33
 */
public class DataxScriptPreviewController {
	private static String path;
	private static ActionEvent actionEvent;
	public void createDataxScriptPreviewWindow(ActionEvent actionEvent, String jsonText, String path) throws IOException, SQLException {
		this.path = path;
		this.actionEvent = actionEvent;

		FXMLLoader fxmlLoader = new FXMLLoader(SJTBController.class.getClassLoader().getResource("sjtb/DataxScriptPreview.fxml"));
		AnchorPane anchorPane = fxmlLoader.load();

		WebView jsonDisplayWebView = (WebView) anchorPane.lookup("#jsonDisplayWebView");
		WebEngine webEngine = jsonDisplayWebView.getEngine();
		webEngine.load(getClass().getResource("/ace/editor_json.html").toExternalForm());
		webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				// 在 WebView 引擎加载完成后设置内容
				String finalContent = jsonText.replace("`", "\\`");
				webEngine.executeScript("editor.setValue(`" + finalContent + "`);");
			}
		});

		Stage stage = new Stage();
		stage.getIcons().add(new Image("VerityIcon.png"));
		stage.setTitle("脚本预览");
		stage.setScene(new Scene(anchorPane));
		stage.show();
	}

	@FXML
	private WebView jsonDisplayWebView;

	@FXML
	private Button executeButton;

	@FXML
	void cancelButtonClick(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void executeButtonClick(ActionEvent event) throws SQLException, IOException {
		saveButtonClick(event);
		String content = (String) jsonDisplayWebView.getEngine().executeScript("editor.getValue()");

		//等待加载页
		Loading loading = new Loading((Stage) ((Node) actionEvent.getSource()).getScene().getWindow(), "同步数据中，请稍后...");
		loading.show();

		new Thread(() -> {
			try {
				//执行脚本
				try {
					JSch jsch = new JSch();
					Session session = jsch.getSession(PropertiesUtil.getProperty("remote.ssh.user"), PropertiesUtil.getProperty("remote.ssh.host"), Integer.parseInt(PropertiesUtil.getProperty("remote.ssh.port")));
					session.setPassword(PropertiesUtil.getProperty("remote.ssh.passwd"));
					session.setConfig("StrictHostKeyChecking", "no");

					// 连接远程主机
					session.connect();

					//写入配置文件
					// 创建SFTP通道
					ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
					channelSftp.connect();
					// 在远程主机上创建文件并写入内容
					try (OutputStream outputStream = channelSftp.put(SomeConstant.DATAXCONFTEMP, ChannelSftp.OVERWRITE)) {
						byte[] contentBytes = content.getBytes();
						outputStream.write(contentBytes);
					} catch (SftpException e) {
						throw new RuntimeException(e);
					}
					// 关闭SFTP通道
					channelSftp.disconnect();

					//执行同步命令
					// 创建执行命令的通道
					ChannelExec channel = (ChannelExec) session.openChannel("exec");
					// 设置要执行的命令
					String command = SomeConstant.DATAXBINPATH + " " + SomeConstant.DATAXCONFTEMP;
					channel.setCommand(command);

					// 获取命令执行的输出流
					InputStream in = channel.getInputStream();
					// 连接通道
					channel.connect();
					// 读取输出流中的数据
					byte[] tmp = new byte[1024];
					while (true) {
						while (in.available() > 0) {
							int i = in.read(tmp, 0, 1024);
							if (i < 0) break;
							loading.showMessage(new String(tmp, 0, i));
						}
						if (channel.isClosed()) {
							if (in.available() > 0) continue;
							loading.showMessage("Exit status: " + channel.getExitStatus());
							loading.showMessage("\n双击鼠标关闭日志");
							loading.closeLoadingView();
							loading.setCloseEvent();
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (Exception ee) {
							ee.printStackTrace();
						}
					}
					channel.disconnect();
					// 关闭会话
					session.disconnect();

				} catch (JSchException | IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@FXML
	void saveButtonClick(ActionEvent event) throws SQLException, IOException {
		String content = (String) jsonDisplayWebView.getEngine().executeScript("editor.getValue()");
		List<Object> params = Arrays.asList(content.replace("\\","\\\\"), ProjectVariableManager.getInstance().getProjectName(), path, SomeConstant.JOB);
		List<List<Object>> paramsList = Arrays.asList(null, params);

		SJTBMapper.updateJobBaseInfo(paramsList);
		cancelButtonClick(event);
	}
}