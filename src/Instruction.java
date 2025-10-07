class Instruction {
    private CommandType cmdType;
    private int literal;
    private int dest;
    private int op1;
    private int op2;

    public Instruction(CommandType cmdType, int literal, int dest, int op1, int op2) {
        this.cmdType = cmdType;
        this.literal = literal;
        this.dest = dest;
        this.op1 = op1;
        this.op2 = op2;
    }

    // Геттеры
    public CommandType getCmdType() { return cmdType; }
    public int getLiteral() { return literal; }
    public int getDest() { return dest; }
    public int getOp1() { return op1; }
    public int getOp2() { return op2; }

    @Override
    public String toString() {
        return String.format("%s literal=%d, R%d, R%d, R%d",
                cmdType, literal, dest, op1, op2);
    }
}

enum CommandType {
    LOAD(0),      // Загрузка из памяти
    STORE(1),     // Сохранение в память
    ADD(2),       // Сложение
    SUB(3),       // Вычитание
    MUL(4),       // Умножение
    CMP(5),       // Сравнение
    JUMP(6),      // Безусловный переход
    JUMP_EQ(7),   // Переход если равно
    JUMP_GT(8),   // Переход если больше
    HALT(15);     // Остановка

    private final int code;

    CommandType(int code) {
        this.code = code;
    }

    public int getCode() { return code; }

    public static CommandType fromCode(int code) {
        for (CommandType type : values()) {
            if (type.code == code) return type;
        }
        throw new IllegalArgumentException("Unknown command type: " + code);
    }

}