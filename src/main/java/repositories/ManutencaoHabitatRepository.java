package repositories;

import models.ManutencaoHabitat;
import models.ManutencaoHabitat.StatusManutencao;
import models.ManutencaoHabitat.PrioridadeManutencao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class ManutencaoHabitatRepository extends BaseRepository<ManutencaoHabitat> {

    public ManutencaoHabitatRepository() {
        super(ManutencaoHabitat.class);
    }

    public List<ManutencaoHabitat> findWithFilters(StatusManutencao status, PrioridadeManutencao prioridade,
            UUID habitatId, UUID responsavelId) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT m FROM ManutencaoHabitat m WHERE 1=1");
            Map<String, Object> parameters = new HashMap<>();

            if (status != null) {
                jpql.append(" AND m.status = :status");
                parameters.put("status", status);
            }

            if (prioridade != null) {
                jpql.append(" AND m.prioridade = :prioridade");
                parameters.put("prioridade", prioridade);
            }

            if (habitatId != null) {
                jpql.append(" AND m.habitat.id = :habitatId");
                parameters.put("habitatId", habitatId);
            }

            if (responsavelId != null) {
                jpql.append(" AND m.responsavel.id = :responsavelId");
                parameters.put("responsavelId", responsavelId);
            }

            jpql.append(" ORDER BY m.prioridade DESC, m.dataProgramada");

            TypedQuery<ManutencaoHabitat> query = em.createQuery(jpql.toString(), ManutencaoHabitat.class);

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}