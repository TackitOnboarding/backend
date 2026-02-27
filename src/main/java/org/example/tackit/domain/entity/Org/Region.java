package org.example.tackit.domain.entity.Org;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "region")
public class Region {
    @Id
    private Integer regionId;

    @Column(unique = true, nullable = false, length = 50)
    private String regionName;

    @Column(unique = true, nullable = false, length = 10)
    private String sidoCode;
}
