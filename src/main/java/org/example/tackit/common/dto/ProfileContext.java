package org.example.tackit.common.dto;

import org.example.tackit.domain.entity.Org.OrgType;

public record ProfileContext(Long id, OrgType type) {
}
