package main.java;

class Command {
    public Command(CommandType type, String[] args) {
        this.type = type;
        this.args = args;
    }

    final CommandType type;
    final String[] args;
}
