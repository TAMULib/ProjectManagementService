package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.custom.CardTypeRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class CardTypeRepoImpl extends AbstractWeaverRepoImpl<CardType, CardTypeRepo> implements CardTypeRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/card-type";
    }

}
