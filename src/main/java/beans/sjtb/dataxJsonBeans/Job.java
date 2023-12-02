package beans.sjtb.dataxJsonBeans;

import java.util.List;

public class Job {

    private List<Content> content;
    private Setting setting;
    public void setContent(List<Content> content) {
         this.content = content;
     }
     public List<Content> getContent() {
         return content;
     }

    public void setSetting(Setting setting) {
         this.setting = setting;
     }
     public Setting getSetting() {
         return setting;
     }

}