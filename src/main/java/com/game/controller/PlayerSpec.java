package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class PlayerSpec {
    public static Specification<Player> getPlayerByNameSpec(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null)
                return criteriaBuilder.conjunction();
           return criteriaBuilder.like(root.get("name"), "%" + name + "%");
       });
    }

    public static Specification<Player> getPlayerByTitleSpec(String title) {
        return ((root, query, criteriaBuilder) -> {
            if (title == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        });
    }

    public static Specification<Player> getPlayerByRaceSpec(Race race) {
        return ((root, query, criteriaBuilder) -> {
            if (race == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("race"), race);
        });
    }

    public static Specification<Player> getPlayerByProfessionSpec(Profession profession) {
        return ((root, query, criteriaBuilder) -> {
            if (profession == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("profession"), profession);
        });
    }

    public static Specification<Player> getPlayerByBirthdaySpec(Long after, Long before) {
        return ((root, query, criteriaBuilder) -> {
            if (after == null && before == null)
                return criteriaBuilder.conjunction();
            else if (after == null && before != null)
                return criteriaBuilder.between(root.get("birthday"), new Date(0), new Date(before));
            else if (after != null && before == null)
                return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(3000,0,1));
            return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        });
    }

    public static Specification<Player> getPlayerByBannedSpec(Boolean banned) {
        return ((root, query, criteriaBuilder) -> {
            if (banned == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("banned"), banned);
        });
    }

    public static Specification<Player> getPlayerByExperienceSpec(Integer minExperience, Integer maxExperience) {
        return ((root, query, criteriaBuilder) -> {
            Integer minInt = 0;
            Integer maxInt = 10000000;

            if (minExperience == null && maxExperience == null)
                return criteriaBuilder.conjunction();
            else if (minExperience == null && maxExperience != null)
                return criteriaBuilder.between(root.get("experience"), minInt, maxExperience);
            else if (minExperience != null && maxExperience == null)
                return criteriaBuilder.between(root.get("experience"), minExperience, maxInt);
            return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
        });
    }

    public static Specification<Player> getPlayerByLevelSpec(Integer minLevel, Integer maxLevel) {
        return ((root, query, criteriaBuilder) -> {
            if (minLevel == null && maxLevel == null)
                return criteriaBuilder.conjunction();
            else if (minLevel == null && maxLevel != null)
                return criteriaBuilder.between(root.get("level"), Integer.MIN_VALUE, maxLevel);
            else if (minLevel != null && maxLevel == null)
                return criteriaBuilder.between(root.get("level"), minLevel, Integer.MAX_VALUE);
            return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
        });
    }
}
