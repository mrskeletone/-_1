import java.io.*;
import java.util.*;

public class MaxFinderProgram {
    private static int count = 0;
    private static boolean flag = false;

    public static void loadMaxFinderProgram(HarvardProcessor processor) {
        // Упрощенная версия с прямыми адресами

        int[] program = {
                // Загрузка длины массива
                encode(CommandType.LOAD, 0, 0, 0, 0),

                //Загрузка первого числа
                encode(CommandType.LOAD, 1, 1, 1, 0),
                //Добавление счетчика
                encode(CommandType.ADD, 1, 14, 14, 15),
                // Сравнить  длину и счетчик
                encode(CommandType.CMP, 0, 4, 14, 0),
                //Если счетчик и длина равна сохраняем и выключаем
                encode(CommandType.JUMP_EQ, 9, 4, 0, 0),
                //Загрузка следуещего числа
                encode(CommandType.LOAD, 1, 2, 14, 0),
                // Сравнить  1 и 2
                encode(CommandType.CMP, 0, 3, 1, 2),
                // прыжок в конец если <0
                encode(CommandType.JUMP_GT, 11, 3, 0, 0),
                // В начало цикла
                encode(CommandType.JUMP, 2, 0, 0, 0),
                //Сохраняем
                encode(CommandType.STORE, 0, 15, 1, 0),
                //Остановка
                encode(CommandType.HALT, 0, 0, 0, 0),
                //Загрузить в R1 большего числа
                encode(CommandType.LOAD, 1, 1, 14, 0),
                //В начало цикла
                encode(CommandType.JUMP, 2, 0, 0, 0)

        };

        for (int i = 0; i < program.length; i++) {
            System.out.println(program[i]);
            processor.programMemory[i] = program[i];
        }
    }

    public static void loadMaxFinderProgramByText(HarvardProcessor processor, File file) {
        List<Integer> listCMD = new ArrayList<>();
        Map<String, Integer> mark = new HashMap<>();
        readAllMarks(mark,file);
        for (String s :
                mark.keySet()) {
            System.out.println(s + " " + mark.get(s));
        }
        try {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String cmd = "";
                while (!cmd.equalsIgnoreCase("EXIT")) {
                    try {
                        cmd = bufferedReader.readLine();
//                        System.out.println(cmd);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (!cmd.equalsIgnoreCase("EXIT"))
                        textToCommand(listCMD, cmd, mark);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        count = 1;
        for (int i = 0; i < listCMD.size(); i++) {
//            System.out.println(listCMD.get(i));
            processor.programMemory[i] = listCMD.get(i);
        }
    }

    private static void readAllMarks(Map<String, Integer> mark, File file) {
        int index = 0;
        try {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String cmd = "";
                while (!cmd.equalsIgnoreCase("EXIT")) {
                    try {
                        cmd = bufferedReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (!cmd.equalsIgnoreCase("EXIT"))
                        if (cmd.charAt(cmd.length() - 1) == ':') {
                            mark.put(cmd, index + count);
                            System.out.println("Добавил " + cmd);
                            count--;

                        }
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void textToCommand(List<Integer> listCMD, String cmd, Map<String, Integer> mark) throws IndexOutOfBoundsException {
        String[] cmd_brk = cmd.split(" ");
        if (!mark.containsKey(cmd))
            switch (cmd_brk[0]) {
                case "LOAD":
                    listCMD.add(encode(CommandType.LOAD,
                            Integer.parseInt(cmd_brk[1]),
                            Integer.parseInt(cmd_brk[2]),
                            Integer.parseInt(cmd_brk[3]),
                            0));
                    break;
                case "STORE":
                    listCMD.add(encode(CommandType.STORE,
                            Integer.parseInt(cmd_brk[1]),
                            Integer.parseInt(cmd_brk[2]),
                            Integer.parseInt(cmd_brk[3]),
                            0));
                    break;
                case "ADD":
                    listCMD.add(encode(CommandType.ADD,
                            Integer.parseInt(cmd_brk[1]),
                            Integer.parseInt(cmd_brk[2]),
                            Integer.parseInt(cmd_brk[3]),
                            Integer.parseInt(cmd_brk[4])));
                    break;
                case "SUB":
                    listCMD.add(encode(CommandType.SUB,
                            Integer.parseInt(cmd_brk[1]),
                            Integer.parseInt(cmd_brk[2]),
                            Integer.parseInt(cmd_brk[3]),
                            Integer.parseInt(cmd_brk[4])));
                    break;
                case "CMP":
                    listCMD.add(encode(CommandType.CMP,
                            0,
                            Integer.parseInt(cmd_brk[1]),
                            Integer.parseInt(cmd_brk[2]),
                            Integer.parseInt(cmd_brk[3])));
                    break;
                case "JUMP":
                    if (mark.containsKey(cmd_brk[1]+":")) {
                        listCMD.add(encode(CommandType.JUMP,
                                mark.get(cmd_brk[1]+":"),
                                0,
                                0,
                                0));
                    } else
                        listCMD.add(encode(CommandType.JUMP,
                                Integer.parseInt(cmd_brk[1]),
                                0,
                                0,
                                0));
                    break;
                case "JUMP_EQ":
                    if (mark.containsKey(cmd_brk[1]+":")) {
                        listCMD.add(encode(CommandType.JUMP_EQ,
                                mark.get(cmd_brk[1]+":"),
                                Integer.parseInt(cmd_brk[2]),
                                0,
                                0));
                    } else
                        listCMD.add(encode(CommandType.JUMP_EQ,
                                Integer.parseInt(cmd_brk[1]),
                                Integer.parseInt(cmd_brk[2]),
                                0,
                                0));
                    break;
                case "JUMP_GT":
                    if (mark.containsKey(cmd_brk[1]+":")) {
                        listCMD.add(encode(CommandType.JUMP_GT,
                                mark.get(cmd_brk[1]+":"),
                                Integer.parseInt(cmd_brk[2]),
                                0,
                                0));
                    } else
                        listCMD.add(encode(CommandType.JUMP_GT,
                                Integer.parseInt(cmd_brk[1]),
                                Integer.parseInt(cmd_brk[2]),
                                0,
                                0));
                    break;
                case "HALT":
                    listCMD.add(encode(CommandType.HALT,
                            0,
                            0,
                            0,
                            0));
                    break;
                default:
                    throw new IndexOutOfBoundsException(cmd + " UNKNOWN INSTRUCTION ");
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