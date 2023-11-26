package io.spentify.expenses;

import io.spentify.expenses.Category.CategoryIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Categories extends JpaRepository<Category, CategoryIdentifier> {

    @Query("""
            SELECT COUNT (c.id) = 1
            FROM Category c
            WHERE c.account = :account and c.name = :name
            """)
    boolean existsByAccountAndName(Category.AccountIdentifier account, String name);

}
