package constants;

/**
 * ClassName:RegexConstant
 * Package:constants
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/9/25 - 22:49
 */
public interface RegexConstant {
	String tableCommentPattern = "(?<=COMMENT=')(.*?)(?=')";
	String fieldCommentPattern = "(?<=COMMENT ')(.*?)(?=')";
	String dataTypePattern = "\\s+`([^`]+)`\\s+";
}
