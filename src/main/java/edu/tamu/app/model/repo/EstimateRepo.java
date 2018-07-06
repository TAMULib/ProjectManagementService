package edu.tamu.app.model.repo;

import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.repo.custom.EstimateRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface EstimateRepo extends WeaverRepo<Estimate>, ServiceMappingRepo<Float, Estimate>, EstimateRepoCustom {

}
