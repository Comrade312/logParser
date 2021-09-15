package dev.entity;

import lombok.Data;

/**
 * Log table column
 */
@Data
public class Column {
    /** Column name */
    private String columnName;

    /** Column size*/
    private Long columnSize;

    /** Left delimiter for log value */
    private String leftLimiter;

    /** Right delimiter for log value */
    private String rightLimiter;
}
