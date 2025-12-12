package dbg;

public class JDIComplexDebuggee {

    // Variables d'instance pour tester receiver-variables
    private int result;
    private String lastOperation;
    private int operationCount;

    public JDIComplexDebuggee() {
        this.result = 0;
        this.lastOperation = "none";
        this.operationCount = 0;
    }

    public int add(int a, int b) {
        // Variables locales pour tester temporaries
        int sum = a + b;
        String operation = "addition";

        this.result = sum;
        this.lastOperation = operation;
        this.operationCount++;

        System.out.println("Addition: " + a + " + " + b + " = " + sum);
        return sum;
    }

    public int multiply(int a, int b) {
        int product = a * b;
        String operation = "multiplication";

        this.result = product;
        this.lastOperation = operation;
        this.operationCount++;

        System.out.println("Multiplication: " + a + " * " + b + " = " + product);
        return product;
    }

    public int factorial(int n) {
        // Méthode récursive pour tester la stack
        if (n <= 1) {
            return 1;
        }

        int previousFactorial = factorial(n - 1);
        int currentFactorial = n * previousFactorial;

        this.result = currentFactorial;
        this.lastOperation = "factorial";
        this.operationCount++;

        return currentFactorial;
    }

    public void processArray(int[] numbers) {
        // Pour tester les arrays
        int total = 0;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < numbers.length; i++) {
            int current = numbers[i];
            total += current;

            if (current > max) {
                max = current;
            }
        }

        this.result = total;
        this.lastOperation = "array processing";
        this.operationCount++;

        System.out.println("Array total: " + total + ", max: " + max);
    }

    public int divide(int numerator, int denominator) {
        // Pour tester step-into vs step-over
        int result = safeDivide(numerator, denominator);

        this.result = result;
        this.lastOperation = "division";
        this.operationCount++;

        return result;
    }

    private int safeDivide(int a, int b) {
        if (b == 0) {
            System.out.println("Error: Division by zero!");
            return 0;
        }
        return a / b;
    }

    public void printStatus() {
        System.out.println("=== Calculator Status ===");
        System.out.println("Result: " + this.result);
        System.out.println("Last operation: " + this.lastOperation);
        System.out.println("Operation count: " + this.operationCount);
        System.out.println("========================");
    }

    public static void main(String[] args) {
        System.out.println("Starting Calculator Demo");

        // Créer une instance pour tester receiver/sender
        JDIComplexDebuggee calc = new JDIComplexDebuggee();

        // Tests simples pour step-over
        int sum1 = calc.add(5, 3);
        int sum2 = calc.add(10, 20);

        // Test multiply
        int product = calc.multiply(4, 7);

        // Test avec array pour voir les variables
        int[] numbers = {1, 2, 3, 4, 5};
        calc.processArray(numbers);

        // Test division pour step-into
        int division1 = calc.divide(20, 4);
        int division2 = calc.divide(10, 0);  // Test error case

        // Test récursif pour voir la stack
        System.out.println("Computing factorial of 5");
        int fact = calc.factorial(5);
        System.out.println("Factorial result: " + fact);

        // Afficher le statut final
        calc.printStatus();

        // Quelques opérations finales
        int finalSum = calc.add(100, 200);

        System.out.println("Demo completed. Final result: " + calc.result);
    }
}