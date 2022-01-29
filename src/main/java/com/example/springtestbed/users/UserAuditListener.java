package com.example.springtestbed.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Slf4j
@Component
public class UserAuditListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeUpdate(User user) {
        if (user.getId() == 0) {
            log.info("[USER AUDIT] about to add a new user");
        } else {
            log.info("[USER AUDIT] about to update/delete user: " + user.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterUpdate(User user) {
        log.info("[USER AUDIT] add/update/delete complete for user: " + user.getId());
    }

    @PostLoad
    private void afterLoad(User user) {
        log.info("[USER AUDIT] user loaded from database: " + user.getId());
    }

}
