package beans.sjtb.dataxJsonBeans;

public class Column {

    private String name;
    private String type;
    private int index;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}