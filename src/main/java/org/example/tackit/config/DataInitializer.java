package org.example.tackit.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tackit.domain.entity.ActiveStatus;
import org.example.tackit.domain.entity.Member;
import org.example.tackit.domain.entity.org.BranchType;
import org.example.tackit.domain.entity.org.Region;
import org.example.tackit.domain.entity.org.University;
import org.example.tackit.domain.member.repository.MemberRepository;
import org.example.tackit.domain.university.dto.UniversityRawDto;
import org.example.tackit.domain.university.repository.RegionRepository;
import org.example.tackit.domain.university.repository.UniversityRepository;
import org.example.tackit.global.utils.HangulUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final UniversityRepository universityRepository;
  private final RegionRepository regionRepository;
  private final ObjectMapper objectMapper;

  // commandLineRunner의 run 메서드는 String... args로 유지 (String[] args와 유사) -> 스프링 부트 공식 문서 참고
  // args를 꼭 배열로 넘겨야 하는 건 아니고 가변적으로 받을 수 있다는 의도를 나타냄
  @Override
  public void run(String... args) throws Exception {
    // 1. Region 지역 데이터 - 대학 데이터보다 먼저 존재해야 함
    initializeRegions();

    // 2. University 대학 데이터
    initializeUniversities();

    // 3. 테스트/관리자 데이터
    initializeAdmin();
  }

  private void initializeRegions() {
    if (regionRepository.count() == 0) {
      List<Region> regions = new ArrayList<>();

      // 0번: 연합 동아리
      regions.add(Region.builder()
          .regionId(0)
          .regionName("전국(연합)")
          .sidoCode("00") // 공공데이터에 없는 가상의 코드
          .build());

      regions.addAll(List.of(
          Region.builder().regionId(1).regionName("서울특별시").sidoCode("11").build(),
          Region.builder().regionId(2).regionName("부산광역시").sidoCode("26").build(),
          Region.builder().regionId(3).regionName("대구광역시").sidoCode("27").build(),
          Region.builder().regionId(4).regionName("인천광역시").sidoCode("28").build(),
          Region.builder().regionId(5).regionName("광주광역시").sidoCode("29").build(),
          Region.builder().regionId(6).regionName("대전광역시").sidoCode("30").build(),
          Region.builder().regionId(7).regionName("울산광역시").sidoCode("31").build(),
          Region.builder().regionId(8).regionName("세종특별자치시").sidoCode("36").build(),
          Region.builder().regionId(9).regionName("경기도").sidoCode("41").build(),
          Region.builder().regionId(10).regionName("강원특별자치도").sidoCode("51").build(),
          Region.builder().regionId(11).regionName("충청북도").sidoCode("43").build(),
          Region.builder().regionId(12).regionName("충청남도").sidoCode("44").build(),
          Region.builder().regionId(13).regionName("전북특별자치도").sidoCode("52").build(),
          Region.builder().regionId(14).regionName("전라남도").sidoCode("46").build(),
          Region.builder().regionId(15).regionName("경상북도").sidoCode("47").build(),
          Region.builder().regionId(16).regionName("경상남도").sidoCode("48").build(),
          Region.builder().regionId(17).regionName("제주특별자치도").sidoCode("50").build()
      ));

      regionRepository.saveAll(regions);
      log.info("Region data initialized (including Union code 0).");
    }
  }

  private void initializeUniversities() throws Exception {
      if (universityRepository.count() > 0) {
          return;
      }

    InputStream inputStream = new ClassPathResource("data/university_data.json").getInputStream();
    JsonNode records = objectMapper.readTree(inputStream).path("records");
    List<University> universityList = new ArrayList<>();

    for (JsonNode node : records) {
      UniversityRawDto dto = objectMapper.treeToValue(node, UniversityRawDto.class);

        if ("대학원".equals(dto.getUniversityType())) {
            continue;
        }

      regionRepository.findBySidoCode(dto.getSidoCode()).ifPresent(region -> {
        universityList.add(University.builder()
            .universityName(dto.getSchoolName())
            .universityChosung(HangulUtils.getChosung(dto.getSchoolName()))
            .branchType("본교".equals(dto.getBranchType()) ? BranchType.MAIN : BranchType.BRANCH)
            .region(region)
            .address(dto.getAddress())
            .build());
      });
    }
    universityRepository.saveAll(universityList);
    log.info("Successfully initialized {} universities.", universityList.size());
  }

  private void initializeAdmin() {
    if (memberRepository.findByEmail("contact.tackit@gmail.com").isEmpty()) {
      memberRepository.save(Member.builder()
          .email("contact.tackit@gmail.com")
          .password(passwordEncoder.encode("admin1"))
          .name("관리자")
          .activeStatus(ActiveStatus.ACTIVE)
          .createdAt(LocalDateTime.now())
          .build());
    }
  }
}
