package io.spentify.expenses;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest(classes = TestExpensesApplication.class)
class ExpensesApplicationTests {

    @Test
    void contextLoads() {
    }

}
