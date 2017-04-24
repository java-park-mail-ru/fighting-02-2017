package support;

/**
 * Created by andrey on 24.04.17.
 */

public  class Coef{
    public static final Integer baseDamage=40;
    public static final Double MH=0.65;
    public static final Double MA=0.95;
    public static final Double ML=0.8;
    public static final Double BH=0.6;
    public static final Double BB=0.8;
    public Double kProb;
    public Double damage;
    public Coef(Double kProb, Double damage){
        this.damage=damage;
        this.kProb=kProb;
    }
    public Coef(){
        this.kProb=1.0;
    }
    public void setKMethod(String method){
        if(method.equals("arm")) kProb*=MA;
        if(method.equals("head")) kProb*=MH;
        if(method.equals("leg")) kProb*=ML;
        }
    public void setKBlock(String target, String block){
        if(target.equals(block)){
            if(block.equals("body")) kProb*=BB;
            else kProb*=BH;
        }
        else kProb=kProb/2.0;
    }
    public void setDamage(){
        damage=(1.0-kProb)*baseDamage;
    }
}