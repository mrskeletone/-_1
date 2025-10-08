public class HarvardProcessor {
    // Память программ и данных
    int[] programMemory;
    private int[] dataMemory;
    private int[] registers;
    private int programCounter;
    private Instruction currentInstruction;

    // Маски для извлечения полей команды
    private static final int CMD_TYPE_MASK = 0xF0000000;
    private static final int LITERAL_MASK = 0x0FFFF000;
    private static final int DEST_MASK = 0x00000F00;
    private static final int OP1_MASK = 0x000000F0;
    private static final int OP2_MASK = 0x0000000F;

    // Сдвиги для извлечения полей
    private static final int CMD_TYPE_SHIFT = 28;
    private static final int LITERAL_SHIFT = 12;
    private static final int DEST_SHIFT = 8;
    private static final int OP1_SHIFT = 4;
    private static final int OP2_SHIFT = 0;

    public HarvardProcessor(int programSize, int dataSize) {
        this.programMemory = new int[programSize];
        this.dataMemory = new int[dataSize];
        this.registers = new int[16]; // 16 регистров (0-15)
        this.programCounter = 0;
    }

    public void initializeDataMemory(int[] array) {
        dataMemory[0] = array.length;
        for (int i = 0; i < array.length; i++) {
            dataMemory[i + 1] = array[i];
        }
    }

    public int[] getRegisters() { return registers; }
    public int[] getDataMemory() { return dataMemory; }

    public void executeProgram() {
        boolean running = true;

        System.out.println("=== НАЧАЛО ВЫПОЛНЕНИЯ ПРОГРАММЫ ===");

        while (running && programCounter < programMemory.length ) {
            System.out.printf("\n---  PC = %d ---\n", programCounter);

            fetchInstruction();
            running = executeInstruction();

            printRegisters();
        }

        System.out.println("\n=== ВЫПОЛНЕНИЕ ПРОГРАММЫ ЗАВЕРШЕНО ===");
    }

    private void fetchInstruction() {
        int instructionWord = programMemory[programCounter];
        currentInstruction = decodeInstruction(instructionWord);
        System.out.printf("Выбрана инструкция: %s\n", currentInstruction);
    }

    private Instruction decodeInstruction(int instructionWord) {
        int cmdTypeCode = (instructionWord & CMD_TYPE_MASK) >>> CMD_TYPE_SHIFT;
        int literal = (instructionWord & LITERAL_MASK) >>> LITERAL_SHIFT;
        int dest = (instructionWord & DEST_MASK) >>> DEST_SHIFT;
        int op1 = (instructionWord & OP1_MASK) >>> OP1_SHIFT;
        int op2 = (instructionWord & OP2_MASK) >>> OP2_SHIFT;

        CommandType cmdType = CommandType.fromCode(cmdTypeCode);
        return new Instruction(cmdType, literal, dest, op1, op2);
    }

    private boolean executeInstruction() {
        CommandType cmdType = currentInstruction.getCmdType();
        int literal = currentInstruction.getLiteral();
        int dest = currentInstruction.getDest();
        int op1 = currentInstruction.getOp1();
        int op2 = currentInstruction.getOp2();
        switch (cmdType) {
            case LOAD:
                // LOAD literal, Rdest, Rop1, Rop2
                // Загрузка из памяти по адресу [Rop1 + literal] в Rdest
                int loadAddress = registers[op1] + literal;
                registers[dest] = dataMemory[loadAddress];
                System.out.printf("LOAD: R%d = MEM[R%d + %d] = MEM[%d] = %d\n",
                        dest, op1, literal, loadAddress, registers[dest]);
                programCounter++;
                break;
            case STORE:
                // STORE literal, Rdest, Rop1, Rop2
                // Сохранение Rop1 в память по адресу [Rdest + literal]
                int storeAddress = registers[dest] + literal;
                dataMemory[storeAddress] = registers[op1];
                System.out.printf("STORE: MEM[R%d + %d] = MEM[%d] = R%d = %d\n",
                        dest, literal, storeAddress, op1, dataMemory[storeAddress]);
                programCounter++;
                break;
            case ADD:
                // ADD literal, Rdest, Rop1, Rop2
                // Rdest = Rop1 + Rop2+literal
                registers[dest] = registers[op1] + registers[op2]+literal;
                System.out.printf("ADD: R%d = R%d + R%d = %d\n", dest, op1, op2, registers[dest]);
                programCounter++;
                break;
            case SUB:
                // SUB literal, Rdest, Rop1, Rop2
                // Rdest = Rop1 - Rop2
                registers[dest] = registers[op1] - registers[op2];
                System.out.printf("SUB: R%d = R%d - R%d = %d\n",
                        dest, op1, op2, registers[dest]);
                programCounter++;
                break;
            case CMP:
                // CMP literal, Rdest, Rop1, Rop2
                // Rdest = sign(Rop1 - Rop2)
                int cmpResult = registers[op1] - registers[op2];
                registers[dest] = Integer.compare(cmpResult, 0);
                System.out.printf("CMP: R%d = compare(R%d, R%d) = %d\n",
                        dest, op1, op2, registers[dest]);
                programCounter++;
                break;
            case JUMP:
                // JUMP literal, Rdest, Rop1, Rop2
                // Безусловный переход на адрес literal
                programCounter = literal;
                System.out.printf("JUMP: PC = %d\n", programCounter);
                break;
            case JUMP_EQ:
                // JUMP_EQ literal, Rdest, Rop1, Rop2
                // Переход если Rdest >= 0
                if (registers[dest] >= 0) {
                    programCounter = literal;
                    System.out.printf("JUMP_EQ: R%d >= 0, PC = %d\n", dest, programCounter);
                } else {
                    programCounter++;
                    System.out.printf("JUMP_EQ: R%d <= 0, no jump\n", dest);
                }
                break;
            case JUMP_GT:
                // JUMP_GT literal, Rdest, Rop1, Rop2
                // Переход если Rdest < 0
                if (registers[dest] < 0) {
                    programCounter = literal;
                    System.out.printf("JUMP_GT: R%d < 0, PC = %d\n", dest, programCounter);
                } else {
                    programCounter++;
                    System.out.printf("JUMP_GT: R%d >= 0, no jump\n", dest);
                }
                break;

            case HALT:
                System.out.println("HALT: Processor stopped");
                return false;

            default:
                System.out.println("UNKNOWN INSTRUCTION");
                return false;
        }

        return true;
    }

    private void printRegisters() {
        System.out.print("Регистры: [");
        for (int i = 0; i < registers.length; i++) {
            if (registers[i] != 0) { // Показываем только ненулевые регистры
                System.out.printf("R%d=%d ", i, registers[i]);
            }
        }
        System.out.println("]");
    }
}