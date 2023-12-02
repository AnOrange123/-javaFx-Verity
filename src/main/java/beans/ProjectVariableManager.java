package beans;

import javafx.scene.control.Tab;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * ClassName:SJKFTabManager
 * Package:beans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/10/29 - 0:51
 */
public class ProjectVariableManager {
	private static String user;
	private static String projectName;
	private static ProjectVariableManager projectVariableManager;

	private ProjectVariableManager() {
	}

	public static ProjectVariableManager getInstance(){
		if (projectVariableManager == null) {
			projectVariableManager = new ProjectVariableManager();
		}
		return projectVariableManager;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		ProjectVariableManager.projectName = projectName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		ProjectVariableManager.user = user;
	}
}