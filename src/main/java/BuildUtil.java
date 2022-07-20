package main.java;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class BuildUtil {
    static void buildSelectFrom(List<Command> selectFromCommands, StringBuilder sb) {
        sb.append(String.format("SELECT * FROM %s", getStringFromArgs(selectFromCommands.get(0).args)));
        selectFromCommands.subList(1, selectFromCommands.size())
                .forEach(c -> sb.append(", ").append(getStringFromArgs(c.args)));
    }

    static void buildSelectWhat(List<Command> selectWhatCommands, StringBuilder sb) {
        String[] allArgsFromSelectWhat = selectWhatCommands.stream()
                .flatMap(c -> Arrays.stream(c.args))
                .toArray(String[]::new);

        if (allArgsFromSelectWhat.length > 0) {
            int index = sb.indexOf("*");
            sb.replace(index, index + 1, getStringFromArgs(allArgsFromSelectWhat));
        }
    }

    static void buildInnerJoin(List<Command> innerJoinCommands, StringBuilder sb) {
        innerJoinCommands
                .forEach(com -> sb.append(
                        String.format(" INNER JOIN %s ON %s = %s", com.args[0], com.args[1], com.args[2])));

    }

    static void buildWhere(List<Command> whereNotNullCommands, List<Command> whereInCommands, StringBuilder sb) {
        String[] allArgsFromWhereNotNull = whereNotNullCommands.stream()
                .flatMap(c -> Arrays.stream(c.args))
                .toArray(String[]::new);

        if (allArgsFromWhereNotNull.length > 0) {
            sb.append(String.format(" WHERE %s IS NOT NULL", getStringFromArgs(allArgsFromWhereNotNull)));
            for (Command c : whereInCommands) {
                String[] strings = Arrays.copyOfRange(c.args, 1, c.args.length);
                sb.append(String.format(" AND %s IN (%s)", c.args[0], getStringFromArgs(strings)));
            }
        } else {
            if (whereInCommands.size() > 0) {
                Command com = whereInCommands.get(0);
                String[] arr = Arrays.copyOfRange(com.args, 1, com.args.length);
                sb.append(String.format(" WHERE %s IN (%s)", com.args[0], getStringFromArgs(arr)));
                for (Command c : whereInCommands.subList(1, whereInCommands.size())) {
                    String[] strings = Arrays.copyOfRange(c.args, 1, c.args.length);
                    sb.append(String.format(" AND %s IN (%s)", c.args[0], getStringFromArgs(strings)));
                }
            }
        }
    }

    static void buildOrderedBy(List<Command> orderedByCommands, StringBuilder sb) {
        String[] allArgsFromOrderedBy = orderedByCommands.stream()
                .flatMap(c -> Arrays.stream(c.args))
                .toArray(String[]::new);

        if (allArgsFromOrderedBy.length > 0) {
            sb.append(String.format(" ORDERED BY %s", getStringFromArgs(allArgsFromOrderedBy)));
        }
    }

    static void buildLimit(List<Command> limitCommands, StringBuilder sb) {
        limitCommands.stream()
                .min(Comparator.comparing(o -> Integer.valueOf(o.args[0])))
                .ifPresent(command ->
                        sb.insert(sb.indexOf("SELECT") + 6,
                                String.format(" TOP(%s)", command.args[0])));
    }

    private static String getStringFromArgs(String[] args) {
        return Arrays.toString(args)
                .replaceAll("]", "")
                .replaceAll("\\[", "");
    }
}
