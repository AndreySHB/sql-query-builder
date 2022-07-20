package main.java;

import java.util.*;

public class SqlQueryBuilder {
    private final Map<CommandType, List<Command>> commands = new HashMap<>();

    {
        Arrays.stream(CommandType.values())
                .forEach(commandType -> commands.put(commandType, new ArrayList<>()));
    }

    @Override
    public String toString() {
        return buildQuery();
    }

    private String buildQuery() {
        if (commands.get(CommandType.SELECT_FROM).isEmpty())
            throw new UnsupportedOperationException("No initial query");

        StringBuilder sb = new StringBuilder();
        BuildUtil.buildSelectFrom(commands.get(CommandType.SELECT_FROM), sb);
        BuildUtil.buildSelectWhat(commands.get(CommandType.SELECT_WHAT), sb);
        BuildUtil.buildInnerJoin(commands.get(CommandType.INNER_JOIN), sb);
        BuildUtil.buildWhere(commands.get(CommandType.WHERE_NOT_NULL), commands.get(CommandType.WHERE_IN), sb);
        BuildUtil.buildOrderedBy(commands.get(CommandType.ORDERED_BY), sb);
        BuildUtil.buildLimit(commands.get(CommandType.LIMIT), sb);
        return sb.toString();
    }

    public SqlQueryBuilder query(String... args) {
        commands.get(CommandType.SELECT_FROM)
                .add(new Command(CommandType.SELECT_FROM, args));
        return this;
    }

    public SqlQueryBuilder select(String... args) {
        commands.get(CommandType.SELECT_WHAT)
                .add(new Command(CommandType.SELECT_WHAT, args));
        return this;
    }

    public SqlQueryBuilder orderedBy(String... args) {
        commands.get(CommandType.ORDERED_BY)
                .add(new Command(CommandType.ORDERED_BY, args));
        return this;
    }

    public SqlQueryBuilder whereNotNull(String... args) {
        commands.get(CommandType.WHERE_NOT_NULL)
                .add(new Command(CommandType.WHERE_NOT_NULL, args));
        return this;
    }

    public SqlQueryBuilder whereIn(String arg1, String[] args) {
        String[] allArgs = new String[args.length + 1];
        allArgs[0] = arg1;
        System.arraycopy(args, 0, allArgs, 1, allArgs.length - 1);
        commands.get(CommandType.WHERE_IN)
                .add(new Command(CommandType.WHERE_IN, allArgs));
        return this;
    }

    public SqlQueryBuilder innerJoin(String... args) {
        commands.get(CommandType.INNER_JOIN)
                .add(new Command(CommandType.INNER_JOIN, args));
        return this;
    }

    public SqlQueryBuilder limit(String... args) {
        commands.get(CommandType.LIMIT)
                .add(new Command(CommandType.LIMIT, args));
        return this;
    }

    public static void main(String[] args) {
        SqlQueryBuilder sqb = new SqlQueryBuilder();
        System.out.println(sqb
                .query("books", "papers")
                .select("authors", "authors2")
                .innerJoin("authors", "authors.id", "Books.authorsId")
                .innerJoin("authors2", "authors.id2", "Books.authorsId2")
                .whereNotNull("authorId, smthElse")
                .whereNotNull("oneMoreThing")
                .whereIn("Lang", new String[]{"en", "fr"})
                .whereIn("Lang2", new String[]{"en2", "fr2"})
                .limit("10")
                .orderedBy("1", "2")
                .orderedBy("3", "4")
                .limit("5"));
    }
}
