package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.Product;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.custom.ProductRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProductRepoImpl extends AbstractWeaverRepoImpl<Product, ProductRepo> implements ProductRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/products";
    }

}
