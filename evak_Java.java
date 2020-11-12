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

        // game loop
        while (true) {
            //--TODO: make more efficient?
            List<PotionRecipe> potionRecipes = new ArrayList<PotionRecipe>();
            Inventory inventory = new Inventory(0, 0, 0, 0, 0);

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

                //--Update potion recipe list
                PotionRecipe potionRecipe = new PotionRecipe(actionId, delta0, delta1, delta2, delta3, price);
                potionRecipes.add(potionRecipe);
                System.err.println("Added following recipe to list: " + actionId + "," + delta0 + "," + delta1 + "," + delta2 + "," + delta3 + "," + price);

            }
            for (int i = 0; i < 2; i++) {
                int inv0 = in.nextInt(); // tier-0 ingredients in inventory
                int inv1 = in.nextInt();
                int inv2 = in.nextInt();
                int inv3 = in.nextInt();
                int score = in.nextInt(); // amount of rupees

                //--Update inventory
                inventory = new Inventory(inv0, inv1, inv2, inv3, score);
                //--TODO change this to local variables after trust is gained
                System.err.println("inventory:" + inventory.getInv0() + "," + inventory.getInv1() + "," + inventory.getInv2() + "," + inventory.getInv3());
            }

            //--1. Check all potions if can brew
            List<PotionRecipe> brewablePotionRecipes = new ArrayList<PotionRecipe>();
            for (PotionRecipe pr : potionRecipes) {
                if (canBrew(pr, inventory)) {
                    brewablePotionRecipes.add(pr);
                }

            }

            //--2. choose best one, maybe by price... actually sort by price

            brewablePotionRecipes.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
            System.err.println("can not Brew, not enough D2");

            //--3. Brew
            brew(brewablePotionRecipes.get(0));

        }
    }

    public static void brew(PotionRecipe potionRecipe) {
        System.out.println("BREW " + potionRecipe.potionId);
    }


    public static boolean canBrew(PotionRecipe pr, Inventory inventory) {
        System.err.println("check if can Brew:" + pr.potionId);

        if (pr.getDelta0() + inventory.getInv0() < 0) {
            System.err.println("can not Brew, not enough D0");
            return false;
        } else if (pr.getDelta1() + inventory.getInv1() < 0) {
            System.err.println("can not Brew, not enough D1");
            return false;
        } else if (pr.getDelta2() + inventory.getInv2() < 0) {
            System.err.println("can not Brew, not enough D2");
            return false;
        } else if (pr.getDelta3() + inventory.getInv3() < 0) {
            System.err.println("can not Brew, not enough D3");
            return false;
        } else {
            System.err.println("canBrew");
            return true;
        }
    }


}


class PotionRecipe {
    int potionId;
    int delta0; // tier-0 ingredient change
    int delta1; // tier-1 ingredient change
    int delta2; // tier-2 ingredient change
    int delta3; // tier-3 ingredient change
    Integer price;

    public PotionRecipe(int potionId, int delta0, int delta1, int delta2, int delta3, Integer price) {
        this.potionId = potionId;
        this.delta0 = delta0;
        this.delta1 = delta1;
        this.delta2 = delta2;
        this.delta3 = delta3;
        this.price = price;
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

    //--TODO: should I make price comparison here?
}

class Inventory {
    int inv0; // tier-0 ingredients in inventory
    int inv1;
    int inv2;
    int inv3;
    int score; // amount of rupees

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