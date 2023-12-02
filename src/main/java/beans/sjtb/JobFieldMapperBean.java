package beans.sjtb;

/**
 * ClassName:JobFieldMapperBean
 * Package:beans.sjtb
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/25 - 21:53
 */
public class JobFieldMapperBean {
	private String sourceField;
	private String targetFiled;

	public JobFieldMapperBean() {
	}

	public String getSourceField() {
		return sourceField;
	}

	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}

	public String getTargetFiled() {
		return targetFiled;
	}

	public void setTargetFiled(String targetFiled) {
		this.targetFiled = targetFiled;
	}
}
