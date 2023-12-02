package service.sjkf;

import beans.ProjectVariableManager;
import beans.SJKFTabManager;
import beans.sjkf.FileTreeBean;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.web.WebView;
import mapper.SJKFMapper;
import service.main.EmptyMainController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * ClassName:SJKFController
 * Package:SJKF
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/4 - 12:34
 */
public class SJKFController {
	@FXML
	private static VBox Main;

	//文件树
	@FXML
	private TreeView<String> SqlFileTree;

	//包含文件树和数据开发标题的vbox
	@FXML
	private VBox MainBox;

	//tab标签内容
	@FXML
	private TabPane tabBox;

	private static SJKFController sjkfController;
	private static VBox root;

	/**
	 * 给文件树创建分支
	 * @param title  分支的名字
	 * @param parent  分支的父级
	 * @param fileType  文件类型
	 * @return
	 */
	public static TreeItem<String> makeBranch(String title, TreeItem<String> parent, String fileType) {
		TreeItem<String> treeItem = new TreeItem<>(title);

		//根据文件类型设置不同的svg图标
		if (SomeConstant.DIRECTORY.equals(fileType)){
			SVGPath svgPath = new SVGPath();
			svgPath.setContent(SomeConstant.DIRECTORY_ICON);
			svgPath.setFill(Color.WHITE);
			svgPath.setStroke(Color.WHITE);
			svgPath.setScaleX(1.25);
			svgPath.setScaleY(1.25);
			svgPath.setStrokeWidth(0.5);
			treeItem.setGraphic(svgPath);
		} else if (SomeConstant.SQLFILE.equals(fileType)) {
			SVGPath svgPath = new SVGPath();
			svgPath.setContent(SomeConstant.SQLFILE_ICON);
			svgPath.setFill(Color.WHITE);
			svgPath.setStroke(Color.WHITE);
			svgPath.setScaleX(0.95);
			svgPath.setScaleY(0.95);
			svgPath.setStrokeWidth(0.2);
			treeItem.setGraphic(svgPath);
		}else{
			SVGPath svgPath = new SVGPath();
			svgPath.setContent(SomeConstant.HQLFILE_ICON);
			svgPath.setFill(Color.WHITE);
			svgPath.setStroke(Color.WHITE);
			svgPath.setScaleX(0.95);
			svgPath.setScaleY(0.95);
			svgPath.setStrokeWidth(0.2);
			treeItem.setGraphic(svgPath);
		}

		parent.getChildren().add(treeItem);
		return treeItem;
	}

	//显示右键菜单
	@FXML
	public void contextMenuDisplay(MouseEvent event) throws SQLException, IOException {
		//判断鼠标点击，如果是右键则显示右键菜单
		if (event.getButton() == MouseButton.SECONDARY) {

			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = SqlFileTree.getSelectionModel().getSelectedItem();

			//显示右键菜单
			MainBox.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
				@Override
				public void handle(ContextMenuEvent event) {
					SqlFileTree.getSelectionModel().clearSelection();
					//获取右键菜单并显示
					ContextMenu customContextMenu = SJKFContextMenuGeneral.getInstance(selectedItem, SqlFileTree);
					customContextMenu.show(MainBox, event.getScreenX(), event.getScreenY());
				}
			});
		}

		//左键时关闭右键菜单
		if (event.getButton() == MouseButton.PRIMARY){
			SJKFContextMenuGeneral.clearInstance();
		}

		//双击文件打开
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 ){
			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = SqlFileTree.getSelectionModel().getSelectedItem();
			SVGPath icon = (SVGPath) selectedItem.getGraphic();

			//选择非空并且点击的非文件夹则打开文件
			if (!SomeConstant.DIRECTORY_ICON.equals(icon.getContent()) && selectedItem.getValue() != null){
				//查询文件路径
				String path = SJKFContextMenuGeneral.pathGet(selectedItem);
				//获取文件内容
				List<Object> params = Arrays.asList(selectedItem.getValue(),path.substring(0, path.lastIndexOf(".")), ProjectVariableManager.getInstance().getProjectName());
				List<List<Object>> paramsList = Arrays.asList(null,params );
				List<LinkedHashMap<Object, Object>> queryResult = SJKFMapper.queryFileContent(paramsList);
				LinkedHashMap<Object, Object> result = queryResult.get(0);

				//创建tab
				Tab tab = new QueryTabController().loadQueryTabWindow((String) result.get(SystemLibConstant.SQLFILEINFO_PARENTFILENAME) + "." + selectedItem.getValue(),(String) result.get(SystemLibConstant.SQLFILEINFO_CONTENT), (String) result.get(SystemLibConstant.SQLFILEINFO_FILETYPE));
				Circle unsavedIndicator = new Circle(4, Color.DARKGOLDENROD);
				tab.setGraphic(unsavedIndicator);

				// 监听Tab的关闭请求事件
				tab.setOnCloseRequest(new EventHandler<Event>() {
					@Override
					public void handle(Event event) {
						try {
							HBox hBox = (HBox) tab.getContent();
							SplitPane splitPane= (SplitPane) hBox.lookup("#splitPane");
							WebView codeEditor = (WebView)splitPane.getItems().get(0);
							if (checkContentChanged(SJKFTabManager.getInstance().getTabPathMapper().get(tab).get(0), codeEditor)) {
								// 取消Tab的关闭请求
								event.consume();
								//未保存
								new UnsaveAlertController().createAlertBoxWindow("当前文件未保存，继续关闭将丢失文件",  tab.getTabPane(), tab);
							} else {
								//已保存
								tab.getTabPane().getTabs().remove(tab);
								LinkedHashMap<Tab, List<String>> tabsMapper = SJKFTabManager.getInstance().getTabPathMapper();
								tabsMapper.remove(tab);
							}
						} catch (SQLException | IOException e) {
							throw new RuntimeException(e);
						}
					}
				});

				// 获取 TabPane 的选择模型
				SingleSelectionModel<Tab> selectionModel = tabBox.getSelectionModel();

				tabBox.getTabs().add(tab);
				//切换tab
				selectionModel.select(tab);
			}
		}
	}

	/**
	 * 检查编辑器内容是否发生变化
	 * @return 变化：true  没有变化：false
	 */
	public static boolean checkContentChanged(String path, WebView webView) throws SQLException, IOException {
		//查询最近一次保存的内容
		List<Object> params = Arrays.asList(path.split("\\.")[path.split("\\.").length-1],path.substring(0, path.lastIndexOf(".")), ProjectVariableManager.getInstance().getProjectName());
		List<List<Object>> paramsList = Arrays.asList(null,params );
		List<LinkedHashMap<Object, Object>> queryResult = SJKFMapper.queryFileContent(paramsList);
		LinkedHashMap<Object, Object> result = queryResult.get(0);
		String originContent = (String) result.get(SystemLibConstant.SQLFILEINFO_CONTENT);

		//校验是否发生变化
		String currentContent = (String) webView.getEngine().executeScript("editor.getValue()");
		return !originContent.equals(currentContent.replace("\\","\\\\"));
	}

	//创建数据开发模块界面
	public static void createSjkfWindow(ActionEvent event) throws IOException, SQLException {
		if (sjkfController == null){
			//获取数据开发界面窗口
			FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjkf/SJKF.fxml"));
			VBox sjkfContent = fxmlLoader.load();
			SJKFController sjkfController = fxmlLoader.getController();

			SJKFController.sjkfController = sjkfController;
			SJKFController.root = sjkfContent;

			//设置sql文件树

			//创建根节点
			TreeItem<String> rootItem = new TreeItem<>("root");
			//查询sql文件树信息
			List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName());
			List<List<Object>> paramsList = Arrays.asList(null,params );
			List<LinkedHashMap<Object, Object>> queryResult = SJKFMapper.queryFileInfoByProject(paramsList);

			//遍历查询结果，将结果封装进beans的集合
			List<FileTreeBean> sqlFileTreeResult = new ArrayList<>();
			for (LinkedHashMap<Object, Object> sqlFileInfo : queryResult) {
				FileTreeBean sqlFileTreeBean = new FileTreeBean();
				sqlFileTreeBean.setFileName((String) sqlFileInfo.get(SystemLibConstant.SQLFILEINFO_FILENAME));
				sqlFileTreeBean.setParentFileName((String) sqlFileInfo.get(SystemLibConstant.SQLFILEINFO_PARENTFILENAME));
				sqlFileTreeBean.setFileType((String) sqlFileInfo.get(SystemLibConstant.SQLFILEINFO_FILETYPE));
				sqlFileTreeBean.setContent((String) sqlFileInfo.get(SystemLibConstant.SQLFILEINFO_CONTENT));
				sqlFileTreeResult.add(sqlFileTreeBean);
			}

			//遍历创建文件树
			for (FileTreeBean sqlFileTreeBean : sqlFileTreeResult) {
				//如果父级为根节点，则直接创建TreeItem
				if ("root".equals(sqlFileTreeBean.getParentFileName())){
					//判断是否已经创建分支，如果没有创建，则创建分支
					TreeItem<String> treeItem = ifExitsAndReturnTreeITem(new TreeItem<>(sqlFileTreeBean.getFileName()), rootItem);
					if (treeItem == rootItem){
						makeBranch(sqlFileTreeBean.getFileName(), rootItem, sqlFileTreeBean.getFileType());
					}
				}else{
					//父级非根节点，对父级按照.切割
					String finalName = sqlFileTreeBean.getParentFileName() + "." + sqlFileTreeBean.getFileName();
					String[] split = finalName.split("\\.");

					//遍历切割完的数组
					TreeItem<String> parentTreeItem = rootItem;
					for (int i = 1; i < split.length; i++) {
						//逐级判断分支是否已经创建，如果没有创建则创建分支
						TreeItem<String> tempParentTreeItem = parentTreeItem;
						parentTreeItem = ifExitsAndReturnTreeITem(new TreeItem<>(split[i]), parentTreeItem);
						if (parentTreeItem == tempParentTreeItem && i < split.length-1){
							parentTreeItem = makeBranch(split[i], parentTreeItem, SomeConstant.DIRECTORY);
						}
						if (parentTreeItem == tempParentTreeItem && i == split.length-1){
							parentTreeItem = makeBranch(split[i], parentTreeItem, sqlFileTreeBean.getFileType());
						}
					}
				}
			}

			TreeView<String> treeView = (TreeView<String>) sjkfContent.lookup("#SqlFileTree");
			treeView.setRoot(rootItem);

			TabPane tabBox = (TabPane) sjkfContent.getChildren().get(0).lookup("#tabBox");
			// 添加监听器来监听 Tab 切换事件
			tabBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				try {
					HBox hBox = (HBox) oldValue.getContent();
					SplitPane splitPane= (SplitPane) hBox.lookup("#splitPane");
					WebView codeEditor = (WebView)splitPane.getItems().get(0);
					if (oldValue != null && checkContentChanged(SJKFTabManager.getInstance().getTabPathMapper().get(oldValue).get(0), codeEditor)) {
						//设置未保存状态
						Circle unsavedIndicator = new Circle(4, Color.DARKGOLDENROD);
						oldValue.setGraphic(unsavedIndicator);
					}
				} catch (SQLException | IOException e) {
					throw new RuntimeException(e);
				}
			});

			//替换主体界面为数据开发界面
			EmptyMainController.replaceMainModule(event,sjkfContent);
		}else {
			//替换主体界面为数据开发界面
			EmptyMainController.replaceMainModule(event,root);
		}
	}

	/**
	 * 判断是否存在子分支并返回分支
	 * @param queryItem 要查询的子分支
	 * @param parentItem  父分支
	 * @return
	 */
	public static TreeItem<String> ifExitsAndReturnTreeITem(TreeItem<String> queryItem, TreeItem<String> parentItem){
		for (TreeItem<String> childItem : parentItem.getChildren()) {
			//如果存在则直接返回
			if (childItem.getValue().equals(queryItem.getValue())){
				return childItem;
			}
		}
		//如果不存在则返回父分支
		return parentItem;
	}
}