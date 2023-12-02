package service.sjtb;

import beans.ProjectVariableManager;
import constants.SomeConstant;
import constants.SystemLibConstant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import mapper.SJMXMapper;
import mapper.SJTBMapper;
import service.main.EmptyMainController;
import service.sjkf.SJKFController;
import service.sjmx.DataModuleEditController;
import service.sjmx.SJMXContextMenuGeneral;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJTBController
 * Package:service.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/24 - 14:46
 */
public class SJTBController {
	@FXML
	private TreeView<String> JobTreeView;

	@FXML
	private VBox Main;

	@FXML
	private VBox MainBox;
	@FXML
	private VBox JobDetailContent;
	private static VBox root;
	private static SJTBController sjtbController;


	//显示右键菜单
	@FXML
	public void contextMenuDisplay(MouseEvent event) throws SQLException, IOException {
		//判断鼠标点击，如果是右键则显示右键菜单
		if (event.getButton() == MouseButton.SECONDARY) {
			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = JobTreeView.getSelectionModel().getSelectedItem();

			//显示右键菜单
			MainBox.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
				@Override
				public void handle(ContextMenuEvent event) {
					JobTreeView.getSelectionModel().clearSelection();
					//获取右键菜单并显示
					ContextMenu customContextMenu = SJTBContextMenuGeneral.getInstance(ProjectVariableManager.getInstance().getProjectName(), selectedItem, JobTreeView);
					customContextMenu.show(MainBox, event.getScreenX(), event.getScreenY());
				}
			});
		}

		//左键时关闭右键菜单
		if (event.getButton() == MouseButton.PRIMARY){
			SJTBContextMenuGeneral.clearInstance();
		}

		//双击文件打开
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 ){
			//获取文件树和选择的文件对象
			TreeItem<String> selectedItem = JobTreeView.getSelectionModel().getSelectedItem();
			SVGPath icon = (SVGPath) selectedItem.getGraphic();

			//选择非空并且点击的非文件夹则打开文件
			if (!SomeConstant.DIRECTORY_ICON.equals(icon.getContent()) && selectedItem.getValue() != null){
				String pathContainsRoot = SJTBContextMenuGeneral.pathGet(selectedItem);
				VBox jobEditContent = new JobEditController().createJobEditWindow(pathContainsRoot);
				JobDetailContent.getChildren().clear();
				JobDetailContent.getChildren().add(jobEditContent);
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
			svgPath.setContent(SomeConstant.JOB_ICON);
			svgPath.setFill(Color.WHITE);
			svgPath.setStroke(Color.WHITE);
			svgPath.setScaleX(1.3);
			svgPath.setScaleY(1.3);
			svgPath.setStrokeWidth(0.8);
			treeItem.setGraphic(svgPath);
		}

		parent.getChildren().add(treeItem);
		return treeItem;
	}

	//打开具体的项目界面
	public static void createSJTBWindow(ActionEvent event, String projectName) throws IOException, SQLException {
		if (sjtbController == null){
			//获取数据开发界面窗口
			FXMLLoader fxmlLoader = new FXMLLoader(SJKFController.class.getClassLoader().getResource("sjtb/SJTB.fxml"));
			VBox projectContent = fxmlLoader.load();

			SJTBController.sjtbController = fxmlLoader.getController();
			SJTBController.root = projectContent;

			//设置文件树
			//创建根节点
			TreeItem<String> rootItem = new TreeItem<>("root");
			//查询文件树信息
			List<Object> params = Arrays.asList(ProjectVariableManager.getInstance().getProjectName());
			List<List<Object>> paramsList = Arrays.asList(null, params);
			List<LinkedHashMap<Object, Object>> queryResult = SJTBMapper.queryFileTreeAll(paramsList);

			//遍历创建文件树
			for (LinkedHashMap<Object, Object> row : queryResult) {
				//切割路径
				String filePath = (String) row.get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_JOBPATH);
				String[] splitPath = filePath.split("\\.");

				//如果父级为根节点，则直接创建TreeItem
				if (splitPath.length == 2){
					//判断是否已经创建分支，如果没有创建，则创建分支
					TreeItem<String> treeItem = ifExitsAndReturnTreeITem(new TreeItem<>(splitPath[1]), rootItem);
					if (treeItem == rootItem){
						makeBranch(splitPath[1], rootItem, (String) row.get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE));
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
							parentTreeItem = makeBranch(splitPath[i], parentTreeItem, (String) row.get(SystemLibConstant.SYNCHRONIZEJOBINFOTABLE_PATHTYPE));
						}
					}
				}
			}

			TreeView<String> treeView = (TreeView<String>) projectContent.lookup("#JobTreeView");
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

}
