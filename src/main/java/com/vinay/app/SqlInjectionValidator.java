package com.vinay.app;

import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class SqlInjectionValidator {

    // Visitor class to ensure safe structure
    public static class SafeSqlVisitor extends ExpressionVisitorAdapter {
        private boolean hasOnlySafeParameters = true;

        @Override
        public void visit(JdbcParameter jdbcParameter) {
            // If we encounter a JdbcParameter ("?"), we know it's safe
            hasOnlySafeParameters = hasOnlySafeParameters && jdbcParameter != null;
        }

        public boolean isOnlySafeParameters() {
            return hasOnlySafeParameters;
        }
    }

    public static boolean isValidSqlTemplate(String sqlTemplate) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sqlTemplate);
            if (statement instanceof Select) {
                PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
                
                // Check if only safe tables are allowed
                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                boolean validTables = tablesNamesFinder.getTableList(statement).stream()
                		.peek(table -> System.out.println(table))
                        .allMatch(table -> table.equalsIgnoreCase("users"));

                if (!validTables) {
                    return false; // Invalid table detected
                }
                
             // Check WHERE clause for placeholder usage
                PlainSelect plainSelect = (PlainSelect) ((Select) statement).getSelectBody();
                if (plainSelect.getWhere() != null) {
                	SafeSqlVisitor visitor = new SafeSqlVisitor();
                	plainSelect.getWhere().accept(visitor);
                	return visitor.isOnlySafeParameters();
                }
            }
        } catch (Exception e) {
            System.err.println("SQL Parsing error: " + e.getMessage());
            return false;
        }
        return false;
    }
}
