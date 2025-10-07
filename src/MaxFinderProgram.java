public class MaxFinderProgram {

    public static void loadMaxFinderProgram(HarvardProcessor processor) {
        // Упрощенная версия с прямыми адресами

        int[] program = {
                // Загрузка длины массива
                encode(CommandType.LOAD, 0, 0, 0, 0),

                //Загрузка первого числа
                encode(CommandType.LOAD,1,1,1,0 ),
                //Добавление счетчика
                encode(CommandType.ADD,1,14,14,15),
                // Сравнить  длину и счетчик
                encode(CommandType.CMP,0,4,14,0),
                //Если счетчик и длина равна сохраняем и выключаем
                encode(CommandType.JUMP_EQ,9,4,0,0),
                //Загрузка следуещего числа
                encode(CommandType.LOAD,1,2,14,0 ),
                // Сравнить  1 и 2
                encode(CommandType.CMP,0,3,1,2),
                // прыжок в конец если <0
                encode(CommandType.JUMP_GT,11,3,0,0),
                // В начало цикла
                encode(CommandType.JUMP,2,0,0,0),
                //Сохраняем
                encode(CommandType.STORE,1,0,1,0),
                //Остановка
                encode(CommandType.HALT, 0, 0, 0, 0) ,
                //Загрузить в R1 большего числа
                encode(CommandType.LOAD,1,1,14,0),
                //В начало цикла
                encode(CommandType.JUMP,2,0,0,0)

        };

        for (int i = 0; i < program.length; i++) {
            processor.programMemory[i] = program[i];
        }
    }

    private static int encode(CommandType cmdType, int literal, int dest, int op1, int op2) {
        return (cmdType.getCode() << 28) |
                ((literal & 0xFFFF) << 12) |
                ((dest & 0xF) << 8) |
                ((op1 & 0xF) << 4) |
                (op2 & 0xF);
    }
}