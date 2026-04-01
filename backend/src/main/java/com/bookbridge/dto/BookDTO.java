package com.bookbridge.dto;
import com.bookbridge.model.Book.BookStatus;
import com.bookbridge.model.Book.Condition;
import lombok.Data;

@Data
public class BookDTO {
    private String     title;
    private String     author;
    private String     subject;
    private String     edition;
    private Condition  condition;
    private String     description;
    private BookStatus status;
}
