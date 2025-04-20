package repositories;

import models.Ingresso;
import models.Ingresso.TipoIngresso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

public class IngressoRepository extends BaseRepository<Ingresso> {
    
    public IngressoRepository() {
        super(Ingresso.class);
    }
    
    public List<Ingresso> findByTipo(TipoIngresso tipo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ingresso> query = em.createQuery(
                "SELECT i FROM Ingresso i WHERE i.tipo = :tipo", Ingresso.class);
            query.setParameter("tipo", tipo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Ingresso> findByUtilizado(boolean utilizado) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ingresso> query = em.createQuery(
                "SELECT i FROM Ingresso i WHERE i.utilizado = :utilizado", Ingresso.class);
            query.setParameter("utilizado", utilizado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Ingresso> findByCompradorId(UUID compradorId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ingresso> query = em.createQuery(
                "SELECT i FROM Ingresso i WHERE i.comprador.id = :compradorId", Ingresso.class);
            query.setParameter("compradorId", compradorId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}