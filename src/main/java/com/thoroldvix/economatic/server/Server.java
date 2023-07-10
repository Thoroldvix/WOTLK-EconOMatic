package com.thoroldvix.economatic.server;

import jakarta.persistence.*;
import lombok.*;

import java.util.Locale;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "server")
public class Server {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "region", nullable = false)
    private Region region;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "faction", nullable = false)
    private Faction faction;

    @Column(name = "locale", nullable = false)
    private Locale locale;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private ServerType type;

    @Column(name = "unique_name", nullable = false, updatable = false)
    private String uniqueName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(id, server.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}