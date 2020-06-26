package ru.realityfamily.controllback.Models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Devices")
public class Devices {

    private long id;
    private String game;
    private int sessionId;

    public Devices() {}

    public Devices(String Game, int SessionId) {
        this.game = Game;
        this.sessionId = SessionId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "Game", nullable = false)
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    @Column(name = "SessionId", nullable = false)
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "{\n\tid=" + id + ";\n\tgame=" + game + ";\n\tip=" + sessionId + ";\n}";
    }
}
