package by.SanchukS.quizer.generators;

import by.SanchukS.quizer.Expression;
import by.SanchukS.quizer.TaskGenerator;
import by.SanchukS.quizer.tasks.ExpressionTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ExpressionTaskGenerator implements TaskGenerator {
    private final Random random = new Random();
    private final int minNumber;
    private final int maxNumber;
    private final List<String> operations;

    /**
     * @param minNumber              минимальное число
     * @param maxNumber              максимальное число
     * @param generateSum            разрешить генерацию с оператором +
     * @param generateDifference     разрешить генерацию с оператором -
     * @param generateMultiplication разрешить генерацию с оператором *
     * @param generateDivision       разрешить генерацию с оператором /
     */
    ExpressionTaskGenerator(
            int minNumber,
            int maxNumber,
            boolean generateSum,
            boolean generateDifference,
            boolean generateMultiplication,
            boolean generateDivision
    ) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;

        List<String> operations = new ArrayList<>();
        if (generateSum) operations.add("+");
        if (generateDifference) operations.add("-");
        if (generateMultiplication) operations.add("*");
        if (generateDivision) operations.add("/");
        this.operations = operations;
    }

    /**
     * return задание типа {@link ExpressionTask}
     */
    public ExpressionTask generate() {
        String operation = operations.get(random.nextInt(operations.size()));
        int firstNumber;
        int secondNumber;
        if (operation.equals("/")) {
            throw new UnsupportedOperationException("Not realized yet");
        } else {
            firstNumber = random.nextInt(maxNumber - minNumber) + minNumber;
            secondNumber = random.nextInt(maxNumber - minNumber) + minNumber;
        }
        return new ExpressionTask(new Expression(firstNumber, operation, secondNumber));
    }
}