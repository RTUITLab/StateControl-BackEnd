package ru.realityfamily.controllback.Models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "states")
public class States {

    private long id;
    private String gameName;
    private List<String> statesList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(name = "GameName", nullable = false)
    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @ElementCollection
    @Column(name = "States", nullable = true)
    public List<String> getStatesList() { return statesList; }
    public void setStatesList(List<String> statesList) {
        this.statesList = statesList;
    }
}
