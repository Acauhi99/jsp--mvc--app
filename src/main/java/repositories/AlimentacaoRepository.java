package repositories;

import models.Alimentacao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AlimentacaoRepository extends BaseRepository<Alimentacao> {
    
    public AlimentacaoRepository() {
        super(Alimentacao.class);
    }
    
    public List<Alimentacao> findByAnimalId(UUID animalId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Alimentacao> query = em.createQuery(
                "SELECT a FROM Alimentacao a WHERE a.animal.id = :animalId ORDER BY a.dataHora DESC", Alimentacao.class);
            query.setParameter("animalId", animalId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Alimentacao> findByFuncionarioId(UUID funcionarioId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Alimentacao> query = em.createQuery(
                "SELECT a FROM Alimentacao a WHERE a.funcionarioResponsavel.id = :funcionarioId ORDER BY a.dataHora DESC", Alimentacao.class);
            query.setParameter("funcionarioId", funcionarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Alimentacao> findByDateRange(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Alimentacao> query = em.createQuery(
                "SELECT a FROM Alimentacao a WHERE a.dataHora BETWEEN :inicio AND :fim ORDER BY a.dataHora DESC", Alimentacao.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Alimentacao> findByTipoAlimento(String tipoAlimento) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Alimentacao> query = em.createQuery(
                "SELECT a FROM Alimentacao a WHERE a.tipoAlimento = :tipoAlimento ORDER BY a.dataHora DESC", Alimentacao.class);
            query.setParameter("tipoAlimento", tipoAlimento);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}