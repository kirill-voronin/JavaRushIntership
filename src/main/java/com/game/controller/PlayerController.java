package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.PutRequestPlayer;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;


@RestController
public class PlayerController {
    @Autowired
    PlayerRepository repository;

    @Autowired
    PlayerDataService service;

    @GetMapping(value = "/rest/players")
    public List<Player> getPlayers(String name,
                                   String title,
                                   Race race,
                                   Profession profession,
                                   Long after,
                                   Long before,
                                   Boolean banned,
                                   Integer minExperience,
                                   Integer maxExperience,
                                   Integer minLevel,
                                   Integer maxLevel,
                                   PlayerOrder order,
                                   Integer pageNumber,
                                   Integer pageSize) {
        List<Player> allPlayers = repository.findAll(Specification.where(where(PlayerSpec.getPlayerByNameSpec(name)
                .and(PlayerSpec.getPlayerByTitleSpec(title)
                .and(PlayerSpec.getPlayerByRaceSpec(race))
                .and(PlayerSpec.getPlayerByProfessionSpec(profession))
                .and(PlayerSpec.getPlayerByBirthdaySpec(after, before))
                .and(PlayerSpec.getPlayerByBannedSpec(banned))
                .and(PlayerSpec.getPlayerByExperienceSpec(minExperience, maxExperience))
                .and(PlayerSpec.getPlayerByLevelSpec(minLevel, maxLevel))
        ))));
        order = order == null ? PlayerOrder.ID : order;
        List<Player> result = new ArrayList<>();

        if (order == PlayerOrder.ID) {
            Collections.sort(allPlayers, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        } else if (order == PlayerOrder.NAME) {
            Collections.sort(allPlayers, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        } else if (order == PlayerOrder.EXPERIENCE) {
            Collections.sort(allPlayers, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getExperience().compareTo(o2.getExperience());
                }
            });
        } else if (order == PlayerOrder.BIRTHDAY) {
            Collections.sort(allPlayers, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getBirthday().compareTo(o2.getBirthday());
                }
            });
        } else if (order == PlayerOrder.LEVEL) {
            Collections.sort(allPlayers, new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return o1.getLevel().compareTo(o2.getLevel());
                }
            });
        }

        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        int startIndex = pageSize * pageNumber;
        int lastIndex = pageSize * pageNumber + pageSize;

        for (int i = startIndex; i < lastIndex; i++)
            if (i < allPlayers.size())
                result.add(allPlayers.get(i));


        return result;
    }

    @GetMapping(value = "/rest/players/count")
    public Integer getCount(String name,
                            String title,
                            Race race,
                            Profession profession,
                            Long after,
                            Long before,
                            Boolean banned,
                            Integer minExperience,
                            Integer maxExperience,
                            Integer minLevel,
                            Integer maxLevel) {
        List<Player> allPlayers = repository.findAll(Specification.where(PlayerSpec.getPlayerByNameSpec(name)
                .and(PlayerSpec.getPlayerByTitleSpec(title)
                .and(PlayerSpec.getPlayerByRaceSpec(race))
                .and(PlayerSpec.getPlayerByProfessionSpec(profession))
                .and(PlayerSpec.getPlayerByBirthdaySpec(after, before))
                .and(PlayerSpec.getPlayerByBannedSpec(banned))
                .and(PlayerSpec.getPlayerByExperienceSpec(minExperience, maxExperience))
                .and(PlayerSpec.getPlayerByLevelSpec(minLevel, maxLevel))
                )));
        Integer result = allPlayers.size();
        return result;
    }

    @GetMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        if (id <= 0 || id % 1 != 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player result = repository.findById(id).orElse(new Player());
        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> deletePlayerById(@PathVariable Long id) {
        if (id <= 0 || id % 1 != 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player result = repository.findById(id).orElse(new Player());
        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/rest/players")
    public ResponseEntity<Player> createNewPlayer(@RequestBody PutRequestPlayer params) {
        if (isRequestParamsValidForCreate(params))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Player result = new Player();

        result.setName(params.getName());
        result.setTitle(params.getTitle());
        result.setRace(params.getRace());
        result.setProfession(params.getProfession());
        result.setExperience(params.getExperience());
        result.setLevel(setLevel(params.getExperience()));
        result.setUntilNextLevel(setUntilNextLevel(result.getLevel(), params.getExperience()));
        result.setBirthday(new Date(params.getBirthday()));
        if (params.getBanned() == null)
            result.setBanned(false);
        else
            result.setBanned(params.getBanned());

        System.out.println(result);

        repository.save(result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private boolean isRequestParamsValidForCreate(PutRequestPlayer params) {
        if (params.isNotComplete() ||
                params.getName().length() > 12 ||
                params.getName().isEmpty() ||
                params.getTitle().length() > 30 ||
                params.getExperience() < 0 ||
                params.getExperience() > 10000000 ||
                new Date(params.getBirthday()).getYear() < 100 ||
                new Date(params.getBirthday()).getYear() > 1100
        )
            return true;

        return false;
    }

    private boolean isRequestParamsValidForPut(PutRequestPlayer params) {
        if (params.getName() != null && params.getName().length() > 12)
                return true;
        if (params.getName() != null && params.getName().isEmpty())
            return true;
        if (params.getTitle() != null && params.getTitle().length() > 30)
            return true;
        if (params.getExperience() != null && params.getExperience() < 0)
            return true;
        if (params.getExperience() != null && params.getExperience() >10000000)
            return true;
        if (params.getBirthday() != null && new Date(params.getBirthday()).getYear() < 100)
            return true;
        if (params.getBirthday() != null && new Date(params.getBirthday()).getYear() > 1100)
            return true;

        return false;
    }

    // попробовать создать модель запроса
    @PostMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> putPlayerById(@PathVariable Long id, @RequestBody PutRequestPlayer params) {
        if (id <= 0 || id % 1 != 0  || isRequestParamsValidForPut(params))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player result = repository.findById(id).orElse(new Player());
        if (result.isEmpty() || result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (params.getName() != null) result.setName(params.getName());
        if (params.getTitle() != null) result.setTitle(params.getTitle());
        if (params.getRace() != null) result.setRace(params.getRace());
        if (params.getProfession() != null)  result.setProfession(params.getProfession());
        if (params.getExperience() != null) result.setExperience(params.getExperience());
        if (params.getExperience() != null)  result.setLevel(setLevel(params.getExperience()));
        if (params.getExperience() != null)  result.setUntilNextLevel(setUntilNextLevel(result.getLevel(), params.getExperience()));
        if (params.getBirthday() != null)  result.setBirthday(new Date(params.getBirthday()));
        if (params.getBanned() != null)  result.setBanned(params.getBanned());

        repository.save(result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public Integer setLevel(Integer exp) {
        return (int) (Math.sqrt(2500 + 200 * exp) - 50) / 100;
    }

    public Integer setUntilNextLevel(Integer lvl, Integer exp) {
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }
}
