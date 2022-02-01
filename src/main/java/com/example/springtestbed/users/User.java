package com.example.springtestbed.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

/**
 * TODO - review jpa auditing capabilities
 *   https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing
 */

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq__username", columnNames = "username")
        }
)
@EntityListeners(UserAuditListener.class)
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    private String username;
    private String password;

    @CreatedDate
    private Instant created;
    @LastModifiedDate
    private Instant lastModified;

    // note - should be ok to eagerly fetch, there aren't that many roles
    @OneToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
