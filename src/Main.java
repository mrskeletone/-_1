import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        HarvardProcessor processor = new HarvardProcessor(50, 100);

        int[] testArray = {5, 8, 3, 12, 7, 9, 15, 4, 11};

        System.out.println("Поиск максимума в массиве: " + Arrays.toString(testArray));
        System.out.println("Ожидаемый максимум: " + findMax(testArray));

        processor.initializeDataMemory(testArray);
        MaxFinderProgram.loadMaxFinderProgram(processor);
        processor.executeProgram();

        int foundMax = processor.getDataMemory()[testArray.length+1];

        System.out.printf("\nНайденный максимум: %d\n", foundMax);
        System.out.printf("Результат %s\n", foundMax == findMax(testArray) ? "ВЕРНЫЙ" : "ОШИБКА");

    }

    private static int findMax(int[] arr) {
        return Arrays.stream(arr).max().getAsInt();
    }
}