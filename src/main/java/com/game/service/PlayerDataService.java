package com.game.service;

import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerDataService {

    @Autowired
    private PlayerRepository playerRepository;

//    Optional<Player> playerOptional = playerRepository.findById(1L);
}
