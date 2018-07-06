package edu.tamu.app.mapping;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.repo.EstimateRepo;

@Service
public class EstimateMappingService extends AbstractMappingService<Float, Estimate, EstimateRepo> {

    @Override
    public Float map(String rawData) {
        Float value;
        try {
            value = Float.valueOf(rawData);
        } catch (Exception e) {
            value = super.map(rawData);
        }
        return value;
    }

    @Override
    public Float handleUnmapped(String rawData) {
        return null;
    }

}
