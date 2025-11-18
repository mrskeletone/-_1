import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        HarvardProcessor processor = new HarvardProcessor(50, 100);

        int[] testArray = {11,5, 8, 3, 12, 7, 20, 15, 4, 11};

        System.out.println("Поиск максимума в массиве: " + Arrays.toString(testArray));
        System.out.println("Ожидаемый максимум: " + findMax(testArray,testArray[0]));

        processor.initializeDataMemory(testArray);
        //MaxFinderProgram.loadMaxFinderProgram(processor);
        File file=new File("C:\\Users\\dever\\IdeaProjects\\TestProcessor\\src\\testCMD.txt");
        MaxFinderProgram.loadMaxFinderProgramByText(processor,file);
        processor.executeProgram();

        int foundMax = processor.getDataMemory()[0];

        System.out.printf("\nНайденный максимум: %d\n", foundMax);
        System.out.printf("Результат %s\n", foundMax == findMax(testArray,testArray[0]) ? "ВЕРНЫЙ" : "ОШИБКА");

    }

    private static int findMax(int[] arr,int index) {
        int max=Integer.MIN_VALUE;
        if(index>arr.length) {
            index=arr.length;
        }
        if(index+1<=arr.length ){
            index+=1;
        }
        if(index==1   ){
            max=arr[1];
        }

        for(int i =1 ;i<index;i++){
            if(max<arr[i]){
                max=arr[i];
            }
        }
        return max;
    }
}