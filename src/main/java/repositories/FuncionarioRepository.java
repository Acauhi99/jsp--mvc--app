package repositories;

import models.Funcionario;
import models.Funcionario.Cargo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class FuncionarioRepository extends BaseRepository<Funcionario> {
    
    public FuncionarioRepository() {
        super(Funcionario.class);
    }
    
    
    public List<Funcionario> findByCargo(Cargo cargo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Funcionario> query = em.createQuery(
                "SELECT f FROM Funcionario f WHERE f.cargo = :cargo", Funcionario.class);
            query.setParameter("cargo", cargo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Optional<Funcionario> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Funcionario> query = em.createQuery(
                "SELECT f FROM Funcionario f WHERE f.email = :email", Funcionario.class);
            query.setParameter("email", email);
            try {
                Funcionario funcionario = query.getSingleResult();
                return Optional.of(funcionario);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } finally {
            em.close();
        }
    }
}