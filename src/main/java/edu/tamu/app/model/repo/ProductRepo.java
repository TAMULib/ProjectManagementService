package edu.tamu.app.model.repo;

import java.util.Optional;

import edu.tamu.app.model.Product;
import edu.tamu.app.model.repo.custom.ProductRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ProductRepo extends WeaverRepo<Product>, ProductRepoCustom {

    public Optional<Product> findByName(String name);

}
