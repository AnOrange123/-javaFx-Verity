package service.sjmx;

import beans.ProjectVariableManager;
import constants.SomeConstant;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.shape.SVGPath;
import mapper.SJMXMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:SJMXContextMenuGeneral
 * Package:service.sjmx
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/10 - 22:05
 */
public class SJMXContextMenuGeneral {
	private static ContextMenu contextMenu;
	private static MenuItem directCreateItem = new MenuItem("新建文件夹");
	private static MenuItem directDeleteItem = new MenuItem("删除文件夹");
	private static MenuItem moduleRenameItem = new MenuItem("重命名");
	private static MenuItem moduleDeleteItem = new MenuItem("删除");
	private static MenuItem moduleCreateItem = new MenuItem("新建模型");

	private SJMXContextMenuGeneral() {
	}

	//单例创建右键菜单
	public static ContextMenu getInstance(String projectName, TreeItem<String> selectedItem, TreeView<String> treeView){
		//右键菜单功能设置
		menuItemFuncSet(projectName, selectedItem, treeView);

		if (contextMenu != null){
			contextMenu.hide();
		}
		contextMenu = new ContextMenu();

		//通过icon弹出右键功能
		if (selectedItem == null){
			contextMenu.getItems().addAll(directCreateItem, moduleCreateItem);
		} else{
			SVGPath icon = (SVGPath) selectedItem.getGraphic();
			if (!SomeConstant.DIRECTORY_ICON.equals(icon.getContent())){
				contextMenu.getItems().addAll(moduleRenameItem, moduleDeleteItem);
			} else {
				contextMenu.getItems().addAll(moduleCreateItem, directDeleteItem);
			}
		}

		return contextMenu;
	}

	private static void menuItemFuncSet(String projectName, TreeItem<String> selectedItem, TreeView<String> treeView) {
		//获取文件绝对路径
		String path = pathGet(selectedItem);

		//文件夹创建动作
		directCreateItem.setOnAction(event -> {
			try {
				new DirectoryCreateController().createConfirmWindow(projectName, path, selectedItem, treeView);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		//文件夹删除动作
		directDeleteItem.setOnAction(event -> {
			List<Object> params1 = Arrays.asList( projectName,path,SomeConstant.DIRECTORY, ProjectVariableManager.getInstance().getProjectName(), ProjectVariableManager.getInstance().getProjectName(), ProjectVariableManager.getInstance().getProjectName());
			List<Object> params2 = Arrays.asList( projectName);
			List<List<Object>> paramsList = Arrays.asList(null, params1, params2);

			try {
				SJMXMapper.deleteDirectory(paramsList, path);
				selectedItem.getParent().getChildren().remove(selectedItem);
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
		});

		//模型重命名动作
		moduleRenameItem.setOnAction(event -> {
			try {
				new FileCreateAndRenameController().createEditModuleWindow(projectName, path, selectedItem, treeView);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		//模型删除动作
		moduleDeleteItem.setOnAction(event -> {
			List<Object> params1 = Arrays.asList(projectName,path, SomeConstant.DIRECTORY);
			List<Object> params2 = Arrays.asList(projectName, path);
			List<List<Object>> paramsList = Arrays.asList(null, params1, params2);

			try {
				SJMXMapper.deleteFile(paramsList);
				selectedItem.getParent().getChildren().remove(selectedItem);
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
		});

		//模型创建动作
		moduleCreateItem.setOnAction(event -> {
			try {
				new FileCreateAndRenameController().createNewModuleWindow(projectName, path, selectedItem, treeView);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	//清除右键菜单
	public static void clearInstance(){
		if (contextMenu != null){
			contextMenu.hide();
		}
	}

	//获取选中文件项的全路径
	public static String pathGet(TreeItem<String> selectedItem){
		String selectedValue;
		if (selectedItem == null){
			selectedValue = "root";
		}else {
			selectedValue = selectedItem.getValue();
			TreeItem<String> selectedParentItem = selectedItem.getParent();
			while (selectedParentItem.getValue() != "root"){
				selectedValue = selectedParentItem.getValue() + "." + selectedValue;
				selectedParentItem = selectedParentItem.getParent();
			}
			selectedValue = "root." + selectedValue;
		}
		return selectedValue;
	}
}
