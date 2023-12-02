package beans.sjtb.dataxJsonBeans;

public class Writer<T> {

    private T parameter;
    private String name;
    public void setParameter(T parameter) {
         this.parameter = parameter;
     }
     public T getParameter() {
         return parameter;
     }

    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

}