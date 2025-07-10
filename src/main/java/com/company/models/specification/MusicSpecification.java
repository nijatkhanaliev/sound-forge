package com.company.models.specification;

import com.company.models.entity.Music;
import com.company.models.enums.MusicStatus;
import org.springframework.data.jpa.domain.Specification;

public class MusicSpecification {

    public static Specification<Music> hasMusicianFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.trim().isEmpty()) {
                return cb.disjunction();
            }

            return cb.like(cb.lower(root.get("user").get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<Music> hasMusicianLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.trim().isEmpty()) {
                return cb.disjunction();
            }

            return cb.like(cb.lower(root.get("user").get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<Music> hasStatus(MusicStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.disjunction();
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Music> withOwnerId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return cb.disjunction();
            }

            return cb.equal(root.get("user").get("id"), id);
        };
    }

    public static Specification<Music> notDeleted() {
        return ((root, query, cb) -> cb.notEqual(root.get("status"), MusicStatus.DELETED));
    }

    public static Specification<Music> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return cb.disjunction();
            }

            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<Music> hasTitle(String title) {
        return ((root, query, cb) -> {
            if (title == null) {
                return cb.disjunction();
            }

            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        });
    }

}
