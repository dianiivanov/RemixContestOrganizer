package com.organizer.contest.remix.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class LobData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Lob
    private byte[] data;

    @OneToOne(mappedBy = "lobData")
    DBFile dbFile;

    public LobData(byte[] bytes) {
        this.data = bytes;
    }
}
