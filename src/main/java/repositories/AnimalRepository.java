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

    public List<Animal> findFiltered(String especie, String classe, String genero, String tipoAmbiente, int offset,
            int limit) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Animal a WHERE 1=1";
            if (especie != null && !especie.isEmpty()) {
                jpql += " AND a.especie = :especie";
            }
            if (classe != null && !classe.isEmpty()) {
                jpql += " AND a.classe = :classe";
            }
            if (genero != null && !genero.isEmpty()) {
                jpql += " AND a.genero = :genero";
            }
            if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
                jpql += " AND a.habitat.tipoAmbiente = :tipoAmbiente";
            }
            TypedQuery<Animal> query = em.createQuery(jpql, Animal.class);
            if (especie != null && !especie.isEmpty()) {
                query.setParameter("especie", especie);
            }
            if (classe != null && !classe.isEmpty()) {
                query.setParameter("classe", models.Animal.Classe.valueOf(classe));
            }
            if (genero != null && !genero.isEmpty()) {
                query.setParameter("genero", models.Animal.Genero.valueOf(genero));
            }
            if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
                query.setParameter("tipoAmbiente", models.Habitat.TipoAmbiente.valueOf(tipoAmbiente));
            }
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countFiltered(String especie, String classe, String genero, String tipoAmbiente) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Animal a WHERE 1=1";
            if (especie != null && !especie.isEmpty()) {
                jpql += " AND a.especie = :especie";
            }
            if (classe != null && !classe.isEmpty()) {
                jpql += " AND a.classe = :classe";
            }
            if (genero != null && !genero.isEmpty()) {
                jpql += " AND a.genero = :genero";
            }
            if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
                jpql += " AND a.habitat.tipoAmbiente = :tipoAmbiente";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            if (especie != null && !especie.isEmpty()) {
                query.setParameter("especie", especie);
            }
            if (classe != null && !classe.isEmpty()) {
                query.setParameter("classe", models.Animal.Classe.valueOf(classe));
            }
            if (genero != null && !genero.isEmpty()) {
                query.setParameter("genero", models.Animal.Genero.valueOf(genero));
            }
            if (tipoAmbiente != null && !tipoAmbiente.isEmpty()) {
                query.setParameter("tipoAmbiente", models.Habitat.TipoAmbiente.valueOf(tipoAmbiente));
            }
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}