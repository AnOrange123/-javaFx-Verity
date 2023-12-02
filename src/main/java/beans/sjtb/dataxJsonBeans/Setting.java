package beans.sjtb.dataxJsonBeans;


public class Setting {

    private Speed speed;
    private ErrorLimit errorLimit;
    public void setSpeed(Speed speed) {
         this.speed = speed;
     }
     public Speed getSpeed() {
         return speed;
     }

    public void setErrorLimit(ErrorLimit errorLimit) {
         this.errorLimit = errorLimit;
     }
     public ErrorLimit getErrorLimit() {
         return errorLimit;
     }

}