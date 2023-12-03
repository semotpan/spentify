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
            WHERE c.account = :account AND c.name = :name
            """)
    boolean existsByAccountAndName(AccountIdentifier account, String name);

    @Query("""
            SELECT COUNT (c.id) = 1
            FROM Category c
            WHERE c.id = :id AND c.account = :account
            """)
    boolean existsByIdAndAccount(CategoryIdentifier id, AccountIdentifier account);

}
