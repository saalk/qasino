package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.ForeverFreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ForeverFreeRepository  extends JpaRepository<ForeverFreeEntity, String> {
}
