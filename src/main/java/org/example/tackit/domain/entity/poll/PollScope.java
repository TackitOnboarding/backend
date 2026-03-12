package org.example.tackit.domain.entity.poll;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PollScope {
  ALL,
  PARTIAL
}