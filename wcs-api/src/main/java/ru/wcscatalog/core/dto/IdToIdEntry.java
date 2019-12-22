package ru.wcscatalog.core.dto;

public class IdToIdEntry {
    private Long primaryId;
    private Long secondaryId;

    public Long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(Long primaryId) {
        this.primaryId = primaryId;
    }

    public Long getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(Long secondaryId) {
        this.secondaryId = secondaryId;
    }
}
