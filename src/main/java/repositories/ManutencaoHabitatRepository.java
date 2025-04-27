package repositories;

import models.ManutencaoHabitat;
import models.ManutencaoHabitat.StatusManutencao;
import models.ManutencaoHabitat.PrioridadeManutencao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ManutencaoHabitatRepository extends BaseRepository<ManutencaoHabitat> {
    
    public ManutencaoHabitatRepository() {
        super(ManutencaoHabitat.class);
    }
    
    public List<ManutencaoHabitat> findByHabitatId(UUID habitatId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.habitat.id = :habitatId ORDER BY m.dataSolicitacao DESC", ManutencaoHabitat.class);
            query.setParameter("habitatId", habitatId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ManutencaoHabitat> findByStatus(StatusManutencao status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.status = :status ORDER BY m.prioridade DESC, m.dataProgramada", ManutencaoHabitat.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ManutencaoHabitat> findByPrioridade(PrioridadeManutencao prioridade) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.prioridade = :prioridade AND m.status != :statusConcluida ORDER BY m.dataProgramada", ManutencaoHabitat.class);
            query.setParameter("prioridade", prioridade);
            query.setParameter("statusConcluida", StatusManutencao.CONCLUIDA);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ManutencaoHabitat> findPendingMaintenance() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.status IN (:statusPendente, :statusEmAndamento) ORDER BY m.prioridade DESC, m.dataProgramada", 
                ManutencaoHabitat.class);
            query.setParameter("statusPendente", StatusManutencao.PENDENTE);
            query.setParameter("statusEmAndamento", StatusManutencao.EM_ANDAMENTO);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ManutencaoHabitat> findScheduledMaintenanceForDate(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.dataProgramada BETWEEN :inicio AND :fim AND m.status != :statusCancelada ORDER BY m.dataProgramada", 
                ManutencaoHabitat.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);
            query.setParameter("statusCancelada", StatusManutencao.CANCELADA);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ManutencaoHabitat> findByResponsavelId(UUID responsavelId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ManutencaoHabitat> query = em.createQuery(
                "SELECT m FROM ManutencaoHabitat m WHERE m.responsavel.id = :responsavelId ORDER BY m.status, m.dataProgramada", ManutencaoHabitat.class);
            query.setParameter("responsavelId", responsavelId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}