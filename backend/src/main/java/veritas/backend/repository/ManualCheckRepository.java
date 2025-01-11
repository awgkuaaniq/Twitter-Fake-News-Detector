package veritas.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import veritas.common.model.ManualCheck;

public interface ManualCheckRepository extends MongoRepository<ManualCheck, String> {
}