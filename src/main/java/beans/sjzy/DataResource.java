package beans.sjzy;

import javafx.scene.control.Button;

/**
 * ClassName:DataResource
 * Package:beans.sjzy
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/18 - 22:15
 */
public class DataResource extends Button {
	private String resourceName;
	private String resourceUrl;
	private String resourceType;
	private Button edit;
	private Button delete;
	private Button login;

	public DataResource(String resourceName, String resourceUrl, String resourceType) {
		setResourceName(resourceName);
		setResourceUrl(resourceUrl);
		setResourceType(resourceType);

		setEdit(new Button("编辑"));
		setDelete(new Button("删除"));
		setLogin(new Button("进入资源"));
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public Button getEdit() {
		return edit;
	}

	public void setEdit(Button edit) {
		this.edit = edit;
	}

	public Button getDelete() {
		return delete;
	}

	public void setDelete(Button delete) {
		this.delete = delete;
	}

	public Button getLogin() {
		return login;
	}

	public void setLogin(Button login) {
		this.login = login;
	}
}
