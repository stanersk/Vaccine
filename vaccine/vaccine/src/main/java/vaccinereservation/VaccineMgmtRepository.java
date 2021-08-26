package vaccinereservation;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="vaccineMgmts", path="vaccineMgmts")
public interface VaccineMgmtRepository extends PagingAndSortingRepository<VaccineMgmt, Long>{


}
