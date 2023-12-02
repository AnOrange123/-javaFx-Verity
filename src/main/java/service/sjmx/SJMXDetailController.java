package service.sjmx;

import beans.ProjectVariableManager;
import beans.SJMXManager;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import mapper.SJMXMapper;
import service.main.EmptyMainController;
import service.sjkf.SJKFController;
import utils.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJMXDetailController
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/10 - 19:27
 */
public class SJMXDetailController {
	@FXML
	private TreeView<String> ModuleTreeView;
	//包含文件树和标题的vbox
	@FXML
	private VBox MainBox;
	@FXML
	private Label SJMXTitleLabel;
	@FXML
	private VBox DataModuleContent;
	private static SJMXDetailController sjmxDetailController;
	private static VBox root;


	//显示右键菜单
	@FXML
	public void contextMenuDisplay(MouseEvent event) throws SQLException, IOException {
		//判断鼠标点击，如果是右键则显示右键菜单
		if (event.getButton() == MouseButton.SECONDARY) {
			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = ModuleTreeView.getSelectionModel().getSelectedItem();

			//显示右键菜单
			MainBox.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
				@Override
				public void handle(ContextMenuEvent event) {
					ModuleTreeView.getSelectionModel().clearSelection();
					//获取右键菜单并显示
					ContextMenu customContextMenu = SJMXContextMenuGeneral.getInstance(SJMXTitleLabel.getText(), selectedItem, ModuleTreeView);
					customContextMenu.show(MainBox, event.getScreenX(), event.getScreenY());
				}
			});
		}

		//左键时关闭右键菜单
		if (event.getButton() == MouseButton.PRIMARY){
			SJMXContextMenuGeneral.clearInstance();
		}

		//双击文件打开
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 ){
			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = ModuleTreeView.getSelectionModel().getSelectedItem();
			SVGPath icon = (SVGPath) selectedItem.getGraphic();

			//选择非空并且点击的非文件夹则打开文件
			if (!SomeConstant.DIRECTORY_ICON.equals(icon.getContent()) && selectedItem.getValue() != null){
				String pathContainsRoot = SJMXContextMenuGeneral.pathGet(selectedItem);
				VBox dataModuleContent = new DataModuleEditController().createDataModuleWindow(SJMXTitleLabel.getText(), pathContainsRoot);
				DataModuleContent.getChildren().clear();
				DataModuleContent.getChildren().add(dataModuleContent);
			}
		}
	}

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
		}else{
			SVGPath svgPath = new SVGPath();
			svgPath.setContent(SomeConstant.MODULE_ICON);
			svgPath.setFill(Color.WHITE);
			svgPath.setStroke(Color.WHITE);
			svgPath.setScaleX(1.3);
			svgPath.setScaleY(1.3);
			svgPath.setStrokeWidth(0.3);
			treeItem.setGraphic(svgPath);
		}

		parent.getChildren().add(treeItem);
		return treeItem;
	}

	//打开具体的项目界面
	public static void createProjectWindow(ActionEvent event, String projectName) throws IOException, SQLException {
		if (sjmxDetailController == null){
			//获取数据模型设计界面窗口
			FXMLLoader fxmlLoader = new FXMLLoader(SJMXDetailController.class.getClassLoader().getResource("sjmx/SJMXDetail.fxml"));
			VBox projectContent = fxmlLoader.load();
			root = projectContent;
			sjmxDetailController = fxmlLoader.getController();

			//设置项目名称
			Label SJMXTitleLabel = (Label) projectContent.lookup("#SJMXTitleLabel");
			SJMXTitleLabel.setText(projectName);

			//设置文件树

			//创建根节点
			TreeItem<String> rootItem = new TreeItem<>("root");
			//查询文件树信息
			List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName());
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> queryResult = SJMXMapper.queryFileTreeAll(paramsList);

			//遍历创建文件树
			for (LinkedHashMap<Object, Object> moduleFileInfo : queryResult) {
				//切割路径
				String filePath = (String) moduleFileInfo.get(SystemLibConstant.DATAMODULEFILETREE_PATH);
				String[] splitPath = filePath.split("\\.");

				//如果父级为根节点，则直接创建TreeItem
				if (splitPath.length == 2){
					//判断是否已经创建分支，如果没有创建，则创建分支
					TreeItem<String> treeItem = ifExitsAndReturnTreeITem(new TreeItem<>(splitPath[1]), rootItem);
					if (treeItem == rootItem){
						makeBranch(splitPath[1], rootItem, (String) moduleFileInfo.get(SystemLibConstant.DATAMODULEFILETREE_TYPE));
					}
				}else{
					//遍历切割完的数组
					TreeItem<String> parentTreeItem = rootItem;
					for (int i = 1; i < splitPath.length; i++) {
						//逐级判断分支是否已经创建，如果没有创建则创建分支
						TreeItem<String> tempParentTreeItem = parentTreeItem;
						parentTreeItem = ifExitsAndReturnTreeITem(new TreeItem<>(splitPath[i]), parentTreeItem);
						if (parentTreeItem == tempParentTreeItem && i < splitPath.length-1){
							parentTreeItem = makeBranch(splitPath[i], parentTreeItem, SomeConstant.DIRECTORY);
						}
						if (parentTreeItem == tempParentTreeItem && i == splitPath.length-1){
							parentTreeItem = makeBranch(splitPath[i], parentTreeItem, (String) moduleFileInfo.get(SystemLibConstant.DATAMODULEFILETREE_TYPE));
						}
					}
				}
			}

			TreeView<String> treeView = (TreeView<String>) projectContent.lookup("#ModuleTreeView");
			treeView.setRoot(rootItem);

			//替换主体界面为数据开发界面
			EmptyMainController.replaceMainModule(event,projectContent);
		}else{
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

	/**
	 * 替换页面中的内容
	 * @param event
	 * @param content  替换后的内容
	 */
	public static void replaceMainModule(ActionEvent event, Node content){
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		VBox moduleContent = (VBox) stage.getScene().getRoot().lookup("#DataModuleContent");
		moduleContent.getChildren().clear();
		moduleContent.getChildren().add(content);
	}
}