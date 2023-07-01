package com.thoroldvix.economatic.server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

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
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Region region;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Faction faction;

    @Column(nullable = false)
    private Locale locale;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private ServerType type;

    @Column(nullable = false, updatable = false)
    private String uniqueName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Server server = (Server) o;
        return getId() != null && Objects.equals(getId(), server.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}