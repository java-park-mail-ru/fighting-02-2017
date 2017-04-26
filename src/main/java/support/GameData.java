package support;

/**
 * Created by andrey on 26.04.17.
 */
public class GameData {
    public  Integer baseDamage=40;
    public Double MH;
    public Double MA;
    public Double ML;
    public Double BH;
    public Double BB;
    public void setCoef(){
        Coef.BH=this.BH;
        Coef.BB=this.BB;
        Coef.baseDamage=this.baseDamage;
        Coef.MA=this.MA;
        Coef.MH=this.MH;
        Coef.ML=this.ML;
    }
}
