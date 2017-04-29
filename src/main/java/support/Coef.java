package support;

/**
 * Created by andrey on 24.04.17.
 */

public class Coef {
    public static Integer baseDamage;
    public static Double MH;
    public static Double MA;
    public static Double ML;
    public static Double BH;
    public static Double BB;
    public Double kProb;
    public Integer damage;

    public Coef(Double kProb, Integer damage) {
        this.damage = damage;
        this.kProb = kProb;
    }

    public Coef() {
        this.kProb = 1.0;
    }

    public void setKMethod(String method) {
        if (method.equals("arm")) kProb *= MA;
        if (method.equals("head")) kProb *= MH;
        if (method.equals("leg")) kProb *= ML;
    }

    public void setKBlock(String target, String block) {
        if (target.equals(block)) {
            if (block.equals("body")) kProb *= BB;
            else kProb *= BH;
        } else kProb = kProb / 2.0;
    }

    public void setDamage() {
        damage = (int) Math.round((1.0 - kProb) * baseDamage);
    }
}