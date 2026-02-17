package org.example.tackit.domain.entity.poll;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PollStatus {
  ONGOING,
  ENDED
}