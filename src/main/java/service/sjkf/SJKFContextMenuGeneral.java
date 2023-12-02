package service.sjkf;

import beans.ProjectVariableManager;
import constants.SomeConstant;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.shape.SVGPath;
import mapper.SJKFMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName:SJKFContextMenuGeneral
 * Package:SJKF
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/11 - 16:48
 */
public class SJKFContextMenuGeneral {
	private static ContextMenu contextMenu;
	private static MenuItem directCreateItem = new MenuItem("新建文件夹");
	private static MenuItem directDeleteItem = new MenuItem("删除文件夹");
	private static MenuItem directRenameItem = new MenuItem("重命名");
	private static MenuItem fileRenameItem = new MenuItem("重命名");
	private static MenuItem fileDeleteItem = new MenuItem("删除");
	private static MenuItem fileCreateItem = new MenuItem("新建文件");

	private SJKFContextMenuGeneral() {
	}

	//单例创建右键菜单
	public static ContextMenu getInstance(TreeItem<String> selectedItem, TreeView<String> treeView){
		//右键菜单功能设置
		menuItemFuncSet(selectedItem, treeView);

		if (contextMenu != null){
			contextMenu.hide();
		}
		contextMenu = new ContextMenu();

		//通过icon弹出右键功能
		if (selectedItem == null){
			contextMenu.getItems().addAll(directCreateItem, fileCreateItem);
		} else{
			SVGPath icon = (SVGPath) selectedItem.getGraphic();
			if (!SomeConstant.DIRECTORY_ICON.equals(icon.getContent())){
				contextMenu.getItems().addAll(fileRenameItem, fileDeleteItem);
			} else{
				contextMenu.getItems().addAll(directCreateItem, directDeleteItem, directRenameItem, fileCreateItem);
			}
		}

		return contextMenu;
	}

	private static void menuItemFuncSet(TreeItem<String> selectedItem, TreeView<String> treeView) {
		//获取文件绝对路径
		String finalSelectedValue = pathGet(selectedItem);

		//创建文件夹动作
		directCreateItem.setOnAction(event -> {
			try {
				new DirectoryCreateController().createConfirmWindow(finalSelectedValue, selectedItem, treeView);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		//删除文件夹动作
		directDeleteItem.setOnAction(event -> {

			List<Object> params = Arrays.asList(selectedItem.getValue(),finalSelectedValue.substring(0, finalSelectedValue.lastIndexOf(".")),SomeConstant.DIRECTORY, ProjectVariableManager.getInstance().getProjectName(), ProjectVariableManager.getInstance().getProjectName(), ProjectVariableManager.getInstance().getProjectName());
			List<List<Object>> paramsList = Arrays.asList(null,params );
			try {
				SJKFMapper.deleteDirectory(paramsList, finalSelectedValue);
				selectedItem.getParent().getChildren().remove(selectedItem);
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
		});

		//文件夹重命名动作
		directRenameItem.setOnAction(event -> {
			try {
				new FileRenameController().renameConfirmWindow(finalSelectedValue, selectedItem, SomeConstant.DIRECTORY);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		//文件重命名动作
		fileRenameItem.setOnAction(event -> {
			try {
				new FileRenameController().renameConfirmWindow(finalSelectedValue, selectedItem, SomeConstant.FILE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		//删除文件动作
		fileDeleteItem.setOnAction(event -> {
			List<Object> params = Arrays.asList(selectedItem.getValue(),finalSelectedValue.substring(0, finalSelectedValue.lastIndexOf(".")), SomeConstant.DIRECTORY, ProjectVariableManager.getInstance().getProjectName());
			List<List<Object>> paramsList = Arrays.asList(null,params );

			try {
				SJKFMapper.deleteFile(paramsList);
				selectedItem.getParent().getChildren().remove(selectedItem);
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
		});

		//文件创建动作
		fileCreateItem.setOnAction(event -> {
			try {
				new FileCreateController().createConfirmWindow(finalSelectedValue, selectedItem, treeView);
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

