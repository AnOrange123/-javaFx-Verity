package beans;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.HashMap;
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
public class SJKFTabManager {
	//tab    [path, type]
	//tab和path的映射
	private static LinkedHashMap<Tab, List<String>> tabsMap;
	private static SJKFTabManager sjkfTabManager;

	private SJKFTabManager() {
	}

	public static SJKFTabManager getInstance(){
		if (sjkfTabManager == null) {
			sjkfTabManager = new SJKFTabManager();
		}
		return sjkfTabManager;
	}

	//单例创建tabs，hashmap封装 tab ————> path
	public LinkedHashMap<Tab, List<String>> getTabPathMapper() {
		if (tabsMap == null) {
			tabsMap = new LinkedHashMap<>();
		}
		return tabsMap;
	}
}
