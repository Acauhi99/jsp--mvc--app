package repositories;

import models.ConsultaVeterinaria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ConsultaVeterinariaRepository extends BaseRepository<ConsultaVeterinaria> {
    
    public ConsultaVeterinariaRepository() {
        super(ConsultaVeterinaria.class);
    }
    
    public List<ConsultaVeterinaria> findByAnimalId(UUID animalId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaVeterinaria> query = em.createQuery(
                "SELECT c FROM ConsultaVeterinaria c WHERE c.animal.id = :animalId ORDER BY c.dataHora DESC", ConsultaVeterinaria.class);
            query.setParameter("animalId", animalId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ConsultaVeterinaria> findByVeterinarioId(UUID veterinarioId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaVeterinaria> query = em.createQuery(
                "SELECT c FROM ConsultaVeterinaria c WHERE c.veterinario.id = :veterinarioId ORDER BY c.dataHora DESC", ConsultaVeterinaria.class);
            query.setParameter("veterinarioId", veterinarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ConsultaVeterinaria> findByDateRange(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaVeterinaria> query = em.createQuery(
                "SELECT c FROM ConsultaVeterinaria c WHERE c.dataHora BETWEEN :inicio AND :fim ORDER BY c.dataHora DESC", ConsultaVeterinaria.class);
            query.setParameter("inicio", inicio);
            query.setParameter("fim", fim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ConsultaVeterinaria> findByAcompanhamentoNecessario(boolean acompanhamento) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaVeterinaria> query = em.createQuery(
                "SELECT c FROM ConsultaVeterinaria c WHERE c.acompanhamentoNecessario = :acompanhamento", ConsultaVeterinaria.class);
            query.setParameter("acompanhamento", acompanhamento);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<ConsultaVeterinaria> findUpcomingFollowUps(LocalDateTime now) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaVeterinaria> query = em.createQuery(
                "SELECT c FROM ConsultaVeterinaria c WHERE c.acompanhamentoNecessario = true AND c.dataRetorno > :now ORDER BY c.dataRetorno", 
                ConsultaVeterinaria.class);
            query.setParameter("now", now);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}