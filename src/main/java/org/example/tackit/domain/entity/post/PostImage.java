package org.example.tackit.domain.entity.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String imageUrl;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Builder
  public PostImage(String imageUrl, Post post) {
    this.imageUrl = imageUrl;
    this.post = post;
  }

  public void updateUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}