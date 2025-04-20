package repositories;

import models.Animal;
import models.Animal.Classe;
import models.Animal.StatusSaude;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

public class AnimalRepository extends BaseRepository<Animal> {
    
    public AnimalRepository() {
        super(Animal.class);
    }
    
    public List<Animal> findByEspecie(String especie) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Animal> query = em.createQuery(
                "SELECT a FROM Animal a WHERE a.especie = :especie", Animal.class);
            query.setParameter("especie", especie);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Animal> findByClasse(Classe classe) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Animal> query = em.createQuery(
                "SELECT a FROM Animal a WHERE a.classe = :classe", Animal.class);
            query.setParameter("classe", classe);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Animal> findByStatusSaude(StatusSaude statusSaude) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Animal> query = em.createQuery(
                "SELECT a FROM Animal a WHERE a.statusSaude = :statusSaude", Animal.class);
            query.setParameter("statusSaude", statusSaude);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Animal> findByHabitatId(UUID habitatId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Animal> query = em.createQuery(
                "SELECT a FROM Animal a WHERE a.habitat.id = :habitatId", Animal.class);
            query.setParameter("habitatId", habitatId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}