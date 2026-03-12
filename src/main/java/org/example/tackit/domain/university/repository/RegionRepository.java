package org.example.tackit.domain.university.repository;

import java.util.Optional;
import org.example.tackit.domain.entity.org.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Integer> {

  // 시도코드(예: "11", "41")로 지역 정보를 찾는 메서드
  Optional<Region> findBySidoCode(String sidoCode);

}
