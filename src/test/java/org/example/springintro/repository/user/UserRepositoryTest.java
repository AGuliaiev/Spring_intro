package org.example.springintro.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.example.springintro.model.Role;
import org.example.springintro.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final Role.RoleName EXPECTED_ROLE = Role.RoleName.USER;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Check if email exists")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users/add-users-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-users-roles.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnTrueIfEmailExists() {
        boolean emailExists = userRepository.existsByEmail(TEST_EMAIL);
        assertThat(emailExists).isTrue();
    }

    @Test
    @DisplayName("Find user by email with roles")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users/add-users-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/remove-users-roles.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFindUserByEmailWithRoles() {
        Optional<User> userOptional = userRepository.findByEmail(TEST_EMAIL);

        assertThat(userOptional).isPresent();

        User user = userOptional.get();
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(user.getRoles()).isNotEmpty();
        assertThat(user.getRoles().iterator().next().getRole()).isEqualTo(EXPECTED_ROLE);
    }
}
