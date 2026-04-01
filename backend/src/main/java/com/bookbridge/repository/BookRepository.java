package com.bookbridge.repository;
import com.bookbridge.model.Book;
import com.bookbridge.model.Book.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // All books NOT owned by this user that are available
    List<Book> findByStatusAndOwnerIdNot(BookStatus status, Long ownerId);

    // All books owned by this user
    List<Book> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    // Search by title or author or subject (case-insensitive)
    @Query("SELECT b FROM Book b WHERE b.owner.id != :userId AND b.status = 'AVAILABLE' " +
           "AND (LOWER(b.title) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(b.subject) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Book> search(String q, Long userId);
}
