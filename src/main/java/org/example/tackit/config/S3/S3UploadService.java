package org.example.tackit.config.S3;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

  private final S3Client s3Client; // 🌟 AmazonS3 -> S3Client 로 변경

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket;

  // 파일 업로드
  public String saveFile(MultipartFile multipartFile) throws IOException {
    String originalFilename = multipartFile.getOriginalFilename();

    // NPE 방지를 위한 방어 로직 (선택사항이나 실무 권장)
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String uuidFileName = UUID.randomUUID() + extension;

    // 🌟 1. V2 방식의 메타데이터 및 요청 객체 생성 (PutObjectRequest)
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(uuidFileName)
        .contentType(multipartFile.getContentType())
        .contentLength(multipartFile.getSize())
        .build();

    // 🌟 2. V2 방식의 파일 업로드 실행 (RequestBody.fromInputStream 사용)
    s3Client.putObject(putObjectRequest,
        RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

    // 🌟 3. V2 방식의 URL 생성 (S3Utilities 활용)
    S3Utilities s3Utilities = s3Client.utilities();
    GetUrlRequest getUrlRequest = GetUrlRequest.builder()
        .bucket(bucket)
        .key(uuidFileName)
        .build();

    return s3Utilities.getUrl(getUrlRequest).toString();
  }

  // 파일 다운로드
  public ResponseEntity<UrlResource> downloadImage(String originalFilename) {
    // V2 방식으로 URL 객체 가져오기
    S3Utilities s3Utilities = s3Client.utilities();
    GetUrlRequest getUrlRequest = GetUrlRequest.builder()
        .bucket(bucket)
        .key(originalFilename)
        .build();
    URL url = s3Utilities.getUrl(getUrlRequest);

    UrlResource urlResource = new UrlResource(url);

    String contentDisposition = "attachment; filename=\"" + originalFilename + "\"";

    // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .body(urlResource);
  }

  // 파일 삭제
  public void deleteImage(String imageUrl) {
    String key = extractKeyFromUrl(imageUrl);

    // 🌟 4. V2 방식의 삭제 요청 객체 생성 및 삭제 실행
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  private String extractKeyFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}