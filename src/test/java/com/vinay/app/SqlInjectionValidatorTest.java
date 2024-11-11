package com.vinay.app;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SqlInjectionValidatorTest {

    @Test
    public void testValidSqlTemplate() {
        String safeSql = "SELECT * FROM users WHERE username = ? AND age = ?";
        assertTrue(SqlInjectionValidator.isValidSqlTemplate(safeSql),
                "Expected SQL to be valid.");
    }

    @Test
    public void testInvalidSqlTemplateDirectInput() {
        String unsafeSql = "SELECT * FROM users WHERE username = 'admin'";
        assertFalse(SqlInjectionValidator.isValidSqlTemplate(unsafeSql),
                "Expected SQL to be invalid due to direct input.");
    }

    @Test
    public void testInvalidSqlTemplateWithDifferentTable() {
        String unsafeSql = "SELECT * FROM orders WHERE order_id = ?";
        assertFalse(SqlInjectionValidator.isValidSqlTemplate(unsafeSql),
                "Expected SQL to be invalid due to unauthorized table.");
    }

    @Test
    public void testValidSqlTemplateWithoutWhereClause() {
        String safeSql = "SELECT * FROM users";
        assertTrue(SqlInjectionValidator.isValidSqlTemplate(safeSql),
                "Expected SQL to be valid without WHERE clause.");
    }
}
