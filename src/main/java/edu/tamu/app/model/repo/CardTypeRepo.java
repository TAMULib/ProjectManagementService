package edu.tamu.app.model.repo;

import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.custom.CardTypeRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface CardTypeRepo extends WeaverRepo<CardType>, ServiceMappingRepo<String, CardType>, CardTypeRepoCustom {

}
