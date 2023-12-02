package beans.sjzy;

/**
 * ClassName:HdfsFileObject
 * Package:com.dataTransmit.parseFrontEndBeans
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/6/28 - 19:54
 */
public class HdfsFileObject {
    private String sourcePosition;
    private String targetPosition;

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public String getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
    }
}
