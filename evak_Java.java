import java.awt.*;
import java.util.*;
import java.io.*;
import java.math.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;




class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        boolean brewed = true;
        PotionRecipe bestProfitRecipe = new PotionRecipe();
        Spell bestLearnableSpell = new Spell();
        int learnedSpell = 0;
        // Base cost of a reagent in turns. Not sure about how to do delta 1 maybe it is 1.5 ?
        CostChart costChart = new CostChart(0.5,2,3,4); //Is it ok to have it here?

        // game loop
        while (true) {
            //--TODO: make more efficient?
            List<PotionRecipe> potionRecipes = new ArrayList<PotionRecipe>();
            List<PotionRecipe> brewablePotionRecipes = new ArrayList<PotionRecipe>();
            Inventory inventory = new Inventory(0, 0, 0, 0, 0);
            List<Spell> spellList = new ArrayList<Spell>();
            List<Spell> spellBook = new ArrayList<Spell>();



            int actionCount = in.nextInt(); // the number of spells and recipes in play
            for (int i = 0; i < actionCount; i++) {
                int actionId = in.nextInt(); // the unique ID of this spell or recipe
                String actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
                int delta0 = in.nextInt(); // tier-0 ingredient change
                int delta1 = in.nextInt(); // tier-1 ingredient change
                int delta2 = in.nextInt(); // tier-2 ingredient change
                int delta3 = in.nextInt(); // tier-3 ingredient change
                int price = in.nextInt(); // the price in rupees if this is a potion
                int tomeIndex = in.nextInt(); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
                int taxCount = in.nextInt(); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
                boolean castable = in.nextInt() != 0; // in the first league: always 0; later: 1 if this is a castable player spell
                boolean repeatable = in.nextInt() != 0; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

                switch (actionType) {
                    case "BREW":
                        //--Update potion recipe list
                        PotionRecipe potionRecipe = new PotionRecipe(actionId, delta0, delta1, delta2, delta3, price, costChart);
                        potionRecipe.updateIngredientCostAndProfit(costChart); // get the new values which are changed when new spells were learned, remember this need to be update before getProfit
                        potionRecipes.add(potionRecipe);
                        //System.err.println("Added following recipe to list: " + actionId + "," + delta0 + "," + delta1 + "," + delta2 + "," + delta3 + "," + price);
                        break;
                    case "CAST":
                        Spell spell = new Spell(actionId,delta0,delta1,delta2,delta3,tomeIndex,taxCount,castable,repeatable);
                        spellList.add(spell);
                        //System.err.println("Added following spell to list: " + actionId + "," + delta0 + "," + delta1 + "," + delta2 + "," + delta3 + "," + tomeIndex + "," + taxCount + "," + castable + "," + repeatable);
                        break;
                    case "OPPONENT_CAST":
                        //System.err.println("OPPONENT_CAST??");
                        break;
                    case "LEARN":
                        Spell learnableSpell = new Spell(actionId,delta0,delta1,delta2,delta3,tomeIndex,taxCount,castable,repeatable);
                        spellBook.add(learnableSpell);
                        System.err.println("Added learnable spell to list: " + actionId + "," + delta0 + "," + delta1 + "," + delta2 + "," + delta3 + "," + tomeIndex + "," + taxCount + "," + castable + "," + repeatable);
                        break;
                    default:
                        System.err.println("Your switch case if broken");
                }

            }
            for (int i = 0; i < 2; i++) {
                int inv0 = in.nextInt(); // tier-0 ingredients in inventory
                int inv1 = in.nextInt();
                int inv2 = in.nextInt();
                int inv3 = in.nextInt();
                int score = in.nextInt(); // amount of rupees

                //--Update inventory
                if(i == 0){
                    inventory = new Inventory(inv0, inv1, inv2, inv3, score);
                }

                //--TODO change this to local variables after trust is gained
                //System.err.println("inventory:" + inventory.getInv0() + "," + inventory.getInv1() + "," + inventory.getInv2() + "," + inventory.getInv3());
            }

            double tmpSpellValue = 0.0;
            //-- Check for worthy spells in tome
            if(learnedSpell < 5){

                for(Spell s : spellBook){
                    double spellValue = giveSpellValue(s);
                    if (tmpSpellValue < spellValue){
                        tmpSpellValue = spellValue;
                        bestLearnableSpell = s;
                    }
                }
            }



            //--1. Check the best profit for potion
            potionRecipes.sort((a, b) -> b.getProfit().compareTo(a.getProfit()));
            if(brewed){
                bestProfitRecipe = potionRecipes.get(0);
                brewed = false;
            }

            System.err.println(bestProfitRecipe);

            /*
            for (PotionRecipe pr : potionRecipes) {
                if (canBrew(pr, inventory)) {
                    brewablePotionRecipes.add(pr);
                }
            }
            */

            //This is temporary
            Spell spell0 = new Spell();
            Spell spell1 = new Spell();
            Spell spell2 = new Spell();
            Spell spell3 = new Spell();

            //--TODO: set spell types for each reagent
            // might need to do all combinations of spell types ?? try to see if any other way
            for (Spell spell : spellList) {
                if(spell.getDelta0() > 0){
                    spell.setspellType(0);
                    spell0 = spell;
                }else if (spell.getDelta1() > 0){
                    spell.setspellType(1);
                    spell1 = spell;
                }else if (spell.getDelta2() > 0){
                    spell.setspellType(2);
                    spell2 = spell;
                }else if (spell.getDelta3() > 0){
                    spell.setspellType(3);
                    spell3 = spell;
                }else{
                    System.err.println("Your spell if else is broken");
                }
            }

            // Try to reach the requirements of recipe
            if(learnedSpell < 5 && tmpSpellValue > 0.0){
                System.err.println("---" + bestLearnableSpell.getTaxCount() + " " + inventory.getInv0());
                if(bestLearnableSpell.getTomeIndex() > inventory.getInv0()){
                    cast(spell0);
                }else{
                    learn(bestLearnableSpell);
                    costChart = costCharUpdater(bestLearnableSpell,costChart);
                    learnedSpell = learnedSpell + 1;
                }
            }else if(Math.abs(bestProfitRecipe.getDelta3()) > inventory.getInv3() && inventory.getInv2() > 0){
                System.err.println(inventory.getInv3() + " " + inventory.getInv2());
                cast(spell3);
            }else if((Math.abs(bestProfitRecipe.getDelta2()) > inventory.getInv2()
                    || Math.abs(bestProfitRecipe.getDelta3()) > inventory.getInv3())
                    && inventory.getInv1() > 0){
                System.err.println(inventory.getInv2() + " " + inventory.getInv1());
                cast(spell2);
            }else if((Math.abs(bestProfitRecipe.getDelta1()) > inventory.getInv1()
                    || Math.abs(bestProfitRecipe.getDelta2()) > inventory.getInv2()
                    || Math.abs(bestProfitRecipe.getDelta3()) > inventory.getInv3())
                    && inventory.getInv0() > 0){
                cast(spell1);
            }else if(Math.abs(bestProfitRecipe.getDelta3()) <= inventory.getInv3()
                    && Math.abs(bestProfitRecipe.getDelta2()) <= inventory.getInv2()
                    && Math.abs(bestProfitRecipe.getDelta1()) <= inventory.getInv1()
                    && Math.abs(bestProfitRecipe.getDelta0()) <= inventory.getInv0()) {
                brewed = true;
                brew(bestProfitRecipe);
            }else {
                cast(spell0);
            }

        }
    }

    public static CostChart costCharUpdater(Spell newLearnedSpell, CostChart costChart){
        int delta0 = newLearnedSpell.getDelta0();
        int delta1 = newLearnedSpell.getDelta1();
        int delta2 = newLearnedSpell.getDelta2();
        int delta3 = newLearnedSpell.getDelta3();

        costChart.setDelta0PriceInTurns(costChart.getDelta0PriceInTurns() / Math.abs(delta0));
        costChart.setDelta0PriceInTurns(costChart.getDelta1PriceInTurns() / Math.abs(delta1));
        costChart.setDelta0PriceInTurns(costChart.getDelta2PriceInTurns() / Math.abs(delta2));
        costChart.setDelta0PriceInTurns(costChart.getDelta3PriceInTurns() / Math.abs(delta3));

        return costChart;
    }

    public static double giveSpellValue(Spell spell){
        //System.err.println("giveSpellValue:" + spell.getSpellId() + " spellid");

        // !!!! Does ingredient cost chart need to be taken into account?
        int delta0 = spell.getDelta0();
        int delta1 = spell.getDelta1();
        int delta2 = spell.getDelta2();
        int delta3 = spell.getDelta3();

        double spellValue = 0;

        // First version greedily find out if spell is positive only
        if(delta0 >= 0
                && delta1 >= 0
                && delta2 >= 0
                && delta3 >= 0){
            return 9999.0;
        }

        return spellValue;
    }

    public static void cast(Spell spell) {
        System.err.println("Casting:" + spell.getspellType() + " spelltype");
        if(spell.castable){
            System.out.println("CAST " + spell.spellId);
        }else{
            rest();
        }

    }

    public static void learn(Spell spell) {
        System.out.println("LEARN " + spell.spellId);
    }

    public static void brew(PotionRecipe potionRecipe) {
        System.out.println("BREW " + potionRecipe.potionId);
    }

    public static void rest() {
        System.out.println("REST");
    }


    public static boolean canBrew(PotionRecipe pr, Inventory inventory) {
        //System.err.println("check if can Brew:" + pr.potionId);

        if (pr.getDelta0() + inventory.getInv0() < 0) {
            //System.err.println("can not Brew, not enough D0");
            return false;
        } else if (pr.getDelta1() + inventory.getInv1() < 0) {
            //System.err.println("can not Brew, not enough D1");
            return false;
        } else if (pr.getDelta2() + inventory.getInv2() < 0) {
            //System.err.println("can not Brew, not enough D2");
            return false;
        } else if (pr.getDelta3() + inventory.getInv3() < 0) {
            //System.err.println("can not Brew, not enough D3");
            return false;
        } else {
            //System.err.println("canBrew");
            return true;
        }
    }


}

class Spell {
    int spellId;
    int delta0;
    int delta1;
    int delta2;
    int delta3;
    int tomeIndex; // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
    int taxCount; // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
    boolean castable; // in the first league: always 0; later: 1 if this is a castable player spell
    boolean repeatable; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
    int spellType; //TODO make better for now 1 2 3 !!!

    public Spell(){

    }

    public Spell(int spellId, int delta0, int delta1, int delta2, int delta3, int tomeIndex, int taxCount, boolean castable, boolean repeatable) {
        this.spellId = spellId;
        this.delta0 = delta0;
        this.delta1 = delta1;
        this.delta2 = delta2;
        this.delta3 = delta3;
        this.tomeIndex = tomeIndex;
        this.taxCount = taxCount;
        this.castable = castable;
        this.repeatable = repeatable;
    }

    public int getspellType() {
        return spellType;
    }
    //TODO make better
    public void setspellType(int spellType) {
        this.spellType = spellType;
    }


    public int getSpellId() {
        return spellId;
    }

    public int getDelta0() {
        return delta0;
    }

    public int getDelta1() {
        return delta1;
    }

    public int getDelta2() {
        return delta2;
    }

    public int getDelta3() {
        return delta3;
    }

    public int getTomeIndex() {
        return tomeIndex;
    }

    public int getTaxCount() {
        return taxCount;
    }

    public boolean isCastable() {
        return castable;
    }

    public boolean isRepeatable() {
        return repeatable;
    }
}

class CostChart{
    double delta0PriceInTurns;
    double delta1PriceInTurns;
    double delta2PriceInTurns;
    double delta3PriceInTurns;

    public CostChart(double delta0PriceInTurns, double delta1PriceInTurns, double delta2PriceInTurns, double delta3PriceInTurns) {
        this.delta0PriceInTurns = delta0PriceInTurns;
        this.delta1PriceInTurns = delta1PriceInTurns;
        this.delta2PriceInTurns = delta2PriceInTurns;
        this.delta3PriceInTurns = delta3PriceInTurns;
    }

    public double getDelta0PriceInTurns() {
        return delta0PriceInTurns;
    }

    public void setDelta0PriceInTurns(double delta0PriceInTurns) {
        this.delta0PriceInTurns = delta0PriceInTurns;
    }

    public double getDelta1PriceInTurns() {
        return delta1PriceInTurns;
    }

    public void setDelta1PriceInTurns(double delta1PriceInTurns) {
        this.delta1PriceInTurns = delta1PriceInTurns;
    }

    public double getDelta2PriceInTurns() {
        return delta2PriceInTurns;
    }

    public void setDelta2PriceInTurns(double delta2PriceInTurns) {
        this.delta2PriceInTurns = delta2PriceInTurns;
    }

    public double getDelta3PriceInTurns() {
        return delta3PriceInTurns;
    }

    public void setDelta3PriceInTurns(double delta3PriceInTurns) {
        this.delta3PriceInTurns = delta3PriceInTurns;
    }
}

class PotionRecipe {
    int potionId;
    int delta0; // tier-0 ingredient change
    int delta1; // tier-1 ingredient change
    int delta2; // tier-2 ingredient change
    int delta3; // tier-3 ingredient change
    Integer price;
    Double ingredientCost;
    Double profit;

    public PotionRecipe(){};

    public PotionRecipe(int potionId, int delta0, int delta1, int delta2, int delta3, Integer price, CostChart costChart) {
        this.potionId = potionId;
        this.delta0 = delta0;
        this.delta1 = delta1;
        this.delta2 = delta2;
        this.delta3 = delta3;
        this.price = price;
        this.ingredientCost = Math.abs(delta0) * costChart.getDelta0PriceInTurns() // not in my right mind... is this stupid ?
                + Math.abs(delta1) * costChart.getDelta1PriceInTurns()
                + Math.abs(delta2) * costChart.getDelta2PriceInTurns()
                + Math.abs(delta3) * costChart.getDelta3PriceInTurns();
        this.profit = 1.0 * price / ingredientCost;
    }

    public Double getIngredientCost() {
        return ingredientCost;
    }

    public void updateIngredientCostAndProfit(CostChart costChart) {
        this.ingredientCost = Math.abs(delta0) * costChart.getDelta0PriceInTurns() // not in my right mind... is this stupid ?
                + Math.abs(delta1) * costChart.getDelta1PriceInTurns()
                + Math.abs(delta2) * costChart.getDelta2PriceInTurns()
                + Math.abs(delta3) * costChart.getDelta3PriceInTurns();
        this.profit = 1.0 * this.price / this.ingredientCost;
    }

    public Double getProfit() {
        //TODO: is this a dangerous ? Can the data be outdated?
        //I do not want to recalculate after every call. Just have to be careful.
        return profit;
    }

    public int getPotionId() {
        return potionId;
    }

    public int getDelta0() {
        return delta0;
    }

    public int getDelta1() {
        return delta1;
    }

    public int getDelta2() {
        return delta2;
    }

    public int getDelta3() {
        return delta3;
    }

    public Integer getPrice() {
        return price;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "PotionRecipe{" +
                "potionId=" + potionId +
                ", delta0=" + delta0 +
                ", delta1=" + delta1 +
                ", delta2=" + delta2 +
                ", delta3=" + delta3 +
                ", price=" + price +
                ", ingredientCost=" + ingredientCost +
                ", profit=" + profit +
                '}';
    }

    //--TODO: should I make price comparison here?
}

class Inventory {
    int inv0; // tier-0 ingredients in inventory
    int inv1;
    int inv2;
    int inv3;
    int score; // amount of rupees
    //--TODO: maybe a full boolean ? 10/10

    public Inventory(int inv0, int inv1, int inv2, int inv3, int score) {
        this.inv0 = inv0;
        this.inv1 = inv1;
        this.inv2 = inv2;
        this.inv3 = inv3;
        this.score = score;
    }

    public int getInv0() {
        return inv0;
    }

    public void setInv0(int inv0) {
        this.inv0 = inv0;
    }

    public int getInv1() {
        return inv1;
    }

    public void setInv1(int inv1) {
        this.inv1 = inv1;
    }

    public int getInv2() {
        return inv2;
    }

    public void setInv2(int inv2) {
        this.inv2 = inv2;
    }

    public int getInv3() {
        return inv3;
    }

    public void setInv3(int inv3) {
        this.inv3 = inv3;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}