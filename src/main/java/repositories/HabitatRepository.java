package repositories;

import models.Habitat;
import models.Habitat.TipoAmbiente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class HabitatRepository extends BaseRepository<Habitat> {
    
    public HabitatRepository() {
        super(Habitat.class);
    }
    
    public List<Habitat> findByTipoAmbiente(TipoAmbiente tipoAmbiente) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Habitat> query = em.createQuery(
                "SELECT h FROM Habitat h WHERE h.tipoAmbiente = :tipoAmbiente", Habitat.class);
            query.setParameter("tipoAmbiente", tipoAmbiente);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Habitat> findByPublicoAcessivel(boolean publicoAcessivel) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Habitat> query = em.createQuery(
                "SELECT h FROM Habitat h WHERE h.publicoAcessivel = :publicoAcessivel", Habitat.class);
            query.setParameter("publicoAcessivel", publicoAcessivel);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}