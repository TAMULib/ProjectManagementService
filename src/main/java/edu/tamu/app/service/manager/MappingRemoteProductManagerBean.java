package edu.tamu.app.service.manager;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.mapping.CardTypeMappingService;
import edu.tamu.app.mapping.EstimateMappingService;
import edu.tamu.app.mapping.StatusMappingService;

public abstract class MappingRemoteProjectManagerBean implements RemoteProjectManagerBean {

    @Autowired
    private CardTypeMappingService cardTypeMappingService;

    @Autowired
    private StatusMappingService statusMappingService;

    @Autowired
    private EstimateMappingService estimateMappingService;

    protected String mapCardType(String rawCardType) {
        return cardTypeMappingService.map(rawCardType);
    }

    protected String mapStatus(String rawStatus) {
        return statusMappingService.map(rawStatus);
    }

    protected Float mapEstimate(String rawEstimate) {
        return estimateMappingService.map(rawEstimate);
    }

}
