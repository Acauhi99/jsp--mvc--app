package repositories;

import models.Funcionario;
import models.Funcionario.Cargo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class FuncionarioRepository extends BaseRepository<Funcionario> {
    
    public FuncionarioRepository() {
        super(Funcionario.class);
    }
    
    public Optional<Funcionario> findByCpf(String cpf) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Funcionario> query = em.createQuery(
                "SELECT f FROM Funcionario f WHERE f.cpf = :cpf", Funcionario.class);
            query.setParameter("cpf", cpf);
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
}