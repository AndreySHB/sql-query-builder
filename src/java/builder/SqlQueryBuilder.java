package java.builder;

import java.util.*;

import static java.builder.BuildUtil.*;
import static java.builder.CommandType.*;

public class SqlQueryBuilder {
    private final Map<CommandType, List<Command>> commands = new HashMap<>();

    {
        Arrays.stream(values())
                .forEach(commandType -> commands.put(commandType, new ArrayList<>()));
    }

    @Override
    public String toString() {
        return buildQuery();
    }

    private String buildQuery() {
        if (commands.get(SELECT_FROM).isEmpty())
            throw new UnsupportedOperationException("No initial query");

        StringBuilder sb = new StringBuilder();
        buildSelectFrom(commands.get(SELECT_FROM), sb);
        buildSelectWhat(commands.get(SELECT_WHAT), sb);
        buildInnerJoin(commands.get(INNER_JOIN), sb);
        buildWhere(commands.get(WHERE_NOT_NULL), commands.get(WHERE_IN), sb);
        buildOrderedBy(commands.get(ORDERED_BY), sb);
        buildLimit(commands.get(LIMIT), sb);
        return sb.toString();
    }

    public SqlQueryBuilder query(String... args) {
        commands.get(SELECT_FROM)
                .add(new Command(SELECT_FROM, args));
        return this;
    }

    public SqlQueryBuilder select(String... args) {
        commands.get(SELECT_WHAT)
                .add(new Command(SELECT_WHAT, args));
        return this;
    }

    public SqlQueryBuilder orderedBy(String... args) {
        commands.get(ORDERED_BY)
                .add(new Command(ORDERED_BY, args));
        return this;
    }

    public SqlQueryBuilder whereNotNull(String... args) {
        commands.get(WHERE_NOT_NULL)
                .add(new Command(WHERE_NOT_NULL, args));
        return this;
    }

    public SqlQueryBuilder whereIn(String arg1, String[] args) {
        String[] allArgs = new String[args.length + 1];
        allArgs[0] = arg1;
        System.arraycopy(args, 0, allArgs, 1, allArgs.length - 1);
        commands.get(WHERE_IN)
                .add(new Command(WHERE_IN, allArgs));
        return this;
    }

    public SqlQueryBuilder innerJoin(String... args) {
        commands.get(INNER_JOIN)
                .add(new Command(INNER_JOIN, args));
        return this;
    }

    public SqlQueryBuilder limit(String... args) {
        commands.get(LIMIT)
                .add(new Command(LIMIT, args));
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
