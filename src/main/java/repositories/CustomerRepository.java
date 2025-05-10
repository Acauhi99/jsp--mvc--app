package repositories;

import models.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerRepository extends BaseRepository<Customer> {

    public CustomerRepository() {
        super(Customer.class);
    }

    public Optional<Customer> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            try {
                Customer customer = query.getSingleResult();
                return Optional.of(customer);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } finally {
            em.close();
        }
    }

    public List<Customer> findAllWithIngressos() {
        EntityManager em = getEntityManager();
        try {
            return em
                    .createQuery("SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.ingressosAdquiridos",
                            Customer.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Customer> findByIdWithIngressos(UUID id) {
        EntityManager em = getEntityManager();
        try {
            List<Customer> result = em.createQuery(
                    "SELECT c FROM Customer c LEFT JOIN FETCH c.ingressosAdquiridos WHERE c.id = :id",
                    Customer.class)
                    .setParameter("id", id)
                    .getResultList();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }
}
