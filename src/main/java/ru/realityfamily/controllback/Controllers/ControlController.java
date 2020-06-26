package ru.realityfamily.controllback.Controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.realityfamily.controllback.Repository.DevicesRepository;
import ru.realityfamily.controllback.Repository.StateRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/control")
public class ControlController {
    @Autowired
    private DevicesRepository devicesRepository;
    @Autowired
    private StateRepository stateRepository;

    @GetMapping(value = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAllGames() {
        List<String> games = new ArrayList<>();
        devicesRepository.findAll().forEach(device -> {
            games.add(device.getGame());
        });
        return games;
    }

    @GetMapping(value = "/{game}/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> getDevicesByGame (@PathVariable(value = "game") String Game) {
        List<Long> devices = new ArrayList<>();
        devicesRepository.findAll().forEach(device -> {
            if (Game.equals(device.getGame())) {
                devices.add(device.getId());
            }
        });
        return devices;
    }

    @GetMapping(value = "/{game}/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> GetGameStates(@PathVariable(value = "game") String Game) {
        List<String> States = new ArrayList<>();
        stateRepository.findAll().forEach(state -> {
            if (Game.equals(state.getGameName())) {
                States.addAll(state.getStatesList());
            }
        });
        return States;
    }

    @PostMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity SendState(@RequestBody String json) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (json.equals("") || json.equals("{}")) {
            return new ResponseEntity<>("No content in request.", HttpStatus.NO_CONTENT);
        }else if (!(obj.has("Device") && obj.has("GameName") && obj.has("State"))) {
            return new ResponseEntity<String>("Incomplete content in the request.", HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(null);
        }
    }
}
