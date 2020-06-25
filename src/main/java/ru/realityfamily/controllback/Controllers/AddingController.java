package ru.realityfamily.controllback.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.realityfamily.controllback.Models.States;
import ru.realityfamily.controllback.Repository.StateRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/add_states")
public class AddingController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping(value = "/check/{game}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> GetGameStates(@PathVariable(value = "game") String Game) {
        List<String> States = new ArrayList<>();
        stateRepository.findAll().forEach(state -> {
            if (Game.equals(state.getGameName())) {
                States.addAll(state.getStatesList());
            }
        });
        return States;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<States> SendState(@RequestBody States states) {
        for (States state: stateRepository.findAll()) {
            if (state.getGameName().equals(states.getGameName())) {
                try {
                    States temp = stateRepository.findById(state.getId())
                            .orElseThrow(() -> new Exception());
                    temp.setStatesList(states.getStatesList());
                    return ResponseEntity.ok(stateRepository.save(temp));
                } catch (Exception e) {
                    return new ResponseEntity<States>(states, HttpStatus.NO_CONTENT);
                }
            }
        }
        return ResponseEntity.ok(stateRepository.save(states));
    }
}
