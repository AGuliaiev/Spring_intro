package org.example.springintro.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.example.springintro.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    private static final Role.RoleName TEST_ROLE_NAME = Role.RoleName.USER;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Find role by role name")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRole_ReturnsRole() {
        Optional<Role> roleOptional = roleRepository.findByRole(TEST_ROLE_NAME);

        assertThat(roleOptional).isPresent();
        Role role = roleOptional.get();
        assertThat(role.getRole()).isEqualTo(TEST_ROLE_NAME);
    }
}
