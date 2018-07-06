package edu.tamu.app.mapping;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;

@Service
public class CardTypeMappingService extends AbstractMappingService<String, CardType, CardTypeRepo> {

    @Override
    public String handleUnmapped(String rawData) {
        return rawData;
    }

}
