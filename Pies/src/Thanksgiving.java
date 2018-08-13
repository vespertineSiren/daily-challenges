import java.util.Scanner;

public class Thanksgiving {

    public static void main(String...strings ) {
        float[][] ingredients = {{10, 14, 10, 42, 24},
                {12, 4, 40, 30, 40},
                {12, 14, 20, 42, 24}};
        float[] pumpkin = {1, 0, 3, 4, 3};
        float[] apple = {0, 1, 4, 3, 2};
        for (float[] sub_ing : ingredients) {
            System.out.println(optimize(sub_ing, pumpkin, apple));
        }

        Scanner ingredientNumber = new Scanner(System.in);
        System.out.println("\nLet's begin baking and enter the ingredients in your pantry");
        System.out.println("Enter the number of pumpkin: ");
        float pumpkins = ingredientNumber.nextFloat();
        System.out.println("Enter the number of apples: ");
        float apples = ingredientNumber.nextFloat();
        System.out.println("Enter the number of eggs: ");
        float eggs = ingredientNumber.nextFloat();
        System.out.println("Enter the amount of milk: ");
        float milk = ingredientNumber.nextFloat();
        System.out.println("Enter the amount of sugar: ");
        float sugar = ingredientNumber.nextFloat();

        float[] ingredientInputs = {pumpkins, apples, eggs, milk, sugar};
        System.out.println((optimize(ingredientInputs, pumpkin, apple)));

        ingredientNumber.close();

    }

    /***
     *
     * @param ingredients total ingredients in the form {pumpkin, apples, eggs, milk, sugar}
     * @param pumpkin pumpkin recipe in the form {pumpkin, apples, eggs, milk, sugar}
     * @param apple apple recipe in the form {pumpkin, apples, eggs, milk, sugar}
     * @return "#pumpkin pumpkin pies and #apple apple pies"
     */
    public static String optimize(float[] ingredients, float[] pumpkin, float[] apple) {
        float[][] constraints = generateConstraints(ingredients, pumpkin, apple);
        int[] opt= null;
        for (int i = 0; i < constraints.length; i++) {
            for (int j = i+1; j < constraints.length; j++) {
                int[][] round_intercepts = roundedIntercept(intercept(constraints[i], constraints[j]));
                if (round_intercepts != null) {
                    for (int[] intercept : round_intercepts) {
                        if (checkFeasibility(intercept, constraints)) {
                            if (opt == null){
                                opt = intercept;
                            } else if (intercept[0] + intercept[1] > opt[0] + opt[1]) opt = intercept;
                        }
                    }
                }
            }
        }
        return opt[0] + " pumpkin pies and " + opt[1] + " apple pies";
    }

    /***
     *
     * @param l1 Line of the form: {total_ingredient, pumpkin_ingredient, apple_ingredient}
     * @param l2 Line of the form: {total_ingredient, pumpkin_ingredient, apple_ingredient}
     * @return Intercept of the two lines in the form {x, y}. x is number of pumpkin pies, y is number of apple pies
     */
    private static float[] intercept(float[] l1, float[] l2) {
        float x = 0
        float y = 0;

        if ((l1[2] == 0 && l2[2] == 0) || (l1[1] == 0 && l2[1] == 0) || (l1[1] == l2[1] && l1[2] == l2[2])){
            return null;
        }

        if (l1[2] == 0 && l2[2] != 0) {
            x = l1[0]/l1[1];
            y = (l2[0] - x*l2[1])/l2[2];
        } else if (l1[2] != 0 && l2[2] == 0) {
            x = l2[0]/l2[1];
            y = (l1[0] - x*l1[1])/l1[2];
        } else {
            float[] norm_l1 = normalize(l1);
            float[] norm_l2 = normalize(l2);
            x = (norm_l1[0]-norm_l2[0])/(norm_l1[1]-norm_l2[1]);
            y = (norm_l1[0]-x*norm_l1[1]);
        }

        return new float[] {x, y};
    }

    /***
     *
     * @param l Line of the form: {total_ingredient, pumpkin_ingredient, apple_ingredient}
     * @return Line of the form: {total_ingredient/apple_ingredient, pumpkin_ingredient/apple_ingredient, 1}
     */
    private static float[] normalize(float[] l) {
        return new float[] {l[0]/l[2], l[1]/l[2], 1};
    }

    /***
     *
     * @param ingredients Total ingredients
     * @param pumpkin Pumpkin recipe
     * @param apple Apple recipe
     * @return Constraints of the form: {total_ingredient, pumpkin_ingredient, apple_ingredient} for linear program
     */
    private static float[][] generateConstraints(float[] ingredients, float[] pumpkin, float[] apple) {
        float[][] constraints = new float[ingredients.length+2][];
        constraints[0] = new float[] {0, 1, 0};
        constraints[1] = new float[] {0, 0, 1};
        for (int i = 2; i < constraints.length; i++) {
            constraints[i] = new float[] {ingredients[i-2], pumpkin[i-2], apple[i-2]};
        }
        return constraints;
    }

    /***
     *
     * @param coords Number of {pumpkin pies, apple pies} satisfying an intercept
     * @param constraints The constraints to test coords against
     * @return true if the coords obey constraints, false otherwise
     */
    private static boolean checkFeasibility(int[] coords, float[][] constraints) {
        for (int i = 0; i < 2; i++) {
            if (constraints[i][1] * coords[0] + constraints[i][2] * coords[1] < constraints[i][0]){
                return false;
            }
        }
        for (int i = 2; i < constraints.length; i++) {
            if (constraints[i][1] * coords[0] + constraints[i][2] * coords[1] > constraints[i][0]){
                return false;
            }
        }
        return true;
    }

    /***
     *
     * @param coords Precise coords of intercept
     * @return possible integer coords of nearest to intercept
     */
    private static int[][] roundedIntercept(float[] coords) {
        if (coords == null){
            return null;
        }
        return new int[][] {{(int) Math.floor(coords[0]), (int) Math.floor(coords[1])},
                {(int) Math.floor(coords[0]), (int) Math.ceil(coords[1])},
                {(int) Math.ceil(coords[0]), (int) Math.ceil(coords[1])},
                {(int) Math.ceil(coords[0]), (int) Math.floor(coords[1])}};
    }

}

